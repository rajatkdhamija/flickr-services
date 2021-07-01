package me.rajatdhamija.dunzoassignment.ui

import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter

class PhotoLoadStateAdapter(
    private val retry: () -> Unit
) : LoadStateAdapter<PhotoLoadStateViewHolder>() {
    override fun onBindViewHolder(holder: PhotoLoadStateViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState
    ): PhotoLoadStateViewHolder {
        return PhotoLoadStateViewHolder.create(parent, retry)
    }
}
