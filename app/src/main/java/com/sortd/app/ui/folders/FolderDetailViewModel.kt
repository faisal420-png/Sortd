package com.sortd.app.ui.folders

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sortd.app.data.Folder
import com.sortd.app.data.LinkRepository
import com.sortd.app.data.SavedLink
import com.sortd.app.ui.nav.Route
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FolderDetailViewModel @Inject constructor(
    private val repo: LinkRepository,
    handle: SavedStateHandle
) : ViewModel() {

    private val folderId: Long = handle.get<String>(Route.FolderDetail.ARG_ID)?.toLongOrNull() ?: -1L

    private val _folder = MutableStateFlow<Folder?>(null)
    val folder: StateFlow<Folder?> = _folder.asStateFlow()

    val links: StateFlow<List<SavedLink>> = repo.observeByFolder(folderId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    init {
        viewModelScope.launch {
            repo.observeFolders().collect { all ->
                _folder.value = all.firstOrNull { it.id == folderId }
            }
        }
    }

    fun toggleFavorite(link: SavedLink) {
        viewModelScope.launch { repo.toggleFavorite(link.id, !link.isFavorite) }
    }

    fun delete(id: Long) {
        viewModelScope.launch { repo.delete(id) }
    }

    fun folderId(): Long = folderId
}
