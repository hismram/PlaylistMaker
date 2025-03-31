package com.example.playlismaker.search.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.activity.ComponentActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.example.playlismaker.R
import com.example.playlismaker.databinding.ActivitySearchBinding
import com.example.playlismaker.search.domain.api.TracksInteractor
import com.example.playlismaker.search.domain.model.Track
import com.example.playlismaker.search.presentation.SearchAdapter
import com.example.playlismaker.player.ui.PlayerActivity
import com.example.playlismaker.search.presentation.SearchViewModel

class SearchActivity : ComponentActivity() {
    private lateinit var binding: ActivitySearchBinding;
    private var searchString: String = SEARCH_DEFAULT
    private var placeholderImg: Int = R.drawable.not_found
    private val tracksList: ArrayList<Track> = ArrayList()
    private lateinit var searchAdapter: SearchAdapter
    private lateinit var viewModel: SearchViewModel;
    private val handler = Handler(Looper.getMainLooper())
    private val searchRunnable = Runnable { search() }
    private var trackClickAllowed = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(
            this, SearchViewModel.getViewModelFactory()
        )[SearchViewModel::class.java]

        viewModel.getTrackListLiveData().observe(this) { tracks ->
            if (tracks != null) {
                tracksList.clear()
                tracksList.addAll(tracks)
                searchAdapter.notifyDataSetChanged()
            }
        }
        viewModel.getSearchStateLiveData().observe(this) { state ->
            when (state) {
                SearchViewModel.STATE_EMPTY -> showPlaceholder(
                    R.string.not_found, R.drawable.not_found
                )
                SearchViewModel.STATE_LOADING -> showProgressBar()
                SearchViewModel.STATE_NETWORK_ERROR -> showPlaceholder(
                    R.string.network_error, R.drawable.network_error
                )
                SearchViewModel.STATE_LIST -> showList()
            }
        }
        searchAdapter = SearchAdapter(tracksList) {
            if (trackClickAllowed()) openPlayer(it)
        }

        binding.tracksView.adapter = searchAdapter

        binding.searchHistoryClear.setOnClickListener {
            viewModel.resetHistory()
        }
        binding.searchInput.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && binding.searchInput.text.isEmpty()) {
                viewModel.showHistory()
            }
        }

        binding.searchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            // Обработка изменения введенного значения
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Отображаем кнопку сброса только если есть введенное значение
                binding.searchCross.isVisible = !s.isNullOrEmpty()
                if (binding.searchInput.hasFocus() && s.isNullOrEmpty()) {
                    viewModel.showHistory()
                } else {
                    clearList()
                }
                // Сохраняем результат ввода в переменную
                searchString = s.toString()
                searchDebounce()
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.searchInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                search()
                true
            }
            false
        }
        binding.tracksViewPlaceholderReload.setOnClickListener { search() }
        binding.searchCross.setOnClickListener { resetSearch() }
        binding.toolbar.setOnClickListener { finish() }
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

    private fun clearList() {
        tracksList.clear()
        searchAdapter.notifyDataSetChanged()
    }

    /**
     * Открывает экран плеера
     * @param track Данные трека для плеера
     */
    private fun openPlayer(track: Track) {
        viewModel.addToHistory(track)

        val intent = Intent(this, PlayerActivity::class.java)

        intent.putExtra(PlayerActivity.Companion.TRACK_ID, track.trackId)

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
        if (binding.searchInput.text.isNotEmpty()) {
            viewModel.search(binding.searchInput.text.toString())
        }
    }

    /**
     * Показывает заглушку, скрывает реестр
     *
     * @param messageId Текст сообщения в заглушке
     * @param imgId Идентификатор картинки заглушки
     */
    private fun showPlaceholder(messageId: Int, imgId: Int) {
        binding.tracksViewPlaceholder.text =
            binding.tracksViewPlaceholder.context.getString(messageId)

        placeholderImg = imgId
        binding.tracksViewPlaceholder.setCompoundDrawablesWithIntrinsicBounds(
            0,
            placeholderImg,
            0,
            0
        )

        binding.searchProgressBar.isVisible = false
        binding.tracksView.isVisible = false
        binding.tracksViewPlaceholder.isVisible = true
        binding.tracksViewPlaceholderReload.isVisible = false
    }

    /**
     * Скрывает заглушку, показывает реестр
     */
    private fun showList() {
        binding.tracksViewPlaceholder.isVisible = false
        binding.tracksViewPlaceholderReload.isVisible = false
        binding.searchProgressBar.isVisible = false
        binding.tracksView.isVisible = true
    }

    /**
     * Показывает ожиданчик
     */
    private fun showProgressBar() {
        binding.tracksViewPlaceholder.isVisible = false
        binding.tracksViewPlaceholderReload.isVisible = false
        binding.tracksView.isVisible = false
        binding.searchProgressBar.isVisible = true

    }

    /**
     * Сбрасывает строку и результаты поиска, скрывает заглушки, показывает список
     */
    private fun resetSearch() {
        val view = this.currentFocus
        val inputMethodManager =
            getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager

        if (view != null) {
            inputMethodManager?.hideSoftInputFromWindow(view.windowToken, 0)
        }
        binding.searchInput.setText(SEARCH_DEFAULT)
        viewModel.showHistory()
    }

    companion object {
        const val SEARCH_DELAY = 1500L
        const val TRACK_CLICK_DELAY = 1500L
        const val SEARCH_DEFAULT = ""
    }
}
