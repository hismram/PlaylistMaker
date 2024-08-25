package com.example.playlismaker

import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class PlayerActivity() : ComponentActivity() {
    private var track: Track? = null
    private var isFavorite: Boolean = false
    private var inPlaylist: Boolean = false
    private var isPlaying: Boolean = false

    private lateinit var backBtn: ImageButton
    private lateinit var queueBtn: ImageButton
    private lateinit var playBtn: ImageButton
    private lateinit var favoriteBtn: ImageButton

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
    }

    /**
     * Получение view компонентов
     */
    private fun initComponents() {
        backBtn = findViewById(R.id.menu_button)
        queueBtn = findViewById(R.id.queue)
        playBtn = findViewById(R.id.play)
        favoriteBtn = findViewById(R.id.favorite)

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
        playBtn.setOnClickListener { togglePlay() }
        favoriteBtn.setOnClickListener {  toggleFavorite() }
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
    private fun togglePlay() {
        isPlaying = !isPlaying

        val iconId = if (isPlaying) {
            R.drawable.pause_icon
        } else {
            R.drawable.play_icon
        }

        playBtn.setImageDrawable(AppCompatResources.getDrawable(favoriteBtn.context, iconId))
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
    }
}
