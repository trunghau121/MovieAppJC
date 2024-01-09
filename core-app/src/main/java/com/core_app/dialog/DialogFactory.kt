package com.core_app.dialog

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface

object DialogFactory {

    fun simpleLoadingDialog(context: Context, cancelableTouch: Boolean = true): Dialog {
        return BaseDialogFactory.simpleLoadingDialog(context, cancelableTouch)
    }

    fun showSimpleDialog(context: Context, message: String) {
        if (message.isEmpty()) return
        BaseDialogFactory.showSimpleDialog(context, message)
    }

    fun showSimpleDialog(context: Context, message: String, title: String) {
        if (message.isEmpty()) return
        BaseDialogFactory.showSimpleDialog(context, message, title)
    }

    fun showSimpleDialog(context: Context, message: String, listener: DialogInterface.OnClickListener) {
        BaseDialogFactory.showSimpleDialog(context, message, listener)
    }
}