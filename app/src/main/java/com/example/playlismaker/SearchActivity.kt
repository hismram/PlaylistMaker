package com.example.playlismaker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar

class SearchActivity : AppCompatActivity() {
    private var searchString: String = SEARCH_DEF
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        // Поле ввода
        val searchInput = findViewById<EditText>(R.id.search_input)
        // Тулбар
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        // Кнопка сброса введенного значения
        val searchCross = findViewById<ImageView>(R.id.search_cross)
        // Список треков
        val tracksView = findViewById<RecyclerView>(R.id.tracks_view)
        // Адаптер списка треков
        val tracksAdapter = TracksAdapter(MOCK_LIST)

        tracksView.adapter = tracksAdapter
        // Обработчики ввода
        searchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            // Обработка изменения введенного значения
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Отображаем кнопку сброса только если есть введенное значение
                searchCross.isVisible = !s.isNullOrEmpty()
                // Сохраняем результат ввода в переменную
                searchString = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        // Сброс поиска
        searchCross.setOnClickListener { searchInput.setText(SEARCH_DEF) }
        toolbar.setOnClickListener{ finish() }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        // Сохраняем состояние
        outState.putString(SEARCH_STRING, searchString)
    }

    // Восстанавливаем состояние
    override fun onRestoreInstanceState(
        savedInstanceState: Bundle
    ) {
        super.onRestoreInstanceState(savedInstanceState)

        val searchInput = findViewById<EditText>(R.id.search_input)

        searchString = savedInstanceState.getString(SEARCH_STRING, SEARCH_DEF)
        searchInput.setText(searchString)
    }

    companion object {
        const val SEARCH_STRING = "SEARCH_STRING"
        const val SEARCH_DEF = ""
        val MOCK_LIST = arrayListOf<Track> (
            Track(
                "Smells Like Teen Spirit",
                "Nirvana",
                "5:01",
                "https://is5-ssl.mzstatic.com/image/thumb/Music115/v4/7b/58/c2/7b58c21a-2b51-2bb2-e59a-9bb9b96ad8c3/00602567924166.rgb.jpg/100x100bb.jpg"
            ),
            Track(
                "Billie Jean",
                "Michael Jackson",
                "4:35",
                "https://is5-ssl.mzstatic.com/image/thumb/Music125/v4/3d/9d/38/3d9d3811-71f0-3a0e-1ada-3004e56ff852/827969428726.jpg/100x100bb.jpg"
            ),
            Track(
                "Stayin' Alive",
                "Bee Gees",
                "4:10",
                "https://is4-ssl.mzstatic.com/image/thumb/Music115/v4/1f/80/1f/1f801fc1-8c0f-ea3e-d3e5-387c6619619e/16UMGIM86640.rgb.jpg/100x100bb.jpg"
            ),
            Track(
                "Whole Lotta Love",
                "Led Zeppelin",
                "5:33",
                "https://is2-ssl.mzstatic.com/image/thumb/Music62/v4/7e/17/e3/7e17e33f-2efa-2a36-e916-7f808576cf6b/mzm.fyigqcbs.jpg/100x100bb.jpg"
            ),
            Track(
                "Sweet Child O'Mine",
                "Guns N' Roses",
                "5:03",
                "https://is5-ssl.mzstatic.com/image/thumb/Music125/v4/a0/4d/c4/a04dc484-03cc-02aa-fa82-5334fcb4bc16/18UMGIM24878.rgb.jpg/100x100bb.jpg"
            ),
            Track(
                "Sweet Child O'MineSweet Child O'MineSweet Child O'MineSweet Child O'MineSweet Child O'MineSweet Child O'Mine",
                "Guns N' RosesGuns N' RosesGuns N' RosesGuns N' RosesGuns N' RosesGuns N' RosesGuns N' RosesGuns N' RosesGuns N' Roses",
                "5:03",
                "https://is5-ssl.mzstatic.com/image/thumb/Music125/v4/a0/4d/c4/a04dc484-03cc-02aa-fa82-5334fcb4bc16/18UMGIM24878.rgb.jpg/100x100bb.jpg"
            )
        )
    }
}