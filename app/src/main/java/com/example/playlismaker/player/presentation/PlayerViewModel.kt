package com.example.playlismaker.player.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlismaker.player.domain.api.PlayerInteractor
import com.example.playlismaker.player.domain.model.PlaybackState
import com.example.playlismaker.player.domain.model.PlayerState
import com.example.playlismaker.player.domain.model.TrackData
import com.example.playlismaker.search.domain.model.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * VievModel для управления плеером
 * @property trackId Идентификатор трека
 * @property playerInteractor Интерактор плеера
 */
class PlayerViewModel(
    private val trackId: Int,
    private val playerInteractor: PlayerInteractor
): ViewModel() {

    /**
     * Текущее состоние плеера
     */
    private lateinit var playerState: PlayerState

    /**
     * LiveData для наблюдения за состоянием плеера
     * Инициализируется null и обновляется после загрузки данных трека
     */
    private var playerStateLiveData = MutableLiveData<PlayerState>(null)

    /**
     * Фоновая задача обновления таймера воспроизведения
     */
    private var timerJob: Job? = null;

    init {
        loadTrackData()
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel();
    }

    /**
     * Загружает данные трека, иницилизирует состоние и плеер
     */
    private fun loadTrackData() {
        playerInteractor.loadTrackData(
            trackId,
            object : PlayerInteractor.PlayerConsumer {
                override fun consume(
                    track: Track
                ) {
                    initializePlayerState(track)
                }

                override fun error() {}
            },
            onCompleteListener = { pausePlayer(true) }
        )
    }

    /**
     * Иницилизирует состояние плеера
     * @param track Данные трека
     */
    private fun initializePlayerState(track: Track) {
        playerState = PlayerState(
            favorite = false,
            inLibrary = false,
            playbackState = PlaybackState.Prepared,
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

    /**
     * Запускает задачу обновления таймера воспроизведения
     * Обновление таймера происходит каждые [TIMER_UPDATE_DELAY] миллисекунд
     */
    private fun startTimer() {
        timerJob = viewModelScope.launch {
            while (playerState.playbackState is PlaybackState.Playing) {
                delay(TIMER_UPDATE_DELAY)
                updatePlaybackTimer()
            }
        }
    }

    /**
     * Обновляет таймер
     */
    private fun updatePlaybackTimer() {
        playerState.timer = playerInteractor.getPlaybackTimer()
        playerStateLiveData.postValue(playerState)
    }

    /**
     * Запускает воспроизведение
     */
    private fun startPlayer() {
        playerInteractor.play()
        playerState.playbackState = PlaybackState.Playing
        playerStateLiveData.postValue(playerState)
        startTimer()
    }

    /**
     * Остонавливает воспроизведение
     * @param resetTimer Сброс таймера при остановке
     */
    private fun pausePlayer(resetTimer: Boolean = false) {
        timerJob?.cancel()
        playerInteractor.pause()
        playerState.playbackState = PlaybackState.Paused

        if (resetTimer) {
            playerState.timer = TIMER_ZERO
        }

        playerStateLiveData.postValue(playerState)
    }

    /**
     * Возвращает [LiveData] с текущим состоянием плеера
     * @return [LiveData]<[PlayerState]> Для отслеживания состояния
     */
    fun getPlayerStateLiveData(): LiveData<PlayerState> = playerStateLiveData

    /**
     * Переключает воспроизведение
     */
    fun togglePlay() {
        if (playerState.playbackState is PlaybackState.Paused ||
            playerState.playbackState is PlaybackState.Prepared
        ) {
            startPlayer()
        } else {
            pausePlayer()
        }
    }

    /**
     * Переключает состояние "Избранного" трека
     */
    fun toggleFavorite() {
        playerState.favorite = !playerState.favorite
        playerStateLiveData.postValue(playerState)
    }

    /**
     * Переключает состояние "В библиотеке"
     */
    fun toggleInLibrary() {
        playerState.inLibrary = !playerState.inLibrary
        playerStateLiveData.postValue(playerState)
    }

    /**
     * Освобождает плеер
     */
    fun releaseMediaPlayer() {
        // Останавливем воспроизмедение что-бы избежать обращение к плееру в параллельном потоке
        playerState.playbackState = PlaybackState.Paused
        playerStateLiveData.postValue(playerState)
        playerInteractor.release()
    }

    companion object {
        /** Интервал обновления таймера */
        private const val TIMER_UPDATE_DELAY = 300L
        /** Начальное значение таймера */
        private const val TIMER_ZERO = "00:00"
    }
}