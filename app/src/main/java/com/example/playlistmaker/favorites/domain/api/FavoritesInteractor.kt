package com.example.playlistmaker.favorites.domain.api

import com.example.playlistmaker.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface FavoritesInteractor {
    fun favoriteTracks(): Flow<List<Track>>
}