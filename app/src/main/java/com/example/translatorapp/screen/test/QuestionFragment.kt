package com.example.translatorapp.screen.test

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.core.content.ContextCompat
import com.example.translatorapp.R
import com.example.translatorapp.base.BaseFragment
import com.example.translatorapp.constant.Constant
import com.example.translatorapp.data.model.Question
import com.example.translatorapp.databinding.FragmentQuestionBinding
import java.util.Timer
import kotlin.concurrent.schedule

class QuestionFragment : BaseFragment<FragmentQuestionBinding>(FragmentQuestionBinding::inflate) {

    private var question: Question? = null
    private var time = Constant.TIME_UNIT
    private var timer: Timer? = null
    private var nextTimer: Timer? = null
    private var selected: Boolean = false

    override fun changeToolbar() {
        // No-op
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.textTime.text = getString(R.string.time, time)
        question?.apply {
            binding.textQuestion.text = text
            if (listAnswer.size == LIST_ANSWER_SIZE) {
                binding.buttonAnswerA.text = listAnswer[ANSWER_A_INDEX].text
                binding.buttonAnswerB.text = listAnswer[ANSWER_B_INDEX].text
                binding.buttonAnswerC.text = listAnswer[ANSWER_C_INDEX].text
                binding.buttonAnswerD.text = listAnswer[ANSWER_D_INDEX].text
            }
        }
        addListener()
        timer = Timer()
        timer?.schedule(0, ONE_SECOND) {
            parentFragment?.activity?.runOnUiThread {
                time--
                if (time <= -1) {
                    nextQuestion(false, Constant.TIME_UNIT)
                } else {
                    binding.textTime.text = getString(R.string.time, time)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        timer?.cancel()
        timer?.purge()
        nextTimer?.cancel()
        nextTimer?.purge()
    }

    override fun onDetach() {
        super.onDetach()
        if (parentFragment is TestFragment) {
            (parentFragment as TestFragment).nextQuestion()
        }
    }

    private fun addListener() {
        binding.buttonAnswerA.setOnClickListener { onClickButton(binding.buttonAnswerA) }
        binding.buttonAnswerB.setOnClickListener { onClickButton(binding.buttonAnswerB) }
        binding.buttonAnswerC.setOnClickListener { onClickButton(binding.buttonAnswerC) }
        binding.buttonAnswerD.setOnClickListener { onClickButton(binding.buttonAnswerD) }
    }

    private fun onClickButton(button: Button) {
        val text = button.text.trim()
        question?.apply {
            for (answer in listAnswer) {
                if (selected) {
                    break
                }
                if (answer.text == text) {
                    selected = true
                    setColor(button, answer.isTrueAnswer)
                    timer?.cancel()
                    nextTimer = Timer()
                    nextTimer?.schedule(ONE_SECOND, ONE_SECOND) {
                        parentFragment?.activity?.runOnUiThread {
                            nextQuestion(answer.isTrueAnswer, Constant.TIME_UNIT - time)
                        }
                    }
                }
            }
        }
    }

    private fun setColor(button: Button, flag: Boolean) {
        context?.let {
            if (flag) {
                button.backgroundTintList = ContextCompat.getColorStateList(it, R.color.color_green)
            } else {
                button.backgroundTintList = ContextCompat.getColorStateList(it, R.color.color_red)
            }
        }
    }

    private fun nextQuestion(flag: Boolean, time: Int) {
        val bundle = Bundle()
        bundle.putBoolean(Constant.KEY_ANSWER, flag)
        bundle.putInt(Constant.KEY_TIME, time)
        parentFragmentManager.setFragmentResult(Constant.KEY_QUESTION, bundle)
        parentFragmentManager.popBackStack()
    }

    companion object {
        private const val LIST_ANSWER_SIZE = 4
        private const val ANSWER_A_INDEX = 0
        private const val ANSWER_B_INDEX = 1
        private const val ANSWER_C_INDEX = 2
        private const val ANSWER_D_INDEX = 3
        private const val ONE_SECOND: Long = 1000

        fun newInstance(question: Question) = QuestionFragment().apply {
            this.question = question
        }
    }
}
