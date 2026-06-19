package com.sortd.launcher.data.repository

import com.sortd.launcher.data.local.dao.NoteDao
import com.sortd.launcher.data.local.entity.NoteEntity
import com.sortd.launcher.domain.model.Note
import com.sortd.launcher.domain.model.NoteColor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NoteRepository @Inject constructor(
    private val noteDao: NoteDao
) {
    fun getAllNotes(): Flow<List<Note>> = noteDao.getAllNotes().map { entities ->
        entities.map { it.toDomain() }
    }

    suspend fun addNote(note: Note): Long {
        return noteDao.insertNote(note.toEntity())
    }

    suspend fun updateNote(note: Note) {
        noteDao.updateNote(note.toEntity())
    }

    suspend fun deleteNote(note: Note) {
        noteDao.deleteNote(note.toEntity())
    }

    suspend fun deleteAllNotes() {
        noteDao.deleteAllNotes()
    }

    private fun NoteEntity.toDomain() = Note(
        id = id,
        title = title,
        content = content,
        backgroundColor = NoteColor.fromValue(backgroundColor),
        createdAt = createdAt,
        lastModified = lastModified
    )

    private fun Note.toEntity() = NoteEntity(
        id = id,
        title = title,
        content = content,
        backgroundColor = backgroundColor.value,
        createdAt = createdAt,
        lastModified = lastModified
    )
}