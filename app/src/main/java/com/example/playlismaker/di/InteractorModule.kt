package com.example.playlismaker.di

import com.example.playlismaker.App
import com.example.playlismaker.player.domain.api.PlayerInteractor
import com.example.playlismaker.player.domain.impl.PlayerInteractorImpl
import com.example.playlismaker.search.domain.api.TracksInteractor
import com.example.playlismaker.search.domain.impl.TracksInteractorImpl
import com.example.playlismaker.settings.domain.api.SettingsInteractor
import com.example.playlismaker.settings.domain.impl.SettingsInteractorImpl
import com.example.playlismaker.sharing.domain.api.SharingInteractor
import com.example.playlismaker.sharing.domain.impl.SharingInteractorImpl
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