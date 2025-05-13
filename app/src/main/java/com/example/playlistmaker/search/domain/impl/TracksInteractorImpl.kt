package com.example.playlistmaker.search.domain.impl

import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.search.domain.api.TracksInteractor
import com.example.playlistmaker.search.domain.api.TracksRepository
import java.io.IOException
import java.util.concurrent.Executor

class TracksInteractorImpl(
    private val tracksRepository: TracksRepository,
    private val executor: Executor
) : TracksInteractor {
    override fun search(expression: String, consumer: TracksInteractor.TracksConsumer) {
        executor.execute {
            try {
                consumer.consume(tracksRepository.search(expression))
            } catch (_: IOException) {
                consumer.error()
            }
        }
    }

    override fun getHistory(consumer: TracksInteractor.TracksConsumer) {
        consumer.consume(tracksRepository.searchHistory())
    }

    override fun addToHistory(track: Track) {
        tracksRepository.addToHistory(track)
    }

    override fun clearHistory() {
        tracksRepository.clearHistory()
    }
}