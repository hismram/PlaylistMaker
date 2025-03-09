package com.example.playlismaker.domain.api

import com.example.playlismaker.domain.models.Track

interface TracksInteractor {
    fun search(expression: String, consumer: TracksConsumer)

    fun getHistory(consumer: TracksConsumer)

    fun addToHistory(track: Track)

    fun clearHistory()

    interface TracksConsumer {
        fun consume(tracks: List<Track>)
        fun error()
    }
}