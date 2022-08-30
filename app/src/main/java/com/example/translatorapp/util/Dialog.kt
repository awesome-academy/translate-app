package com.example.translatorapp.util

import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import com.example.translatorapp.R

class Dialog(
    private val builder: AlertDialog.Builder
) {

    private var myDialog: AlertDialog? = null

    fun buildDialog(
        title: String? = null,
        icon: Int? = null,
        msg: String? = null,
        cancellable: Boolean = true
    ) {
        builder.apply {
            setTitle(title)
            icon?.let { setIcon(it) }
            setMessage(msg)
            setCancelable(cancellable)
        }
    }

    fun buildDialog(
        @StringRes title: Int,
        icon: Int? = null,
        @StringRes msg: Int,
        cancellable: Boolean = true
    ) {
        builder.apply {
            setTitle(title)
            icon?.let { setIcon(it) }
            setMessage(msg)
            setCancelable(cancellable)
        }
    }

    fun setCancelButton(text: String) {
        builder.setNegativeButton(text) { dialog, _ ->
            dialog.cancel()
        }
    }

    fun setPositiveButton(text: String, callBack: (DialogInterface, Int) -> Unit) {
        builder.setPositiveButton(text) { dialog, which ->
            callBack(dialog, which)
        }
    }

    fun setPositiveButton(text: Int, callBack: (DialogInterface, Int) -> Unit) {
        builder.setPositiveButton(text) { dialog, which ->
            callBack(dialog, which)
        }
    }

    fun showLoadingDialog() {
        builder.setView(R.layout.progress)
            .setCancelable(false)
        myDialog = builder.create()
        myDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        myDialog?.show()
    }

    fun show() = builder.create().show()

    fun isShowing() = myDialog?.isShowing ?: false

    fun dismiss() = myDialog?.cancel()
}
