package com.example.playlismaker.player.data

import android.media.MediaPlayer
import com.example.playlismaker.player.domain.api.PlayerRepository
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerRepositoryImpl: PlayerRepository {
    private val mediaPlayer: MediaPlayer = MediaPlayer()

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

    override fun getPlaybackTimer(): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault())
            .format(mediaPlayer.currentPosition)
    }

    override fun release() {
        mediaPlayer.release()
    }

}