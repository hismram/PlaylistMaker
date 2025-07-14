package com.example.playlistmaker.search.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentSearchBinding
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.player.ui.PlayerActivity
import com.example.playlistmaker.presentation.TracksAdapter
import com.example.playlistmaker.search.domain.model.ListState
import com.example.playlistmaker.search.presentation.SearchViewModel
import com.example.playlistmaker.utils.debounce
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {
    private var _binding: FragmentSearchBinding? = null
    private val binding get(): FragmentSearchBinding = _binding!!
    private var searchString: String = SEARCH_DEFAULT
    private var placeholderImg: Int = R.drawable.not_found
    private val tracksList: ArrayList<Track> = ArrayList()
    private var searchAdapter: TracksAdapter? = null
    private val viewModel: SearchViewModel by viewModel()
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var searchDebounce: (String) -> Unit
    private lateinit var allowTrackClickDebounce: (Track) -> Unit
    private var trackClickAllowed = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)

        searchDebounce = debounce<Any>(
            SEARCH_DELAY,
            viewModel.viewModelScope,
            true
        ) { search() }

        allowTrackClickDebounce = debounce<Track>(
            TRACK_CLICK_DELAY,
            viewModel.viewModelScope,
            true
        ) { trackClickAllowed = true }


        viewModel.getSearchStateLiveData().observe(viewLifecycleOwner) { state ->
            when (state) {
                ListState.Error -> showPlaceholder(
                    R.string.network_error, R.drawable.network_error
                )
                is ListState.Loaded -> showList(state.tracks)
                is ListState.History -> showHistory(state.tracks)
                ListState.Loading -> showProgressBar()
                ListState.NotFound -> showPlaceholder(R.string.not_found, R.drawable.not_found)
            }
        }

        searchAdapter = TracksAdapter(tracksList) {
            if (trackClickAllowed) {
                trackClickAllowed = false
                openPlayer(it)
                allowTrackClickDebounce(it)
            }
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

        binding.searchInput.doOnTextChanged { text, _, _, _ ->
            // Отображаем кнопку сброса только если есть введенное значение
            binding.searchCross.isVisible = !text.isNullOrEmpty()
            if (binding.searchInput.hasFocus() && text.isNullOrEmpty()) {
                viewModel.showHistory()
            } else {
                clearList()
            }
            // Сохраняем результат ввода в переменную
            searchString = text.toString()
            searchDebounce(searchString)
        }

        binding.searchInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                search()
                true
            }
            false
        }
        binding.tracksViewPlaceholderReload.setOnClickListener { search(true) }
        binding.searchCross.setOnClickListener { resetSearch() }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacksAndMessages(null)
        _binding = null
        searchAdapter = null
    }

    private fun clearList() {
        binding.searchHistoryHeader.isVisible = false
        binding.searchHistoryClear.isVisible = false
        tracksList.clear()
        searchAdapter?.notifyDataSetChanged()
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
     * Запускает поиск, заполняеет список треков результом или показывает заглушку
     */
    private fun search(force: Boolean = false) {
        if (isAdded && binding.searchInput.text.isNotEmpty()) {
            viewModel.search(binding.searchInput.text.toString(), force)
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
        binding.tracksViewPlaceholderReload.isVisible = true
    }

    private fun updateTracksList(tracks: List<Track>) {
        tracksList.clear()
        tracksList.addAll(tracks)
        searchAdapter?.notifyDataSetChanged()
    }

    /**
     * Скрывает заглушку, показывает реестр
     */
    private fun showList(tracks: List<Track>) {
        if (!isAdded) return

        updateTracksList(tracks)
        binding.tracksViewPlaceholder.isVisible = false
        binding.tracksViewPlaceholderReload.isVisible = false
        binding.searchProgressBar.isVisible = false
        binding.tracksView.isVisible = true
        binding.searchHistoryClear.isVisible = false
        binding.searchHistoryHeader.isVisible = false
    }

    private fun showHistory(tracks: List<Track>) {
        if (!isAdded) return

        updateTracksList(tracks)
        binding.searchHistoryClear.isVisible = tracks.isNotEmpty()
        binding.searchHistoryHeader.isVisible = tracks.isNotEmpty()
        binding.tracksViewPlaceholder.isVisible = false
        binding.tracksViewPlaceholderReload.isVisible = false
        binding.searchProgressBar.isVisible = false
        binding.tracksView.isVisible = true
    }

    /**
     * Показывает ожиданчик
     */
    private fun showProgressBar() {
        binding.searchHistoryHeader.isVisible = false
        binding.searchHistoryClear.isVisible = false
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
        private const val SEARCH_DELAY = 1500L
        private const val TRACK_CLICK_DELAY = 1500L
        private const val SEARCH_DEFAULT = ""
    }
}
