package com.example.playlistmaker.di

import com.example.playlistmaker.App
import com.example.playlistmaker.player.domain.api.PlayerInteractor
import com.example.playlistmaker.player.domain.impl.PlayerInteractorImpl
import com.example.playlistmaker.search.domain.api.TracksInteractor
import com.example.playlistmaker.search.domain.impl.TracksInteractorImpl
import com.example.playlistmaker.settings.domain.api.SettingsInteractor
import com.example.playlistmaker.settings.domain.impl.SettingsInteractorImpl
import com.example.playlistmaker.sharing.domain.api.SharingInteractor
import com.example.playlistmaker.sharing.domain.impl.SharingInteractorImpl
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val interactorModule = module {
    factory<TracksInteractor> {
        TracksInteractorImpl(get(), get())
    }

    factory<SettingsInteractor> {
        SettingsInteractorImpl(androidApplication() as App, get())
    }

    factory<SharingInteractor> {
        SharingInteractorImpl(androidApplication() as App, get())
    }

    factory<PlayerInteractor> {
        PlayerInteractorImpl(get(), get())
    }
}