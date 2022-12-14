package com.example.translatorapp.base

interface BasePresenter<T> {
    fun onStart()
    fun onStop()
    fun setView(view: T)
}
