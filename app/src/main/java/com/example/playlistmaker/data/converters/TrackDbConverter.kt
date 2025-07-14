package com.example.playlistmaker.data.converters

import com.example.playlistmaker.data.db.entity.TrackEntity
import com.example.playlistmaker.domain.model.Track

class TrackDbConverter {

    fun map(track: Track): TrackEntity {
        return TrackEntity(
            track.trackId,
            track.trackName,
            track.collectionName,
            track.artistName,
            track.releaseDate,
            track.primaryGenreName,
            track.country,
            track.artworkUrl100,
            track.previewUrl,
            track.trackTimeMillis
        )
    }

    fun map(track: TrackEntity): Track {
        return Track(
            track.trackId,
            track.trackName,
            track.collectionName,
            track.artistName,
            track.releaseDate,
            track.primaryGenreName,
            track.country,
            track.artworkUrl100,
            track.previewUrl,
            track.trackTimeMillis
        )
    }

}