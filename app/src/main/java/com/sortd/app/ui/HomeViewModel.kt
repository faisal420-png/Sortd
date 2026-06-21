package com.sortd.app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sortd.app.data.LinkRepository
import com.sortd.app.data.SavedLink
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val isSaving: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repo: LinkRepository
) : ViewModel() {

    val links: StateFlow<List<SavedLink>> = repo.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    fun save(url: String) {
        if (url.isBlank()) return
        _uiState.value = _uiState.value.copy(isSaving = true, error = null)
        viewModelScope.launch {
            val result = repo.saveLink(url)
            _uiState.value = HomeUiState(
                isSaving = false,
                error = result.exceptionOrNull()?.message
            )
        }
    }

    fun delete(id: Long) {
        viewModelScope.launch { repo.delete(id) }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
