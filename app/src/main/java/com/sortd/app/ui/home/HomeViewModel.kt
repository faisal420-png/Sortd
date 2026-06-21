package com.sortd.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sortd.app.data.LinkRepository
import com.sortd.app.data.SavedLink
import com.sortd.app.data.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class HomeFilter { ALL, FAVORITES }

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repo: LinkRepository,
    private val settings: SettingsRepository
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _filter = MutableStateFlow(HomeFilter.ALL)
    val filter: StateFlow<HomeFilter> = _filter.asStateFlow()

    val gridView: StateFlow<Boolean> = settings.gridView
        .stateIn(viewModelScope, SharingStarted.Eagerly, true)

    val links: StateFlow<List<SavedLink>> = combine(_query.debounce(180), _filter) { q, f -> q to f }
        .flatMapLatest { (q, f) ->
            when {
                q.isNotBlank() -> repo.search(q)
                f == HomeFilter.FAVORITES -> repo.observeFavorites()
                else -> repo.observeAll()
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun setQuery(q: String) { _query.value = q }
    fun setFilter(f: HomeFilter) { _filter.value = f }
    fun toggleGrid() {
        viewModelScope.launch { settings.setGridView(!gridView.value) }
    }

    fun toggleFavorite(link: SavedLink) {
        viewModelScope.launch { repo.toggleFavorite(link.id, !link.isFavorite) }
    }

    fun delete(id: Long) { viewModelScope.launch { repo.delete(id) } }
}
