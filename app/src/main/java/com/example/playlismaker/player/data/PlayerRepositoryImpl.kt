package com.example.playlismaker.player.data

import com.example.playlismaker.data.dto.GetStringValueRequest
import com.example.playlismaker.data.dto.GetStringValueResponse
import com.example.playlismaker.data.dto.Response
import com.example.playlismaker.data.dto.ResponseCodes
import com.example.playlismaker.data.local.SharedPreferencesClient
import com.example.playlismaker.player.domain.api.PlayerRepository
import com.example.playlismaker.search.data.dto.HistoryRequest
import com.example.playlismaker.search.data.dto.HistoryResponse
import com.example.playlismaker.search.domain.model.Track
import com.google.gson.Gson

class PlayerRepositoryImpl(
    val historyId: String,
    val preferencesClient: SharedPreferencesClient
) : PlayerRepository {
    override fun getTrack(id: Int) {
        val response = preferencesClient.doRequest(GetStringValueRequest(historyId))
        var list =  listOf<Track>();

        if (response is GetStringValueResponse) {
            list = Gson().fromJson(
                response.results,
                Array<Track>::class.java
            ).toCollection(ArrayList())
        }

        list.find { it.trackId == id }
    }
}