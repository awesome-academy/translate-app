package com.example.translatorapp.screen.test

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.example.translatorapp.R
import com.example.translatorapp.base.BaseFragment
import com.example.translatorapp.base.OnItemClickListener
import com.example.translatorapp.constant.Constant
import com.example.translatorapp.data.model.Exam
import com.example.translatorapp.data.model.Question
import com.example.translatorapp.data.repository.source.question.ExamDataSource
import com.example.translatorapp.data.repository.source.question.ExamRepository
import com.example.translatorapp.databinding.FragmentTestBinding
import com.example.translatorapp.screen.MainActivity
import com.example.translatorapp.screen.test.adapter.ExamAdapter
import com.example.translatorapp.util.Dialog
import com.example.translatorapp.util.addFragmentToChild

class TestFragment :
    BaseFragment<FragmentTestBinding>(FragmentTestBinding::inflate),
    OnItemClickListener<Exam>,
    TestContract.View {

    private var currentQuestion = 0
    private var lengthExam = 0
    private var currentExam: Exam? = null
    private var score = 0
    private var totalTime = 0
    private val myActivity by lazy { activity as? MainActivity }
    private val presenter by lazy {
        TestPresenter.getInstance(
            ExamRepository.getInstance(
                ExamDataSource.getInstance()
            )
        )
    }

    override fun changeToolbar() {
        myActivity?.apply {
            enableView(true)
            changeToolbar(getString(R.string.title_test_fragment), R.drawable.ic_back)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.setView(this)
        context?.let {
            presenter.getExam(it)
        }
        childFragmentManager.setFragmentResultListener(Constant.KEY_QUESTION, this) { _, result ->
            val isTrueAnswer = result.getBoolean(Constant.KEY_ANSWER)
            val time = result.getInt(Constant.KEY_TIME)
            if (isTrueAnswer) {
                score++
            }
            totalTime += time
        }
    }

    override fun onGetQuestionComplete(data: List<Exam>) {
        val adapter = ExamAdapter()
        adapter.updateData(data)
        adapter.regisListener(this)
        binding.recyclerListTest.adapter = adapter
    }

    override fun onDestroy() {
        super.onDestroy()
        myActivity?.unCheckItem()
    }

    override fun onClick(data: Exam) {
        binding.recyclerListTest.visibility = View.INVISIBLE
        this.lengthExam = data.listQuestion.size
        this.currentExam = data
        nextQuestion()
    }

    private fun addQuestionFragment(question: Question) {
        if (!isRemoving) {
            addFragmentToChild(
                fragment = QuestionFragment.newInstance(question),
                addToBackStack = true,
                container = binding.frameTestContainer.id,
                customAnimation = true
            )
        }
    }

    private fun showDialog() {
        val minute = totalTime / Constant.TIME_UNIT
        val second = totalTime - minute * Constant.TIME_UNIT
        context?.let { context ->
            val msg = getString(
                R.string.your_score,
                score,
                lengthExam
            ) + "\n" + getString(R.string.your_time, minute, second)

            val builder = AlertDialog.Builder(context, R.style.AlertDialogTheme)
            val dialog = Dialog(builder)
            dialog.apply {
                buildDialog(
                    title = currentExam?.name,
                    msg = msg,
                    cancellable = true
                )
                setPositiveButton(getString(R.string.confirm)) { dialog, _ ->
                    dialog.cancel()
                }
            }
            dialog.show()
        }
    }

    fun nextQuestion() {
        currentExam?.let {
            if (currentQuestion < lengthExam) {
                addQuestionFragment(it.listQuestion[currentQuestion])
                currentQuestion++
            } else {
                showDialog()
                currentQuestion = 0
                score = 0
                totalTime = 0
                binding.recyclerListTest.visibility = View.VISIBLE
            }
        }
    }

    companion object {
        fun newInstance() = TestFragment()
    }
}
