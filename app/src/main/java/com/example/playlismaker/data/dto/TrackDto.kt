package com.example.playlismaker.data.dto

import java.util.Date

data class TrackDto(
    val trackId: Int,
    val trackName: String?,
    val collectionName: String? = null,
    val artistName: String?,
    val releaseDate: Date?,
    val primaryGenreName: String?,
    val country: String?,
    val artworkUrl100: String?,
    val previewUrl: String?,
    val trackTimeMillis: Int? = null
)