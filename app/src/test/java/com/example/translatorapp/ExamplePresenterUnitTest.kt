package com.example.translatorapp

import com.example.translatorapp.data.model.BackTranslation
import com.example.translatorapp.data.model.Example
import com.example.translatorapp.data.model.Language
import com.example.translatorapp.data.model.Word
import com.example.translatorapp.data.repository.OnResultListener
import com.example.translatorapp.data.repository.Repository
import com.example.translatorapp.screen.example.ExampleContract
import com.example.translatorapp.screen.example.ExamplePresenter
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.Assert
import org.junit.Test

class ExamplePresenterUnitTest {
    private val view = mockk<ExampleContract.View>(relaxed = true)
    private val exampleRepository = mockk<Repository.ExampleRepository>()
    private val examplePresenter =
        ExamplePresenter(exampleRepository).apply { this.setView(view) }

    @Test
    fun `getExample success`() {
        val sourceWord = Word("hello", "hello")
        val targetWord = Word("xin chào", "Xin chào")
        val backTranslation = BackTranslation(sourceWord, targetWord, true)
        val source =
            Language(
                code = "en",
                name = "English",
                nativeName = "English",
                isTransliterate = false,
                transliterateScript = null,
                dictionaryScript = mutableListOf("vi"),
                isSupportDictionary = true
            )
        val target =
            Language(
                code = "vi",
                name = "Vietnamese",
                nativeName = "Tiếng Việt",
                isTransliterate = false,
                transliterateScript = null,
                dictionaryScript = mutableListOf("en"),
                isSupportDictionary = true
            )
        val listExample = mutableListOf(
            Example("a", "b", "c", "a1", "b1", "c1")
        )
        val exampleListener = slot<OnResultListener<List<Example>>>()
        every {
            exampleRepository.getExample(
                backTranslation,
                source.code,
                target.code,
                capture(exampleListener)
            )
        } answers {
            exampleListener.captured.onSuccess(listExample)
        }
        examplePresenter.getExample(backTranslation, source, target)
        verify(exactly = 1) {
            view.onGetExampleComplete(listExample)
        }
    }

    @Test
    fun `getExample error`() {
        val sourceWord = Word("hello", "hello")
        val targetWord = Word("xin chào", "Xin chào")
        val backTranslation = BackTranslation(sourceWord, targetWord, true)
        val source =
            Language(
                code = "en",
                name = "English",
                nativeName = "English",
                isTransliterate = false,
                transliterateScript = null,
                dictionaryScript = mutableListOf("vi"),
                isSupportDictionary = true
            )
        val target =
            Language(
                code = "vi",
                name = "Vietnamese",
                nativeName = "Tiếng Việt",
                isTransliterate = false,
                transliterateScript = null,
                dictionaryScript = mutableListOf("en"),
                isSupportDictionary = true
            )
        val exampleListener = slot<OnResultListener<List<Example>>>()
        every {
            exampleRepository.getExample(
                backTranslation,
                source.code,
                target.code,
                capture(exampleListener)
            )
        } answers {
            exampleListener.captured.onError(R.string.error_get_example)
        }
        examplePresenter.getExample(backTranslation, source, target)
        verify(exactly = 1) {
            view.onError(R.string.error_get_example)
        }
    }

    @Test
    fun testGetInstance() {
        val presenter = ExamplePresenter.getInstance(exampleRepository)
        val presenter1 = ExamplePresenter.getInstance(exampleRepository)
        Assert.assertEquals(presenter, presenter1)
    }
}
