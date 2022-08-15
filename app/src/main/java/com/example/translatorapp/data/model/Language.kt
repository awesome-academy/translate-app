package com.example.translatorapp.data.model

data class Language(
    val code: String,
    val name: String,
    val nativeName: String,
    var isTransliterate: Boolean,
    var transliterateScript: Pair<String, String>?,
    val dictionaryScript: MutableList<String>,
    var isSupportDictionary: Boolean
) {
    fun addTransliterate(fromScript: String, toScript: String) {
        transliterateScript = fromScript to toScript
    }

    fun supportDictionary(isSupportDictionary: Boolean) {
        this.isSupportDictionary = isSupportDictionary
    }

    fun addDictionary(toCode: String) {
        dictionaryScript.add(toCode)
    }

    fun getFullName() = "$name ($nativeName)"
}
