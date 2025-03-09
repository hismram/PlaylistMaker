package com.example.playlismaker

import android.app.Application
import android.app.UiModeManager
import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlismaker.domain.api.SettingsInteractor

class App: Application() {
    var darkTheme = false
    private lateinit var settingsInteractor: SettingsInteractor
    override fun onCreate() {
        settingsInteractor = Creator.provideSettingsInteractor(
            getSharedPreferences(PREFERENCES_STORAGE_ID, MODE_PRIVATE)
        )

        val mode =  settingsInteractor.getDarkMode() ?: AppCompatDelegate.MODE_NIGHT_NO

        darkTheme = mode == AppCompatDelegate.MODE_NIGHT_YES

        changeNightMode(mode)

        super.onCreate()
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        val nightMode =
            if (darkThemeEnabled) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO

        changeNightMode(nightMode)

        settingsInteractor.setDarkMode(nightMode)
    }

    private fun changeNightMode(mode: Int) {
        val uiModeManager = getSystemService(Context.UI_MODE_SERVICE) as UiModeManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            uiModeManager.setApplicationNightMode(mode)
        } else {
            AppCompatDelegate.setDefaultNightMode(mode)
        }
    }

    companion object {
        const val PREFERENCES_STORAGE_ID = "playlistmaker_storage"
    }
}