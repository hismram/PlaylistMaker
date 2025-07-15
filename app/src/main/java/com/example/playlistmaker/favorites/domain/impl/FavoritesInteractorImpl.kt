package com.example.playlistmaker.favorites.domain.impl

import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.favorites.domain.api.FavoritesInteractor
import com.example.playlistmaker.favorites.domain.api.FavoritesRepository
import kotlinx.coroutines.flow.Flow

class FavoritesInteractorImpl(
    private val favoritesRepository: FavoritesRepository
): FavoritesInteractor {
    override fun favoriteTracks(): Flow<List<Track>> {
        return favoritesRepository.favoriteTracks()
    }
}