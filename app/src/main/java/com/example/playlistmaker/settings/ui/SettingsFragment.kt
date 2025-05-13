package com.example.playlistmaker.settings.ui

import android.app.UiModeManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.example.playlistmaker.databinding.FragmentSettingsBinding
import com.example.playlistmaker.settings.presentation.SettingsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment() {
    private val viewModel: SettingsViewModel by viewModel()
    private var _binding: FragmentSettingsBinding? = null
    private val binding get(): FragmentSettingsBinding = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingsBinding.inflate(
            inflater, container, false
        )

        val activity = requireActivity()
        val darkMode = if (viewModel.getThemeLiveData().value != null) {
            viewModel.getThemeLiveData().value!!
        } else {
            val uiModeManager = activity.getSystemService(
                Context.UI_MODE_SERVICE
            ) as UiModeManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                uiModeManager.nightMode == UiModeManager.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES
            }
        }

        binding.themeSwitcher.isChecked = darkMode
        binding.themeSwitcher.setOnCheckedChangeListener { _, checked ->
            viewModel.changeTheme(checked)
        }
        binding.share.setOnClickListener { viewModel.share() }
        binding.writeToSupport.setOnClickListener { viewModel.writeToSupport() }
        binding.termsOfUse.setOnClickListener { viewModel.termsOfUse() }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}