package com.example.playlismaker.creator

import android.app.Application
import android.content.SharedPreferences
import com.example.playlismaker.App
import com.example.playlismaker.settings.data.SettingsRepositoryImpl
import com.example.playlismaker.search.data.TracksRepositoryImpl
import com.example.playlismaker.settings.domain.impl.SettingsInteractorImpl
import com.example.playlismaker.search.domain.impl.TracksInteractorImpl
import com.example.playlismaker.data.local.SharedPreferencesClient
import com.example.playlismaker.data.network.ITunesApi
import com.example.playlismaker.data.network.RetrofitNetworkClient
import com.example.playlismaker.player.domain.api.PlayerInteractor
import com.example.playlismaker.player.domain.impl.PlayerInteractorImpl
import com.example.playlismaker.sharing.data.ExternalNavigatorImpl
import com.example.playlismaker.sharing.domain.impl.SharingInteractorImpl
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Creator {
    private fun getTracksRepository(
        historyId: String,
        sharedPref: SharedPreferences
    ): TracksRepositoryImpl {
        val retrofit = Retrofit.Builder()
            .baseUrl(I_TUNES_ADDRESS)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return TracksRepositoryImpl(
            historyId,
            RetrofitNetworkClient(retrofit.create(ITunesApi::class.java)),
            SharedPreferencesClient(sharedPref)
        )
    }

    private fun getSettingsRepository(sharedPref: SharedPreferences): SettingsRepositoryImpl {
        return SettingsRepositoryImpl(SharedPreferencesClient(sharedPref))
    }

    fun provideTracksInteractor(
        application: App
    ): TracksInteractorImpl {
        val sharedPreferences = application.getSharedPreferences(
            App.PREFERENCES_STORAGE_ID,
            Application.MODE_PRIVATE
        )

        return TracksInteractorImpl(
            getTracksRepository(App.SEARCH_HISTORY_STORAGE_ID, sharedPreferences)
        )
    }

    fun provideSettingsInteractor(application: App): SettingsInteractorImpl {
        val sharedPreferences = application.getSharedPreferences(
            App.PREFERENCES_STORAGE_ID,
            Application.MODE_PRIVATE
        )

        return SettingsInteractorImpl(
            application,
            getSettingsRepository(sharedPreferences)
        )
    }

    fun provideSharingInteractor(application: App): SharingInteractorImpl {
        return SharingInteractorImpl(
            application,
            ExternalNavigatorImpl(application)
        )
    }

    fun providePlayerInteractor(application: App): PlayerInteractor {
        val sharedPreferences = application.getSharedPreferences(
            App.PREFERENCES_STORAGE_ID,
            Application.MODE_PRIVATE
        )

        return PlayerInteractorImpl(
            getTracksRepository(App.SEARCH_HISTORY_STORAGE_ID, sharedPreferences)
        )
    }

    private const val I_TUNES_ADDRESS = "https://itunes.apple.com/"
}