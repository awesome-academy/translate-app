package com.example.translatorapp.screen.example

import android.os.Bundle
import android.view.View
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

class ExampleFragment :
    BaseFragment<FragmentExampleBinding>(FragmentExampleBinding::inflate),
    ExampleContract.View {

    private var source: Language? = null
    private var target: Language? = null
    private var backTranslation: BackTranslation? = null
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
