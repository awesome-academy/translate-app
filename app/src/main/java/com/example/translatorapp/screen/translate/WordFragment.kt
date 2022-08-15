package com.example.translatorapp.screen.translate

import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.translatorapp.base.BaseFragment
import com.example.translatorapp.base.OnItemClickListener
import com.example.translatorapp.data.model.BackTranslation
import com.example.translatorapp.databinding.FragmentWordBinding
import com.example.translatorapp.screen.translate.adapter.MeanAdapter
import com.example.translatorapp.screen.translate.adapter.WordAdapter

class WordFragment(
    private val list: List<List<Any>>,
    private val visibleButton: () -> Unit
) : BaseFragment<FragmentWordBinding>(FragmentWordBinding::inflate),
    OnItemClickListener<BackTranslation> {

    override fun changeToolbar() {
        (parentFragment as TranslateFragment).changeIconToolbar()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bind.means.apply {
            val meanAdapter = MeanAdapter()
            meanAdapter.updatedData(list[0])
            adapter = meanAdapter
            isNestedScrollingEnabled = false
        }
        bind.sampleWord.apply {
            val wordAdapter = WordAdapter()
            wordAdapter.updateData(list[1])
            wordAdapter.registerListener(this@WordFragment)
            adapter = wordAdapter
            isNestedScrollingEnabled = false
        }
    }

    override fun onDestroyView() {
        visibleButton()
        (parentFragment as TranslateFragment).changeToolbar()
        super.onDestroyView()
    }

    override fun onClick(data: BackTranslation) {
        Toast.makeText(context, "example", Toast.LENGTH_SHORT).show()
    }

    companion object {
        fun newInstance(list: List<List<Any>>, visibleButton: () -> Unit) =
            WordFragment(list, visibleButton)
    }
}
