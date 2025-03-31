package com.example.playlismaker.search.data

import com.example.playlismaker.data.NetworkClient
import com.example.playlismaker.data.dto.GetStringValueRequest
import com.example.playlismaker.data.dto.GetStringValueResponse
import com.example.playlismaker.data.dto.SetStringValueRequest
import com.example.playlismaker.data.local.SharedPreferencesClient
import com.example.playlismaker.search.domain.api.TracksRepository
import com.example.playlismaker.search.domain.model.Track
import com.example.playlismaker.search.data.dto.*
import com.google.gson.Gson

class TracksRepositoryImpl(
    private val historyId: String,
    private val networkClient: NetworkClient,
    private val preferencesClient: SharedPreferencesClient
) : TracksRepository {
    private val historyList: ArrayList<Track>

    init {
        historyList = readHistory()
    }

    override fun search(expression: String): List<Track> {
        val response = networkClient.doRequest(SearchRequest(expression))

        if (response.resultCode == 200) {
            return (response as SearchResponse).results.map {
                Track(
                    it.trackId,
                    it.trackName,
                    it.collectionName,
                    it.artistName,
                    it.releaseDate,
                    it.primaryGenreName,
                    it.country,
                    it.artworkUrl100,
                    it.previewUrl,
                    it.trackTimeMillis
                )
            }
        } else {
            return emptyList()
        }
    }

    override fun searchHistory(): List<Track> {
        return historyList
    }

    override fun addToHistory(track: Track) {
        historyList.removeIf { it.trackId == track.trackId }

        historyList.add(0, track)

        if (historyList.size > 10) {
            historyList.removeLast()
        }

        val jsonList = Gson().toJson(historyList)

        preferencesClient.doRequest(SetStringValueRequest(historyId, jsonList))
    }

    override fun clearHistory() {
        preferencesClient.doRequest(SetStringValueRequest(historyId, EMPTY_LIST))
    }

    private fun readHistory(): ArrayList<Track> {
        val response = preferencesClient.doRequest(GetStringValueRequest(historyId))

        return if (response is GetStringValueResponse) {
            Gson().fromJson(
                response.results ?: EMPTY_LIST,
                Array<Track>::class.java
            ).toCollection(ArrayList())
        } else {
            ArrayList()
        }
    }

    companion object {
        const val EMPTY_LIST = "[]"
    }
}