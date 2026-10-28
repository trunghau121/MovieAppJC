package com.movieappjc.steptracker.service

import android.Manifest
import android.app.AlarmManager
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
import android.os.IBinder
import android.os.SystemClock
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.movieappjc.R
import com.movieappjc.steptracker.data.local.StepStoreData
import com.movieappjc.steptracker.domain.models.StepBlock
import com.movieappjc.steptracker.domain.repository.StepRepository
import com.movieappjc.steptracker.util.Formatters
import com.movieappjc.steptracker.widget.StepTrackerWidget
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@AndroidEntryPoint
class BackgroundStepTrackerService : Service(), SensorEventListener {

    @Inject
    lateinit var stepRepository: StepRepository
    @Inject
    lateinit var storeData: StepStoreData

    private var sensorManager: SensorManager? = null
    private var stepSensor: Sensor? = null
    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    @Volatile private var savedDbStepsToday: Long = 0L

    // Measure elapsed time in milliseconds from boot (independent of clock/timezone changes)
    @Volatile private var batchStartRealtime = SystemClock.elapsedRealtime()

    private var notificationUpdateJob: Job? = null
    private var saveLocalStepsJob: Job? = null
    private val channelId = "secure_step_channel"

    // Listen for natural midnight OR manual time/timezone changes
    private val systemTimeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                Intent.ACTION_DATE_CHANGED -> {
                    // Natural midnight rollover
                    serviceScope.launch { resetStepsForNewDay() }
                }
                Intent.ACTION_TIME_CHANGED, Intent.ACTION_TIMEZONE_CHANGED -> {
                    // Save current steps immediately when user manually changes time/timezone
                    serviceScope.launch {
                        saveLocalSteps(now = System.currentTimeMillis())
                    }
                }
            }
        }
    }

    override fun onCreate() {
        super.onCreate()

        // 1. Initialize and start foreground service immediately
        safeStartForeground()

        sensorManager = getSystemService(SENSOR_SERVICE) as? SensorManager

        if (!hasActivityPermission()) {
            stopSelf()
            return
        }

        // 2. Load today's already saved DB steps into RAM
        loadTodayDbSteps()

        // 3. Register hardware step counter sensor (Optimized delay for battery)
        stepSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        safeRegisterSensors()

        // 4. Register broadcast receiver for date and time changes
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_DATE_CHANGED)
            addAction(Intent.ACTION_TIME_CHANGED)
            addAction(Intent.ACTION_TIMEZONE_CHANGED)
        }
        try {
            registerReceiver(systemTimeReceiver, filter)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        safeStartForeground()
        safeRegisterSensors()
        return START_STICKY
    }

    private fun safeStartForeground() {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as? NotificationManager ?: return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Step Tracker Service", NotificationManager.IMPORTANCE_LOW)
            channel.setShowBadge(false)
            notificationManager.createNotificationChannel(channel)
        }

        serviceScope.launch(Dispatchers.Main) {
            val notification = serviceScope.async(Dispatchers.IO) {
                createNotification(savedDbStepsToday + stepRepository.currentBatchSteps)
            }.await()

            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                    startForeground(1, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_HEALTH)
                } else {
                    startForeground(1, notification)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun safeRegisterSensors() {
        stepSensor?.let { sensor ->
            try {
                sensorManager?.unregisterListener(this, sensor)
                // Use SENSOR_DELAY_NORMAL to optimize battery usage
                sensorManager?.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun createNotification(currentSteps: Long): Notification {
        val maxSteps = 10000L
        val accent = ContextCompat.getColor(this, R.color.primary_second)
        val progressColor = ContextCompat.getColor(this, R.color.violet)

        val progress = ((currentSteps * 100) / maxSteps).toInt().coerceIn(0, 100)

        val style = NotificationCompat.ProgressStyle()
            .setStyledByProgress(false)
            .setProgress(progress)
            .setProgressSegments(
                listOf(
                    NotificationCompat.ProgressStyle.Segment(progress).setColor(progressColor),
                    NotificationCompat.ProgressStyle.Segment(100).setColor(accent)
                )
            )

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle(getString(R.string.notif_steps_today, Formatters.steps(currentSteps)))
            .setContentText(getString(R.string.notif_goal_progress, progress, Formatters.steps(maxSteps)))
            .setSmallIcon(android.R.drawable.ic_menu_compass)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setStyle(style)
            .setShortCriticalText("$progress%")
            .setProgress(maxSteps.toInt(), progress, false)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            notification
                .setCategory(Notification.CATEGORY_WORKOUT)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        }

        return notification.build()
    }

    private fun liveNotificationUpdate(isReset: Boolean = false) {
        notificationUpdateJob?.cancel()
        notificationUpdateJob = serviceScope.launch {
            val totalSteps = savedDbStepsToday + stepRepository.currentBatchSteps
            if (totalSteps == 0L && !isReset) return@launch

            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as? NotificationManager
            notificationManager?.notify(1, createNotification(totalSteps))

            try {
                StepTrackerWidget.updateWidget(this@BackgroundStepTrackerService, totalSteps)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun loadTodayDbSteps() {
        serviceScope.launch {
            try {
                val localRecords = stepRepository.getLocalSteps()
                savedDbStepsToday = localRecords.sumOf { it.count }
                liveNotificationUpdate()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private suspend fun resetStepsForNewDay() {
        val now = System.currentTimeMillis()

        if (stepRepository.currentBatchSteps > 0) {
            saveLocalSteps(now)
        }

        savedDbStepsToday = 0L
        stepRepository.currentBatchSteps = 0L
        stepRepository.startTimeOfBatch = now
        batchStartRealtime = SystemClock.elapsedRealtime()

        liveNotificationUpdate(isReset = true)
    }



    @Synchronized
    private fun addStepsAndCheckBatch(newlyDetectedSteps: Long): Boolean {
        stepRepository.currentBatchSteps += newlyDetectedSteps
        val nowRealtime = SystemClock.elapsedRealtime()
        return stepRepository.currentBatchSteps >= 30 || (nowRealtime - batchStartRealtime > 10_000L)
    }

    @Synchronized
    private fun prepareBatchToSave(now: Long): StepBlock? {
        val stepsToSave = stepRepository.currentBatchSteps
        if (stepsToSave == 0L) return null

        val batchStart = stepRepository.startTimeOfBatch
        stepRepository.currentBatchSteps = 0L
        stepRepository.startTimeOfBatch = now
        batchStartRealtime = SystemClock.elapsedRealtime()

        savedDbStepsToday += stepsToSave

        return StepBlock(batchStart, now, stepsToSave)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type != Sensor.TYPE_STEP_COUNTER) return

        if (!hasActivityPermission()) {
            cleanUpAndStop()
            return
        }

        val totalStepsSinceBoot = event.values[0].toLong()

        // Handle first sensor trigger or device reboot scenarios
        if (storeData.previousTotalSteps == -1L || totalStepsSinceBoot < storeData.previousTotalSteps) {
            storeData.previousTotalSteps = totalStepsSinceBoot
            stepRepository.startTimeOfBatch = System.currentTimeMillis()
            batchStartRealtime = SystemClock.elapsedRealtime()
            return
        }

        val newlyDetectedSteps = totalStepsSinceBoot - storeData.previousTotalSteps

        if (newlyDetectedSteps > 0) {
            storeData.previousTotalSteps = totalStepsSinceBoot

            val shouldSaveImmediately = addStepsAndCheckBatch(newlyDetectedSteps)

            // Refresh UI with new step count
            liveNotificationUpdate()

            saveLocalStepsJob?.cancel()
            saveLocalStepsJob = if (shouldSaveImmediately) {
                serviceScope.launch {
                    saveLocalSteps(System.currentTimeMillis())
                }
            } else {
                serviceScope.launch {
                    delay(10000.milliseconds)
                    saveLocalSteps(System.currentTimeMillis())
                }
            }
        }
    }

    private suspend fun saveLocalSteps(now: Long) {
        val block = prepareBatchToSave(now) ?: return

        try {
            stepRepository.saveLocalSteps(block)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun hasActivityPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_GRANTED
        } else true
    }

    private fun cleanUpAndStop() {
        try {
            sensorManager?.unregisterListener(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        try {
            unregisterReceiver(systemTimeReceiver)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        storeData.isTrackingEnabled = false
        stopSelf()
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
    override fun onBind(intent: Intent?): IBinder? = null

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)

        if (storeData.isTrackingEnabled) {
            try {
                val restartIntent = Intent(applicationContext, BackgroundStepTrackerService::class.java).also {
                    it.setPackage(packageName)
                }
                val pendingIntent = PendingIntent.getService(
                    applicationContext,
                    1,
                    restartIntent,
                    PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
                )
                val alarmManager = applicationContext.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
                alarmManager?.set(
                    AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime() + 1000,
                    pendingIntent
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        try {
            sensorManager?.unregisterListener(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        try {
            unregisterReceiver(systemTimeReceiver)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        notificationUpdateJob?.cancel()
        saveLocalStepsJob?.cancel()

        runBlocking(Dispatchers.IO) {
            try {
                saveLocalSteps(now = System.currentTimeMillis())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        try {
            serviceScope.cancel()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
