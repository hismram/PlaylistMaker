package com.example.playlismaker.domain.impl

import com.example.playlismaker.domain.api.TracksInteractor
import com.example.playlismaker.domain.api.TracksRepository
import com.example.playlismaker.domain.models.Track
import java.util.concurrent.Executors
import java.io.IOException

class TracksInteractorImpl(private val repository: TracksRepository) : TracksInteractor {
    private val executor = Executors.newCachedThreadPool()

    override fun search(expression: String, consumer: TracksInteractor.TracksConsumer) {
        executor.execute {
            try {
                consumer.consume(repository.search(expression))
            } catch (e: IOException) {
                consumer.error()
            }
        }
    }

    override fun getHistory(consumer: TracksInteractor.TracksConsumer) {
        consumer.consume(repository.searchHistory())
    }

    override fun addToHistory(track: Track) {
        repository.addToHistory(track)
    }

    override fun clearHistory() {
        repository.clearHistory()
    }
}