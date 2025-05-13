package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.domain.model.Track

interface TracksRepository {
    fun search(expression: String): List<Track>

    fun searchHistory(): List<Track>

    fun addToHistory(track: Track)

    fun clearHistory()
}