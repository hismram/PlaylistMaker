package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface TracksInteractor {
    fun search(expression: String): Flow<Pair<List<Track>?, String?>>

    fun getHistory(consumer: TracksConsumer)

    fun addToHistory(track: Track)

    fun clearHistory()

    interface TracksConsumer {
        fun consume(tracks: List<Track>)
        fun error()
    }
}