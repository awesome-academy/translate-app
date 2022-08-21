package com.example.translatorapp.screen.history

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.widget.SearchView
import androidx.appcompat.app.AlertDialog
import com.example.translatorapp.R
import com.example.translatorapp.base.BaseFragment
import com.example.translatorapp.base.OnItemClickListener
import com.example.translatorapp.base.OnItemLongClickListener
import com.example.translatorapp.constant.Constant
import com.example.translatorapp.data.model.History
import com.example.translatorapp.data.repository.source.history.HistoryDataSource
import com.example.translatorapp.data.repository.source.history.HistoryRepository
import com.example.translatorapp.databinding.FragmentHistoryBinding
import com.example.translatorapp.screen.MainActivity
import com.example.translatorapp.screen.history.adapter.HistoryAdapter
import com.example.translatorapp.util.Dialog

class HistoryFragment :
    BaseFragment<FragmentHistoryBinding>(FragmentHistoryBinding::inflate),
    HistoryContract.View,
    OnItemLongClickListener<History>,
    OnItemClickListener<History> {

    private val adapter by lazy { HistoryAdapter() }
    private val myActivity by lazy { activity as? MainActivity }
    private val presenter by lazy {
        HistoryPresenter.getInstance(
            HistoryRepository.getInstance(
                HistoryDataSource.getInstance()
            )
        )
    }

    override fun changeToolbar() {
        myActivity?.let {
            it.enableView(true)
            it.changeToolbar(getString(R.string.title_history_screen), R.drawable.ic_back)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        presenter.setView(this)
        context?.let {
            presenter.getHistory(it)
        }
    }

    override fun onClick(data: History) {
        val bundle = Bundle()
        bundle.putParcelable(Constant.KEY_DATA_HISTORY, data)
        parentFragmentManager.setFragmentResult(Constant.KEY_HISTORY, bundle)
        activity?.onBackPressed()
    }

    override fun onLongClick(data: History) {
        context?.let { context ->
            val builder = AlertDialog.Builder(context, R.style.AlertDialogTheme)
            val dialog = Dialog(builder)
            dialog.apply {
                buildDialog(
                    title = getString(R.string.title_dialog_remove),
                    icon = R.drawable.ic_delete,
                    msg = getString(R.string.msg_dialog_remove, data.sourceWord),
                    cancellable = true
                )
                setCancelButton(getString(R.string.refuse))
                setPositiveButton(getString(R.string.confirm)) { _, _ ->
                    presenter.listHistory.remove(data)
                    presenter.writeHistory(
                        context,
                        presenter.listHistory.joinToString(separator = "\n") {
                            "${it.sourceCode}\t${it.targetCode}\t" +
                                "${it.sourceWord}\t${it.meanWord}"
                        },
                        false
                    )
                    adapter.removeData(data)
                }
                show()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_search, menu)
        super.onCreateOptionsMenu(menu, inflater)
        val searchItem = menu.findItem(R.id.search_bar)
        val searchView = searchItem.actionView as SearchView
        searchView.maxWidth = Int.MAX_VALUE
        searchView.apply {
            setOnSearchClickListener {
                myActivity?.enableItemToolbar(false)
            }

            setOnCloseListener {
                myActivity?.enableItemToolbar(true)
                adapter.updateData(presenter.listHistory)
                return@setOnCloseListener false
            }

            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(p0: String?): Boolean {
                    // No-op
                    return false
                }

                override fun onQueryTextChange(text: String?): Boolean {
                    text?.let { query ->
                        if (query.isNotEmpty()) {
                            val listResult =
                                presenter.listHistory.filter { it.sourceWord.contains(query) }
                            adapter.updateData(listResult)
                        } else {
                            adapter.updateData(presenter.listHistory)
                        }
                    }
                    return false
                }
            })
        }
    }

    override fun onGetHistoryComplete(data: List<History>) {
        adapter.updateData(presenter.listHistory)
        adapter.registerClickListener(this, this)
        binding.recyclerListHistory.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        myActivity?.let {
            it.unCheckItem()
            it.enableItemToolbar(true)
        }
    }

    companion object {
        fun newInstance() = HistoryFragment()
    }
}
