package com.example.playlismaker.player.domain.model

data class PlayerState(
    var favorite: Boolean,
    var inLibrary: Boolean,
    var playbackState: PlaybackState,
    var timer: String,
    val trackData: TrackData
)
