package com.example.translatorapp.screen.translate

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import com.example.translatorapp.R
import com.example.translatorapp.base.BaseFragment
import com.example.translatorapp.constant.Constant
import com.example.translatorapp.data.model.History
import com.example.translatorapp.data.model.Language
import com.example.translatorapp.data.repository.source.history.HistoryDataSource
import com.example.translatorapp.data.repository.source.history.HistoryRepository
import com.example.translatorapp.data.repository.source.language.LanguageDataSource
import com.example.translatorapp.data.repository.source.language.LanguageRepository
import com.example.translatorapp.data.repository.source.word.WordDataSource
import com.example.translatorapp.data.repository.source.word.WordRepository
import com.example.translatorapp.databinding.FragmentTranslateBinding
import com.example.translatorapp.screen.MainActivity
import com.example.translatorapp.screen.language.LanguageSourceFragment
import com.example.translatorapp.screen.language.LanguageTargetFragment
import com.example.translatorapp.screen.setting.SettingFragment
import com.example.translatorapp.util.Dialog
import com.example.translatorapp.util.NetworkUtils
import com.example.translatorapp.util.addFragmentToChild
import com.example.translatorapp.util.addFragmentToParent
import java.util.Locale

class TranslateFragment :
    BaseFragment<FragmentTranslateBinding>(FragmentTranslateBinding::inflate),
    TranslateContract.View {

    val speak by lazy { Speak() }
    private val myActivity by lazy { activity as? MainActivity }
    private val stateView by lazy { StateView() }
    private val clipBroad by lazy { ClipBroad() }
    private val dialogLoading by lazy {
        context?.let {
            Dialog(AlertDialog.Builder(it))
        }
    }
    private val dialogNotify by lazy {
        context?.let {
            Dialog(AlertDialog.Builder(it, R.style.AlertDialogTheme))
        }
    }
    private val presenter by lazy {
        TranslatePresenter.getInstance(
            LanguageRepository.getInstance(
                LanguageDataSource.getInstance()
            ),
            WordRepository.getInstance(
                WordDataSource.getInstance()
            ),
            HistoryRepository.getInstance(
                HistoryDataSource.getInstance()
            )
        )
    }

    override fun changeToolbar() {
        myActivity?.let {
            it.enableView(false)
            it.changeToolbar(getString(R.string.title_app), R.drawable.ic_menu)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sharedPreferences =
            activity?.getSharedPreferences(Constant.KEY_SETTING, Context.MODE_PRIVATE)
        sharedPreferences?.getFloat(Constant.KEY_PITCH, SettingFragment.NORMAL)?.let {
            speak.setPitch(it)
        }
        sharedPreferences?.getFloat(Constant.KEY_SPEED, SettingFragment.NORMAL)?.let {
            speak.setSpeed(it)
        }
        stateView.addListener()
        parentFragmentManager.setFragmentResultListener(
            Constant.KEY_HISTORY,
            this
        ) { _, result ->
            val history = result.getParcelable<History>(Constant.KEY_DATA_HISTORY)

            stateView.setLanguage(
                history?.sourceCode?.trim(),
                binding.buttonFrom,
                getString(R.string.auto_detect)
            ) {
                presenter.sourceLang = it
            }

            stateView.setLanguage(history?.targetCode, binding.buttonTo) {
                presenter.targetLang = it
            }
            history?.apply {
                binding.editInput.setText(sourceWord.trim())
                binding.buttonTranslate.callOnClick()
            }
        }

        presenter.apply {
            setView(this@TranslateFragment)
            activity?.applicationContext?.let {
                stateView.getLang(it)
            }
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
        val sharedPreferences = activity?.getSharedPreferences(Constant.KEY_TRANSLATE, Context.MODE_PRIVATE)
        if (presenter.targetLang != null) {
            sharedPreferences?.edit()
                ?.putString(Constant.KEY_LANGUAGE_TO, presenter.targetLang?.code)
                ?.putString(Constant.KEY_LANGUAGE_FROM, presenter.sourceLang?.code)
                ?.apply()
        }
        super.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        super.onDestroy()
        speak.release()
    }

    override fun onGetLanguageSuccess(data: List<Language>) {
        activity?.runOnUiThread {
            val sharedPreferences =
                activity?.getSharedPreferences(Constant.KEY_TRANSLATE, Context.MODE_PRIVATE)
            sharedPreferences?.getString(Constant.KEY_LANGUAGE_FROM, null).let {
                stateView.setLanguage(
                    it,
                    binding.buttonFrom,
                    getString(R.string.auto_detect)
                ) { language ->
                    presenter.sourceLang = language
                }
            }
            sharedPreferences?.getString(Constant.KEY_LANGUAGE_TO, null).let {
                stateView.setLanguage(it, binding.buttonTo) { language ->
                    presenter.targetLang = language
                }
            }
            if (dialogLoading?.isShowing() == true) {
                dialogLoading?.dismiss()
            }
        }
    }

    override fun onTranslateSentenceComplete(data: String) {
        activity?.runOnUiThread {
            binding.editInput.isEnabled = false
            binding.editOutput.setText(data)
            context?.let {
                presenter.writeHistory(
                    it,
                    "${presenter.sourceLang?.code}\t${presenter.targetLang?.code}\t" +
                        "${binding.editInput.text.trim()}\t${data.split("\n")[0]}\n",
                    true
                )
            }
            if (dialogLoading?.isShowing() == true) {
                dialogLoading?.dismiss()
            }
        }
    }

    override fun onBreakSentenceComplete(data: List<String>) {
        activity?.runOnUiThread {
            binding.buttonTranslate.visibility = View.GONE
            val fragment = SentenceFragment.newInstance(data) {
                stateView.clearState()
                binding.buttonTranslate.visibility = View.VISIBLE
            }
            addFragmentToChild(
                fragment = fragment,
                addToBackStack = true,
                container = binding.frameSupLayoutContainer.id,
                customAnimation = false
            )
        }
    }

    override fun onDictionaryLookupComplete(data: List<List<Any>>) {
        activity?.runOnUiThread {
            binding.editInput.isEnabled = false
            binding.buttonTranslate.visibility = View.GONE
            binding.editOutput.setText(data[2][0].toString())
            context?.let {
                presenter.writeHistory(
                    it,
                    "${presenter.sourceLang?.code}\t${presenter.targetLang?.code}\t" +
                        "${binding.editInput.text.trim()}\t${
                        data[2][0].toString().split("\n")[0]
                        }\n",
                    true
                )
            }
            val res = listOf(data[0], data[1])
            var sourceLang = presenter.sourceLang
            if (sourceLang == null) {
                sourceLang = presenter.sourceLangTemp
            }
            val wordFragment = WordFragment.newInstance(
                sourceLang = sourceLang,
                targetLang = presenter.targetLang,
                sourceWord = binding.editInput.text.toString(),
                list = res
            ) {
                stateView.clearState()
                binding.buttonTranslate.visibility = View.VISIBLE
            }
            addFragmentToChild(
                fragment = wordFragment,
                addToBackStack = true,
                container = binding.frameSupLayoutContainer.id,
                customAnimation = false
            )
            if (dialogLoading?.isShowing() == true) {
                dialogLoading?.dismiss()
            }
        }
    }

    override fun onError(message: Int) {
        myActivity?.runOnUiThread {
            if (dialogLoading?.isShowing() == true) {
                dialogLoading?.dismiss()
            }
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
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

        fun setLanguage(
            code: String?,
            button: Button,
            text: String? = null,
            changeData: (Language?) -> Unit
        ) {
            val language = presenter.mapLanguage[code]
            if (code == null || language == null) {
                if (text != null) {
                    button.text = text
                } else {
                    button.text = presenter.listLanguage[0].nativeName
                }
                changeData(null)
            } else {
                button.text = language.nativeName
                changeData(language)
            }
        }

        fun addListener() {
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
                myActivity?.let {
                    addFragmentToParent(
                        fragment = sourceFragment,
                        addToBackStack = true,
                        container = it.findLayoutContainer()
                    )
                }
            }

            binding.buttonTo.setOnClickListener {
                val targetFragment = LanguageTargetFragment.newInstance(
                    presenter.listLanguage,
                    { stateView.clearState() }
                ) { data ->
                    binding.buttonTo.text = data.nativeName
                    presenter.targetLang = data
                }
                myActivity?.let {
                    addFragmentToParent(
                        fragment = targetFragment,
                        addToBackStack = true,
                        container = it.findLayoutContainer()
                    )
                }
            }

            binding.buttonTranslate.setOnClickListener {
                if (binding.editInput.text.toString().trim().isNotEmpty()) {
                    activity?.applicationContext?.let {
                        if (NetworkUtils.isNetworkAvailable(it)) {
                            dialogLoading?.showLoadingDialog()
                            presenter.getTranslate(binding.editInput.text.toString().trim())
                            stateView.enableImage(View.VISIBLE)
                        } else {
                            dialogNotify?.let { dialog ->
                                NetworkUtils.setDialogAction(dialog) {
                                    binding.buttonTranslate.callOnClick()
                                }
                            }
                        }
                    }
                }
            }

            binding.imageCopyOutput.setOnClickListener { clipBroad.copyToClipboard(binding.editOutput.text.toString()) }

            binding.imageCopyInput.setOnClickListener { clipBroad.copyToClipboard(binding.editInput.text.toString()) }

            stateView.addSpeakerListener()
        }

        private fun addSpeakerListener() {
            binding.imageInputSpeaker.setOnClickListener {
                presenter.sourceLang?.code?.let {
                    speak.speakOut(
                        binding.editInput.text.toString(),
                        Locale(it.substringBefore("-"))
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

        fun getLang(applicationContext: Context) {
            if (NetworkUtils.isNetworkAvailable(applicationContext)) {
                dialogLoading?.showLoadingDialog()
                presenter.onStart()
            } else {
                dialogLoading?.let {
                    NetworkUtils.setDialogAction(it) { getLang(applicationContext) }
                }
            }
        }
    }

    companion object {
        fun newInstance() = TranslateFragment()
    }
}
