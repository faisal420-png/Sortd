package com.sortd.app.ui.folders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sortd.app.data.FolderWithCount
import com.sortd.app.data.LinkRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FoldersViewModel @Inject constructor(
    private val repo: LinkRepository
) : ViewModel() {

    val folders: StateFlow<List<FolderWithCount>> = repo.observeFoldersWithCounts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun createFolder(name: String, colorHex: String) {
        if (name.isBlank()) return
        viewModelScope.launch { repo.createFolder(name, colorHex) }
    }

    fun rename(id: Long, name: String) {
        if (name.isBlank()) return
        viewModelScope.launch { repo.renameFolder(id, name) }
    }

    fun delete(id: Long) {
        viewModelScope.launch { repo.deleteFolder(id) }
    }
}
