package me.rajatdhamija.dunzoassignment.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import me.rajatdhamija.dunzoassignment.R
import me.rajatdhamija.dunzoassignment.db.Photo

class PhotoViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val ivImage: AppCompatImageView = view.findViewById(R.id.ivImage)
    private var photo: Photo? = null


    init {
        view.setOnClickListener {

        }
    }

    fun bind(photo: Photo?) {
        photo?.let {
            val url = itemView.context.getString(R.string.url)
            Glide.with(itemView.context)
                .load(String.format(url, it.farm, it.server, it.id, it.secret))
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .into(ivImage)
        }
    }

    companion object {
        fun create(parent: ViewGroup): PhotoViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_photo, parent, false)
            return PhotoViewHolder(view)
        }
    }
}