package me.rajatdhamija.dunzoassignment.api

import me.rajatdhamija.dunzoassignment.Constants.API_KEY
import me.rajatdhamija.dunzoassignment.Constants.FORMAT
import me.rajatdhamija.dunzoassignment.Constants.METHOD
import me.rajatdhamija.dunzoassignment.Constants.NO_JSON_CALLBACK
import me.rajatdhamija.dunzoassignment.Constants.PAGE
import me.rajatdhamija.dunzoassignment.Constants.PER_PAGE
import me.rajatdhamija.dunzoassignment.Constants.TEXT
import me.rajatdhamija.dunzoassignment.Endpoints.GET_DATA
import me.rajatdhamija.dunzoassignment.model.FlickrSearchResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface FlickrService {

    @GET(GET_DATA)
    suspend fun getData(
        @Query(METHOD) method: String,
        @Query(API_KEY) apiKey: String,
        @Query(TEXT) searchText: String,
        @Query(FORMAT) format: String,
        @Query(NO_JSON_CALLBACK) noJsonCallBack: Int,
        @Query(PER_PAGE) perPage: Int,
        @Query(PAGE) page: Int
    ): FlickrSearchResponse

    companion object {
        private const val BASE_URL = "https://api.flickr.com/"

        fun create(): FlickrService {
            val logger = HttpLoggingInterceptor()
            logger.level = HttpLoggingInterceptor.Level.BODY

            val client = OkHttpClient.Builder()
                .addInterceptor(logger)
                .build()
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(FlickrService::class.java)
        }
    }
}