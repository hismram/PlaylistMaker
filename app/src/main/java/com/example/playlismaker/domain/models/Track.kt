package com.example.playlismaker.domain.models

import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class Track(
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
) {
    fun toJSON(): String {
        return Gson().toJson(this)
    }

    fun getReleaseDate(): String {
        var date = ""
        if (releaseDate != null) {
            date = SimpleDateFormat("yyyy", Locale.getDefault()).format(releaseDate)
        }

        return date
    }

    fun getTrackTime(): String {
        var trackTime = ""

        if (trackTimeMillis != null) {
            trackTime = SimpleDateFormat(
                "mm:ss", Locale.getDefault()
            ).format(this.trackTimeMillis)
        }

        return trackTime
    }

    fun getLargeArtwork(): String {
        var uri = ""

        if (artworkUrl100 != null) {
            uri = artworkUrl100.replaceAfterLast('/', "512x512bb.jpg")
        }

        return uri
    }

    companion object {
        fun fromJSON(data: String): Track {
            return Gson().fromJson(data, Track::class.java)
        }
    }
}
