package com.example.playlismaker.search.domain.impl

import com.example.playlismaker.search.domain.model.Track
import com.example.playlismaker.search.domain.api.TracksInteractor
import com.example.playlismaker.search.domain.api.TracksRepository
import java.io.IOException
import java.util.concurrent.Executors

class TracksInteractorImpl(private val tracksRepository: TracksRepository) : TracksInteractor {
    private val executor = Executors.newCachedThreadPool()

    override fun search(expression: String, consumer: TracksInteractor.TracksConsumer) {
        executor.execute {
            try {
                consumer.consume(tracksRepository.search(expression))
            } catch (e: IOException) {
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