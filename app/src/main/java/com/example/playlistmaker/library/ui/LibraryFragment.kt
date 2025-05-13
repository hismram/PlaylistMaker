package com.example.playlistmaker.library.ui

import com.example.playlistmaker.library.presentation.LibraryPagerAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentLibraryBinding
import com.example.playlistmaker.library.presentation.LibraryViewModel
import com.google.android.material.tabs.TabLayoutMediator
import org.koin.androidx.viewmodel.ext.android.viewModel

class LibraryFragment : Fragment() {
    private var _binding: FragmentLibraryBinding? = null
    private val binding get(): FragmentLibraryBinding = _binding!!
    private lateinit var tabsMediator: TabLayoutMediator
    private val viewModel: LibraryViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val activity = requireActivity()
        val app = activity.application

        _binding = FragmentLibraryBinding.inflate(inflater, container, false)

        binding.pager.adapter = LibraryPagerAdapter(
            activity.supportFragmentManager, lifecycle
        )

        tabsMediator = TabLayoutMediator(binding.tabLayout, binding.pager) {
                tab, position ->
            when (position) {
                0 -> tab.text = app.getString(R.string.favorite_tracks)
                1 -> tab.text = app.getString(R.string.playlists)
            }
        }

        tabsMediator.attach()

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        tabsMediator.detach()
        _binding = null
    }
}