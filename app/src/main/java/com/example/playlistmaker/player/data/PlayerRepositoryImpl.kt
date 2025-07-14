package com.example.playlistmaker.player.data

import android.media.MediaPlayer
import com.example.playlistmaker.data.converters.TrackDbConverter
import com.example.playlistmaker.data.db.AppDatabase
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.player.domain.api.PlayerRepository
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerRepositoryImpl(
    private val mediaPlayer: MediaPlayer,
    private val appDatabase: AppDatabase,
    private val trackDbConverter: TrackDbConverter
): PlayerRepository {
    override fun initMediaPlayer(uri: String, onReady: () -> Unit, onComplete: () -> Unit) {
        mediaPlayer.setDataSource(uri)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            mediaPlayer.setOnCompletionListener { onComplete() }
            onReady()
        }
    }

    override fun play() {
        mediaPlayer.start()
    }

    override fun pause() {
        mediaPlayer.pause()
    }

    override suspend fun isFavorite(id: Int): Boolean {
        return appDatabase.trackDao().inFavorite(id)
    }

    override suspend fun addToFavorite(track: Track) {
        appDatabase.trackDao().insertTracks(listOf(trackDbConverter.map(track)))
    }

    override suspend fun removeFromFavorite(id: Int) {
        appDatabase.trackDao().deleteById(id)
    }

    override fun getPlaybackTimer(): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault())
            .format(mediaPlayer.currentPosition)
    }

    override fun release() {
        mediaPlayer.release()
    }
}