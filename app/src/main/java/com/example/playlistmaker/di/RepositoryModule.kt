package com.example.playlistmaker.di

import com.example.playlistmaker.data.converters.TrackDbConverter
import com.example.playlistmaker.favorites.data.FavoritesRepositoryImpl
import com.example.playlistmaker.favorites.domain.api.FavoritesRepository
import com.example.playlistmaker.player.data.PlayerRepositoryImpl
import com.example.playlistmaker.player.domain.api.PlayerRepository
import com.example.playlistmaker.search.data.TracksRepositoryImpl
import com.example.playlistmaker.search.domain.api.TracksRepository
import com.example.playlistmaker.settings.data.SettingsRepositoryImpl
import com.example.playlistmaker.settings.domain.api.SettingsRepository
import org.koin.dsl.module

const val SEARCH_HISTORY_STORAGE_ID = "search_history"

val repositoryModule = module {
    factory<TrackDbConverter> { TrackDbConverter() }

    factory<PlayerRepository> {
        PlayerRepositoryImpl(get(), get(), get())
    }

    factory<TracksRepository> {
        TracksRepositoryImpl(SEARCH_HISTORY_STORAGE_ID, get(), get(), get())
    }

    single<SettingsRepository> {
        SettingsRepositoryImpl(get())
    }

    single<FavoritesRepository> {
        FavoritesRepositoryImpl(get(), get())
    }
}