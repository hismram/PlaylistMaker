package com.example.playlismaker.settings.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
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

    fun changeTheme(newValue: Boolean) {
        if (darkThemeLiveData.value != newValue) {
            settingsInteractor.setDarkMode(newValue)
            darkThemeLiveData.value = newValue
        }
    }
}