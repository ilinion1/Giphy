package com.gerija.giphy.data.api

import com.gerija.giphy.data.api.dto.GifsContainer
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("trending")
    suspend fun getGifsAreTrending(
        @Query("api_key") apiKey: String = API_KEY,
        @Query("limit") limit: Int = LIMIT,
        @Query("offset") offset: Int,
        @Query("rating") rating: String = RATING,
    ): GifsContainer

    @GET("search")
    suspend fun getSearchGifs(
        @Query("api_key") api: String = API_KEY,
        @Query("q") field: String,
        @Query("limit") limit: Int = LIMIT,
        @Query("offset") offset: Int,
        @Query("rating") rating: String = RATING,
        @Query("lang") lang: String = LANG
    ): GifsContainer

    companion object{
        private const val API_KEY = "U6BGrK0dzxJPCZy8fi2Bl0LuEeyVG5oW"
        private const val LIMIT = 26
        private const val RATING = "g"
        private const val LANG = "en"
    }
}