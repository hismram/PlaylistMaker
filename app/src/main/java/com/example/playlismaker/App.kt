package com.example.playlismaker

import android.app.Application
import android.app.UiModeManager
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate

class App: Application() {
    var darkTheme = false
    private lateinit var sharedPref: SharedPreferences
    override fun onCreate() {
        sharedPref = getSharedPreferences(PREFERENCES_STORAGE_ID, MODE_PRIVATE)
        val mode = sharedPref.getInt(DARK_THEME, AppCompatDelegate.MODE_NIGHT_NO)
        val uiModeManager = getSystemService(Context.UI_MODE_SERVICE) as UiModeManager

        darkTheme = mode == AppCompatDelegate.MODE_NIGHT_YES

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            uiModeManager.setApplicationNightMode(UiModeManager.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(mode)
        }

        super.onCreate()
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        val uiModeManager = getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
        val nightMode =
            if (darkThemeEnabled) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO

        AppCompatDelegate.setDefaultNightMode(nightMode)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            uiModeManager.setApplicationNightMode(nightMode)
        } else {
            AppCompatDelegate.setDefaultNightMode(nightMode)
        }

        sharedPref.edit().putInt(DARK_THEME, nightMode).apply()
    }

    companion object {
        const val PREFERENCES_STORAGE_ID = "playlistmaker_storage"
        const val DARK_THEME = "dark_theme"
    }
}