package com.example.playlismaker.settings.ui

import android.app.UiModeManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import com.example.playlismaker.databinding.ActivitySettingsBinding
import com.example.playlismaker.settings.presentation.SettingsViewModel

class SettingsActivity : ComponentActivity() {
    private lateinit var viewModel: SettingsViewModel
    private lateinit var binding: ActivitySettingsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(
            this, SettingsViewModel.getViewModelFactory()
        )[SettingsViewModel::class.java]

        val darkMode = if (viewModel.getThemeLiveData().value != null) {
            viewModel.getThemeLiveData().value!!
        } else {
            val uiModeManager = getSystemService(UI_MODE_SERVICE) as UiModeManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                uiModeManager.nightMode == UiModeManager.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES
            }
        }

        binding.toolbar.setOnClickListener { finish() }
        binding.themeSwitcher.isChecked = darkMode
        binding.themeSwitcher.setOnCheckedChangeListener { _, checked ->
            viewModel.changeTheme(checked)
        }
        binding.share.setOnClickListener { viewModel.share() }
        binding.writeToSupport.setOnClickListener { viewModel.writeToSupport() }
        binding.termsOfUse.setOnClickListener { viewModel.termsOfUse() }
    }
}