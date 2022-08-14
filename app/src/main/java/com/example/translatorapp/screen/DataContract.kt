package com.example.translatorapp.screen

import com.example.translatorapp.data.model.Language

class DataContract {

    interface View {
        fun onGetLanguageSuccess(data: List<Language>)
    }

    interface Presenter {
        fun getLanguage()
    }
}
