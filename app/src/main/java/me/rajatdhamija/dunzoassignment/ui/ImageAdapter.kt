package me.rajatdhamija.dunzoassignment.ui

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import me.rajatdhamija.dunzoassignment.R

class ImageAdapter : PagingDataAdapter<UiModel, RecyclerView.ViewHolder>(UIMODEL_COMPARATOR) {
    companion object {
        private val UIMODEL_COMPARATOR = object : DiffUtil.ItemCallback<UiModel>() {
            override fun areItemsTheSame(oldItem: UiModel, newItem: UiModel): Boolean {
                return (oldItem is UiModel.PhotoItem && newItem is UiModel.PhotoItem &&
                        oldItem.photo.id == newItem.photo.id) ||
                        (oldItem is UiModel.SeparatorItem && newItem is UiModel.SeparatorItem &&
                                oldItem.description == newItem.description)
            }

            override fun areContentsTheSame(oldItem: UiModel, newItem: UiModel): Boolean =
                oldItem == newItem
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val uiModel = getItem(position)
        uiModel.let {
            (holder as PhotoViewHolder).bind((uiModel as UiModel.PhotoItem).photo)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.item_photo
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return PhotoViewHolder.create(parent)
    }
}

