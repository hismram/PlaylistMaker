package com.example.playlismaker

import android.content.SharedPreferences
import com.google.gson.Gson

class SearchHistory(val sharedPref: SharedPreferences) {
    private var list: ArrayList<Track>

    init {
        list = read()
    }

    /**
     * Добавляет запись трека в историю
     * @param track Добавляемый трек
     */
    fun add(track: Track) {
        list.removeIf { it.trackId == track.trackId }

        list.add(0, track)

        if (list.size > 10) {
            list.removeLast()
        }

        save()
    }

    /**
     * Возарвщает список истории треков
     */
    fun getList(): ArrayList<Track> {
        return list
    }

    /**
     * Очищает историю
     */
    fun clear() {
        list.clear()
        save()
    }

    /**
     * Записывает список треков в хранилище
     */
    private fun save() {
        val jsonList = Gson().toJson(list)

        sharedPref.edit().putString(HISTORY_STORAGE_ID, jsonList).apply()
    }

    /**
     * Получает историю из хранилища
     */
    private fun read(): ArrayList<Track> {
        val jsonList = sharedPref.getString(HISTORY_STORAGE_ID, "[]")

        return Gson().fromJson(jsonList, Array<Track>::class.java).toCollection(ArrayList())
    }

    companion object {
        const val HISTORY_STORAGE_ID = "search_history"
    }
}