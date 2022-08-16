package com.example.translatorapp.screen.translate

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.View
import android.widget.Toast
import com.example.translatorapp.R
import com.example.translatorapp.base.BaseFragment
import com.example.translatorapp.data.model.Language
import com.example.translatorapp.data.repository.source.language.LanguageDataSource
import com.example.translatorapp.data.repository.source.language.LanguageRepository
import com.example.translatorapp.data.repository.source.word.WordDataSource
import com.example.translatorapp.data.repository.source.word.WordRepository
import com.example.translatorapp.databinding.FragmentTranslateBinding
import com.example.translatorapp.screen.MainActivity
import com.example.translatorapp.screen.language.LanguageSourceFragment
import com.example.translatorapp.screen.language.LanguageTargetFragment
import com.example.translatorapp.util.addFragment
import java.util.Locale

class TranslateFragment :
    BaseFragment<FragmentTranslateBinding>(FragmentTranslateBinding::inflate),
    TranslateContract.View {

    private val speak by lazy { Speak() }
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
        addListener()
        presenter.apply {
            setView(this@TranslateFragment)
            getLanguage()
        }
    }

    override fun onDestroy() {
        speak.release()
        super.onDestroy()
    }

    override fun onGetLanguageSuccess(data: List<Language>) {
        activity?.runOnUiThread {
            presenter.source = data[0]
            presenter.target = data[1]
            bind.buttonFrom.text = data[0].nativeName
            bind.buttonTo.text = data[1].nativeName
        }
    }

    override fun onTranslateSentenceComplete(data: String) {
        activity?.runOnUiThread {
            bind.editOutput.setText(data)
        }
    }

    override fun onBreakSentenceComplete(data: List<String>) {
        activity?.runOnUiThread {
            bind.buttonTranslate.visibility = View.GONE
            val fragment = SentenceFragment.newInstance(data) {
                clearState()
                bind.buttonTranslate.visibility = View.VISIBLE
            }
            addFragment(
                fragment = fragment,
                addToBackStack = true,
                container = bind.frameSupLayoutContainer.id,
                manager = childFragmentManager
            )
        }
    }

    override fun onDictionaryLookupComplete(data: List<List<Any>>) {
        activity?.runOnUiThread {
            bind.buttonTranslate.visibility = View.GONE
            bind.editOutput.setText(data[2][0].toString())
            val res = listOf(data[0], data[1])
            val wordFragment = WordFragment.newInstance(
                sourceLang = presenter.source,
                targetLang = presenter.target,
                list = res
            ) {
                clearState()
                bind.buttonTranslate.visibility = View.VISIBLE
            }
            addFragment(
                fragment = wordFragment,
                addToBackStack = true,
                container = bind.frameSupLayoutContainer.id,
                manager = childFragmentManager
            )
        }
    }

    private fun addListener() {
        bind.buttonFrom.setOnClickListener {
            val sourceFragment =
                LanguageSourceFragment.newInstance(
                    presenter.listLanguage,
                    { clearState() }
                ) { data, text ->
                    data?.let {
                        bind.buttonFrom.text = it.nativeName
                        presenter.source = it
                    }
                    text?.let { bind.buttonFrom.text = it }
                }
            addFragment(
                fragment = sourceFragment,
                addToBackStack = true,
                container = (activity as MainActivity).findLayoutContainer(),
                manager = parentFragmentManager
            )
        }

        bind.buttonTo.setOnClickListener {
            val targetFragment = LanguageTargetFragment.newInstance(
                presenter.listLanguage,
                { clearState() }
            ) { data ->
                bind.buttonTo.text = data.nativeName
                presenter.target = data
            }
            addFragment(
                fragment = targetFragment,
                addToBackStack = true,
                container = (activity as MainActivity).findLayoutContainer(),
                manager = parentFragmentManager
            )
        }

        bind.buttonTranslate.setOnClickListener {
            if (bind.editInput.text.toString().trim().isNotEmpty()) {
                presenter.getTranslate(bind.editInput.text.toString().trim())
                enableImage(View.VISIBLE)
            }
        }

        bind.imageCopyOutput.setOnClickListener { clipBroad.copyToClipboard(bind.editOutput.text.toString()) }

        bind.imageCopyInput.setOnClickListener { clipBroad.copyToClipboard(bind.editInput.text.toString()) }

        bind.imageInputSpeaker.setOnClickListener {
            presenter.source?.code?.let { speak.speakOut(bind.editInput.text.toString(), Locale(it)) }
        }

        bind.imageOutputSpeaker.setOnClickListener {
            presenter.target?.code?.let {
                val text = bind.editOutput.text.toString().substringBefore("\n")
                speak.speakOut(text, Locale(it.substringBefore("-")))
            }
        }
    }

    private fun clearState() {
        enableImage(View.INVISIBLE)
        if (childFragmentManager.backStackEntryCount > 0) {
            childFragmentManager.popBackStack()
        }
        bind.editOutput.setText("")
    }

    private fun enableImage(visibility: Int) {
        bind.imageCopyInput.visibility = visibility
        bind.imageInputSpeaker.visibility = visibility
        bind.imageCopyOutput.visibility = visibility
        bind.imageOutputSpeaker.visibility = visibility
    }

    private inner class Speak {

        private var textToSpeech: TextToSpeech? = null

        init {
            textToSpeech = TextToSpeech(context) {
                textToSpeech?.setSpeechRate(1F)
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

    companion object {
        fun newInstance() = TranslateFragment()
    }
}
