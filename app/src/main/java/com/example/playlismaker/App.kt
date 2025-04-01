package com.example.playlismaker

import android.app.Application
import android.app.UiModeManager
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlismaker.creator.Creator
import com.example.playlismaker.settings.domain.api.SettingsInteractor

class App: Application() {
    var darkTheme: Boolean = false
    private lateinit var settingsInteractor: SettingsInteractor
    override fun onCreate() {
        settingsInteractor = Creator.provideSettingsInteractor(this)
        val darkThemeSetting = settingsInteractor.getDarkMode()

        if (darkThemeSetting != null) {
            switchTheme(darkThemeSetting)
        }

        super.onCreate()
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        val nightMode =
            if (darkThemeEnabled) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO

        changeNightMode(nightMode)
    }

    private fun changeNightMode(mode: Int) {
        val uiModeManager = getSystemService(UI_MODE_SERVICE) as UiModeManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            uiModeManager.setApplicationNightMode(mode)
        } else {
            AppCompatDelegate.setDefaultNightMode(mode)
        }
    }

    companion object {
        const val PREFERENCES_STORAGE_ID = "playlistmaker_storage"
        const val SEARCH_HISTORY_STORAGE_ID = "search_history"
    }
}
