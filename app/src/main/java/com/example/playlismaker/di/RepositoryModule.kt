package com.example.playlismaker.di

import com.example.playlismaker.player.data.PlayerRepositoryImpl
import com.example.playlismaker.player.domain.api.PlayerRepository
import com.example.playlismaker.search.data.TracksRepositoryImpl
import com.example.playlismaker.search.domain.api.TracksRepository
import com.example.playlismaker.settings.data.SettingsRepositoryImpl
import com.example.playlismaker.settings.domain.api.SettingsRepository
import org.koin.dsl.module

const val SEARCH_HISTORY_STORAGE_ID = "search_history"

val repositoryModule = module {
    factory<PlayerRepository> {
        PlayerRepositoryImpl(get())
    }

    factory<TracksRepository> {
        TracksRepositoryImpl(SEARCH_HISTORY_STORAGE_ID, get(), get(), get())
    }

    single<SettingsRepository> {
        SettingsRepositoryImpl(get())
    }
}