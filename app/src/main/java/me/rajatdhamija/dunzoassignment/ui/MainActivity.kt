package me.rajatdhamija.dunzoassignment.ui

import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import me.rajatdhamija.dunzoassignment.Injection
import me.rajatdhamija.dunzoassignment.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel
    private val adapter = ImageAdapter()
    private var searchJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        viewModel = ViewModelProvider(this, Injection.provideViewModelFactory(this))
            .get(MainViewModel::class.java)
        initAdapter()
        val query = savedInstanceState?.getString(LAST_SEARCH_QUERY) ?: DEFAULT_QUERY
        search(query)
        initSearch(query)
        binding.retryButton.setOnClickListener { adapter.retry() }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(LAST_SEARCH_QUERY, binding.searchPhoto.text.trim().toString())
    }

    private fun initSearch(query: String?) {
        query?.let {
            binding.searchPhoto.setText(query)

            binding.searchPhoto.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    updatePhotoListFromInput()
                    true
                } else {
                    false
                }
            }
            binding.searchPhoto.setOnKeyListener { _, keyCode, event ->
                if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    updatePhotoListFromInput()
                    true
                } else {
                    false
                }
            }
            lifecycleScope.launch {
                adapter.loadStateFlow
                    .distinctUntilChangedBy { it.refresh }
                    .filter { it.refresh is LoadState.NotLoading }
                    .collect { binding.list.scrollToPosition(0) }
            }
        }
    }


    private fun updatePhotoListFromInput() {
        binding.searchPhoto.text.trim().let {
            if (it.isNotEmpty()) {
                search(it.toString())
            }
        }
    }

    private fun search(query: String?) {
        searchJob?.cancel()
        query?.let {
            searchJob = lifecycleScope.launch {
                viewModel.searchPhoto(query).collectLatest {
                    adapter.submitData(it)
                }
            }
        }
    }

    private fun initAdapter() {
        val header = PhotoLoadStateAdapter { adapter.retry() }
        binding.list.adapter = adapter.withLoadStateHeaderAndFooter(
            header = header,
            footer = PhotoLoadStateAdapter { adapter.retry() }
        )

        adapter.addLoadStateListener { loadState ->

            val isListEmpty = loadState.refresh is LoadState.NotLoading && adapter.itemCount == 0
            showEmptyList(isListEmpty)
            header.loadState = loadState.mediator
                ?.refresh
                ?.takeIf { it is LoadState.Error && adapter.itemCount > 0 }
                ?: loadState.prepend
            binding.list.isVisible =
                loadState.source.refresh is LoadState.NotLoading || loadState.mediator?.refresh is LoadState.NotLoading
            binding.progressBar.isVisible = loadState.mediator?.refresh is LoadState.Loading
            binding.retryButton.isVisible =
                loadState.mediator?.refresh is LoadState.Error && adapter.itemCount == 0
            val errorState = loadState.source.append as? LoadState.Error
                ?: loadState.source.prepend as? LoadState.Error
                ?: loadState.append as? LoadState.Error
                ?: loadState.prepend as? LoadState.Error
            errorState?.let {
                Toast.makeText(
                    this,
                    "\uD83D\uDE28 Wooops ${it.error}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun showEmptyList(show: Boolean) {
        if (show) {
            binding.emptyList.visibility = View.VISIBLE
            binding.list.visibility = View.GONE
        } else {
            binding.emptyList.visibility = View.GONE
            binding.list.visibility = View.VISIBLE
        }
    }

    companion object {
        private const val LAST_SEARCH_QUERY: String = "last_search_query"
        private const val DEFAULT_QUERY = "Android"
    }
}