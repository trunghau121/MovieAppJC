package com.movieappjc.steptracker.util

import android.app.AlertDialog
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.widget.CheckBox
import androidx.core.net.toUri
import com.movieappjc.BuildConfig
import com.movieappjc.R
import com.movieappjc.steptracker.data.local.StepStoreData

object AutoStartHelper {

    private fun getPowerManagerIntents(context: Context): List<Intent> {
        val intents = mutableListOf(
            Intent().setComponent(ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity")),
            Intent().setComponent(ComponentName("com.letv.android.letvsafe", "com.letv.android.letvsafe.AutobootManageActivity")),
            Intent().setComponent(ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity")),
            Intent().setComponent(ComponentName("com.coloros.oppoguardelf", "com.coloros.powermanager.fuelgaue.PowerUsageModelActivity")),
            Intent().setComponent(ComponentName("com.coloros.oppoguardelf", "com.coloros.powermanager.fuelgaue.PowerSaverModeActivity")),
            Intent().setComponent(ComponentName("com.coloros.oppoguardelf", "com.coloros.powermanager.fuelgaue.PowerConsumptionActivity")),
            Intent().setComponent(ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startup.StartupAppListActivity")),
            Intent().setComponent(ComponentName("com.oppo.safe", "com.oppo.safe.permission.startup.StartupAppListActivity")),
            Intent().setComponent(ComponentName("com.iqoo.secure", "com.iqoo.secure.ui.phoneoptimize.AddWhiteListActivity")),
            Intent().setComponent(ComponentName("com.iqoo.secure", "com.iqoo.secure.ui.phoneoptimize.BgStartUpManager")),
            Intent().setComponent(ComponentName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.BgStartUpManagerActivity")),
            Intent().setComponent(ComponentName("com.asus.mobilemanager", "com.asus.mobilemanager.entry.FunctionActivity")),
            Intent().setComponent(ComponentName("com.asus.mobilemanager", "com.asus.mobilemanager.autostart.AutoStartActivity")),
            Intent().setComponent(ComponentName("com.letv.android.letvsafe", "com.letv.android.letvsafe.AutobootManageActivity"))
                .setData("mobilemanager://function/entry/AutoStart".toUri()),
            Intent().setComponent(ComponentName("com.meizu.safe", "com.meizu.safe.security.SHOW_APPSEC"))
                .addCategory(Intent.CATEGORY_DEFAULT)
                .putExtra("packageName", BuildConfig.APPLICATION_ID)
        )

        // Phân nhánh theo phiên bản Android cho Huawei
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            intents.add(Intent().setComponent(ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.startupmgr.ui.StartupNormalAppListActivity")))
        } else {
            intents.add(Intent().setComponent(ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.appcontrol.activity.StartupAppControlActivity")))
        }

        return intents
    }

    fun startPowerSaverIntent(context: Context) {
        val storeData = StepStoreData(context)

        if (!storeData.skipProtectedAppCheck) {
            var foundCorrectIntent = false

            for (intent in getPowerManagerIntents(context)) {
                if (isCallable(context, intent)) {
                    foundCorrectIntent = true

                    val dontShowAgain = CheckBox(context).apply {
                        text = "Do not show again"
                        setOnCheckedChangeListener { _, isChecked ->
                            storeData.skipProtectedAppCheck = isChecked
                        }
                    }

                    AlertDialog.Builder(context)
                        .setTitle("${Build.MANUFACTURER} Protected Apps")
                        .setMessage(String.format("%s requires to be enabled in 'Protected Apps' to function properly.\n", context.getString(R.string.app_name)))
                        .setView(dontShowAgain)
                        .setPositiveButton("Go to settings") { _, _ ->
                            try {
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                        .setNegativeButton(android.R.string.cancel, null)
                        .show()

                    break
                }
            }

            if (!foundCorrectIntent) {
                storeData.skipProtectedAppCheck = true
            }
        }
    }

    private fun isCallable(context: Context, intent: Intent?): Boolean {
        if (intent == null) return false
        return try {
            val list = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                // Tương thích API 33+ (Android 13)
                context.packageManager.queryIntentActivities(
                    intent, PackageManager.ResolveInfoFlags.of(PackageManager.MATCH_DEFAULT_ONLY.toLong())
                )
            } else {
                @Suppress("DEPRECATION")
                context.packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
            }
            list.isNotEmpty()
        } catch (_: Exception) {
            false
        }
    }
}
