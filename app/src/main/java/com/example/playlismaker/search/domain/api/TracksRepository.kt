package com.example.playlismaker.search.domain.api

import com.example.playlismaker.search.domain.model.Track

interface TracksRepository {
    fun search(expression: String): List<Track>

    fun searchHistory(): List<Track>

    fun addToHistory(track: Track)

    fun clearHistory()
}