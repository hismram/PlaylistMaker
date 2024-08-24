package com.example.playlismaker

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.playlismaker.service.ITunesService
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton

class SearchActivity : AppCompatActivity() {
    private var searchString: String = SEARCH_DEF

    // Сервис ITunes
    private val service = ITunesService()

    private var placeholderImg: Int = R.drawable.not_found
    // Список треков
    private val tracksList: ArrayList<Track> = ArrayList()
    private lateinit var tracksAdapter: TracksAdapter
    private lateinit var historyController: SearchHistory
    private lateinit var toolbar: MaterialToolbar
    private lateinit var searchHistoryHeader:TextView
    private lateinit var searchHistoryClear:MaterialButton
    private lateinit var searchInput: EditText
    private lateinit var searchCross: ImageView
    private lateinit var tracksView: RecyclerView
    private lateinit var tracksPlaceholder: TextView
    private lateinit var tracksPlaceholderReload: MaterialButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        historyController = SearchHistory(getSharedPreferences(App.PREFERENCES_STORAGE_ID, MODE_PRIVATE))
        //tracksList.addAll(historyController.getList())
        tracksAdapter = TracksAdapter(tracksList) {
            historyController.add(it)
        }
        toolbar = findViewById(R.id.toolbar)
        searchHistoryHeader = findViewById(R.id.search_history_header)
        searchHistoryClear = findViewById(R.id.search_history_clear)
        searchInput = findViewById(R.id.search_input)
        searchCross = findViewById(R.id.search_cross)
        tracksView = findViewById(R.id.tracks_view)
        tracksPlaceholder = findViewById(R.id.tracks_view_placeholder)
        tracksPlaceholderReload = findViewById(R.id.tracks_view_placeholder_reload)

        tracksView.adapter = tracksAdapter

        searchHistoryClear.setOnClickListener {
            historyController.clear()
            tracksList.clear()
            hideHistory()
        }
        searchInput.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && searchInput.text.isEmpty()) {
                showHistory()
            } else {
                hideHistory()
            }
        }

        searchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            // Обработка изменения введенного значения
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Отображаем кнопку сброса только если есть введенное значение
                searchCross.isVisible = !s.isNullOrEmpty()
                if (searchInput.hasFocus() && s.isNullOrEmpty()) {
                    showHistory()
                } else {
                    hideHistory()
                }
                // Сохраняем результат ввода в переменную
                searchString = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {}
        })


        searchInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                search()
                true
            }
            false
        }
        tracksPlaceholderReload.setOnClickListener { search() }
        searchCross.setOnClickListener { resetSearch() }
        toolbar.setOnClickListener { finish() }

        searchInput.requestFocus()
    }

    /**
     * Сохраняет состояние
     */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        // Сохраняем состояние
        outState.putString(SEARCH_STRING, searchString)
        outState.putInt(PLACEHOLDER_VISIBILITY, tracksPlaceholder.visibility)
        outState.putString(PLACEHOLDER_MSG, tracksPlaceholder.text.toString())
        outState.putInt(PLACEHOLDER_IMG, placeholderImg)
        outState.putInt(PLACEHOLDER_BTN_VISIBILITY, tracksPlaceholderReload.visibility)
        outState.putInt(LIST_VISIBILITY, tracksView.visibility)
    }

    /**
     * Восстанавливпет состояние
     */
    override fun onRestoreInstanceState(
        savedInstanceState: Bundle
    ) {
        super.onRestoreInstanceState(savedInstanceState)

        val listVisibility = savedInstanceState.getInt(LIST_VISIBILITY)
        val placeholderVisibility = savedInstanceState.getInt(PLACEHOLDER_VISIBILITY)
        val placeholderMsg = savedInstanceState.getString(PLACEHOLDER_MSG)
        val placeholderImg = savedInstanceState.getInt(PLACEHOLDER_IMG)
        val placeholderBtnVisibility = savedInstanceState.getInt(PLACEHOLDER_BTN_VISIBILITY)

        searchString = savedInstanceState.getString(SEARCH_STRING, SEARCH_DEF)
        searchInput.setText(searchString)
        tracksView.visibility = listVisibility
        tracksPlaceholder.visibility = placeholderVisibility
        tracksPlaceholder.setCompoundDrawablesWithIntrinsicBounds(
            0, placeholderImg, 0, 0
        )
        tracksPlaceholder.text = placeholderMsg
        tracksPlaceholderReload.visibility = placeholderBtnVisibility

    }

    /**
     * Запускает поиск, заполняеет список треков результом или показывает заглушку
     */
    private fun search() {
        val context = searchInput.context

        if (searchInput.text.isNotEmpty()) {
            service.search(searchInput.text.toString(), {
                if (it?.isNotEmpty() == true) {
                    showList()
                    tracksList.clear()
                    tracksList.addAll(it)
                    tracksAdapter.notifyDataSetChanged()
                } else {
                    showPlaceholder(
                        context.getString(R.string.not_found),
                        R.drawable.not_found,
                        false
                    )
                }
            }, {
                showPlaceholder(
                    context.getString(R.string.network_error),
                    R.drawable.network_error,
                    true
                )
            })
        }
    }

    /**
     * Показывает заглушку, скрывает реестр
     *
     * @param msg Текст сообщения в заглушке
     * @param imgId Идентификатор картинки заглушки
     * @param reload Флаг показа кнопки перезагрузки
     */
    private fun showPlaceholder(msg: String, imgId: Int, reload: Boolean) {
        tracksPlaceholder.text = msg

        placeholderImg = imgId
        tracksPlaceholder.setCompoundDrawablesWithIntrinsicBounds(
            0,
            placeholderImg,
            0,
            0
        )

        tracksView.visibility = View.GONE
        tracksPlaceholder.visibility = View.VISIBLE
        tracksPlaceholderReload.isVisible = reload
    }

    private fun showHistory() {
        tracksList.clear()
        tracksList.addAll(historyController.getList())

        searchHistoryHeader.isVisible = tracksList.isNotEmpty()
        searchHistoryClear.isVisible = tracksList.isNotEmpty()

        tracksAdapter.notifyDataSetChanged()
    }

    private fun hideHistory() {
        searchHistoryClear.isVisible = false
        searchHistoryHeader.isVisible = false
        tracksList.clear()
        tracksAdapter.notifyDataSetChanged()
    }

    /**
     * Скрывает заглушку, показывает реестр
     */
    private fun showList() {
        tracksPlaceholder.visibility = View.GONE
        tracksPlaceholderReload.visibility = View.GONE
        tracksView.visibility = View.VISIBLE
    }

    /**
     * Сбрасывает строку и результаты поиска, скрывает заглушки, показывает список
     */
    private fun resetSearch() {
        val view = this.currentFocus
        val inputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager

        if (view != null) {
            inputMethodManager?.hideSoftInputFromWindow(view.windowToken, 0)
        }
        searchInput.setText(SEARCH_DEF)
        tracksList.clear()
        tracksList.addAll(historyController.getList())
        tracksAdapter.notifyDataSetChanged()
        showList()
    }

    companion object {
        const val SEARCH_STRING = "SEARCH_STRING"
        const val PLACEHOLDER_VISIBILITY = "PLACEHOLDER_VISIBILITY"
        const val PLACEHOLDER_MSG = "PLACEHOLDER_MSG"
        const val PLACEHOLDER_IMG = "PLACEHOLDER_IMG"
        const val PLACEHOLDER_BTN_VISIBILITY = "PLACEHOLDER_BTN_VISIBILITY"
        const val LIST_VISIBILITY = "LIST_VISIBILITY"
        const val SEARCH_DEF = ""
    }
}