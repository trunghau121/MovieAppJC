package com.core_app.dialog

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Window
import androidx.appcompat.app.AlertDialog
import com.core_app.R
import com.core_app.extension.getString

object BaseDialogFactory {
    fun simpleLoadingDialog(context: Context, cancelableTouch: Boolean = true): Dialog {
        val mProgressDialog = Dialog(context)
        mProgressDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        mProgressDialog.window?.requestFeature(Window.FEATURE_NO_TITLE);
        mProgressDialog.setContentView(R.layout.loading_layout)
        mProgressDialog.setCancelable(true)
        mProgressDialog.setCanceledOnTouchOutside(cancelableTouch)
        return mProgressDialog
    }

    fun showSimpleDialog(context: Context, message: String) {
        if (message.isEmpty()) return
        val dialog = AlertDialog.Builder(context)
            .setTitle("Thông báo")
            .setMessage(message)
            .setPositiveButton(context.getString(R.string.dialog_ok_button_title), null)
            .setCancelable(true)
            .create()
        dialog.show()
    }

    fun showSimpleDialog(context: Context, message: String, title: String) {
        if (message.isEmpty()) return
        val dialog = AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(context.getString(R.string.dialog_ok_button_title), null)
            .setCancelable(true)
            .create()
        dialog.show()
    }

    fun showSimpleDialog(context: Context, message: String, listener: DialogInterface.OnClickListener) {
        val dialog = AlertDialog.Builder(context)
            .setTitle("Lỗi")
            .setMessage(message)
            .setPositiveButton(R.string.dialog_ok_button_title, listener)
            .setCancelable(false)
            .create()
        try {
            dialog.show()
        }catch (e: Exception){

        }
    }

    fun showConfirmDialogCancelCallback(context: Context, message: String, onConfirm: () -> Unit, onCancel: () -> Unit){
        AlertDialog.Builder(context)
            .setTitle(getString(R.string.app_name))
            .setMessage(message)
            .setPositiveButton("Ok") { _, _ ->
                onConfirm()
            }
            .setNegativeButton(getString(R.string.dialog_cancel_button_title)) { _, _ ->
                onCancel()
            }
            .setOnCancelListener {
                onCancel()
            }
            .show()
    }

    fun showConfirmDialog(context: Context, message: String, onConfirm: () -> Unit, onCancel: (() -> Unit)? = null) {
        AlertDialog.Builder(context)
            .setTitle(getString(R.string.app_name))
            .setMessage(message)
            .setPositiveButton(getString(R.string.yup)) { _, _ ->
                onConfirm()
            }
            .setNegativeButton(getString(R.string.no)) { _, _ ->
                onCancel?.invoke()
            }
            .show()
    }
}