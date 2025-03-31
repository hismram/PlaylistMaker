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
import com.example.playlismaker.search.domain.model.Track
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(
    trackId: Int,
    playerInteractor: PlayerInteractor
): ViewModel() {
    private var trackLiveData = MutableLiveData<Track>(null)
    private var timerLiveData = MutableLiveData(TIMER_ZERO)
    private var favoriteLiveData = MutableLiveData(false)
    private var inLibraryLiveData = MutableLiveData(false)
    private var playbackLiveData = MutableLiveData<Int>(PLAYER_STATE_DEFAULT)
    private var mediaPlayer: MediaPlayer? = null

    // Обновление таймера во время воспроизведения
    private val playbackTimerUpdate = object : Runnable {
        override fun run() {
            while (playbackLiveData.value == PLAYER_STATE_PLAYNG) {
                timerLiveData.postValue(
                    SimpleDateFormat("mm:ss", Locale.getDefault())
                        .format(mediaPlayer?.currentPosition)
                )
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
                        trackLiveData.postValue(track)
                        playbackLiveData.postValue(PLAYER_STATE_PREPARED)
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
                    playbackLiveData.value = PLAYER_STATE_PAUSED
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
        playbackLiveData.value = PLAYER_STATE_PLAYNG
    }

    // Остановка плеера
    private fun pausePlayer() {
        mediaPlayer?.pause()
        playbackLiveData.value = PLAYER_STATE_PAUSED
    }

    fun getTrackData(): LiveData<Track> = trackLiveData
    fun getPlaybackLiveData(): LiveData<Int> = playbackLiveData
    fun getTimerLiveData(): LiveData<String> = timerLiveData
    fun getFavoriteLiveData(): LiveData<Boolean> = favoriteLiveData
    fun getInLibraryLiveData(): LiveData<Boolean> = inLibraryLiveData

    // Переключение состояния плеера
    fun togglePlay() {
        if (playbackLiveData.value == PLAYER_STATE_PAUSED ||
            playbackLiveData.value == PLAYER_STATE_PREPARED) {
            startPlayer()
        } else {
            pausePlayer()
        }
    }

    fun toggleFavorite() {
        favoriteLiveData.value = !(favoriteLiveData.value!!)
    }

    fun toggleInLibrary() {
        inLibraryLiveData.value = !(inLibraryLiveData.value!!)
    }

    // Освобождает плеер
    fun releaseMediaPlayer() {
        // Останавливем воспроизмедение что-бы избежать обращение к плееру в параллельном потоке
        playbackLiveData.value = PLAYER_STATE_PAUSED
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

        const val PLAYER_STATE_DEFAULT = 0
        const val PLAYER_STATE_PREPARED = 1
        const val PLAYER_STATE_PLAYNG = 2
        const val PLAYER_STATE_PAUSED = 3
        const val TIMER_UPDATE_DELAY = 500L
        const val TIMER_ZERO = "0.00"
    }
}