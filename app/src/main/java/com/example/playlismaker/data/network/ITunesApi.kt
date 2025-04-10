package com.example.playlismaker.data.network

import com.example.playlismaker.search.data.dto.SearchResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ITunesApi {
    @GET("/search?entity=song")
    fun search(@Query("term") searchString: String): Call<SearchResponse>
}