package com.example.playlismaker.player.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlismaker.Helpers
import com.example.playlismaker.R
import com.example.playlismaker.databinding.ActivityPlayerBinding
import com.example.playlismaker.player.presentation.PlayerViewModel
import com.example.playlismaker.search.domain.model.Track

class PlayerActivity : ComponentActivity() {
    private var trackId: Int = -1

    private lateinit var binding: ActivityPlayerBinding
    private lateinit var viewModel: PlayerViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        trackId = intent.getIntExtra(TRACK_ID, -1)
        if (trackId == -1) {
            throw Exception("Incorrect track id")
            return
        }

        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(
            this,
            PlayerViewModel.getViewModelFactory(
                trackId
            )
        )[PlayerViewModel::class.java]

        viewModel.getTrackData().observe(this) { trackData ->
            if (trackData !== null) {
                initTrackData(trackData)
                initHandlers()
            }
        }

        viewModel.getPlaybackLiveData().observe(this) { state ->
            changePlaybackState(state)
        }
        viewModel.getTimerLiveData().observe(this) { time ->
            binding.timer.text = time
        }
        viewModel.getFavoriteLiveData().observe(this) { isFavorite ->
            val icon = if (isFavorite) {
                R.drawable.favorite_marked_icon
            } else {
                R.drawable.favorite_icon
            }

            binding.favorite.setImageDrawable(AppCompatResources.getDrawable(
                binding.favorite.context,
                icon
            ))
        }
        viewModel.getInLibraryLiveData().observe(this) { inLibrary ->
            val icon = if (inLibrary) {
                R.drawable.queue_marked_icon
            } else {
                R.drawable.queue_icon
            }

            binding.queue.setImageDrawable(AppCompatResources.getDrawable(
                binding.queue.context,
                icon
            ))
        }

        binding.menuButton.setOnClickListener { finish() }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.releaseMediaPlayer()
    }

    private fun changePlaybackState(state: Int) {
        val icon = if (state == PlayerViewModel.PLAYER_STATE_PLAYNG) {
            R.drawable.pause_icon
        } else {
            R.drawable.play_icon
        }

        binding.play.setImageDrawable(
            AppCompatResources.getDrawable(binding.play.context, icon)
        )
    }

    private fun initTrackData(track: Track) {
        val context  = binding.albumCover.context

        Glide.with(binding.albumCover)
            .load(track.getLargeArtwork())
            .placeholder(R.drawable.album_placeholder)
            .transform(
                RoundedCorners(
                    Helpers.Companion.dpToPx(
                        context.resources.getFloat(R.dimen.track_cover_large_corner_radius),
                        context
                    )
                )
            )
            .into(binding.albumCover)

        binding.progressBar.isVisible = false

        binding.timer.isVisible = true

        binding.albumCover.isVisible = true
        binding.playControlsLayout.isVisible = true

        binding.trackName.text = track.trackName
        binding.trackName.isVisible = true

        binding.trackArtist.text = track.artistName
        binding.trackArtist.isVisible = true

        binding.trackDuration.text = track.getTrackTime()
        binding.trackDuration.isVisible = binding.trackDuration.text.isNotEmpty()
        binding.durationLabel.isVisible = binding.trackDuration.text.isNotEmpty()

        binding.trackAlbum.text = track.collectionName
        binding.trackAlbum.isVisible = binding.trackAlbum.text.isNotEmpty()
        binding.albumLabel.isVisible = binding.trackAlbum.text.isNotEmpty()

        binding.trackYear.text = track.getReleaseDate()
        binding.trackYear.isVisible = binding.trackYear.text.isNotEmpty()
        binding.yearLabel.isVisible = binding.trackYear.text.isNotEmpty()

        binding.trackGenre.text = track.primaryGenreName
        binding.trackGenre.isVisible = binding.trackGenre.text.isNotEmpty()
        binding.genreLabel.isVisible = binding.trackGenre.text.isNotEmpty()

        binding.trackCountry.text = track.country
        binding.trackCountry.isVisible = binding.trackGenre.text.isNotEmpty()
        binding.countryLabel.isVisible = binding.trackGenre.text.isNotEmpty()
    }

    /**
     * Инициализация обработчиков
     */
    private fun initHandlers() {
        binding.queue.setOnClickListener { addToPlaylist() }
        binding.play.setOnClickListener {
            viewModel.togglePlay()
        }
        binding.favorite.setOnClickListener {
            toggleFavorite()
        }
    }

    /**
     * Переключатель избранного трека
     */
    private fun toggleFavorite() {
        viewModel.toggleFavorite()
    }

    /**
     * Добавление в плейлист
     */
    private fun addToPlaylist() {
        viewModel.toggleInLibrary()
    }

    companion object {
        const val TRACK_ID = "TRACK_ID"
    }
}