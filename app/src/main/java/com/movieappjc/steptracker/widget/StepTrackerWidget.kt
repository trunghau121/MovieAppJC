package com.movieappjc.steptracker.widget

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.background
import androidx.glance.color.ColorProvider
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.state.GlanceStateDefinition
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.movieappjc.R
import com.movieappjc.steptracker.data.local.StepStoreData
import com.movieappjc.steptracker.util.Formatters
import com.movieappjc.steptracker.util.TimeUtils

class StepTrackerWidget : GlanceAppWidget() {

    override val stateDefinition: GlanceStateDefinition<Preferences> = PreferencesGlanceStateDefinition

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val launchIntent = context.packageManager.getLaunchIntentForPackage(context.packageName)

        provideContent {
            val prefs = currentState<Preferences>()

            // A value stored for a previous day must not be shown today
            val stepsFromState = if (prefs[DAY_KEY] == TimeUtils.startOfDayMillis()) prefs[STEPS_KEY] else null
            val currentSteps = stepsFromState ?: StepStoreData(context).todaySteps

            GlanceTheme {
                WidgetContent(steps = currentSteps, launchIntent = launchIntent)
            }
        }
    }

    @Composable
    private fun WidgetContent(steps: Long, launchIntent: Intent?) {
        val context = LocalContext.current

        val rootModifier = GlanceModifier
            .fillMaxSize()
            .background(ColorProvider(day = Color.White, night = Color(0xFF1E1B2E)))
            .cornerRadius(20.dp)
            .padding(14.dp)
            .let { if (launchIntent != null) it.clickable(actionStartActivity(launchIntent)) else it }

        Box(
            modifier = rootModifier,
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = GlanceModifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left avatar / icon (placeholder drawable: replace with your own asset)
                Image(
                    provider = ImageProvider(android.R.drawable.ic_menu_compass),
                    contentDescription = null,
                    modifier = GlanceModifier.size(40.dp)
                )

                Spacer(modifier = GlanceModifier.width(10.dp))

                // Step count text column
                Column(
                    modifier = GlanceModifier.defaultWeight()
                ) {
                    Text(
                        text = context.getString(R.string.step_widget_title),
                        style = TextStyle(
                            fontSize = 12.sp,
                            color = ColorProvider(day = Color(0xFF757575), night = Color(0xFFA0A0B0))
                        )
                    )
                    Spacer(modifier = GlanceModifier.height(2.dp))
                    Text(
                        text = Formatters.steps(steps),
                        style = TextStyle(
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = ColorProvider(day = Color(0xFF111111), night = Color.White)
                        )
                    )
                }

                // Right App branding
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Image(
                        provider = ImageProvider(android.R.drawable.ic_dialog_info),
                        contentDescription = null,
                        modifier = GlanceModifier.size(24.dp)
                    )
                    Spacer(modifier = GlanceModifier.height(2.dp))
                    Text(
                        text = context.getString(R.string.step_widget_brand),
                        style = TextStyle(
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = ColorProvider(day = Color(0xFF2196F3), night = Color(0xFF64B5F6))
                        )
                    )
                }
            }
        }
    }

    companion object {
        val STEPS_KEY = longPreferencesKey("today_steps")
        val DAY_KEY = longPreferencesKey("today_steps_day")

        suspend fun updateWidget(context: Context, steps: Long) {
            val appContext = context.applicationContext
            StepStoreData(appContext).todaySteps = steps

            val today = TimeUtils.startOfDayMillis()
            val widget = StepTrackerWidget()
            val glanceIds = GlanceAppWidgetManager(appContext).getGlanceIds(StepTrackerWidget::class.java)
            for (glanceId in glanceIds) {
                updateAppWidgetState(appContext, glanceId) { prefs ->
                    prefs[STEPS_KEY] = steps
                    prefs[DAY_KEY] = today
                }
                widget.update(appContext, glanceId)
            }
        }
    }
}

class StepTrackerWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = StepTrackerWidget()
}
