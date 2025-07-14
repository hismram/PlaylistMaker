package com.example.playlistmaker.player.domain.api

import com.example.playlistmaker.domain.model.Track

interface PlayerInteractor {
    suspend fun loadTrackData(
        trackId: Int,
        consumer: PlayerConsumer,
        onCompleteListener: () -> Unit
    )
    fun getPlaybackTimer(): String
    fun play()
    fun pause()
    fun release()
    suspend fun addToFavorite(track: Track)
    suspend fun removeFromFavorite(id: Int)


    interface PlayerConsumer {
        fun consume(track: Track)
        fun error()
    }
}