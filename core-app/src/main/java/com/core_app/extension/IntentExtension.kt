package com.core_app.extension

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.core.app.ShareCompat

inline fun <reified T : Any> Activity.launchActivity(
    requestCode: Int = -1,
    options: Bundle? = null,
    noinline init: Intent.() -> Unit = {}
) {
    val intent = newIntent<T>(this)
    intent.init()
    startActivityForResult(intent, requestCode, options)
}

inline fun <reified T : Any> Context.launchActivity(
    options: Bundle? = null,
    noinline init: Intent.() -> Unit = {}
) {
    val intent = newIntent<T>(this)
    intent.init()
    startActivity(intent, options)
}

inline fun <reified T : Any> newIntent(context: Context): Intent =
    Intent(context, T::class.java)

fun Activity.launchActivity(
    packageName: String,
    className: String,
    flags: Int = -1,
    bundle: Bundle? = null
) {
    val intent = Intent(Intent.ACTION_VIEW).setClassName(packageName, className)
    if (flags != -1) {
        intent.flags = flags
    }
    if (bundle != null) {
        intent.putExtras(bundle)
    }
    startActivity(intent)
}

fun Context.getActivity(): Activity? {
    if (this is ContextWrapper) {
        return this as? Activity
    }
    return null
}

@SuppressLint("QueryPermissionsNeeded")
fun Context.rateOnGooglePlay() {
    val intent = Intent(Intent.ACTION_VIEW)
    intent.data = Uri.parse("market://details?id=${packageName}")
    packageManager?.let { packageManager ->
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        }
    }
}

@SuppressLint("QueryPermissionsNeeded")
fun Context.shareApplication() {
    val appPackageName = packageName

    val shareIntent = getActivity()?.let {
        ShareCompat.IntentBuilder(it)
            .setType("text/plain")
            .setText(
                "https://play.google.com/store/apps/details?id=$appPackageName"
            )
            .intent
    }
    if (shareIntent?.resolveActivity(packageManager) != null) {
        startActivity(shareIntent)
    }
}

fun Context.openAppOnPlayStore() = try {
    startActivity(
        Intent(
            Intent.ACTION_VIEW,
            Uri.parse("market://details?id=$packageName")
        )
    )
} catch (ex: ActivityNotFoundException) {
    startActivity(
        Intent(
            Intent.ACTION_VIEW,
            Uri.parse(
                ("https://play.google.com/store/apps/details?id=$packageName")
            )
        )
    )
}
