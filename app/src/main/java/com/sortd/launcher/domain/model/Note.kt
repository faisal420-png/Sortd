package com.sortd.launcher.domain.model

data class Note(
    val id: Long = 0,
    val title: String = "",
    val content: String,
    val backgroundColor: NoteColor = NoteColor.YELLOW,
    val createdAt: Long = System.currentTimeMillis(),
    val lastModified: Long = System.currentTimeMillis()
)

enum class NoteColor(val value: String, val hex: Long) {
    YELLOW("yellow", 0xFFFFF3CD),
    PINK("pink", 0xFFF8D7DA),
    BLUE("blue", 0xFFD1ECF1),
    GREEN("green", 0xFFD4EDDA);

    companion object {
        fun fromValue(value: String): NoteColor {
            return entries.find { it.value == value } ?: YELLOW
        }
    }
}