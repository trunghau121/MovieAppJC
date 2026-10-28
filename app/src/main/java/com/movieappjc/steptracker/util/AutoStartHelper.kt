package com.movieappjc.steptracker.util

import android.app.Activity
import android.app.AlertDialog
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.net.toUri
import com.movieappjc.BuildConfig
import com.movieappjc.R
import com.movieappjc.steptracker.data.local.StepStoreData

object AutoStartHelper {

    private const val TAG = "AutoStartHelper"

    // NOTE: since Android 11 every package below must be listed in <queries> in AndroidManifest.xml,
    // otherwise queryIntentActivities() always returns an empty list.
    private fun getPowerManagerIntents(context: Context): List<Intent> {
        val intents = mutableListOf(
            // Xiaomi / Redmi / POCO (MIUI & HyperOS)
            Intent().setComponent(ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity")),
            Intent().setComponent(ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartDetailActivity")).putExtra("extra_pkgname", context.packageName),
            Intent("miui.intent.action.OP_AUTO_START").addCategory(Intent.CATEGORY_DEFAULT),
            // Letv
            Intent().setComponent(ComponentName("com.letv.android.letvsafe", "com.letv.android.letvsafe.AutobootManageActivity")),
            // Huawei
            Intent().setComponent(ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity")),
            // Oppo / Realme
            Intent().setComponent(ComponentName("com.coloros.oppoguardelf", "com.coloros.powermanager.fuelgaue.PowerUsageModelActivity")),
            Intent().setComponent(ComponentName("com.coloros.oppoguardelf", "com.coloros.powermanager.fuelgaue.PowerSaverModeActivity")),
            Intent().setComponent(ComponentName("com.coloros.oppoguardelf", "com.coloros.powermanager.fuelgaue.PowerConsumptionActivity")),
            Intent().setComponent(ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startup.StartupAppListActivity")),
            Intent().setComponent(ComponentName("com.oppo.safe", "com.oppo.safe.permission.startup.StartupAppListActivity")),
            // Vivo / iQOO
            Intent().setComponent(ComponentName("com.iqoo.secure", "com.iqoo.secure.ui.phoneoptimize.AddWhiteListActivity")),
            Intent().setComponent(ComponentName("com.iqoo.secure", "com.iqoo.secure.ui.phoneoptimize.BgStartUpManager")),
            Intent().setComponent(ComponentName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.BgStartUpManagerActivity")),
            // Asus
            Intent().setComponent(ComponentName("com.asus.mobilemanager", "com.asus.mobilemanager.entry.FunctionActivity"))
                .setData("mobilemanager://function/entry/AutoStart".toUri()),
            Intent().setComponent(ComponentName("com.asus.mobilemanager", "com.asus.mobilemanager.autostart.AutoStartActivity")),
            // Meizu
            Intent().setComponent(ComponentName("com.meizu.safe", "com.meizu.safe.security.SHOW_APPSEC"))
                .addCategory(Intent.CATEGORY_DEFAULT)
                .putExtra("packageName", BuildConfig.APPLICATION_ID)
        )

        // Huawei screen differs by Android version
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            intents.add(Intent().setComponent(ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.startupmgr.ui.StartupNormalAppListActivity")))
        } else {
            intents.add(Intent().setComponent(ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.appcontrol.activity.StartupAppControlActivity")))
        }

        return intents
    }

    /**
     * Shows the "Protected Apps" prompt at most ONCE (only when this device has a known OEM screen).
     * Call it from the "start tracking" flow only - never from onStart/resume.
     */
    fun startPowerSaverIntent(activity: Activity) {
        val storeData = StepStoreData(activity)
        if (storeData.skipProtectedAppCheck) return
        if (activity.isFinishing || activity.isDestroyed) return

        // If nothing is callable we do NOT persist "skip": the check may work after <queries> is fixed
        val intent = getPowerManagerIntents(activity).firstOrNull { isCallable(activity, it) } ?: return

        storeData.skipProtectedAppCheck = true // shown once only

        AlertDialog.Builder(activity)
            .setTitle(activity.getString(R.string.step_protected_title, Build.MANUFACTURER))
            .setMessage(activity.getString(R.string.step_protected_message, activity.getString(R.string.app_name)))
            .setPositiveButton(R.string.step_protected_go_settings) { _, _ ->
                try {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    activity.startActivity(intent)
                } catch (e: Exception) {
                    Log.w(TAG, "Unable to open OEM settings screen", e)
                }
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }

    private fun isCallable(context: Context, intent: Intent?): Boolean {
        if (intent == null) return false
        return try {
            val list = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
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
