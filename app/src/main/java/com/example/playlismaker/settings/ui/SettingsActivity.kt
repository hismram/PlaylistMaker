package com.example.playlismaker.settings.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
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

        binding.toolbar.setOnClickListener { finish() }
        binding.themeSwitcher.isChecked = viewModel.getThemeLiveData().value == true
        binding.themeSwitcher.setOnCheckedChangeListener { _, checked ->
            viewModel.changeTheme(checked)
        }
        binding.share.setOnClickListener { viewModel.share() }
        binding.writeToSupport.setOnClickListener { viewModel.writeToSupport() }
        binding.termsOfUse.setOnClickListener { viewModel.termsOfUse() }
    }
}