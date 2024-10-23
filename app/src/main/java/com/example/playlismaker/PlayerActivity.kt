package com.example.playlismaker

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerActivity() : ComponentActivity() {
    private var track: Track? = null
    private var isFavorite: Boolean = false
    private var inPlaylist: Boolean = false
    private var playerState: Int = STATE_DEFAULT
    private val mediaPlayer = MediaPlayer()
    private val handler = Handler(Looper.getMainLooper())
    private val playbackTimerUpdate = object : Runnable {
        override fun run() {
            updateTimerText(SimpleDateFormat("mm:ss", Locale.getDefault())
                .format(mediaPlayer.currentPosition))
            if (playerState == STATE_PLAYING)
                handler.postDelayed(this, TIMER_UPDATE_DELAY)
        }
    }

    private lateinit var backBtn: ImageButton
    private lateinit var queueBtn: ImageButton
    private lateinit var playBtn: ImageButton
    private lateinit var favoriteBtn: ImageButton

    private lateinit var timer: TextView

    private lateinit var durationLabel: TextView
    private lateinit var albumLabel: TextView
    private lateinit var yearLabel: TextView
    private lateinit var genreLabel: TextView
    private lateinit var countryLabel: TextView

    private lateinit var albumCover: ImageView
    private lateinit var trackName: TextView
    private lateinit var trackArtist: TextView
    private lateinit var trackDuration: TextView
    private lateinit var trackAlbum: TextView
    private lateinit var trackYear: TextView
    private lateinit var trackGenre: TextView
    private lateinit var trackCountry: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val data = intent.getStringExtra(TRACK_DATA)

        if (data != null) {
            track = Track.fromJSON(data)
        }

        setContentView(R.layout.activity_player)
        initComponents()
        initTrackData()
        initHandlers()
        initMediaPlayer()
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
        changePlayBtn()
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(playbackTimerUpdate)
        mediaPlayer.release()
    }

    /**
     * Получение view компонентов
     */
    private fun initComponents() {
        backBtn = findViewById(R.id.menu_button)
        queueBtn = findViewById(R.id.queue)
        playBtn = findViewById(R.id.play)
        favoriteBtn = findViewById(R.id.favorite)

        timer = findViewById(R.id.timer)

        durationLabel = findViewById(R.id.duration_label)
        albumLabel = findViewById(R.id.album_label)
        yearLabel = findViewById(R.id.year_label)
        genreLabel = findViewById(R.id.genre_label)
        countryLabel = findViewById(R.id.country_label)

        albumCover = findViewById(R.id.album_cover)
        trackName = findViewById(R.id.track_name)
        trackArtist = findViewById(R.id.track_artist)
        trackDuration = findViewById(R.id.track_duration)
        trackAlbum = findViewById(R.id.track_album)
        trackYear = findViewById(R.id.track_year)
        trackGenre = findViewById(R.id.track_genre)
        trackCountry = findViewById(R.id.track_country)
    }

    /**
     * Инициализация данных
     */
    private fun initTrackData() {
        if (track != null) {
            val context = albumCover.context

            Glide.with(albumCover)
                .load(track?.getLargeArtwork())
                .placeholder(R.drawable.album_placeholder)
                .transform(
                    RoundedCorners(
                        Helpers.dpToPx(
                            context.resources.getFloat(R.dimen.track_cover_large_corner_radius),
                            context
                        )
                    )
                )
                .into(albumCover)

            trackName.text = track?.trackName ?: trackName.text
            trackArtist.text = track?.artistName ?: trackArtist.text

            trackDuration.text = track?.getTrackTime()
            trackDuration.isVisible = trackDuration.text.isNotEmpty()
            durationLabel.isVisible = trackDuration.text.isNotEmpty()

            trackAlbum.text = track?.collectionName
            trackAlbum.isVisible = trackAlbum.text.isNotEmpty()
            albumLabel.isVisible = trackAlbum.text.isNotEmpty()

            trackYear.text = track?.getReleaseDate()
            trackYear.isVisible = trackYear.text.isNotEmpty()
            yearLabel.isVisible = trackYear.text.isNotEmpty()

            trackGenre.text = track?.primaryGenreName
            trackGenre.isVisible = trackGenre.text.isNotEmpty()
            genreLabel.isVisible = trackGenre.text.isNotEmpty()

            trackCountry.text = track?.country
            trackCountry.isVisible = trackCountry.text.isNotEmpty()
            countryLabel.isVisible = trackCountry.text.isNotEmpty()
        }
    }

    /**
     * Инициализация обработчиков
     */
    fun initHandlers() {
        backBtn.setOnClickListener { finish() }
        queueBtn.setOnClickListener { addToPlaylist() }
        playBtn.setOnClickListener {
            playbackControl()
            changePlayBtn()
        }
        favoriteBtn.setOnClickListener {  toggleFavorite() }
    }

    /**
     * Инициализация плеера
     */
    fun initMediaPlayer() {
        val mediaUrl = track?.previewUrl

        if (mediaUrl != null) {
            mediaPlayer.setDataSource(mediaUrl)
            mediaPlayer.prepareAsync()
            mediaPlayer.setOnPreparedListener {
                playBtn.isEnabled = true
                playerState = STATE_PREPARED
            }

            mediaPlayer.setOnCompletionListener {
                playerState = STATE_PREPARED
                handler.removeCallbacks(playbackTimerUpdate)
                updateTimerText(TIMER_ZERO)
                changePlayBtn()
            }
        }
    }

    /**
     * Вспомогательная функция обновления текста таймера
     * @param text Ноовое значение
     */
    private fun updateTimerText(text: String) {
        timer.text = text
    }

    /**
     * Управление воспроизседением
     */
    private fun playbackControl() {
        when (playerState) {
            STATE_PLAYING -> pausePlayer()
            STATE_PREPARED, STATE_PAUSED -> startPlayer()
        }
    }

    /**
     * Запускает воспроизведение и устанавливает соответствующий статус
     */
    private fun startPlayer() {
        mediaPlayer.start()
        handler.post(playbackTimerUpdate)
        playerState = STATE_PLAYING
    }

    /**
     * Останавливает воспроизведение и устанавливает соответствующий статус
     */
    private fun pausePlayer() {
        mediaPlayer.pause()
        handler.removeCallbacks(playbackTimerUpdate)
        playerState = STATE_PAUSED
    }

    /**
     * Переключатель избранного трека
     */
    private fun toggleFavorite() {
        isFavorite = !isFavorite
        val iconId = if (isFavorite) {
            R.drawable.favorite_marked_icon
        } else {
            R.drawable.favorite_icon
        }

        favoriteBtn.setImageDrawable(AppCompatResources.getDrawable(favoriteBtn.context, iconId))
    }

    /**
     * Переключатель воспроизведения
     */
    private fun changePlayBtn() {
        val iconId = if (playerState == STATE_PLAYING) {
            R.drawable.pause_icon
        } else {
            R.drawable.play_icon
        }

        playBtn.setImageDrawable(AppCompatResources.getDrawable(playBtn.context, iconId))
    }

    /**
     * Добавление в плейлист
     */
    private fun addToPlaylist() {
        inPlaylist = !inPlaylist

        val iconId = if (inPlaylist) {
            R.drawable.queue_marked_icon
        } else {
            R.drawable.queue_icon
        }

        queueBtn.setImageDrawable(AppCompatResources.getDrawable(favoriteBtn.context, iconId))
    }

    companion object {
        const val TRACK_DATA = "TRACK_DATA"
        const val TIMER_UPDATE_DELAY = 500L
        const val TIMER_ZERO = "00:00"
        const val STATE_DEFAULT = 0
        const val STATE_PREPARED = 1
        const val STATE_PLAYING = 2
        const val STATE_PAUSED = 3
    }
}
