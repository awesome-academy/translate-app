package com.example.translatorapp

import android.content.Context
import com.example.translatorapp.data.model.Language
import com.example.translatorapp.data.repository.OnResultListener
import com.example.translatorapp.data.repository.Repository
import com.example.translatorapp.screen.translate.TranslateContract
import com.example.translatorapp.screen.translate.TranslatePresenter
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.spyk
import io.mockk.verify
import org.junit.Assert
import org.junit.Test

class TranslatePresenterUnitTest {
    private val view = mockk<TranslateContract.View>(relaxed = true)
    private val langRepository = mockk<Repository.LanguageRepository>()
    private val wordRepository = mockk<Repository.WordRepository>()
    private val historyRepository = mockk<Repository.HistoryRepository>(relaxed = true)
    private val translatePresenter =
        TranslatePresenter(langRepository, wordRepository, historyRepository)
            .apply { setView(view) }

    @Test
    fun getLanguageOnSuccess() {
        val language1 =
            Language(
                code = "ja",
                name = "Japanese",
                nativeName = "日本語",
                isTransliterate = true,
                transliterateScript = "Jpan" to "Latn",
                dictionaryScript = mutableListOf("en"),
                isSupportDictionary = true
            )
        val language2 = Language(
            code = "ar",
            name = "Arabic",
            nativeName = "Arabic",
            isTransliterate = false,
            transliterateScript = null,
            dictionaryScript = mutableListOf(),
            isSupportDictionary = false
        )
        val map = mapOf("ja" to language1, "ar" to language2)
        val list = listOf(language2, language1)
        val listener = slot<OnResultListener<Map<String, Language>>>()
        every {
            langRepository.getLanguage(capture(listener))
        } answers {
            listener.captured.onSuccess(map)
        }
        translatePresenter.getLanguage()
        verify(exactly = 1) {
            view.onGetLanguageSuccess(list)
        }
        Assert.assertTrue(translatePresenter.mapLanguage.isNotEmpty())
        Assert.assertTrue(translatePresenter.listLanguage.isNotEmpty())
    }

    @Test
    fun getLanguageOnError() {
        val listener = slot<OnResultListener<Map<String, Language>>>()
        every {
            langRepository.getLanguage(capture(listener))
        } answers {
            listener.captured.onError(R.string.error_language)
        }
        translatePresenter.getLanguage()
        verify(exactly = 1) {
            view.onError(R.string.error_language)
        }
    }

    @Test
    fun `onStart emptyList getLang success`() {
        val language1 =
            Language(
                code = "ja",
                name = "Japanese",
                nativeName = "日本語",
                isTransliterate = true,
                transliterateScript = "Jpan" to "Latn",
                dictionaryScript = mutableListOf("en"),
                isSupportDictionary = true
            )
        val language2 = Language(
            code = "ar",
            name = "Arabic",
            nativeName = "Arabic",
            isTransliterate = false,
            transliterateScript = null,
            dictionaryScript = mutableListOf(),
            isSupportDictionary = false
        )
        val map = mapOf("ja" to language1, "ar" to language2)
        val list = listOf(language2, language1)
        translatePresenter.mapLanguage.clear()
        translatePresenter.listLanguage.clear()
        val languageListener = slot<OnResultListener<Map<String, Language>>>()
        every {
            langRepository.getLanguage(capture(languageListener))
        } answers {
            languageListener.captured.onSuccess(map)
        }
        translatePresenter.onStart()
        verify {
            view.onGetLanguageSuccess(list)
        }
    }

    @Test
    fun `onStart emptyList getLang error`() {
        translatePresenter.mapLanguage.clear()
        translatePresenter.listLanguage.clear()
        val languageListener = slot<OnResultListener<Map<String, Language>>>()
        every {
            langRepository.getLanguage(capture(languageListener))
        } answers {
            languageListener.captured.onError(R.string.error_language)
        }
        translatePresenter.onStart()
        verify {
            view.onError(R.string.error_language)
        }
    }

    @Test
    fun `onStart notEmptyList`() {
        val language1 =
            Language(
                code = "ja",
                name = "Japanese",
                nativeName = "日本語",
                isTransliterate = true,
                transliterateScript = "Jpan" to "Latn",
                dictionaryScript = mutableListOf("en"),
                isSupportDictionary = true
            )
        val language2 = Language(
            code = "ar",
            name = "Arabic",
            nativeName = "Arabic",
            isTransliterate = false,
            transliterateScript = null,
            dictionaryScript = mutableListOf(),
            isSupportDictionary = false
        )
        translatePresenter.mapLanguage.putAll(mapOf("ja" to language1, "ar" to language2))
        val listLanguage = listOf(language2, language1)
        translatePresenter.listLanguage.addAll(listLanguage)
        translatePresenter.onStart()
        verify {
            view.onGetLanguageSuccess(listLanguage)
        }
    }

    @Test
    fun testWriteHistory() {
        val context = mockk<Context>()
        translatePresenter.writeHistory(context, "test", true)
        verify(exactly = 1) {
            historyRepository.writeHistory(context, "test", true)
        }
    }

    @Test
    fun `getTranslate word`() {
        val sourceWord = "hello"
        val fakePresenter = spyk(translatePresenter)
        every { fakePresenter.getTranslateWord(sourceWord) } returns Unit
        fakePresenter.getTranslate(sourceWord)
        verify(exactly = 1) {
            fakePresenter.getTranslateWord(sourceWord)
        }
    }

    @Test
    fun `getTranslate sentence`() {
        val sentence = "hello cuong"
        val fakePresenter = spyk(translatePresenter)
        every { fakePresenter.getTranslateSentence(sentence) } returns Unit
        every { fakePresenter.breakSentence(sentence) } returns Unit
        fakePresenter.getTranslate(sentence)
        verify(exactly = 1) {
            fakePresenter.getTranslateSentence(sentence)
            fakePresenter.breakSentence(sentence)
        }
    }

    @Test
    fun testGetInstance() {
        val presenter = TranslatePresenter.getInstance(langRepository, wordRepository, historyRepository)
        val presenter1 = TranslatePresenter.getInstance(langRepository, wordRepository, historyRepository)
        Assert.assertEquals(presenter1, presenter)
    }
}
