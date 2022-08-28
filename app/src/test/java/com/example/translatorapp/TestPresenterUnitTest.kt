package com.example.translatorapp

import android.content.Context
import com.example.translatorapp.data.model.Answer
import com.example.translatorapp.data.model.Exam
import com.example.translatorapp.data.model.Question
import com.example.translatorapp.data.repository.OnResultListener
import com.example.translatorapp.data.repository.Repository
import com.example.translatorapp.screen.test.TestContract
import com.example.translatorapp.screen.test.TestPresenter
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.Assert
import org.junit.Test

class TestPresenterUnitTest {
    private val view = mockk<TestContract.View>(relaxed = true)
    private val examRepository = mockk<Repository.ExamRepository>()
    private val testPresenter = TestPresenter(examRepository).apply { setView(view) }

    @Test
    fun `getExam success`() {
        val context = mockk<Context>()
        val listAnswer =
            mutableListOf(
                Answer("a", false),
                Answer("b", false),
                Answer("c", false),
                Answer("d", true)
            )
        val listQuestion = mutableListOf(Question("myQuestion", listAnswer))
        val listExam = mutableListOf(Exam("Exam 1", listQuestion))
        val examListener = slot<OnResultListener<List<Exam>>>()
        every {
            println("a $examRepository $context")
            examRepository.getExam(context, capture(examListener))
        } answers {
            examListener.captured.onSuccess(listExam)
        }
        testPresenter.getExam(context)
        verify(exactly = 1) {
            view.onGetQuestionComplete(listExam)
        }
    }

    @Test
    fun `getExam error`() {
        val context = mockk<Context>()
        val listener = slot<OnResultListener<List<Exam>>>()
        every {
            examRepository.getExam(context, capture(listener))
        } answers {
            listener.captured.onError(R.string.error_get_exam)
        }
        testPresenter.getExam(context)
        verify(exactly = 1) {
            view.onError(R.string.error_get_exam)
        }
    }

    @Test
    fun testGetInstance() {
        val presenter = TestPresenter.getInstance(examRepository)
        val presenter1 = TestPresenter.getInstance(examRepository)
        Assert.assertEquals(presenter, presenter1)
    }
}
