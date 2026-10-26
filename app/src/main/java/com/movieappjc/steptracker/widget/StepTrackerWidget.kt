package com.movieappjc.steptracker.widget

import android.content.Context
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
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.appwidget.updateAll
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
import com.movieappjc.steptracker.data.local.StepStoreData
import com.movieappjc.steptracker.util.Formatters

class StepTrackerWidget : GlanceAppWidget() {

    override val stateDefinition: GlanceStateDefinition<Preferences> = PreferencesGlanceStateDefinition

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            val prefs = currentState<Preferences>()
            val currentSteps = prefs[STEPS_KEY] ?: StepStoreData(context).todaySteps

            GlanceTheme {
                WidgetContent(steps = currentSteps)
            }
        }
    }

    @Composable
    private fun WidgetContent(steps: Long) {
        Box(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(ColorProvider(day = Color.White, night = Color(0xFF1E1B2E)))
                .cornerRadius(20.dp)
                .padding(14.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = GlanceModifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left avatar / icon
                Image(
                    provider = ImageProvider(android.R.drawable.ic_menu_compass),
                    contentDescription = "Avatar",
                    modifier = GlanceModifier.size(40.dp)
                )

                Spacer(modifier = GlanceModifier.width(10.dp))

                // Step count text column
                Column(
                    modifier = GlanceModifier.defaultWeight()
                ) {
                    Text(
                        text = "Daily Step",
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
                        contentDescription = "Logo",
                        modifier = GlanceModifier.size(24.dp)
                    )
                    Spacer(modifier = GlanceModifier.height(2.dp))
                    Text(
                        text = "STEP MOVE",
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

        suspend fun updateWidget(context: Context, steps: Long) {
            StepStoreData(context).todaySteps = steps
            val glanceManager = GlanceAppWidgetManager(context)
            val glanceIds = glanceManager.getGlanceIds(StepTrackerWidget::class.java)
            for (glanceId in glanceIds) {
                updateAppWidgetState(context, glanceId) { prefs ->
                    prefs[STEPS_KEY] = steps
                }
                StepTrackerWidget().update(context, glanceId)
            }
            if (glanceIds.isEmpty()) {
                StepTrackerWidget().updateAll(context)
            }
        }
    }
}

class StepTrackerWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = StepTrackerWidget()
}
