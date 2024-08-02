package com.example.playlismaker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.ImageView
import androidx.core.view.isVisible
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
    }
}