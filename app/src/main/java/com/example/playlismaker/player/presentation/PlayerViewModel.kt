package com.example.playlismaker.player.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlismaker.player.domain.api.PlayerInteractor
import com.example.playlismaker.player.domain.model.PlaybackState
import com.example.playlismaker.player.domain.model.PlayerState
import com.example.playlismaker.player.domain.model.TrackData
import com.example.playlismaker.search.domain.model.Track

class PlayerViewModel(
    trackId: Int,
    val playerInteractor: PlayerInteractor
): ViewModel() {
    private lateinit var playerState: PlayerState
    private var playerStateLiveData = MutableLiveData<PlayerState>(null)

    // Обновление таймера во время воспроизведения
    private val playbackTimerUpdate = object : Runnable {
        override fun run() {
            while (playerState.playbackState == PlaybackState.PLAYING) {
                playerState.timer = playerInteractor.getPlaybackTimer()
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

                override fun error() {}
            },
            {
                playerState.playbackState = PlaybackState.PAUSED
                playerState.timer = TIMER_ZERO
                playerStateLiveData.postValue(playerState)
            }
        )
    }

    // Запуск плеера
    private fun startPlayer() {
        playerInteractor.play()
        Thread(playbackTimerUpdate).start()
        playerState.playbackState = PlaybackState.PLAYING
        playerStateLiveData.postValue(playerState)
    }

    // Остановка плеера
    private fun pausePlayer() {
        playerInteractor.pause()
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
        playerState.favorite = !playerState.favorite
        playerStateLiveData.postValue(playerState)
    }

    fun toggleInLibrary() {
        playerState.inLibrary = !playerState.inLibrary
        playerStateLiveData.postValue(playerState)
    }

    // Освобождает плеер
    fun releaseMediaPlayer() {
        // Останавливем воспроизмедение что-бы избежать обращение к плееру в параллельном потоке
        playerState.playbackState = PlaybackState.PAUSED
        playerStateLiveData.postValue(playerState)
        playerInteractor.release()
    }

    companion object {
        const val TIMER_UPDATE_DELAY = 500L
        const val TIMER_ZERO = "00.00"
    }
}