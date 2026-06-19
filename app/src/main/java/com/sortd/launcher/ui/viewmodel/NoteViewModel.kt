package com.sortd.launcher.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sortd.launcher.data.repository.NoteRepository
import com.sortd.launcher.domain.model.Note
import com.sortd.launcher.domain.model.NoteColor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteViewModel @Inject constructor(
    private val noteRepository: NoteRepository
) : ViewModel() {

    val notes: StateFlow<List<Note>> = noteRepository.getAllNotes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addNote() {
        viewModelScope.launch {
            noteRepository.addNote(
                Note(
                    content = "",
                    backgroundColor = NoteColor.YELLOW,
                    lastModified = System.currentTimeMillis()
                )
            )
        }
    }

    fun updateNote(note: Note) {
        viewModelScope.launch {
            noteRepository.updateNote(note.copy(lastModified = System.currentTimeMillis()))
        }
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch {
            noteRepository.deleteNote(note)
        }
    }

    fun clearAllNotes() {
        viewModelScope.launch {
            noteRepository.deleteAllNotes()
        }
    }

    fun changeNoteColor(note: Note, color: NoteColor) {
        viewModelScope.launch {
            noteRepository.updateNote(
                note.copy(
                    backgroundColor = color,
                    lastModified = System.currentTimeMillis()
                )
            )
        }
    }
}