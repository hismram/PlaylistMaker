package com.example.playlistmaker.library.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.playlistmaker.databinding.FragmentFavoriteBinding
import com.example.playlistmaker.library.presentation.FavoriteViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoriteFragment : Fragment() {
    companion object {
        fun newInstance() = FavoriteFragment()
    }

    private val viewModel: FavoriteViewModel by viewModel()
    private var _binding: FragmentFavoriteBinding? = null
    private val binding get(): FragmentFavoriteBinding = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}