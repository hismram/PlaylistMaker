package com.example.playlismaker.player.presentation

import android.media.MediaPlayer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlismaker.App
import com.example.playlismaker.creator.Creator
import com.example.playlismaker.player.domain.api.PlayerInteractor
import com.example.playlismaker.player.domain.model.PlaybackState
import com.example.playlismaker.player.domain.model.PlayerState
import com.example.playlismaker.player.domain.model.TrackData
import com.example.playlismaker.search.domain.model.Track
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(
    trackId: Int,
    playerInteractor: PlayerInteractor
): ViewModel() {
    private lateinit var playerState: PlayerState
    private var playerStateLiveData = MutableLiveData<PlayerState>(null)
    private var mediaPlayer: MediaPlayer? = null

    // Обновление таймера во время воспроизведения
    private val playbackTimerUpdate = object : Runnable {
        override fun run() {
            while (playerState.playbackState == PlaybackState.PLAYING) {
                playerState.timer = SimpleDateFormat("mm:ss", Locale.getDefault())
                    .format(mediaPlayer?.currentPosition)

                playerStateLiveData.postValue(playerState)
                Thread.sleep(TIMER_UPDATE_DELAY)
            }
        }
    }


    init {
        // Получение данных трека и иницализация плеера
        playerInteractor.loadTrackData(
            trackId,
            object : PlayerInteractor.PlayerConsumer {
                override fun consume(
                    track: Track
                ) {
                    mediaPlayer = initMediaPlayer(track.previewUrl) {
                        playerState = PlayerState(
                            favorite = false,
                            inLibrary = false,
                            playbackState = PlaybackState.PREPARED,
                            timer = TIMER_ZERO,
                            trackData = TrackData(
                                name = track.trackName,
                                artist = track.artistName,
                                album = track.collectionName,
                                genre = track.primaryGenreName,
                                year = track.getReleaseDate(),
                                country = track.country,
                                cover = track.getLargeArtwork(),
                                duraration = track.getTrackTime()
                            )
                        )

                        playerStateLiveData.postValue(playerState)
                    }
                }

                override fun error() {}
            }
        )
    }

    private fun initMediaPlayer(previewUrl: String?, onPrepared: () -> Unit): MediaPlayer {
        val mediaPlayer = MediaPlayer()

        if (previewUrl != null) {
            try {
                mediaPlayer.setDataSource(previewUrl)
                mediaPlayer.prepareAsync()
                mediaPlayer.setOnPreparedListener{ onPrepared() }
                mediaPlayer.setOnCompletionListener {
                    playerState.playbackState = PlaybackState.PAUSED
                    playerState.timer = TIMER_ZERO
                    playerStateLiveData.postValue(playerState)
                }
            } catch(e: Exception) {
                e.printStackTrace()
            }
        }

        return mediaPlayer
    }

    // Запуск плеера
    private fun startPlayer() {
        mediaPlayer?.start()
        Thread(playbackTimerUpdate).start()
        playerState.playbackState = PlaybackState.PLAYING
        playerStateLiveData.postValue(playerState)
    }

    // Остановка плеера
    private fun pausePlayer() {
        mediaPlayer?.pause()
        playerState.playbackState = PlaybackState.PAUSED
        playerStateLiveData.postValue(playerState)
    }

    fun getPlayerStateLiveData(): LiveData<PlayerState> = playerStateLiveData

    // Переключение состояния плеера
    fun togglePlay() {
        if (playerState.playbackState == PlaybackState.PAUSED ||
            playerState.playbackState == PlaybackState.PREPARED
        ) {
            startPlayer()
        } else {
            pausePlayer()
        }
    }

    fun toggleFavorite() {
        playerState.favorite = !(playerState.favorite)
        playerStateLiveData.postValue(playerState)
    }

    fun toggleInLibrary() {
        playerState.inLibrary = playerState.inLibrary
        playerStateLiveData.postValue(playerState)
    }

    // Освобождает плеер
    fun releaseMediaPlayer() {
        // Останавливем воспроизмедение что-бы избежать обращение к плееру в параллельном потоке
        playerState.playbackState = PlaybackState.PAUSED
        playerStateLiveData.postValue(playerState)
        mediaPlayer?.release()
    }

    companion object {
        fun getViewModelFactory(trackId: Int): ViewModelProvider.Factory = viewModelFactory {
            initializer {

                val interactor = Creator.providePlayerInteractor((this[APPLICATION_KEY] as App))

                PlayerViewModel(
                    trackId,
                    interactor
                )
            }
        }

        const val TIMER_UPDATE_DELAY = 500L
        const val TIMER_ZERO = "00.00"
    }
}