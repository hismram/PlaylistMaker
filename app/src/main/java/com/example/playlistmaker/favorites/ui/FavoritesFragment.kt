package com.example.playlistmaker.favorites.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.databinding.FragmentFavoritesBinding
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.favorites.presentation.FavoritesViewModel
import com.example.playlistmaker.player.ui.PlayerActivity
import com.example.playlistmaker.presentation.TracksAdapter
import com.example.playlistmaker.utils.debounce
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoritesFragment : Fragment() {
    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!
    private val favoritesList: ArrayList<Track> = ArrayList()
    private lateinit var favoritesAdapter: TracksAdapter
    private val viewModel: FavoritesViewModel by viewModel()
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var allowTrackClickDebounce: (Track) -> Unit
    private var trackClickAllowed = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)

        allowTrackClickDebounce = debounce<Track>(
            TRACK_CLICK_DELAY,
            viewModel.viewModelScope,
            true
        ) { trackClickAllowed = true }

        viewModel.getFavoritesLiveData().observe(viewLifecycleOwner) { tracks ->
            if (tracks.isEmpty()) {
                showPlaceholder()
            } else {
                showFavorites(tracks)
            }
        }

        favoritesAdapter = TracksAdapter(favoritesList) {
            if (trackClickAllowed) {
                trackClickAllowed = false
                openPlayer(it)
                allowTrackClickDebounce(it)
            }
        }

        binding.tracksView.adapter = favoritesAdapter

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.loadFavorites()
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadFavorites()
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacksAndMessages(null)
        _binding = null
    }

    private fun openPlayer(track: Track) {
        val intent = Intent(activity, PlayerActivity::class.java)
        intent.putExtra(PlayerActivity.TRACK_ID, track.trackId)
        startActivity(intent)
    }

    private fun showFavorites(tracks: List<Track>) {
        favoritesList.clear()
        favoritesList.addAll(tracks)
        favoritesAdapter.notifyDataSetChanged()

        binding.tracksViewPlaceholder.isVisible = false
        binding.tracksView.isVisible = true
    }

    private fun showPlaceholder() {
        binding.tracksViewPlaceholder.isVisible = true
        binding.tracksView.isVisible = false
    }

    companion object {
        const val TRACK_CLICK_DELAY = 1500L

        fun newInstance() = FavoritesFragment()
    }
}