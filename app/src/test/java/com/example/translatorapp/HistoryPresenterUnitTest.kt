package com.example.translatorapp

import android.content.Context
import com.example.translatorapp.data.model.History
import com.example.translatorapp.data.repository.OnResultListener
import com.example.translatorapp.data.repository.Repository
import com.example.translatorapp.screen.history.HistoryContract
import com.example.translatorapp.screen.history.HistoryPresenter
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.Assert
import org.junit.Test

class HistoryPresenterUnitTest {
    private val view = mockk<HistoryContract.View>(relaxed = true)
    private val historyRepository = mockk<Repository.HistoryRepository>(relaxed = true)
    private val historyPresenter =
        HistoryPresenter(historyRepository).apply { setView(view) }

    @Test
    fun `getHistory success`() {
        val context = mockk<Context>()
        val listHistory = mutableListOf(
            History("en", "vi", "hello", "Xin chào")
        )
        val historyListener = slot<OnResultListener<List<History>>>()
        every {
            historyRepository.getHistory(context, capture(historyListener))
        } answers {
            historyListener.captured.onSuccess(listHistory)
        }
        historyPresenter.getHistory(context)
        verify(exactly = 1) {
            view.onGetHistoryComplete(listHistory)
        }
    }

    @Test
    fun `getHistory error`() {
        val context = mockk<Context>()
        val historyListener = slot<OnResultListener<List<History>>>()
        every {
            historyRepository.getHistory(context, capture(historyListener))
        } answers {
            historyListener.captured.onError(R.string.error_get_history)
        }
        historyPresenter.getHistory(context)
        verify(exactly = 1) {
            view.onError(R.string.error_get_history)
        }
    }

    @Test
    fun testWriteHistory() {
        val context = mockk<Context>()
        historyPresenter.writeHistory(context, "test", true)
        verify(exactly = 1) {
            historyRepository.writeHistory(context, "test", true)
        }
    }

    @Test
    fun testGetInstance() {
        val presenter = HistoryPresenter.getInstance(historyRepository)
        val presenter1 = HistoryPresenter.getInstance(historyRepository)
        Assert.assertEquals(presenter, presenter1)
    }
}
