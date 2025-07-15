package com.example.playlistmaker.favorites.domain.api

import com.example.playlistmaker.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface FavoritesRepository {
    fun favoriteTracks(): Flow<List<Track>>
}