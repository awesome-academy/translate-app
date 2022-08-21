package com.example.translatorapp.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.appcompat.app.AlertDialog
import com.example.translatorapp.R

object NetworkUtils {
    fun isNetworkAvailable(context: Context): Boolean {
        val connectManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val networkCapabilities =
                connectManager.getNetworkCapabilities(connectManager.activeNetwork)
            if (networkCapabilities != null) {
                if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    true
                } else networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
            } else {
                false
            }
        } else {
            val networkInfo = connectManager.activeNetworkInfo
            networkInfo != null && networkInfo.isConnected
        }
    }

    fun setDialogAction(context: Context, change: () -> Unit) {
        val dialog = Dialog(AlertDialog.Builder(context, R.style.AlertDialogTheme))
        dialog.apply {
            buildDialog(
                title = context.getString(R.string.title_no_internet),
                icon = R.drawable.ic_no_internet,
                msg = context.getString(R.string.msg_no_internet),
                cancellable = false
            )
            setPositiveButton(context.getString(R.string.retry)) {
                    _, _ ->
                change()
            }
        }
        dialog.show()
    }
}
