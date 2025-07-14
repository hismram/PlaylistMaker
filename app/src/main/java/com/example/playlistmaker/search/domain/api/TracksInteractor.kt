package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface TracksInteractor {
    fun search(expression: String): Flow<Result<List<Track>>>

    fun getHistory(consumer: TracksConsumer)

    fun addToHistory(track: Track)

    fun clearHistory()

    interface TracksConsumer {
        fun consume(tracks: List<Track>)
        fun error()
    }
}