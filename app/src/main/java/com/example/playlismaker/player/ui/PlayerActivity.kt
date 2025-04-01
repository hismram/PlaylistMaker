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
import com.example.playlismaker.player.domain.model.PlaybackState
import com.example.playlismaker.player.domain.model.TrackData
import com.example.playlismaker.player.presentation.PlayerViewModel

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

        viewModel.getPlayerStateLiveData().observe(this) { playerState ->
            if (playerState != null) {
                initTrackData(playerState.trackData)
                initHandlers()
                changePlaybackState(playerState.playbackState)
                updateFavorite(playerState.favorite)
                updateInLibrary(playerState.inLibrary)
                binding.timer.text = playerState.timer
            }
        }

        binding.menuButton.setOnClickListener { finish() }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.releaseMediaPlayer()
    }

    private fun updateFavorite(isFavorite: Boolean) {
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

    private fun updateInLibrary(inLibrary: Boolean) {
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

    private fun changePlaybackState(state: PlaybackState) {
        val icon = if (state == PlaybackState.PLAYING) {
            R.drawable.pause_icon
        } else {
            R.drawable.play_icon
        }

        binding.play.setImageDrawable(
            AppCompatResources.getDrawable(binding.play.context, icon)
        )
    }

    private fun initTrackData(trackData: TrackData) {
        val context  = binding.albumCover.context

        Glide.with(binding.albumCover)
            .load(trackData.cover)
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

        binding.trackName.text = trackData.name
        binding.trackName.isVisible = true

        binding.trackArtist.text = trackData.artist
        binding.trackArtist.isVisible = true

        binding.trackDuration.text = trackData.duraration
        binding.trackDuration.isVisible = binding.trackDuration.text.isNotEmpty()
        binding.durationLabel.isVisible = binding.trackDuration.text.isNotEmpty()

        binding.trackAlbum.text = trackData.album
        binding.trackAlbum.isVisible = binding.trackAlbum.text.isNotEmpty()
        binding.albumLabel.isVisible = binding.trackAlbum.text.isNotEmpty()

        binding.trackYear.text = trackData.year
        binding.trackYear.isVisible = binding.trackYear.text.isNotEmpty()
        binding.yearLabel.isVisible = binding.trackYear.text.isNotEmpty()

        binding.trackGenre.text = trackData.genre
        binding.trackGenre.isVisible = binding.trackGenre.text.isNotEmpty()
        binding.genreLabel.isVisible = binding.trackGenre.text.isNotEmpty()

        binding.trackCountry.text = trackData.country
        binding.trackCountry.isVisible = binding.trackGenre.text.isNotEmpty()
        binding.countryLabel.isVisible = binding.trackGenre.text.isNotEmpty()
    }

    /**
     * Инициализация обработчиков
     */
    private fun initHandlers() {
        binding.queue.setOnClickListener {
            viewModel.toggleInLibrary()
        }
        binding.play.setOnClickListener {
            viewModel.togglePlay()
        }
        binding.favorite.setOnClickListener {
            viewModel.toggleFavorite()
        }
    }

    companion object {
        const val TRACK_ID = "TRACK_ID"
    }
}