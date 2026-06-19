package com.sortd.launcher.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sortd.launcher.domain.model.Note
import com.sortd.launcher.domain.model.NoteColor
import com.sortd.launcher.ui.viewmodel.NoteViewModel

@Composable
fun NotesPage(viewModel: NoteViewModel, modifier: Modifier = Modifier) {
    val notes by viewModel.notes.collectAsState()

    Column(modifier = modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(text = "Quick Notes", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
            if (notes.isNotEmpty()) {
                TextButton(onClick = { viewModel.clearAllNotes() }) { Text("Clear All", color = MaterialTheme.colorScheme.error) }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        if (notes.isEmpty()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(imageVector = Icons.Default.NoteAdd, contentDescription = null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "No notes yet", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f))
                }
            }
        } else {
            LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(notes, key = { it.id }) { note ->
                    NoteItem(note = note, onContentChange = { viewModel.updateNote(note.copy(content = it)) }, onDelete = { viewModel.deleteNote(note) }, onColorChange = { viewModel.changeNoteColor(note, it) })
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = { viewModel.addNote() }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
            Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Add Note")
        }
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteItem(note: Note, onContentChange: (String) -> Unit, onDelete: () -> Unit, onColorChange: (NoteColor) -> Unit, modifier: Modifier = Modifier) {
    val bgColor = when (note.backgroundColor) {
        NoteColor.YELLOW -> Color(0xFFFFF3CD); NoteColor.PINK -> Color(0xFFF8D7DA)
        NoteColor.BLUE -> Color(0xFFD1ECF1); NoteColor.GREEN -> Color(0xFFD4EDDA)
    }
    val textColor = when (note.backgroundColor) {
        NoteColor.YELLOW -> Color(0xFF856404); NoteColor.PINK -> Color(0xFF721C24)
        NoteColor.BLUE -> Color(0xFF0C5460); NoteColor.GREEN -> Color(0xFF155724)
    }

    SwipeToDismissBox(
        state = rememberSwipeToDismissBoxState(confirmValueChange = { value -> value == SwipeToDismissBoxValue.EndToStart.also { if (it) onDelete() } }),
        backgroundContent = {
            Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.errorContainer, RoundedCornerShape(12.dp)).padding(horizontal = 20.dp), contentAlignment = Alignment.CenterEnd) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.onErrorContainer)
            }
        },
        enableDismissFromEndToStart = true,
        enableDismissFromStartToEnd = false
    ) {
        Surface(modifier = modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), color = bgColor, shadowElevation = 2.dp) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                    NoteColor.entries.forEach { color ->
                        val ch = when (color) { NoteColor.YELLOW -> Color(0xFFFFF3CD); NoteColor.PINK -> Color(0xFFF8D7DA); NoteColor.BLUE -> Color(0xFFD1ECF1); NoteColor.GREEN -> Color(0xFFD4EDDA) }
                        Box(modifier = Modifier.size(20.dp).clip(RoundedCornerShape(4.dp)).background(ch)
                            .then(if (note.backgroundColor == color) Modifier.background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f), RoundedCornerShape(4.dp)) else Modifier)) {
                            Surface(modifier = Modifier.fillMaxSize(), shape = RoundedCornerShape(2.dp), color = ch,
                                border = if (note.backgroundColor == color) androidx.compose.foundation.BorderStroke(2.dp, Color.Black.copy(alpha = 0.3f)) else null) {}
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = note.content, onValueChange = onContentChange, modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Type your note...", color = textColor.copy(alpha = 0.5f)) },
                    textStyle = MaterialTheme.typography.bodyMedium.copy(color = textColor),
                    colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent, focusedBorderColor = Color.Transparent, unfocusedBorderColor = Color.Transparent, cursorColor = textColor),
                    minLines = 1)
            }
        }
    }
}