package me.rajatdhamija.dunzoassignment.model

import com.google.gson.annotations.SerializedName
import me.rajatdhamija.dunzoassignment.Constants
import me.rajatdhamija.dunzoassignment.db.Photo

data class FlickrSearchResponse(
    @SerializedName(Constants.PHOTOS) val photos: Photos,
    @SerializedName(Constants.STAT) val stat: String
)

data class Photos(
    @SerializedName(Constants.PAGE) val page: Int,
    @SerializedName(Constants.PAGES) val pages: Int,
    @SerializedName(Constants.PER_PAGE) val per_page: Int,
    @SerializedName(Constants.TOTAL) val total: Int,
    @SerializedName(Constants.PHOTO) val photos: List<Photo>
)