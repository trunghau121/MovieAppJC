package com.movieappjc.steptracker.service

import android.Manifest
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Handler
import android.os.HandlerThread
import android.os.IBinder
import android.os.Looper
import android.os.PowerManager
import android.os.SystemClock
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import androidx.core.content.ContextCompat
import com.movieappjc.R
import com.movieappjc.steptracker.data.local.StepStoreData
import com.movieappjc.steptracker.di.AppCoroutineScope
import com.movieappjc.steptracker.domain.models.StepBlock
import com.movieappjc.steptracker.domain.repository.StepRepository
import com.movieappjc.steptracker.util.Formatters
import com.movieappjc.steptracker.util.StepConstants
import com.movieappjc.steptracker.util.TimeUtils
import com.movieappjc.steptracker.widget.StepTrackerWidget
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import javax.inject.Inject

@SuppressLint("LogNotTimber")
@AndroidEntryPoint
class BackgroundStepTrackerService : Service(), SensorEventListener {

    @Inject lateinit var stepRepository: StepRepository
    @Inject lateinit var storeData: StepStoreData
    @Inject lateinit var appScope: AppCoroutineScope

    private val accumulator = StepAccumulator()

    // Serialises every operation that touches both the DB and the accumulator's saved/in-flight state
    private val ioMutex = Mutex()
    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val mainHandler = Handler(Looper.getMainLooper())

    private var sensorManager: SensorManager? = null
    private var stepSensor: Sensor? = null
    private var sensorThread: HandlerThread? = null

    private var accentColor = 0
    private var progressColor = 0

    @Volatile private var isFirstSensorEvent = true
    @Volatile private var isStopping = false
    @Volatile private var isSensorRegistered = false
    @Volatile private var isReceiverRegistered = false
    @Volatile private var lastPermissionCheckElapsed = 0L
    @Volatile private var lastNotificationUpdateElapsed = 0L
    @Volatile private var lastWidgetUpdateElapsed = 0L

    @Volatile private var notificationUpdateJob: Job? = null
    @Volatile private var delayedSaveJob: Job? = null
    @Volatile private var widgetJob: Job? = null

    private val powerManager by lazy { getSystemService(POWER_SERVICE) as? PowerManager }

    private val contentIntent: PendingIntent? by lazy {
        packageManager.getLaunchIntentForPackage(packageName)?.let {
            PendingIntent.getActivity(
                this, 0, it,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
        }
    }

    private val systemReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                Intent.ACTION_DATE_CHANGED -> serviceScope.launch { handleDayChange() }
                Intent.ACTION_TIME_CHANGED,
                Intent.ACTION_TIMEZONE_CHANGED -> serviceScope.launch { handleClockChange() }
                Intent.ACTION_SCREEN_ON -> {
                    // Deliver steps batched in the sensor FIFO and refresh UI immediately
                    flushSensor()
                    liveNotificationUpdate(isReset = true)
                }
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()

        // Colors must be resolved BEFORE the first notification is built
        accentColor = ContextCompat.getColor(this, R.color.primary_second)
        progressColor = ContextCompat.getColor(this, R.color.violet)
        createNotificationChannel()

        if (!safeStartForeground()) {
            shutdown(disableTracking = false)
            return
        }
        if (!hasActivityPermission()) {
            shutdown(disableTracking = true)
            return
        }

        sensorManager = getSystemService(SENSOR_SERVICE) as? SensorManager
        stepSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        if (stepSensor == null) {
            shutdown(disableTracking = true)
            return
        }

        sensorThread = HandlerThread("StepSensorThread").also { it.start() }

        loadTodayDbSteps()
        registerSensor()
        registerSystemReceiver()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (isStopping) return START_NOT_STICKY

        if (!safeStartForeground()) {
            shutdown(disableTracking = false)
            return START_NOT_STICKY
        }
        if (!hasActivityPermission()) {
            shutdown(disableTracking = true)
            return START_NOT_STICKY
        }

        // Sensor is registered once; later start commands (e.g. app opened) only pull batched events
        if (isSensorRegistered) flushSensor() else registerSensor()

        // START_STICKY lets the system restart the service after it is killed.
        // (No AlarmManager restart hack: it is not allowed to start foreground services on Android 12+.)
        return START_STICKY
    }

    // ---------------------------------------------------------------------------------------------
    // Sensor handling
    // ---------------------------------------------------------------------------------------------

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type != Sensor.TYPE_STEP_COUNTER || isStopping) return

        if (!isPermissionStillGranted()) {
            mainHandler.post { shutdown(disableTracking = true) }
            return
        }

        val total = event.values[0].toLong()
        val now = System.currentTimeMillis()

        if (isFirstSensorEvent) {
            isFirstSensorEvent = false
            handleFirstSensorEvent(total, now)
            return
        }

        // Do not rely only on ACTION_DATE_CHANGED (some OEMs drop it): check the day on every event
        if (accumulator.isDifferentDay(now)) {
            serviceScope.launch { handleDayChange() }
        }

        val last = accumulator.lastSensorValue()
        if (last !in 0L..total) {
            // Counter was reset while running: just take a new baseline
            accumulator.setSensorValue(total)
            return
        }

        val delta = total - last
        when {
            delta <= 0L -> Unit
            delta > StepConstants.MAX_STEPS_PER_SENSOR_EVENT -> {
                // Sensor glitch: ignore the jump but keep the baseline in sync
                accumulator.setSensorValue(total)
                if (accumulator.isIdle() && storeData.isTrackingEnabled) {
                    storeData.previousTotalSteps = total
                }
            }
            else -> {
                val saveNow = accumulator.add(delta, total)
                liveNotificationUpdate()
                scheduleSave(immediately = saveNow)
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit

    /**
     * First event after the service (re)started. The hardware counter kept counting while we were dead,
     * so the difference to the last persisted baseline is recovered instead of being thrown away.
     */
    private fun handleFirstSensorEvent(total: Long, now: Long) {
        val persisted = storeData.previousTotalSteps
        val lastSave = storeData.lastSaveTime

        val gap = when {
            persisted < 0L -> 0L        // no baseline yet
            total >= persisted -> total - persisted
            else -> total               // device rebooted: the counter restarted from 0
        }

        // The last save may be from yesterday; anything older than that is not recovered (unknown split).
        val dayStart = accumulator.dayStartMillis()
        val startOfYesterday = TimeUtils.startOfDayMillis(dayStart - 1)
        val recoverable = gap in 1..StepConstants.MAX_RECOVERY_STEPS &&
                lastSave >= startOfYesterday && lastSave <= now

        if (recoverable) {
            // Baseline moves first; the recovered steps are then added (possibly after a DB write)
            accumulator.setSensorValue(total)
            if (lastSave >= dayStart) {
                accumulator.addRecovered(gap, startAt = lastSave)
                liveNotificationUpdate()
                scheduleSave(immediately = true)
            } else {
                recoverAcrossMidnight(gap, total, lastSave, now, dayStart)
            }
        } else {
            accumulator.setSensorValue(total)
            accumulator.restartBatchClock(now)
            if (accumulator.isIdle() && storeData.isTrackingEnabled) {
                storeData.previousTotalSteps = total
            }
        }
    }

    /**
     * The service was dead at midnight, so the real split of [gap] between yesterday and today is unknown.
     * Best effort: split proportionally to the time spent on each side of midnight. Yesterday's share is saved
     * as a block that ends yesterday; today's share is added to the current batch.
     * (If the service is alive at midnight nothing is estimated: the batch is closed exactly at midnight.)
     */
    private fun recoverAcrossMidnight(gap: Long, total: Long, lastSave: Long, now: Long, dayStart: Long) {
        val span = (now - lastSave).coerceAtLeast(1L)
        val yesterdayShare = (gap * (dayStart - lastSave) / span).coerceIn(0L, gap)

        serviceScope.launch {
            ioMutex.withLock {
                // NonCancellable: block insert, baseline and accumulator update must happen together
                withContext(NonCancellable) {
                    var todayShare = gap - yesterdayShare
                    if (yesterdayShare > 0L) {
                        val saved = try {
                            stepRepository.saveLocalSteps(StepBlock(lastSave, dayStart - 1, yesterdayShare))
                            true
                        } catch (e: Exception) {
                            Log.e(TAG, "Failed to save yesterday's recovered steps", e)
                            false
                        }
                        if (saved) {
                            // Yesterday's share is now covered; today's share stays recoverable until it is saved
                            if (storeData.isTrackingEnabled) {
                                storeData.previousTotalSteps = total - todayShare
                                storeData.lastSaveTime = dayStart - 1
                            }
                        } else {
                            todayShare += yesterdayShare // do not lose the steps
                        }
                    }
                    if (todayShare > 0L) accumulator.addRecovered(todayShare, startAt = dayStart)
                }
            }
            liveNotificationUpdate(isReset = true)
            scheduleSave(immediately = true)
        }
    }

    private fun registerSensor() {
        val sensor = stepSensor ?: return
        try {
            val handler = sensorThread?.looper?.let { Handler(it) }
            // maxReportLatency lets the CPU sleep while the hardware FIFO collects steps
            val ok = sensorManager?.registerListener(
                this,
                sensor,
                SensorManager.SENSOR_DELAY_NORMAL,
                StepConstants.SENSOR_MAX_REPORT_LATENCY_US,
                handler
            ) ?: false
            isSensorRegistered = ok
            if (!ok) Log.w(TAG, "Step counter listener could not be registered")
        } catch (e: Exception) {
            Log.e(TAG, "registerListener failed", e)
        }
    }

    private fun flushSensor() {
        try {
            sensorManager?.flush(this)
        } catch (e: Exception) {
            Log.w(TAG, "Sensor flush failed", e)
        }
    }

    private fun registerSystemReceiver() {
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_DATE_CHANGED)
            addAction(Intent.ACTION_TIME_CHANGED)
            addAction(Intent.ACTION_TIMEZONE_CHANGED)
            addAction(Intent.ACTION_SCREEN_ON)
        }
        try {
            ContextCompat.registerReceiver(this, systemReceiver, filter, ContextCompat.RECEIVER_NOT_EXPORTED)
            isReceiverRegistered = true
        } catch (e: Exception) {
            Log.e(TAG, "registerReceiver failed", e)
        }
    }

    // ---------------------------------------------------------------------------------------------
    // Persistence
    // ---------------------------------------------------------------------------------------------

    /**
     * Debounced save: while steps keep arriving the save waits, but [StepAccumulator.add] asks for an
     * immediate save once a batch is big/old enough. Only the delay is ever cancelled - a running
     * DB write is protected by NonCancellable in [persistBatch].
     */
    private fun scheduleSave(immediately: Boolean) {
        delayedSaveJob?.cancel()
        if (immediately) {
            serviceScope.launch { flushBatch() }
        } else {
            delayedSaveJob = serviceScope.launch {
                delay(StepConstants.BATCH_MAX_DURATION_MS)
                flushBatch()
            }
        }
    }

    private suspend fun flushBatch() {
        val saved = ioMutex.withLock { persistBatch(System.currentTimeMillis()) }
        if (saved) liveNotificationUpdate()
    }

    /** Must be called with [ioMutex] held. Returns true if a block was written. */
    private suspend fun persistBatch(closeAt: Long): Boolean {
        val ticket = accumulator.beginSave(closeAt) ?: return false
        return withContext(NonCancellable) {
            try {
                stepRepository.saveLocalSteps(ticket.block)
                accumulator.commit(ticket.block)
                // Baseline moves only together with the saved block (and not while tracking is being stopped)
                if (storeData.isTrackingEnabled && ticket.sensorValue >= 0L) {
                    storeData.previousTotalSteps = ticket.sensorValue
                }
                storeData.lastSaveTime = ticket.block.endTime
                true
            } catch (e: Exception) {
                Log.e(TAG, "Failed to save step block, rolling back", e)
                accumulator.rollback(ticket.block)
                false
            }
        }
    }

    private fun loadTodayDbSteps() {
        serviceScope.launch {
            ioMutex.withLock { reloadTodayLocked(System.currentTimeMillis()) }
        }
    }

    /** Must be called with [ioMutex] held. */
    private suspend fun reloadTodayLocked(now: Long) {
        val saved = try {
            stepRepository.getTodayTotalSteps()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load today's steps", e)
            return
        }
        accumulator.resetDay(now, saved)
        liveNotificationUpdate(isReset = true)
    }

    private suspend fun handleDayChange() {
        ioMutex.withLock {
            val now = System.currentTimeMillis()
            if (!accumulator.isDifferentDay(now)) return@withLock

            // Close the old day's batch at its boundary so it is not attributed to the new day
            val oldDayEnd = accumulator.dayEndMillis()
            val closeAt = if (now >= oldDayEnd) oldDayEnd - 1 else now
            persistBatch(closeAt)
            reloadTodayLocked(now)
        }
    }

    private suspend fun handleClockChange() {
        ioMutex.withLock {
            val now = System.currentTimeMillis()
            persistBatch(now)
            reloadTodayLocked(now)
        }
    }

    // ---------------------------------------------------------------------------------------------
    // Notification & widget
    // ---------------------------------------------------------------------------------------------

    private fun liveNotificationUpdate(isReset: Boolean = false) {
        if (isStopping) return
        val now = SystemClock.elapsedRealtime()
        val sinceLast = now - lastNotificationUpdateElapsed

        notificationUpdateJob?.cancel()
        notificationUpdateJob = serviceScope.launch {
            // Throttle to 200ms (0.2s): walking steps (~500ms apart) update instantly with zero delay,
            // while rapid sensor bursts are capped to avoid OS rate-limiting.
            if (!isReset && sinceLast < 200L) {
                delay(200L - sinceLast)
            }
            lastNotificationUpdateElapsed = SystemClock.elapsedRealtime()
            val total = accumulator.totalToday()
            if (total == 0L && !isReset) return@launch
            (getSystemService(NOTIFICATION_SERVICE) as? NotificationManager)
                ?.notify(StepConstants.NOTIFICATION_ID, createNotification(total))
        }
        requestWidgetUpdate(force = isReset)
    }

    /**
     * Leading + trailing throttle. Skipped while the screen is off (nobody sees the widget);
     * the screen-on broadcast forces a refresh.
     */
    private fun requestWidgetUpdate(force: Boolean = false) {
        if (isStopping) return
        if (!force && powerManager?.isInteractive == false) return
        val sinceLast = SystemClock.elapsedRealtime() - lastWidgetUpdateElapsed
        widgetJob?.cancel()
        widgetJob = serviceScope.launch {
            if (!force && sinceLast < StepConstants.WIDGET_UPDATE_INTERVAL_MS) {
                delay(StepConstants.WIDGET_UPDATE_INTERVAL_MS - sinceLast)
            }
            lastWidgetUpdateElapsed = SystemClock.elapsedRealtime()
            updateWidgetSafely(accumulator.totalToday())
        }
    }

    private suspend fun updateWidgetSafely(total: Long) {
        try {
            StepTrackerWidget.updateWidget(applicationContext, total)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.w(TAG, "Widget update failed", e)
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val manager = getSystemService(NOTIFICATION_SERVICE) as? NotificationManager ?: return
        val channel = NotificationChannel(
            CHANNEL_ID,
            getString(R.string.step_notification_channel_name),
            NotificationManager.IMPORTANCE_LOW
        ).apply { setShowBadge(false) }
        manager.createNotificationChannel(channel)
    }

    private fun createNotification(currentSteps: Long): Notification {
        val goal = StepConstants.DAILY_GOAL
        val progress = ((currentSteps * 100) / goal).toInt().coerceIn(0, 100)

        // Segment lengths must add up to the 0..100 scale of `progress`; zero-length segments are invalid
        val segments = buildList {
            if (progress > 0) {
                add(NotificationCompat.ProgressStyle.Segment(progress).setColor(progressColor))
            }
            if (progress < 100) {
                add(NotificationCompat.ProgressStyle.Segment(100 - progress).setColor(accentColor))
            }
        }
        val style = NotificationCompat.ProgressStyle()
            .setStyledByProgress(false)
            .setProgress(progress)
            .setProgressSegments(segments)

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(getString(R.string.notif_steps_today, Formatters.steps(currentSteps)))
            .setContentText(getString(R.string.notif_goal_progress, progress, Formatters.steps(goal)))
            .setSmallIcon(android.R.drawable.ic_menu_compass)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setContentIntent(contentIntent)
            .setStyle(style)
            .setShortCriticalText("$progress%")
            .setProgress(100, progress, false) // fallback bar for devices without ProgressStyle support
            .setCategory(NotificationCompat.CATEGORY_WORKOUT)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .build()
    }

    // ---------------------------------------------------------------------------------------------
    // Foreground / lifecycle helpers
    // ---------------------------------------------------------------------------------------------

    /** Returns false if the system refused to promote the service to foreground. */
    private fun safeStartForeground(): Boolean {
        val notification = createNotification(accumulator.totalToday())
        return try {
            startForegroundTyped(notification)
            true
        } catch (_: SecurityException) {
            // Typed start rejected (missing FGS permission / type): retry without a type
            try {
                startForeground(StepConstants.NOTIFICATION_ID, notification)
                true
            } catch (e2: Exception) {
                Log.e(TAG, "startForeground failed", e2)
                false
            }
        } catch (e: Exception) {
            // e.g. ForegroundServiceStartNotAllowedException when started from the background on Android 12+
            Log.e(TAG, "startForeground failed", e)
            false
        }
    }

    private fun startForegroundTyped(notification: Notification) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(
                StepConstants.NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_HEALTH
            )
        } else {
            startForeground(StepConstants.NOTIFICATION_ID, notification)
        }
    }

    private fun hasActivityPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION) ==
                    PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    // The check is a binder call, so it is done at most once per interval from the sensor callback
    private fun isPermissionStillGranted(): Boolean {
        val nowElapsed = SystemClock.elapsedRealtime()
        if (nowElapsed - lastPermissionCheckElapsed < StepConstants.PERMISSION_RECHECK_INTERVAL_MS) return true
        lastPermissionCheckElapsed = nowElapsed
        return hasActivityPermission()
    }

    /** Stops the service. [disableTracking] = true also turns the feature off and drops the sensor baseline. */
    private fun shutdown(disableTracking: Boolean) {
        if (isStopping) return
        isStopping = true
        if (disableTracking) {
            storeData.isTrackingEnabled = false
            storeData.previousTotalSteps = -1L
        }
        ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        isStopping = true

        try {
            sensorManager?.unregisterListener(this)
        } catch (e: Exception) {
            Log.w(TAG, "unregisterListener failed", e)
        }
        isSensorRegistered = false

        if (isReceiverRegistered) {
            try {
                unregisterReceiver(systemReceiver)
            } catch (_: IllegalArgumentException) {
            }
            isReceiverRegistered = false
        }

        sensorThread?.quitSafely()
        sensorThread = null

        notificationUpdateJob?.cancel()
        delayedSaveJob?.cancel()
        widgetJob?.cancel()

        // Persist the remaining steps off the main thread and independent of this service's lifetime.
        // Going through ioMutex keeps the order consistent with any write that is still in flight.
        val mutex = ioMutex
        appScope.scope.launch {
            mutex.withLock { persistBatch(System.currentTimeMillis()) }
            updateWidgetSafely(accumulator.totalToday())
        }

        serviceScope.cancel()
    }

    private companion object {
        const val TAG = "StepTrackerService"
        const val CHANNEL_ID = "secure_step_channel"
    }
}