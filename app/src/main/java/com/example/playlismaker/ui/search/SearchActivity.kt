package com.example.playlismaker.ui.search

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.playlismaker.App
import com.example.playlismaker.Creator
import com.example.playlismaker.ui.player.PlayerActivity
import com.example.playlismaker.R
import com.example.playlismaker.domain.models.Track
import com.example.playlismaker.domain.api.TracksInteractor
import com.example.playlismaker.presentation.TracksAdapter
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton

class SearchActivity : AppCompatActivity() {
    private var searchString: String = SEARCH_DEFAULT
    private var placeholderImg: Int = R.drawable.not_found
    private val tracksList: ArrayList<Track> = ArrayList()
    private lateinit var tracksProvider: TracksInteractor
    private lateinit var tracksAdapter: TracksAdapter
    private lateinit var toolbar: MaterialToolbar
    private lateinit var searchHistoryHeader:TextView
    private lateinit var searchHistoryClear:MaterialButton
    private lateinit var searchInput: EditText
    private lateinit var searchCross: ImageView
    private lateinit var searchProgressBar: ProgressBar
    private lateinit var tracksView: RecyclerView
    private lateinit var tracksPlaceholder: TextView
    private lateinit var tracksPlaceholderReload: MaterialButton
    private val searchConsumer = object : TracksInteractor.TracksConsumer {
        override fun consume(tracks: List<Track>) {
            tracksList.clear()
            tracksList.addAll(tracks)
            handler.post {
                if (tracks.isNotEmpty()) {
                    showList()
                    tracksAdapter.notifyDataSetChanged()
                } else {
                    showPlaceholder(
                        R.string.not_found,
                        R.drawable.not_found
                    )
                }
            }
        }

        override fun error() {
            handler.post {
                showPlaceholder(
                    R.string.network_error,
                    R.drawable.network_error
                )
            }
        }

    }
    private val handler = Handler(Looper.getMainLooper())
    private val searchRunnable = Runnable { search() }
    private var trackClickAllowed = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        tracksProvider = Creator.provideTracksInteractor(
            SEARCH_HISTORY_ID,
            getSharedPreferences(App.PREFERENCES_STORAGE_ID, MODE_PRIVATE)
        )
        tracksAdapter = TracksAdapter(tracksList) {
            if (trackClickAllowed()) openPlayer(it)
        }
        toolbar = findViewById(R.id.toolbar)
        searchHistoryHeader = findViewById(R.id.search_history_header)
        searchHistoryClear = findViewById(R.id.search_history_clear)
        searchInput = findViewById(R.id.search_input)
        searchCross = findViewById(R.id.search_cross)
        searchProgressBar = findViewById(R.id.search_progress_bar)
        tracksView = findViewById(R.id.tracks_view)
        tracksPlaceholder = findViewById(R.id.tracks_view_placeholder)
        tracksPlaceholderReload = findViewById(R.id.tracks_view_placeholder_reload)

        tracksView.adapter = tracksAdapter

        searchHistoryClear.setOnClickListener {
            tracksProvider.clearHistory()
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
                searchDebounce()
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
        outState.putInt(LIST_VISIBILITY, tracksView.visibility)
        outState.putString(SEARCH_STRING, searchString)
        outState.putInt(PLACEHOLDER_VISIBILITY, tracksPlaceholder.visibility)
        outState.putString(PLACEHOLDER_MSG, tracksPlaceholder.text.toString())
        outState.putInt(PLACEHOLDER_IMG, placeholderImg)
        outState.putInt(PLACEHOLDER_BTN_VISIBILITY, tracksPlaceholderReload.visibility)
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

        searchString = savedInstanceState.getString(SEARCH_STRING, SEARCH_DEFAULT)
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
     * debounce клика по треку
     */
    private fun trackClickAllowed(): Boolean {
        val current = trackClickAllowed

        if (trackClickAllowed) {
            trackClickAllowed = false
            handler.postDelayed({trackClickAllowed = true}, TRACK_CLICK_DELAY)
        }

        return current
    }

    /**
     * Открывает экран плеера
     * @param track Данные трека для плеера
     */
    private fun openPlayer(track: Track) {
        tracksProvider.addToHistory(track)
        //historyController.add(track)
        val intent = Intent(this, PlayerActivity::class.java)

        intent.putExtra(PlayerActivity.TRACK_DATA, track.toJSON())

        startActivity(intent)
    }

    /**
     * Запускает отложенную задачу на запуск поиска
     */
    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DELAY)
    }

    /**
     * Запускает поиск, заполняеет список треков результом или показывает заглушку
     */
    private fun search() {
        //val context = searchInput.context

        if (searchInput.text.isNotEmpty()) {
            showSearchProgressBar()
            tracksProvider.search(searchInput.text.toString(), searchConsumer)
        }
    }

    /**
     * Показывает заглушку, скрывает реестр
     *
     * @param messageId Текст сообщения в заглушке
     * @param imgId Идентификатор картинки заглушки
     */
    private fun showPlaceholder(messageId: Int, imgId: Int) {
        tracksPlaceholder.text = tracksPlaceholder.context.getString(messageId)

        placeholderImg = imgId
        tracksPlaceholder.setCompoundDrawablesWithIntrinsicBounds(
            0,
            placeholderImg,
            0,
            0
        )

        searchProgressBar.visibility = View.GONE
        tracksView.visibility = View.GONE
        tracksPlaceholder.visibility = View.VISIBLE
        tracksPlaceholderReload.isVisible = false
    }

    /**
     * Показывает список истории поиска
     */
    private fun showHistory() {
        tracksList.clear()
        tracksProvider.getHistory(object : TracksInteractor.TracksConsumer {
            override fun consume(tracks: List<Track>) {
                tracksList.addAll(tracks)

                handler.post {
                    searchHistoryHeader.isVisible = tracksList.isNotEmpty()
                    searchHistoryClear.isVisible = tracksList.isNotEmpty()

                    tracksAdapter.notifyDataSetChanged()
                }
            }

            override fun error() {}
        })
    }

    /**
     * Скрывает историю поиска
     */
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
        searchProgressBar.visibility = View.GONE
        tracksView.visibility = View.VISIBLE
    }

    /**
     * Показывает ожиданчик
     */
    private fun showSearchProgressBar() {
        tracksPlaceholder.visibility = View.GONE
        tracksPlaceholderReload.visibility = View.GONE
        tracksView.visibility = View.GONE
        searchProgressBar.visibility = View.VISIBLE

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
        searchInput.setText(SEARCH_DEFAULT)
        tracksList.clear()
        tracksProvider.getHistory(object : TracksInteractor.TracksConsumer {
            override fun consume(tracks: List<Track>) {
                tracksList.addAll(tracks)
                handler.post {
                    tracksAdapter.notifyDataSetChanged()
                    showList()
                }
            }

            override fun error() {}
        })
    }

    companion object {
        const val SEARCH_HISTORY_ID = "search_history"
        const val SEARCH_STRING = "SEARCH_STRING"
        const val SEARCH_DELAY = 1500L
        const val TRACK_CLICK_DELAY = 1500L
        const val PLACEHOLDER_VISIBILITY = "PLACEHOLDER_VISIBILITY"
        const val PLACEHOLDER_MSG = "PLACEHOLDER_MSG"
        const val PLACEHOLDER_IMG = "PLACEHOLDER_IMG"
        const val PLACEHOLDER_BTN_VISIBILITY = "PLACEHOLDER_BTN_VISIBILITY"
        const val LIST_VISIBILITY = "LIST_VISIBILITY"
        const val SEARCH_DEFAULT = ""
    }
}