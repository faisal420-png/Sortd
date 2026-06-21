package com.sortd.app.ui.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sortd.app.data.Folder
import com.sortd.app.data.LinkRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AddLinkState(
    val isSaving: Boolean = false,
    val done: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class AddLinkViewModel @Inject constructor(
    private val repo: LinkRepository
) : ViewModel() {

    val folders: StateFlow<List<Folder>> = repo.observeFolders()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _state = MutableStateFlow(AddLinkState())
    val state: StateFlow<AddLinkState> = _state.asStateFlow()

    fun save(url: String, folderId: Long?) {
        if (url.isBlank()) return
        _state.value = AddLinkState(isSaving = true)
        viewModelScope.launch {
            val result = repo.saveLink(url, folderId)
            _state.value = AddLinkState(
                isSaving = false,
                done = result.isSuccess,
                error = result.exceptionOrNull()?.message
            )
        }
    }

    fun reset() { _state.value = AddLinkState() }
}
