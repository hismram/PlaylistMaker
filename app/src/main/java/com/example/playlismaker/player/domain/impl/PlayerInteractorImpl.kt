package com.example.playlismaker.player.domain.impl

import com.example.playlismaker.player.domain.api.PlayerInteractor
import com.example.playlismaker.player.domain.api.PlayerRepository
import com.example.playlismaker.search.domain.api.TracksRepository

class PlayerInteractorImpl(
    val tracksRepository: TracksRepository,
    val playerRepository: PlayerRepository
) : PlayerInteractor {

    override fun loadTrackData(
        trackId: Int,
        consumer: PlayerInteractor.PlayerConsumer,
        onComplete: () -> Unit
    ) {
        val history = tracksRepository.searchHistory()
        val track = history.find { it.trackId == trackId }

        if (track == null || track.previewUrl == null) {
            return
        }

        playerRepository.initMediaPlayer(track.previewUrl, {
            consumer.consume(track)
        }, {
            onComplete()
        })
    }

    override fun getPlaybackTimer(): String {
        return playerRepository.getPlaybackTimer()
    }

    override fun play() {
        playerRepository.play()
    }

    override fun pause() {
        playerRepository.pause()
    }

    override fun release() {
        playerRepository.release()
    }
}