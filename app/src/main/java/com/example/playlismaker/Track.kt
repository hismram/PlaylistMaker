package com.example.playlismaker

data class Track(
    val trackName: String?,
    val artistName: String?,
    val trackTime: String? = null,
    val artworkUrl100: String?,
    val trackTimeMillis: Int? = null
)
