package com.example.playlistmaker

import android.app.Application
import android.app.UiModeManager
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.di.dataModule
import com.example.playlistmaker.di.interactorModule
import com.example.playlistmaker.di.repositoryModule
import com.example.playlistmaker.di.viewModelModule
import com.example.playlistmaker.settings.domain.api.SettingsInteractor
import org.koin.android.ext.android.getKoin
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App: Application() {
    var darkTheme: Boolean = false
    private lateinit var settingsInteractor: SettingsInteractor
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@App)
            modules(dataModule, repositoryModule, interactorModule, viewModelModule)
        }

        settingsInteractor = getKoin().get()
        val darkThemeSetting = settingsInteractor.getDarkMode()

        if (darkThemeSetting != null) {
            switchTheme(darkThemeSetting)
        }
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
}
