package com.example.playlistmaker.search.domain.impl

import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.search.domain.api.TracksInteractor
import com.example.playlistmaker.search.domain.api.TracksRepository
import kotlinx.coroutines.flow.Flow

class TracksInteractorImpl(
    private val tracksRepository: TracksRepository
) : TracksInteractor {
    override fun search(expression: String): Flow<Result<List<Track>>> {
        return tracksRepository.search(expression)
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