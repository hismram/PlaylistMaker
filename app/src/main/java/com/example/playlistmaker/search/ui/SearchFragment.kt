package com.example.playlistmaker.search.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentSearchBinding
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.search.presentation.SearchAdapter
import com.example.playlistmaker.player.ui.PlayerActivity
import com.example.playlistmaker.search.domain.model.ListState
import com.example.playlistmaker.search.presentation.SearchViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {
    private var _binding: FragmentSearchBinding? = null
    private val binding get(): FragmentSearchBinding = _binding!!
    private var searchString: String = SEARCH_DEFAULT
    private var placeholderImg: Int = R.drawable.not_found
    private val tracksList: ArrayList<Track> = ArrayList()
    private lateinit var searchAdapter: SearchAdapter
    private val viewModel: SearchViewModel by viewModel()
    private val handler = Handler(Looper.getMainLooper())
    private val searchRunnable = Runnable { search() }
    private var trackClickAllowed = true
    private val textWatcher = object : TextWatcher {
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
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)

        viewModel.getSearchStateLiveData().observe(viewLifecycleOwner) { state ->
            when (state) {
                ListState.Error -> showPlaceholder(
                    R.string.network_error, R.drawable.network_error
                )
                is ListState.Loaded -> showList(state.tracks)
                ListState.Loading -> showProgressBar()
                ListState.NotFound -> showPlaceholder(R.string.not_found, R.drawable.not_found)
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

        binding.searchInput.addTextChangedListener(textWatcher)

        binding.searchInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                search()
                true
            }
            false
        }
        binding.tracksViewPlaceholderReload.setOnClickListener { search() }
        binding.searchCross.setOnClickListener { resetSearch() }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacksAndMessages(null)
        binding.searchInput.removeTextChangedListener(textWatcher)
        _binding = null
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

        val intent = Intent(activity, PlayerActivity::class.java)

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
        if (isAdded && binding.searchInput.text.isNotEmpty()) {
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
    private fun showList(tracks: List<Track>) {
        if (!isAdded) return

        tracksList.clear()
        tracksList.addAll(tracks)
        searchAdapter.notifyDataSetChanged()

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
        val inputMethodManager = context?.getSystemService(Context.INPUT_METHOD_SERVICE)
                as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(binding.searchInput.windowToken, 0)
        binding.searchInput.setText(SEARCH_DEFAULT)
        viewModel.showHistory()
    }

    companion object {
        const val SEARCH_DELAY = 1500L
        const val TRACK_CLICK_DELAY = 1500L
        const val SEARCH_DEFAULT = ""
    }
}
