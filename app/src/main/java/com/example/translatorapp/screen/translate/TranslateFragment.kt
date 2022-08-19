package com.example.translatorapp.screen.translate

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import com.example.translatorapp.R
import com.example.translatorapp.base.BaseFragment
import com.example.translatorapp.constant.Constant
import com.example.translatorapp.data.model.Language
import com.example.translatorapp.data.repository.source.language.LanguageDataSource
import com.example.translatorapp.data.repository.source.language.LanguageRepository
import com.example.translatorapp.data.repository.source.word.WordDataSource
import com.example.translatorapp.data.repository.source.word.WordRepository
import com.example.translatorapp.databinding.FragmentTranslateBinding
import com.example.translatorapp.screen.MainActivity
import com.example.translatorapp.screen.language.LanguageSourceFragment
import com.example.translatorapp.screen.language.LanguageTargetFragment
import com.example.translatorapp.screen.setting.SettingFragment
import com.example.translatorapp.util.addFragment
import java.util.Locale

class TranslateFragment :
    BaseFragment<FragmentTranslateBinding>(FragmentTranslateBinding::inflate),
    TranslateContract.View {

    val speak by lazy { Speak() }
    private val stateView by lazy { StateView() }
    private val clipBroad by lazy { ClipBroad() }
    private val presenter by lazy {
        TranslatePresenter.getInstance(
            LanguageRepository.getInstance(
                LanguageDataSource.getInstance()
            ),
            WordRepository.getInstance(
                WordDataSource.getInstance()
            )
        )
    }

    override fun changeToolbar() {
        (activity as MainActivity).let {
            it.enableView(false)
            it.changeToolbar(getString(R.string.title_app), R.drawable.ic_menu)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sharedPreferences = activity?.getSharedPreferences(Constant.KEY_SETTING, Context.MODE_PRIVATE)
        sharedPreferences?.getFloat(Constant.KEY_PITCH, SettingFragment.NORMAL)?.let {
            speak.setPitch(it)
        }
        sharedPreferences?.getFloat(Constant.KEY_SPEED, SettingFragment.NORMAL)?.let {
            speak.setSpeed(it)
        }
        addListener()
        presenter.apply {
            setView(this@TranslateFragment)
            getLanguage()
        }
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if (savedInstanceState != null && savedInstanceState.getBoolean(Constant.KEY_VISIBLE)) {
            stateView.enableImage(View.VISIBLE)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean(Constant.KEY_VISIBLE, binding.imageOutputSpeaker.isVisible)
        super.onSaveInstanceState(outState)
    }

    override fun onStop() {
        super.onStop()
        speak.release()
        val sharedPreferences = activity?.getSharedPreferences(Constant.KEY_TRANSLATE, Context.MODE_PRIVATE)
        if (presenter.targetLang != null) {
            sharedPreferences?.edit()
                ?.putInt(Constant.KEY_POSITION_TO, presenter.listLanguage.indexOf(presenter.targetLang))
                ?.putInt(Constant.KEY_POSITION_FROM, presenter.listLanguage.indexOf(presenter.sourceLang))
                ?.apply()
        }
    }

    override fun onGetLanguageSuccess(data: List<Language>) {
        activity?.runOnUiThread {
            var sourceLangIndex = 0
            var targetLanguageIndex = 1
            val sharedPreferences =
                activity?.getSharedPreferences(Constant.KEY_TRANSLATE, Context.MODE_PRIVATE)
            sharedPreferences?.getInt(Constant.KEY_POSITION_FROM, -1)?.let {
                sourceLangIndex = it
            }
            sharedPreferences?.getInt(Constant.KEY_POSITION_TO, -1)?.let {
                if (it != -1) {
                    targetLanguageIndex = it
                }
            }
            if (sourceLangIndex == -1) {
                binding.buttonFrom.text = getString(R.string.auto_detect)
                presenter.sourceLang = null
            } else {
                presenter.sourceLang = data[sourceLangIndex]
                binding.buttonFrom.text = data[sourceLangIndex].nativeName
            }
            presenter.targetLang = data[targetLanguageIndex]
            binding.buttonTo.text = data[targetLanguageIndex].nativeName
        }
    }

    override fun onTranslateSentenceComplete(data: String) {
        activity?.runOnUiThread {
            binding.editOutput.setText(data)
        }
    }

    override fun onBreakSentenceComplete(data: List<String>) {
        activity?.runOnUiThread {
            binding.buttonTranslate.visibility = View.GONE
            val fragment = SentenceFragment.newInstance(data) {
                stateView.clearState()
                binding.buttonTranslate.visibility = View.VISIBLE
            }
            addFragment(
                fragment = fragment,
                addToBackStack = true,
                container = binding.frameSupLayoutContainer.id,
                manager = childFragmentManager
            )
        }
    }

    override fun onDictionaryLookupComplete(data: List<List<Any>>) {
        activity?.runOnUiThread {
            binding.buttonTranslate.visibility = View.GONE
            binding.editOutput.setText(data[2][0].toString())
            val res = listOf(data[0], data[1])
            val wordFragment = WordFragment.newInstance(
                sourceLang = presenter.sourceLang,
                targetLang = presenter.targetLang,
                list = res
            ) {
                stateView.clearState()
                binding.buttonTranslate.visibility = View.VISIBLE
            }
            addFragment(
                fragment = wordFragment,
                addToBackStack = true,
                container = binding.frameSupLayoutContainer.id,
                manager = childFragmentManager
            )
        }
    }

    private fun addListener() {
        binding.buttonFrom.setOnClickListener {
            val sourceFragment =
                LanguageSourceFragment.newInstance(
                    presenter.listLanguage,
                    { stateView.clearState() }
                ) { data, text ->
                    data?.let {
                        binding.buttonFrom.text = it.nativeName
                    }
                    presenter.sourceLang = data
                    text?.let { binding.buttonFrom.text = it }
                }
            addFragment(
                fragment = sourceFragment,
                addToBackStack = true,
                container = (activity as MainActivity).findLayoutContainer(),
                manager = parentFragmentManager
            )
        }

        binding.buttonTo.setOnClickListener {
            val targetFragment = LanguageTargetFragment.newInstance(
                presenter.listLanguage,
                { stateView.clearState() }
            ) { data ->
                binding.buttonTo.text = data.nativeName
                presenter.targetLang = data
            }
            addFragment(
                fragment = targetFragment,
                addToBackStack = true,
                container = (activity as MainActivity).findLayoutContainer(),
                manager = parentFragmentManager
            )
        }

        binding.buttonTranslate.setOnClickListener {
            if (binding.editInput.text.toString().trim().isNotEmpty()) {
                presenter.getTranslate(binding.editInput.text.toString().trim())
                stateView.enableImage(View.VISIBLE)
            }
        }

        binding.imageCopyOutput.setOnClickListener { clipBroad.copyToClipboard(binding.editOutput.text.toString()) }

        binding.imageCopyInput.setOnClickListener { clipBroad.copyToClipboard(binding.editInput.text.toString()) }

        binding.imageInputSpeaker.setOnClickListener {
            presenter.sourceLang?.code?.let {
                speak.speakOut(
                    binding.editInput.text.toString(),
                    Locale(it)
                )
            }
        }

        binding.imageOutputSpeaker.setOnClickListener {
            presenter.targetLang?.code?.let {
                val text = binding.editOutput.text.toString().substringBefore("\n")
                speak.speakOut(text, Locale(it.substringBefore("-")))
            }
        }
    }

    inner class Speak {

        private var textToSpeech: TextToSpeech? = null
        private var pitch = 1F
        private var speed = 1F

        init {
            textToSpeech = TextToSpeech(context) {
                textToSpeech?.setPitch(pitch)
                textToSpeech?.setSpeechRate(speed)
            }
        }

        private fun setVoice(textToSpeech: TextToSpeech, local: Locale) {
            val setVoice = textToSpeech.voices
            for (value in setVoice) {
                if (value.name.contains(local.language)) {
                    textToSpeech.voice = value
                    break
                }
            }
        }

        fun speakOut(text: String, local: Locale) {
            textToSpeech?.let {
                val support = it.isLanguageAvailable(local)
                if (
                    support != TextToSpeech.LANG_NOT_SUPPORTED &&
                    support != TextToSpeech.LANG_MISSING_DATA
                ) {
                    if (it.isSpeaking) {
                        it.stop()
                    }
                    setVoice(it, local)
                    it.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
                } else {
                    Toast.makeText(context, getString(R.string.msg_not_support), Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }

        fun setSpeed(speed: Float) {
            textToSpeech?.setSpeechRate(speed)
        }

        fun setPitch(pitch: Float) {
            textToSpeech?.setPitch(pitch)
        }

        fun release() {
            textToSpeech?.stop()
            textToSpeech?.shutdown()
        }
    }

    private inner class ClipBroad {

        fun copyToClipboard(text: String) {
            activity?.let {
                val clipboard = it.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                clipboard.setPrimaryClip(ClipData.newPlainText("label", text))
                Toast.makeText(context, getString(R.string.msg_copy), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private inner class StateView {

        fun enableImage(visibility: Int) {
            binding.imageCopyInput.visibility = visibility
            binding.imageInputSpeaker.visibility = visibility
            binding.imageCopyOutput.visibility = visibility
            binding.imageOutputSpeaker.visibility = visibility
        }

        fun clearState() {
            enableImage(View.INVISIBLE)
            if (childFragmentManager.backStackEntryCount > 0) {
                childFragmentManager.popBackStack()
            }
            binding.editOutput.setText("")
        }
    }

    companion object {
        fun newInstance() = TranslateFragment()
    }
}
