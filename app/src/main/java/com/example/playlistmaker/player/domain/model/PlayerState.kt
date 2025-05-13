package com.example.playlistmaker.player.domain.model

data class PlayerState(
    var favorite: Boolean,
    var inLibrary: Boolean,
    var playbackState: PlaybackState,
    var timer: String,
    val trackData: TrackData
)
