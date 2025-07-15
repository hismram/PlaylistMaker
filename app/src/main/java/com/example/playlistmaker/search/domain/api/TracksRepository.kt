package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface TracksRepository {
    fun search(expression: String): Flow<Result<List<Track>>>

    fun searchHistory(): List<Track>

    fun addToHistory(track: Track)

    fun clearHistory()
}