package com.example.translatorapp.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class History(
    val sourceCode: String,
    val targetCode: String,
    val sourceWord: String,
    val meanWord: String
) : Parcelable
