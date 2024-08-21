package com.example.playlismaker.service

import com.example.playlismaker.Track
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ITunesService {
    private val baseUrl = "https://itunes.apple.com/"

    private val service = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ITunesApi::class.java)

    fun search(
        searchString: String,
        callback: (List<Track>?) -> Unit,
        errback: (String) -> Unit
    ) {
        service.search(searchString).enqueue(object : Callback<SearchResponse> {
            override fun onResponse(
                call: Call<SearchResponse>,
                response: Response<SearchResponse>
            ) {
                when (response.code()) {
                    200 -> callback(response.body()?.results)
                }
            }

            override fun onFailure(call: Call<SearchResponse>, t: Throwable) {
                errback(t.message.toString())
            }

        })
    }
}
