package com.example.playlismaker

import android.content.SharedPreferences
import com.example.playlismaker.data.SettingsRepositoryImpl
import com.example.playlismaker.data.TracksRepositoryImpl
import com.example.playlismaker.data.local.SharedPreferencesClient
import com.example.playlismaker.data.network.RetrofitNetworkClient
import com.example.playlismaker.domain.api.SettingsInteractor
import com.example.playlismaker.domain.api.SettingsRepository
import com.example.playlismaker.domain.api.TracksInteractor
import com.example.playlismaker.domain.api.TracksRepository
import com.example.playlismaker.domain.impl.SettingsInteractorImpl
import com.example.playlismaker.domain.impl.TracksInteractorImpl

object Creator {
    private fun getTracksRepository(historyId: String, sharedPref: SharedPreferences): TracksRepository {
        return TracksRepositoryImpl(
            historyId,
            RetrofitNetworkClient(),
            SharedPreferencesClient(sharedPref)
        )
    }

    private fun getSettingsRepository(sharedPref: SharedPreferences): SettingsRepository {
        return SettingsRepositoryImpl(SharedPreferencesClient(sharedPref))
    }

    fun provideTracksInteractor(historyId: String, sharedPref: SharedPreferences): TracksInteractor {
        return TracksInteractorImpl(getTracksRepository(historyId, sharedPref))
    }

    fun provideSettingsInteractor(sharedPref: SharedPreferences): SettingsInteractor {
        return SettingsInteractorImpl(getSettingsRepository(sharedPref))
    }
}