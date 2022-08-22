package com.example.translatorapp.util

import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog

class Dialog(
    private val builder: AlertDialog.Builder
) {

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

    fun show() = builder.create().show()
}
