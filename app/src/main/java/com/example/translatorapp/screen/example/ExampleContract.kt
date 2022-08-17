package com.example.translatorapp.screen.example

import com.example.translatorapp.data.model.BackTranslation
import com.example.translatorapp.data.model.Example
import com.example.translatorapp.data.model.Language

class ExampleContract {

    interface View {
        fun onGetExampleComplete(listExample: List<Example>)
    }

    interface Presenter {
        fun getExample(backTranslation: BackTranslation, source: Language, target: Language)
    }
}
