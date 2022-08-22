package com.example.translatorapp.screen.setting

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import androidx.appcompat.app.AppCompatDelegate
import com.example.translatorapp.R
import com.example.translatorapp.base.BaseFragment
import com.example.translatorapp.constant.Constant
import com.example.translatorapp.databinding.FragmentSettingBinding
import com.example.translatorapp.screen.MainActivity
import com.example.translatorapp.screen.translate.TranslateFragment

class SettingFragment : BaseFragment<FragmentSettingBinding>(FragmentSettingBinding::inflate) {

    private val myActivity by lazy { activity as? MainActivity }
    private var speak: TranslateFragment.Speak? = null
    private var pitch = 0F
    private var speed = 0F
    private var isDarkMode = false

    override fun changeToolbar() {
        myActivity?.let {
            it.enableView(true)
            it.changeToolbar(getString(R.string.title_setting_fragment), R.drawable.ic_back)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        restoreState()
        binding.spinnerPitch.setSelection(1)
        addSpinnerListener(binding.spinnerPitch) { data ->
            pitch = data
            speak?.setPitch(data)
        }
        addSpinnerListener(binding.spinnerSpeed) { data ->
            speed = data
            speak?.setSpeed(data)
        }
        binding.switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            isDarkMode = isChecked
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        val sharedPreferences = activity?.getSharedPreferences(Constant.KEY_SETTING, Context.MODE_PRIVATE)
        sharedPreferences?.edit()
            ?.putFloat(Constant.KEY_PITCH, pitch)
            ?.putFloat(Constant.KEY_SPEED, speed)
            ?.putBoolean(Constant.KEY_DARK_MODE, isDarkMode)
            ?.apply()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean(Constant.KEY_SETTING, true)
        super.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        myActivity?.apply {
            unCheckItem()
        }
        super.onDestroy()
    }

    private fun addSpinnerListener(spinner: Spinner, changeData: (Float) -> Unit) {
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                p2: Long
            ) {
                when (position) {
                    0 -> changeData(LOW)
                    1 -> changeData(NORMAL)
                    2 -> changeData(HIGH)
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                // no implement
            }
        }
    }

    private fun restoreState() {
        val sharedPreferences = activity?.getSharedPreferences(Constant.KEY_SETTING, Context.MODE_PRIVATE)
        sharedPreferences?.getFloat(Constant.KEY_PITCH, 1F)?.let {
            pitch = it
        }
        sharedPreferences?.getFloat(Constant.KEY_SPEED, 1F)?.let {
            speed = it
        }
        sharedPreferences?.getBoolean(Constant.KEY_DARK_MODE, false)?.let {
            isDarkMode = it
            binding.switchDarkMode.isChecked = isDarkMode
        }
        fun restoreSpinnerState(spinner: Spinner, state: Float) {
            when (state) {
                LOW -> spinner.setSelection(0)
                NORMAL -> spinner.setSelection(1)
                HIGH -> spinner.setSelection(2)
            }
        }
        restoreSpinnerState(binding.spinnerPitch, pitch)
        restoreSpinnerState(binding.spinnerSpeed, speed)
    }

    companion object {

        private const val LOW = 0.5F
        const val NORMAL = 1F
        private const val HIGH = 3F

        fun newInstance(speak: TranslateFragment.Speak) = SettingFragment().apply {
            this.speak = speak
        }
    }
}
