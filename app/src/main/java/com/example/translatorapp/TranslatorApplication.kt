package com.example.translatorapp

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import com.example.translatorapp.constant.Constant

class TranslatorApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        val sharedPreferences = getSharedPreferences(Constant.KEY_SETTING, Context.MODE_PRIVATE)
        sharedPreferences?.getBoolean(Constant.KEY_DARK_MODE, false)?.let {
            if (it) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }
    }
}
