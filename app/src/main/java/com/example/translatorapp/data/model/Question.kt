package com.example.translatorapp.data.model

data class Question(
    val text: String,
    val listAnswer: MutableList<Answer>
)
