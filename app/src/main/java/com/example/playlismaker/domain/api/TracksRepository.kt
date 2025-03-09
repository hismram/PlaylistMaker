package com.example.playlismaker.domain.api

import com.example.playlismaker.domain.models.Track

interface TracksRepository {
    fun search(expression: String): List<Track>

    fun searchHistory(): List<Track>

    fun addToHistory(track: Track)

    fun clearHistory()
}