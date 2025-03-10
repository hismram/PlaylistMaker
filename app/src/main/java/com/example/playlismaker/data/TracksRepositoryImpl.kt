package com.example.playlismaker.data

import com.example.playlismaker.data.dto.TracksClearHistoryRequest
import com.example.playlismaker.data.dto.TracksHistoryRequest
import com.example.playlismaker.data.dto.TracksHistoryResponse
import com.example.playlismaker.data.dto.TracksSearchRequest
import com.example.playlismaker.data.dto.TracksSearchResponse
import com.example.playlismaker.data.dto.TracksSetHistoryRequest
import com.example.playlismaker.data.local.SharedPreferencesClient
import com.example.playlismaker.domain.api.TracksRepository
import com.example.playlismaker.domain.models.Track
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
        val response = networkClient.doRequest(TracksSearchRequest(expression))

        if (response.resultCode == 200) {
            return (response as TracksSearchResponse).results.map {
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

        preferencesClient.doRequest(TracksSetHistoryRequest(historyId, jsonList))
    }

    override fun clearHistory() {
        preferencesClient.doRequest(TracksClearHistoryRequest(historyId))
    }

    private fun readHistory(): ArrayList<Track> {
        val response = preferencesClient.doRequest(TracksHistoryRequest(historyId))

        return if (response is TracksHistoryResponse) {
            Gson().fromJson(
                response.results,
                Array<Track>::class.java
            ).toCollection(ArrayList())
        } else {
            ArrayList()
        }
    }
}