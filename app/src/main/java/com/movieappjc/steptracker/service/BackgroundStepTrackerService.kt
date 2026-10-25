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
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import com.movieappjc.R
import com.movieappjc.steptracker.data.local.StepStoreData
import com.movieappjc.steptracker.domain.models.StepBlock
import com.movieappjc.steptracker.domain.repository.StepRepository
import com.movieappjc.steptracker.domain.usecase.ProcessStepsUseCase
import com.movieappjc.steptracker.util.Formatters
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.time.Instant
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

    private lateinit var sensorManager: SensorManager
    private var stepSensor: Sensor? = null
    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    @Volatile private var currentBatchSteps = 0L
    @Volatile private var startTimeOfBatch: Long = System.currentTimeMillis()

    private var notificationUpdateJob: Job? = null
    private val channelId = "secure_step_channel"

    override fun onCreate() {
        super.onCreate()
        // 1. Fulfill Android 12+ strict foreground service 5-second rule immediately
        safeStartForeground()

        if (!hasActivityPermission()) {
            stopSelf()
            return
        }

        // Register step counter sensor
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        stepSensor?.let { sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL) }

        // 2. Start the loop to update the notification UI periodically
        startNotificationUpdateLoop()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Re-call startForeground to prevent crashes when the app is reopened
        safeStartForeground()
        return START_STICKY
    }

    private fun safeStartForeground() {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Step Tracker Service", NotificationManager.IMPORTANCE_LOW)
            channel.setShowBadge(false) // Disable notification dot on app icon
            notificationManager.createNotificationChannel(channel)
        }

        // Create an initial notification showing 0 steps
        val notification = createNotification(0L)

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

    private fun startNotificationUpdateLoop() {
        if (notificationUpdateJob?.isActive == true) return

        notificationUpdateJob = serviceScope.launch {
            while (isActive) {
                val startOfDay = getStartOfDayMillis()
                val now = System.currentTimeMillis()

                // 1. Fetch local steps recorded by the phone today
                val localRecords = stepRepository.getLocalSteps(startOfDay, now).toMutableList()

                // Add uncommitted steps currently in RAM
                if (currentBatchSteps > 0) {
                    localRecords.add(StepBlock(startTimeOfBatch, now, currentBatchSteps))
                }

                // 2. Fetch smartwatch steps from Health Connect
                val watchRecords = mutableListOf<StepBlock>()
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        val status = HealthConnectClient.getSdkStatus(this@BackgroundStepTrackerService)
                        if (status == HealthConnectClient.SDK_AVAILABLE) {
                            val client = HealthConnectClient.getOrCreate(this@BackgroundStepTrackerService)
                            val readPermission = HealthPermission.getReadPermission(StepsRecord::class)

                            if (client.permissionController.getGrantedPermissions().contains(readPermission)) {
                                val response = client.readRecords(
                                    ReadRecordsRequest(
                                        recordType = StepsRecord::class,
                                        timeRangeFilter = TimeRangeFilter.between(
                                            Instant.ofEpochMilli(startOfDay),
                                            Instant.ofEpochMilli(now)
                                        )
                                    )
                                )

                                // Exclude phone-generated steps to avoid double counting
                                val filteredWatchData = response.records.filter {
                                    !it.metadata.dataOrigin.packageName.contains("com.android.healthconnect.phone")
                                }

                                watchRecords.addAll(filteredWatchData.map {
                                    StepBlock(it.startTime.toEpochMilli(), it.endTime.toEpochMilli(), it.count)
                                })
                            }
                        }
                    }
                } catch (_: Exception) {
                    // Ignore Health Connect errors quietly
                }

                // 3. Process and merge blocks (Prioritize watch data over phone data)
                val processedBlocks = processStepsUseCase.execute(localRecords, watchRecords)

                // 4. Calculate final accurate total
                val finalTotalSteps = processedBlocks.sumOf { it.stepCount }

                // 5. Update the notification UI
                val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.notify(1, createNotification(finalTotalSteps))

                // Wait 30 seconds before the next update (Adjusted for testing/real-time feel)
                delay(30_000L)
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

            val now = System.currentTimeMillis()

            // Save to database every 50 steps or every 20 seconds to prevent data loss
            if (currentBatchSteps >= 50 || startTimeOfBatch + 20_000L < now) {
                serviceScope.launch {
                    stepRepository.saveLocalSteps(StepBlock(startTimeOfBatch, now, currentBatchSteps))
                    currentBatchSteps = 0L
                    startTimeOfBatch = now
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
        sensorManager.unregisterListener(this)
        storeData.isTrackingEnabled = false // Use StoreData instead of direct SharedPreferences
        stopSelf()
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        sensorManager.unregisterListener(this)
        notificationUpdateJob?.cancel()
        serviceScope.cancel()
    }
}