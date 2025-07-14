package com.example.playlistmaker.player.domain.impl

import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.player.domain.api.PlayerInteractor
import com.example.playlistmaker.player.domain.api.PlayerRepository
import com.example.playlistmaker.search.domain.api.TracksRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PlayerInteractorImpl(
    val tracksRepository: TracksRepository,
    val playerRepository: PlayerRepository
) : PlayerInteractor {

    override suspend fun loadTrackData(
        trackId: Int,
        consumer: PlayerInteractor.PlayerConsumer,
        onComplete: () -> Unit
    ) {
        // Запускаем корутину для получения статуса
        CoroutineScope(Dispatchers.IO).launch {
            val history = tracksRepository.searchHistory()
            var track: Track? = null;

            val fromHistory = history.find { it.trackId == trackId }
            val fromFavorite = playerRepository.readFavorite(trackId)

            if (fromHistory != null) {
                track = fromHistory
            } else if (fromFavorite != null) {
                track = fromFavorite
            } else {
                return@launch
            }

            if (track.previewUrl == null) return@launch
            val isFavorite = playerRepository.isFavorite(trackId)
            val updatedTrack = track.copy(isFavorite = isFavorite)

            playerRepository.initMediaPlayer(
                track.previewUrl!!,
                { consumer.consume(updatedTrack) },
                { onComplete() }
            )
        }
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

    override suspend fun addToFavorite(track: Track) {
        playerRepository.addToFavorite(track)
    }

    override suspend fun removeFromFavorite(id: Int) {
        playerRepository.removeFromFavorite(id)
    }
}