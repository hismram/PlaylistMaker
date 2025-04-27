package com.example.playlismaker.di

import android.content.Context
import android.media.MediaPlayer
import com.example.playlismaker.App
import com.example.playlismaker.data.NetworkClient
import com.example.playlismaker.data.PreferencesClient
import com.example.playlismaker.data.local.SharedPreferencesClient
import com.example.playlismaker.data.network.ITunesApi
import com.example.playlismaker.data.network.RetrofitNetworkClient
import com.example.playlismaker.sharing.data.ExternalNavigatorImpl
import com.example.playlismaker.sharing.domain.api.ExternalNavigator
import com.google.gson.Gson
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module;
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.Executor
import java.util.concurrent.Executors

const val I_TUNES_ADDRESS = "https://itunes.apple.com/"
const  val PREFERENCES_STORAGE_ID = "playlistmaker_storage"

val dataModule = module {
    single<ITunesApi> {
        Retrofit.Builder()
            .baseUrl(I_TUNES_ADDRESS)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ITunesApi::class.java)
    }

    factory<Executor> {
        Executors.newCachedThreadPool()
    }

    single<Gson> {
        Gson()
    }

    factory<MediaPlayer> {
        MediaPlayer()
    }

    single<PreferencesClient> {
        SharedPreferencesClient(
            androidContext().getSharedPreferences(PREFERENCES_STORAGE_ID, Context.MODE_PRIVATE)
        )
    }

    single<ExternalNavigator> {
        ExternalNavigatorImpl(androidApplication() as App)
    }

    single<NetworkClient> {
        RetrofitNetworkClient(get())
    }
}
