package com.example.playlismaker.settings.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlismaker.App
import com.example.playlismaker.creator.Creator
import com.example.playlismaker.settings.domain.api.SettingsInteractor
import com.example.playlismaker.sharing.domain.api.SharingInteractor

class SettingsViewModel(
    private val settingsInteractor: SettingsInteractor,
    private val sharingInteractor: SharingInteractor
) : ViewModel() {
    val darkThemeLiveData: MutableLiveData<Boolean> = MutableLiveData()

    init {
        darkThemeLiveData.value = settingsInteractor.getDarkMode()
    }

    fun getThemeLiveData(): LiveData<Boolean> = darkThemeLiveData

    fun share() {
        sharingInteractor.shareApp()
    }

    fun writeToSupport() {
        sharingInteractor.writeToSupport()
    }

    fun termsOfUse() {
        sharingInteractor.openTerms()
    }

    fun changeTheme(bool: Boolean) {
        if (darkThemeLiveData.value != bool) {
            settingsInteractor.setDarkMode(bool)
        }
    }

    companion object {
        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val settingsInteractor = Creator.provideSettingsInteractor(
                    this[APPLICATION_KEY] as App
                )
                val sharingInteractor = Creator.provideSharingInteractor(
                    this[APPLICATION_KEY] as App
                )

                SettingsViewModel(settingsInteractor, sharingInteractor)
            }
        }
    }
}