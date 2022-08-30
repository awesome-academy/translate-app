package com.example.translatorapp.data.repository

import androidx.annotation.StringRes

interface OnResultListener<T> {
    fun onSuccess(data: T)
    fun onError(@StringRes message: Int)
}
