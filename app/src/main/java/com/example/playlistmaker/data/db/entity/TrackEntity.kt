package com.example.playlistmaker.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.playlistmaker.data.db.converters.DateConverter
import java.util.Date

@Entity(tableName = "track_table")
@TypeConverters(DateConverter::class)
data class TrackEntity(
    val trackId: Int,
    val trackName: String? = null,
    val collectionName: String? = null,
    val artistName: String? = null,
    val releaseDate: Date? = null,
    val primaryGenreName: String? = null,
    val country: String? = null,
    val artworkUrl100: String? = null,
    val previewUrl: String? = null,
    val trackTimeMillis: Int? = null,
    @PrimaryKey(autoGenerate = true)
    val id: Long? = null
)