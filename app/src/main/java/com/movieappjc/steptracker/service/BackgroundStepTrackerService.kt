package com.movieappjc.steptracker.service

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.movieappjc.R
import com.movieappjc.steptracker.data.local.StepStoreData
import com.movieappjc.steptracker.domain.models.StepBlock
import com.movieappjc.steptracker.domain.repository.StepRepository
import com.movieappjc.steptracker.domain.usecase.ProcessStepsUseCase
import com.movieappjc.steptracker.util.Formatters
import com.movieappjc.steptracker.widget.StepTrackerWidget
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@AndroidEntryPoint
class BackgroundStepTrackerService : Service(), SensorEventListener {

    @Inject
    lateinit var stepRepository: StepRepository
    @Inject
    lateinit var storeData: StepStoreData
    @Inject
    lateinit var processStepsUseCase: ProcessStepsUseCase

    private var sensorManager: SensorManager? = null
    private var stepSensor: Sensor? = null
    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    @Volatile private var currentBatchSteps = 0L
    @Volatile private var startTimeOfBatch: Long = System.currentTimeMillis()
    @Volatile private var savedDbStepsToday: Long = 0L

    private val channelId = "secure_step_channel"

    override fun onCreate() {
        super.onCreate()
        // 1. Fulfill Android 12+ strict foreground service 5-second rule immediately
        safeStartForeground()

        sensorManager = getSystemService(SENSOR_SERVICE) as? SensorManager

        if (!hasActivityPermission()) {
            stopSelf()
            return
        }

        // 2. Load today's already saved DB steps into RAM
        loadTodayDbSteps()

        // 3. Register step counter sensor
        stepSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        stepSensor?.let { sensorManager?.registerListener(this, it, SensorManager.SENSOR_DELAY_FASTEST) }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        safeStartForeground()
        return START_STICKY
    }

    private fun safeStartForeground() {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as? NotificationManager ?: return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Step Tracker Service", NotificationManager.IMPORTANCE_LOW)
            channel.setShowBadge(false)
            notificationManager.createNotificationChannel(channel)
        }

        val totalStepsNow = savedDbStepsToday + currentBatchSteps
        val notification = createNotification(totalStepsNow)

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

    private fun updateNotificationImmediately() {
        val totalStepsNow = savedDbStepsToday + currentBatchSteps
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as? NotificationManager
        notificationManager?.notify(1, createNotification(totalStepsNow))

        // Update Glance Home Screen Widget instantly
        serviceScope.launch {
            try {
                StepTrackerWidget.updateWidget(this@BackgroundStepTrackerService, totalStepsNow)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun createNotification(currentSteps: Long): Notification {
        val maxSteps = 10000
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
            .setContentText(getString(R.string.notif_goal_progress, progress, Formatters.steps(maxSteps.toLong())))
            .setSmallIcon(android.R.drawable.ic_menu_compass)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setStyle(style)
            .setShortCriticalText("$progress%")
            .setProgress(maxSteps, progress, false)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            notification
                .setCategory(Notification.CATEGORY_WORKOUT)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        }

        return notification.build()
    }

    private fun loadTodayDbSteps() {
        serviceScope.launch {
            try {
                val startOfDay = getStartOfDayMillis()
                val now = System.currentTimeMillis()
                val localRecords = stepRepository.getLocalSteps(startOfDay, now)
                val processedBlocks = processStepsUseCase.execute(localRecords)
                savedDbStepsToday = processedBlocks.sumOf { it.stepCount }
                updateNotificationImmediately()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun getStartOfDayMillis(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
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
            startTimeOfBatch = System.currentTimeMillis()
            return
        }

        val newlyDetectedSteps = totalStepsSinceBoot - storeData.previousTotalSteps

        if (newlyDetectedSteps > 0) {
            currentBatchSteps += newlyDetectedSteps
            storeData.previousTotalSteps = totalStepsSinceBoot

            // 1. UPDATE NOTIFICATION IMMEDIATELY!
            updateNotificationImmediately()

            val now = System.currentTimeMillis()

            // 2. Save to database if >= 50 steps OR >= 20 seconds with new steps
            if (currentBatchSteps >= 50 || (currentBatchSteps > 0 && startTimeOfBatch + 20_000L < now)) {
                val batchToSave = currentBatchSteps
                val batchStartTime = startTimeOfBatch

                currentBatchSteps = 0L
                startTimeOfBatch = now
                savedDbStepsToday += batchToSave

                serviceScope.launch {
                    try {
                        stepRepository.saveLocalSteps(StepBlock(batchStartTime, now, batchToSave))
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
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
        storeData.isTrackingEnabled = false
        stopSelf()
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        try {
            sensorManager?.unregisterListener(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        try {
            serviceScope.cancel()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
