package com.example.translatorapp.data.repository

interface OnResultListener<T> {
    fun onSuccess(data: T)
}
