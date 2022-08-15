package com.example.translatorapp.data.model

data class Language(
    val code: String,
    val name: String,
    val nativeName: String,
    var isTransliterate: Boolean,
    val transliterateScript: MutableMap<String, String>,
    val dictionaryScript: MutableMap<String, String>,
    var isSupportDictionary: Boolean
) {
    fun addTransliterate(fromScript: String, toScript: String) {
        transliterateScript[fromScript] = toScript
    }

    fun supportDictionary(isSupportDictionary: Boolean) {
        this.isSupportDictionary = isSupportDictionary
    }

    fun addDictionary(fromScript: String, toScript: String) {
        dictionaryScript[fromScript] = toScript
    }

    fun getFullName() = "$name ($nativeName)"
}
