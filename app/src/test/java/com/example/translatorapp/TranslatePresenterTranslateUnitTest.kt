package com.example.translatorapp

import com.example.translatorapp.data.model.DictionaryLookup
import com.example.translatorapp.data.model.Language
import com.example.translatorapp.data.model.Word
import com.example.translatorapp.data.repository.OnResultListener
import com.example.translatorapp.data.repository.Repository
import com.example.translatorapp.screen.translate.TranslateContract
import com.example.translatorapp.screen.translate.TranslatePresenter
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.Before
import org.junit.Test

class TranslatePresenterTranslateUnitTest {
    private val view = mockk<TranslateContract.View>(relaxed = true)
    private val langRepository = mockk<Repository.LanguageRepository>()
    private val wordRepository = mockk<Repository.WordRepository>()
    private val historyRepository = mockk<Repository.HistoryRepository>()
    private val translatePresenter =
        TranslatePresenter(langRepository, wordRepository, historyRepository)
            .apply { setView(view) }

    @Before
    fun initialPresenter() {
        val english =
            Language(
                code = "en",
                name = "English",
                nativeName = "English",
                isTransliterate = false,
                transliterateScript = null,
                dictionaryScript = mutableListOf("ja"),
                isSupportDictionary = true
            )
        val japanese = Language(
            code = "ja",
            name = "Japanese",
            nativeName = "日本語",
            isTransliterate = true,
            transliterateScript = "Jpan" to "Latn",
            dictionaryScript = mutableListOf("en"),
            isSupportDictionary = true
        )
        val map = mutableMapOf("ja" to japanese, "en" to english)
        val list = listOf(japanese, english)
        translatePresenter.apply {
            mapLanguage.putAll(map)
            listLanguage.addAll(list)
            sourceLang = english
            targetLang = japanese
        }
    }

    @Test
    fun `translateSentence success transliterate success`() {
        val translateListener = slot<OnResultListener<String>>()
        val transliterateListener = slot<OnResultListener<String>>()
        val sourceWord = "hello"
        val targetWord = "こんにちは"
        val transliterateText = "Konichiwa"
        val japanese = Language(
            code = "ja",
            name = "Japanese",
            nativeName = "日本語",
            isTransliterate = true,
            transliterateScript = "Jpan" to "Latn",
            dictionaryScript = mutableListOf("en"),
            isSupportDictionary = true
        )
        every {
            wordRepository.translateSentence(sourceWord, "en", "ja", capture(translateListener))
        } answers {
            translateListener.captured.onSuccess(targetWord)
        }
        every {
            wordRepository.transliterate(targetWord, japanese, capture(transliterateListener))
        } answers {
            transliterateListener.captured.onSuccess(transliterateText)
        }
        translatePresenter.getTranslateSentence(sourceWord)
        verify(exactly = 1) {
            view.onTranslateSentenceComplete("$targetWord\n$transliterateText")
        }
    }

    @Test
    fun `translateSentence success transliterate error`() {
        val translateListener = slot<OnResultListener<String>>()
        val transliterateListener = slot<OnResultListener<String>>()
        val sourceWord = "hello"
        val targetWord = "こんにちは"
        val japanese = Language(
            code = "ja",
            name = "Japanese",
            nativeName = "日本語",
            isTransliterate = true,
            transliterateScript = "Jpan" to "Latn",
            dictionaryScript = mutableListOf("en"),
            isSupportDictionary = true
        )
        every {
            wordRepository.translateSentence(sourceWord, "en", "ja", capture(translateListener))
        } answers {
            translateListener.captured.onSuccess(targetWord)
        }
        every {
            wordRepository.transliterate(targetWord, japanese, capture(transliterateListener))
        } answers {
            transliterateListener.captured.onError(R.string.error_transliterate)
        }
        translatePresenter.getTranslateSentence(sourceWord)
        verify(exactly = 1) {
            view.onError(R.string.error_transliterate)
        }
    }

    @Test
    fun `translateSentence success not support transliterate`() {
        val translateListener = slot<OnResultListener<String>>()
        val sourceWord = "hello"
        val targetWord = "こんにちは"
        val japanese = Language(
            code = "ja",
            name = "Japanese",
            nativeName = "日本語",
            isTransliterate = false,
            transliterateScript = null,
            dictionaryScript = mutableListOf("en"),
            isSupportDictionary = true
        )
        translatePresenter.targetLang = japanese
        every {
            wordRepository.translateSentence(sourceWord, "en", "ja", capture(translateListener))
        } answers {
            translateListener.captured.onSuccess(targetWord)
        }
        translatePresenter.getTranslateSentence(sourceWord)
        verify {
            view.onTranslateSentenceComplete(targetWord)
        }
    }

    @Test
    fun `translateSentence error`() {
        val translateListener = slot<OnResultListener<String>>()
        val sourceWord = "hello"
        every {
            wordRepository.translateSentence(sourceWord, "en", "ja", capture(translateListener))
        } answers {
            translateListener.captured.onError(R.string.error_translate)
        }
        translatePresenter.getTranslateSentence(sourceWord)
        verify(exactly = 1) {
            view.onError(R.string.error_translate)
        }
    }

    @Test
    fun breakSentenceSuccess() {
        val listSentence = mutableListOf("How are you ?", "What did you to day ?")
        val sentence = "How are you ? What did you to day ?"
        val listener = slot<OnResultListener<List<String>>>()
        every {
            wordRepository.breakSentence(sentence, capture(listener))
        } answers {
            listener.captured.onSuccess(listSentence)
        }
        translatePresenter.breakSentence(sentence)
        verify(exactly = 1) {
            view.onBreakSentenceComplete(listSentence)
        }
    }

    @Test
    fun breakSentenceError() {
        val sentence = "How are you ? What did you to day ?"
        val listener = slot<OnResultListener<List<String>>>()
        every {
            wordRepository.breakSentence(sentence, capture(listener))
        } answers {
            listener.captured.onError(R.string.error_break_sentence)
        }
        translatePresenter.breakSentence(sentence)
        verify(exactly = 1) {
            view.onError(R.string.error_break_sentence)
        }
    }

    // test getTranslateWord function
    @Test
    fun `dictionaryLookup success transliterate success`() {
        val sourceWord = "hello"
        val targetWord = "こんにちは"
        val transliterate = "Konichiwa"
        val dictionaryLookup =
            DictionaryLookup(Word(sourceWord, sourceWord), Word(targetWord, targetWord), "Noun")
        val transliterateResponseList = mutableListOf(
            listOf(dictionaryLookup),
            listOf(),
            listOf("$targetWord\n$transliterate")
        )
        setupTestGetTranslateWord(transliterate)
        translatePresenter.getTranslateWord(sourceWord)
        verify(exactly = 1) {
            view.onDictionaryLookupComplete(transliterateResponseList)
        }
    }

    // test getTranslateWord function
    @Test
    fun `dictionaryLookup success transliterate error`() {
        val sourceWord = "hello"
        setupTestGetTranslateWord(isTransliterateError = true)
        translatePresenter.getTranslateWord(sourceWord)
        verify(exactly = 1) {
            view.onError(R.string.error_transliterate)
        }
    }

    // test getTranslateWord function
    @Test
    fun `dictionaryLookup success not support transliterate`() {
        val sourceWord = "hello"
        val targetWord = "こんにちは"
        val targetLang = Language(
            code = "ja",
            name = "Japanese",
            nativeName = "日本語",
            isTransliterate = false,
            transliterateScript = null,
            dictionaryScript = mutableListOf("en"),
            isSupportDictionary = true
        )
        translatePresenter.targetLang = targetLang
        translatePresenter.mapLanguage["ja"] = targetLang
        translatePresenter.listLanguage[1] = targetLang
        val dictionaryLookup =
            DictionaryLookup(Word(sourceWord, sourceWord), Word(targetWord, targetWord), "Noun")
        val transliterateResponseList =
            mutableListOf(listOf(dictionaryLookup), listOf(), listOf(targetWord))
        setupTestGetTranslateWord()
        translatePresenter.getTranslateWord(sourceWord)
        verify(exactly = 1) {
            view.onDictionaryLookupComplete(transliterateResponseList)
        }
    }

    // test getTranslateWord function
    @Test
    fun `dictionaryLookup error`() {
        val sourceWord = "hello"
        setupTestGetTranslateWord(sourceWord, isDictionaryLookupError = true)
        translatePresenter.getTranslateWord(sourceWord)
        verify(exactly = 1) {
            view.onError(R.string.error_translate)
        }
    }

    // test getTranslateWord function
    @Test
    fun `not support dictionaryLookup translateSentence success transliterate success`() {
        val sourceWord = "hello"
        val targetWord = "こんにちは"
        val transliterate = "konichiwa"
        val transliterateWord = "$targetWord\n$transliterate"
        val sourceLang = Language(
            code = "en",
            name = "English",
            nativeName = "English",
            isTransliterate = false,
            transliterateScript = null,
            dictionaryScript = mutableListOf(),
            isSupportDictionary = false
        )
        translatePresenter.sourceLang = sourceLang
        translatePresenter.mapLanguage["en"] = sourceLang
        translatePresenter.listLanguage[0] = sourceLang
        setupTestGetTranslateWord(transliterate)
        translatePresenter.getTranslateWord(sourceWord)
        verify(exactly = 1) {
            view.onTranslateSentenceComplete(transliterateWord)
        }
    }

    // test getTranslateWord function
    @Test
    fun `not support dictionaryLookup translateSentence success transliterate error`() {
        val sourceWord = "hello"
        val sourceLang = Language(
            code = "en",
            name = "English",
            nativeName = "English",
            isTransliterate = false,
            transliterateScript = null,
            dictionaryScript = mutableListOf(),
            isSupportDictionary = false
        )
        translatePresenter.sourceLang = sourceLang
        translatePresenter.mapLanguage["en"] = sourceLang
        translatePresenter.listLanguage[0] = sourceLang
        setupTestGetTranslateWord(isTransliterateError = true)
        translatePresenter.getTranslateWord(sourceWord)
        verify(exactly = 1) {
            view.onError(R.string.error_transliterate)
        }
    }

    // test getTranslateWord function
    @Test
    fun `not support dictionaryLookup translateSentence success not support transliterate`() {
        val sourceWord = "hello"
        val targetWord = "こんにちは"
        val sourceLang = Language(
            code = "en",
            name = "English",
            nativeName = "English",
            isTransliterate = false,
            transliterateScript = null,
            dictionaryScript = mutableListOf(),
            isSupportDictionary = false
        )
        translatePresenter.sourceLang = sourceLang
        translatePresenter.mapLanguage["en"] = sourceLang
        translatePresenter.listLanguage[0] = sourceLang
        val japanese = Language(
            code = "ja",
            name = "Japanese",
            nativeName = "日本語",
            isTransliterate = false,
            transliterateScript = null,
            dictionaryScript = mutableListOf("en"),
            isSupportDictionary = true
        )
        translatePresenter.targetLang = japanese
        setupTestGetTranslateWord()
        translatePresenter.getTranslateWord(sourceWord)
        verify(exactly = 1) {
            view.onTranslateSentenceComplete(targetWord)
        }
    }

    // test getTranslateWord function
    @Test
    fun `not support dictionaryLookup translateSentence error`() {
        val sourceWord = "hello"
        val sourceLang = Language(
            code = "en",
            name = "English",
            nativeName = "English",
            isTransliterate = false,
            transliterateScript = null,
            dictionaryScript = mutableListOf(),
            isSupportDictionary = false
        )
        translatePresenter.sourceLang = sourceLang
        translatePresenter.mapLanguage["en"] = sourceLang
        translatePresenter.listLanguage[0] = sourceLang
        setupTestGetTranslateWord(sourceWord, isTranslateSentenceError = true)
        translatePresenter.getTranslateWord(sourceWord)
        verify(exactly = 1) {
            view.onError(R.string.error_translate)
        }
    }

    // test getTranslateWord function
    @Test
    fun `detectLang success dictionaryLookup success transliterate success`() {
        val sourceWord = "hello"
        val targetWord = "こんにちは"
        val transliterateWord = "Konichiwa"
        translatePresenter.sourceLang = null
        val dictionaryLookup =
            DictionaryLookup(Word(sourceWord, sourceWord), Word(targetWord, targetWord), "Noun")
        val transliterateResponseList = mutableListOf(
            listOf(dictionaryLookup),
            listOf(),
            listOf("$targetWord\n$transliterateWord")
        )
        setupTestGetTranslateWord(transliterateWord)
        translatePresenter.getTranslateWord(sourceWord)
        verify(exactly = 1) {
            view.onDictionaryLookupComplete(transliterateResponseList)
        }
    }

    // test getTranslateWord function
    @Test
    fun `detectLang success dictionaryLookup success transliterate error`() {
        val sourceWord = "hello"
        val transliterateWord = "Konichiwa"
        translatePresenter.sourceLang = null
        setupTestGetTranslateWord(transliterateWord, isTransliterateError = true)
        translatePresenter.getTranslateWord(sourceWord)
        verify(exactly = 1) {
            view.onError(R.string.error_transliterate)
        }
    }

    // test getTranslateWord function
    @Test
    fun `detectLang success dictionaryLookup error`() {
        val sourceWord = "hello"
        translatePresenter.sourceLang = null
        setupTestGetTranslateWord(sourceWord, isDictionaryLookupError = true)
        translatePresenter.getTranslateWord(sourceWord)
        verify(exactly = 1) {
            view.onError(R.string.error_translate)
        }
    }

    // test getTranslateWord function
    @Test
    fun `detectLang success not support dictionaryLookup translateSentence success transliterate success`() {
        val sourceWord = "hello"
        val targetWord = "こんにちは"
        val transliterate = "Konichiwa"
        val english = Language(
            code = "en",
            name = "English",
            nativeName = "English",
            isTransliterate = false,
            transliterateScript = null,
            dictionaryScript = mutableListOf(),
            isSupportDictionary = false
        )
        translatePresenter.mapLanguage["en"] = english
        translatePresenter.listLanguage[0] = english
        translatePresenter.sourceLang = null
        setupTestGetTranslateWord(transliterate)
        translatePresenter.getTranslateWord(sourceWord)
        verify(exactly = 1) {
            view.onTranslateSentenceComplete("$targetWord\n$transliterate")
        }
    }

    // test getTranslateWord function
    @Test
    fun `detectLang success not support dictionaryLookup translateSentence success transliterate error`() {
        val sourceWord = "hello"
        val english = Language(
            code = "en",
            name = "English",
            nativeName = "English",
            isTransliterate = false,
            transliterateScript = null,
            dictionaryScript = mutableListOf(),
            isSupportDictionary = false
        )
        translatePresenter.mapLanguage["en"] = english
        translatePresenter.listLanguage[0] = english
        translatePresenter.sourceLang = null
        setupTestGetTranslateWord(isTransliterateError = true)
        translatePresenter.getTranslateWord(sourceWord)
        verify(exactly = 1) {
            view.onError(R.string.error_transliterate)
        }
    }

    // test getTranslateWord function
    @Test
    fun `detectLang success not support dictionaryLookup translateSentence error`() {
        val sourceWord = "hello"
        val english = Language(
            code = "en",
            name = "English",
            nativeName = "English",
            isTransliterate = false,
            transliterateScript = null,
            dictionaryScript = mutableListOf(),
            isSupportDictionary = false
        )
        translatePresenter.mapLanguage["en"] = english
        translatePresenter.listLanguage[0] = english
        translatePresenter.sourceLang = null
        setupTestGetTranslateWord(sourceWord, isTranslateSentenceError = true)
        translatePresenter.getTranslateWord(sourceWord)
        verify(exactly = 1) {
            view.onError(R.string.error_translate)
        }
    }

    // test getTranslateWord function
    @Test
    fun `detectLang error`() {
        val sourceWord = "hello"
        translatePresenter.sourceLang = null
        setupTestGetTranslateWord(sourceWord, isDetectLangError = true)
        translatePresenter.getTranslateWord(sourceWord)
        verify(exactly = 1) {
            view.onError(R.string.error_detect_language)
        }
    }

    private fun setupTestGetTranslateWord(
        transliterateWord: String = "",
        isDetectLangError: Boolean = false,
        isDictionaryLookupError: Boolean = false,
        isTranslateSentenceError: Boolean = false,
        isTransliterateError: Boolean = false
    ) {
        val sourceWord = "hello"
        val targetWord = "こんにちは"
        addAnswerTranslateWord(sourceWord, targetWord, isDetectLangError, isDictionaryLookupError)
        addAnswerTranslateSentence(
            sourceWord = sourceWord,
            targetWord = targetWord,
            transliterateWord = transliterateWord,
            isTranslateSentenceError = isTranslateSentenceError,
            isTransliterateError = isTransliterateError
        )
    }

    private fun addAnswerTranslateWord(
        sourceWord: String,
        targetWord: String,
        isDetectLangError: Boolean,
        isDictionaryLookupError: Boolean
    ) {
        val dictionaryLookup =
            DictionaryLookup(Word(sourceWord, sourceWord), Word(targetWord, targetWord), "Noun")
        val dictionaryLookupResponseList =
            mutableListOf<List<Any>>(listOf(dictionaryLookup), listOf())
        val detectLangListener = slot<OnResultListener<String>>()
        val dictionaryLookupListener = slot<OnResultListener<MutableList<List<Any>>>>()
        if (isDetectLangError) {
            every {
                wordRepository.detectLang(sourceWord, capture(detectLangListener))
            } answers {
                detectLangListener.captured.onError(R.string.error_detect_language)
            }
        } else {
            every {
                wordRepository.detectLang(sourceWord, capture(detectLangListener))
            } answers {
                detectLangListener.captured.onSuccess("en")
            }
        }

        if (isDictionaryLookupError) {
            every {
                wordRepository.dictionaryLookup(
                    sourceWord,
                    "en",
                    "ja",
                    capture(dictionaryLookupListener)
                )
            } answers {
                dictionaryLookupListener.captured.onError(R.string.error_translate)
            }
        } else {
            every {
                wordRepository.dictionaryLookup(
                    sourceWord,
                    "en",
                    "ja",
                    capture(dictionaryLookupListener)
                )
            } answers {
                dictionaryLookupListener.captured.onSuccess(dictionaryLookupResponseList)
            }
        }
    }

    private fun addAnswerTranslateSentence(
        sourceWord: String,
        targetWord: String,
        transliterateWord: String,
        isTranslateSentenceError: Boolean,
        isTransliterateError: Boolean
    ) {
        val transliterateListener = slot<OnResultListener<String>>()
        val translateSentenceListener = slot<OnResultListener<String>>()
        if (isTranslateSentenceError) {
            every {
                wordRepository.translateSentence(
                    sourceWord,
                    translatePresenter.sourceLang?.code,
                    "ja",
                    capture(translateSentenceListener)
                )
            } answers {
                translateSentenceListener.captured.onError(R.string.error_translate)
            }
        } else {
            every {
                wordRepository.translateSentence(
                    sourceWord,
                    translatePresenter.sourceLang?.code,
                    "ja",
                    capture(translateSentenceListener)
                )
            } answers {
                translateSentenceListener.captured.onSuccess(targetWord)
            }
        }

        if (isTransliterateError) {
            every {
                translatePresenter.targetLang?.let {
                    wordRepository.transliterate(targetWord, it, capture(transliterateListener))
                }
            } answers {
                transliterateListener.captured.onError(R.string.error_transliterate)
            }
        } else {
            every {
                translatePresenter.targetLang?.let {
                    wordRepository.transliterate(targetWord, it, capture(transliterateListener))
                }
            } answers {
                transliterateListener.captured.onSuccess(transliterateWord)
            }
        }
    }
}
