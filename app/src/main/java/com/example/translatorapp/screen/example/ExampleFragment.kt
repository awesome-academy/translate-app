package com.example.translatorapp.screen.example

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.translatorapp.R
import com.example.translatorapp.base.BaseFragment
import com.example.translatorapp.data.model.BackTranslation
import com.example.translatorapp.data.model.Example
import com.example.translatorapp.data.model.Language
import com.example.translatorapp.data.repository.source.example.ExampleDataSource
import com.example.translatorapp.data.repository.source.example.ExampleRepository
import com.example.translatorapp.databinding.FragmentExampleBinding
import com.example.translatorapp.screen.MainActivity
import com.example.translatorapp.screen.example.adapter.ExampleAdapter
import com.example.translatorapp.util.Dialog

class ExampleFragment :
    BaseFragment<FragmentExampleBinding>(FragmentExampleBinding::inflate),
    ExampleContract.View {

    private var source: Language? = null
    private var target: Language? = null
    private var backTranslation: BackTranslation? = null
    private val dialog by lazy { context?.let { Dialog(AlertDialog.Builder(it)) } }
    private val presenter by lazy {
        ExamplePresenter.getInstance(
            ExampleRepository.getInstance(
                ExampleDataSource.getInstance()
            )
        )
    }

    override fun changeToolbar() {
        (activity as? MainActivity)?.let {
            it.enableView(true)
            it.changeToolbar(getString(R.string.title_example_fragment), R.drawable.ic_back)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.setView(this)
        backTranslation?.let {
            binding.textSourceWord.text =
                getString(R.string.text_source_suffix, it.sampleWord.displayText)
            target?.let { target ->
                source?.let { source ->
                    dialog?.showLoadingDialog()
                    presenter.getExample(it, source, target)
                }
            }
        }
    }

    override fun onGetExampleComplete(listExample: List<Example>) {
        activity?.runOnUiThread {
            val adapter = ExampleAdapter()
            adapter.updateData(listExample)
            binding.recyclerListExample.adapter = adapter
            if (dialog?.isShowing() == true) {
                dialog?.dismiss()
            }
        }
    }

    override fun onError(message: Int) {
        activity?.runOnUiThread {
            if (dialog?.isShowing() == true) {
                dialog?.dismiss()
            }
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        fun newInstance(backTranslation: BackTranslation, source: Language?, target: Language?) =
            ExampleFragment().apply {
                this.backTranslation = backTranslation
                this.source = source
                this.target = target
            }
    }
}
