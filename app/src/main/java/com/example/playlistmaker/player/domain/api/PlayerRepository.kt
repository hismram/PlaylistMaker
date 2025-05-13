package com.example.playlistmaker.player.domain.api

interface PlayerRepository {
    fun initMediaPlayer(uri: String, onReady: () -> Unit, onComplete: () -> Unit)
    fun play()
    fun pause()
    fun getPlaybackTimer(): String
    fun release()
}