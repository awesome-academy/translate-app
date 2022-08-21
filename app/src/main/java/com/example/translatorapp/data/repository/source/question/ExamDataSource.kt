package com.example.translatorapp.data.repository.source.question

import android.content.Context
import com.example.translatorapp.R
import com.example.translatorapp.data.model.Answer
import com.example.translatorapp.data.model.Exam
import com.example.translatorapp.data.model.Question
import com.example.translatorapp.data.repository.OnResultListener
import com.example.translatorapp.data.repository.source.DataSource
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

class ExamDataSource : DataSource.ExamDataSource {

    override fun getExam(context: Context, listener: OnResultListener<List<Exam>>) {
        val threadPoolExecutor = ThreadPoolExecutor(
            1, 1, KEEP_ALIVE_TIME, TimeUnit.SECONDS, LinkedBlockingQueue()
        )
        threadPoolExecutor.execute {
            val inputStream = context.resources.openRawResource(R.raw.question)
            val reader = BufferedReader(InputStreamReader(inputStream))
            val line = reader.readLine()
            inputStream.close()
            reader.close()
            listener.onSuccess(getExam(line))
        }
        threadPoolExecutor.shutdown()
    }

    private fun getExam(json: String): List<Exam> {
        val listExam = mutableListOf<Exam>()
        val res = JSONArray(json)
        val resLength = res.length() - 1
        for (i in 0..resLength) {
            val examJson = res.getJSONObject(i)
            val name = examJson.getString(NAME)
            val listQuestion = mutableListOf<Question>()
            val listQuestionJson = examJson.getJSONArray(LIST_QUESTION)
            val listQuestionLength = listQuestionJson.length() - 1
            for (j in 0..listQuestionLength) {
                listQuestion.add(getQuestion(listQuestionJson.getJSONObject(j)))
            }
            listExam.add(Exam(name, listQuestion))
        }
        return listExam
    }

    private fun getQuestion(questionJson: JSONObject): Question {
        val listAnswer = mutableListOf<Answer>()
        val ques = questionJson.getString(TEXT)
        val listAnswerJson = questionJson.getJSONArray(LIST_ANSWER)
        val listAnswerLength = listAnswerJson.length() - 1
        for (i in 0..listAnswerLength) {
            val answerJson = listAnswerJson.getJSONObject(i)
            listAnswer.add(Answer(answerJson.getString(TEXT), answerJson.getBoolean(TRUE_ANSWER)))
        }
        return Question(ques, listAnswer)
    }

    companion object {
        private const val KEEP_ALIVE_TIME: Long = 60
        private const val NAME = "name"
        private const val LIST_QUESTION = "listQuestion"
        private const val TEXT = "text"
        private const val LIST_ANSWER = "listAnswer"
        private const val TRUE_ANSWER = "isTrueAnswer"
        private var instance: ExamDataSource? = null

        fun getInstance() = synchronized(this) {
            instance ?: ExamDataSource().also { instance = it }
        }
    }
}
