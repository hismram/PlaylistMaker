package com.example.playlismaker.data.network

import com.example.playlismaker.data.dto.TracksSearchResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ITunesApi {
    @GET("/search?entity=song")
    fun search(@Query("term") searchString: String): Call<TracksSearchResponse>
}