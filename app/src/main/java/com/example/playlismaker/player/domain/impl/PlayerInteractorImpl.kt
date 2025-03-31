package com.example.playlismaker.player.domain.impl

import com.example.playlismaker.player.domain.api.PlayerInteractor
import com.example.playlismaker.search.domain.api.TracksRepository

class PlayerInteractorImpl(
    val tracksRepository: TracksRepository
) : PlayerInteractor {

    override fun loadTrackData(
        trackId: Int,
        consumer: PlayerInteractor.PlayerConsumer
    ) {
        val history = tracksRepository.searchHistory()
        val track = history.find { it.trackId == trackId }

        if (track == null) {
            return
        }

        consumer.consume(track)
    }
}