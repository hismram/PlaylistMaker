package com.example.playlistmaker.player.domain.api

import com.example.playlistmaker.domain.model.Track

interface PlayerRepository {
    fun initMediaPlayer(uri: String, onReady: () -> Unit, onComplete: () -> Unit)
    fun play()
    fun pause()
    suspend fun isFavorite(id: Int): Boolean
    suspend fun addToFavorite(track: Track)
    suspend fun removeFromFavorite(id: Int)
    fun getPlaybackTimer(): String
    fun release()
}