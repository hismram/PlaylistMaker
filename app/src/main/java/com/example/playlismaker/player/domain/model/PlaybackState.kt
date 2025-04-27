package com.example.playlismaker.player.domain.model

sealed class PlaybackState {
    object Playing : PlaybackState()
    object Paused : PlaybackState()
    object Prepared : PlaybackState()
}
