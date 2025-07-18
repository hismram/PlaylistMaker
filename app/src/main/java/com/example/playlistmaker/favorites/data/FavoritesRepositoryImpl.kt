package com.example.playlistmaker.favorites.data

import com.example.playlistmaker.data.converters.TrackDbConverter
import com.example.playlistmaker.data.db.dao.TrackDao
import com.example.playlistmaker.data.db.entity.TrackEntity
import com.example.playlistmaker.favorites.domain.api.FavoritesRepository
import com.example.playlistmaker.domain.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FavoritesRepositoryImpl(
    private val trackDao: TrackDao,
    private val trackDbConverter: TrackDbConverter
): FavoritesRepository {
    override fun favoriteTracks(): Flow<List<Track>> = flow {
        val tracks = trackDao.getTracks()

        emit(converterFromTrackEntity(tracks))
    }

    private fun converterFromTrackEntity(tracks: List<TrackEntity>): List<Track> {
        return tracks.map { track -> trackDbConverter.map(track) }
    }
}