package com.example.translatorapp.screen.example

import androidx.annotation.StringRes
import com.example.translatorapp.data.model.BackTranslation
import com.example.translatorapp.data.model.Example
import com.example.translatorapp.data.model.Language

class ExampleContract {

    interface View {
        fun onGetExampleComplete(listExample: List<Example>)
        fun onError(@StringRes message: Int)
    }

    interface Presenter {
        fun getExample(backTranslation: BackTranslation, source: Language, target: Language)
    }
}
