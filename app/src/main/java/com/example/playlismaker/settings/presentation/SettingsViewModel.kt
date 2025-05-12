package com.example.playlismaker.settings.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlismaker.settings.domain.api.SettingsInteractor
import com.example.playlismaker.sharing.domain.api.SharingInteractor

/**
 * ViewModel экрана настроек
 *
 * Содержит:
 * - Переключатель темы
 * - Отправку ссылки на приложение
 * - Связь с техподдержкой
 * - Пользовательское соглашение
 *
 * @property settingsInteractor Интерактор для работы с настройками
 * @property sharingInteractor Интерактор для работы с внешними приложениями
 */
class SettingsViewModel(
    private val settingsInteractor: SettingsInteractor,
    private val sharingInteractor: SharingInteractor
) : ViewModel() {

    /**
     * [LiveData] с текущим состоянием темной темы
     * - `null` - тема не определена, берется системная
     */
    val darkThemeLiveData: MutableLiveData<Boolean> = MutableLiveData()

    init {
        darkThemeLiveData.value = settingsInteractor.getDarkMode()
    }

    /**
     * Возвращает [LiveData] для отслеживания состояния
     *
     * @return [LiveData]<[Boolean]>
     */
    fun getThemeLiveData(): LiveData<Boolean> = darkThemeLiveData

    /**
     * Открыает диалог "Поделится"
     */
    fun share() {
        sharingInteractor.shareApp()
    }

    /**
     * Открывает email клиент для связи с техподдержкой
     */
    fun writeToSupport() {
        sharingInteractor.writeToSupport()
    }

    /**
     * Открывает браузер с пользовательским соглашением
     */
    fun termsOfUse() {
        sharingInteractor.openTerms()
    }

    /**
     * Изменяет тему приложения
     * @param newValue новое состояние темы
     */
    fun changeTheme(newValue: Boolean) {
        if (darkThemeLiveData.value != newValue) {
            settingsInteractor.setDarkMode(newValue)
            darkThemeLiveData.value = newValue
        }
    }
}