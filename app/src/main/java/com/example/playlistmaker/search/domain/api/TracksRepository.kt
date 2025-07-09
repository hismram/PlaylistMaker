package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface TracksRepository {
    fun search(expression: String): Flow<Pair<List<Track>?, String?>>

    fun searchHistory(): List<Track>

    fun addToHistory(track: Track)

    fun clearHistory()
}