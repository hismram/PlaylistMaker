package com.example.playlistmaker.search.data

import com.example.playlistmaker.data.NetworkClient
import com.example.playlistmaker.data.PreferencesClient
import com.example.playlistmaker.data.dto.GetStringValueRequest
import com.example.playlistmaker.data.dto.GetStringValueResponse
import com.example.playlistmaker.data.dto.SetStringValueRequest
import com.example.playlistmaker.search.domain.api.TracksRepository
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.search.data.dto.*
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException

class TracksRepositoryImpl(
    private val historyId: String,
    private val gson: Gson,
    private val networkClient: NetworkClient,
    private val preferencesClient: PreferencesClient
) : TracksRepository {
    private val historyList: ArrayList<Track>

    init {
        historyList = readHistory()
    }

    override fun search(expression: String): Flow<Pair<List<Track>?, String?>> = flow {
        try {
            val response = networkClient.doRequest(SearchRequest(expression))

            if (response.resultCode == 200) {
                with (response as SearchResponse) {
                    val data = response.results.map {
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
                    emit(Pair(data, null))
                }
            } else {
                emit(Pair(emptyList(), null))
            }
        } catch (e: IOException) {
            emit(Pair(null, e.message))
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

        val jsonList = gson.toJson(historyList)

        preferencesClient.doRequest(SetStringValueRequest(historyId, jsonList))
    }

    override fun clearHistory() {
        preferencesClient.doRequest(SetStringValueRequest(historyId, EMPTY_LIST))
    }

    private fun readHistory(): ArrayList<Track> {
        val response = preferencesClient.doRequest(GetStringValueRequest(historyId))

        return if (response is GetStringValueResponse) {
            gson.fromJson(
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