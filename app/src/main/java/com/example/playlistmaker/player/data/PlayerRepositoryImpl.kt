package com.example.playlistmaker.player.data

import android.media.MediaPlayer
import com.example.playlistmaker.data.converters.TrackDbConverter
import com.example.playlistmaker.data.db.dao.TrackDao
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.player.domain.api.PlayerRepository
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerRepositoryImpl(
    private val mediaPlayer: MediaPlayer,
    private val trackDao: TrackDao,
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
        return trackDao.inFavorite(id)
    }

    override suspend fun addToFavorite(track: Track) {
        trackDao.insertTracks(listOf(trackDbConverter.map(track)))
    }

    override suspend fun removeFromFavorite(id: Int) {
        trackDao.deleteById(id)
    }

    override suspend fun readFavorite(id: Int): Track? {
        val trackEntity = trackDao.read(id);

        return if (trackEntity != null) {
            trackDbConverter.map(trackEntity)
        } else {
            null
        }
    }

    override fun getPlaybackTimer(): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault())
            .format(mediaPlayer.currentPosition)
    }

    override fun release() {
        mediaPlayer.release()
    }
}