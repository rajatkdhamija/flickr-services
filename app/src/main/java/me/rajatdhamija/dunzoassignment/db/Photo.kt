package me.rajatdhamija.dunzoassignment.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import me.rajatdhamija.dunzoassignment.Constants

@Entity(tableName = "photos")
data class Photo(
    @PrimaryKey @field:SerializedName(Constants.ID) val id: String,
    @field:SerializedName(Constants.OWNER) val owner: String,
    @field:SerializedName(Constants.SECRET) val secret: String,
    @field:SerializedName(Constants.SERVER) val server: String,
    @field:SerializedName(Constants.FARM) val farm: Int,
    @field:SerializedName(Constants.TITLE) val title: String,
    @field:SerializedName(Constants.IS_PUBLIC) val isPublic: Int,
    @field:SerializedName(Constants.IS_FRIEND) val isFriend: Int,
    @field:SerializedName(Constants.IS_FAMILY) val isFamily: Int
)