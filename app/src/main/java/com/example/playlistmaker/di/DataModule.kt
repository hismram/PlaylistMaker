package com.example.playlistmaker.di

import android.content.Context
import android.media.MediaPlayer
import androidx.room.Room
import com.example.playlistmaker.App
import com.example.playlistmaker.data.NetworkClient
import com.example.playlistmaker.data.PreferencesClient
import com.example.playlistmaker.data.db.AppDatabase
import com.example.playlistmaker.data.local.SharedPreferencesClient
import com.example.playlistmaker.data.network.ITunesApi
import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.sharing.data.ExternalNavigatorImpl
import com.example.playlistmaker.sharing.domain.api.ExternalNavigator
import com.google.gson.Gson
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.Executor
import java.util.concurrent.Executors

const val I_TUNES_ADDRESS = "https://itunes.apple.com/"
const  val PREFERENCES_STORAGE_ID = "playlistmaker_storage"

val dataModule = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            "database.db"
        ).build().trackDao()
    }

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
