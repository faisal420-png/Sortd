package com.sortd.app.ui.nav

sealed class Route(val path: String) {
    data object Home : Route("home")
    data object Folders : Route("folders")
    data object Settings : Route("settings")
    data object FolderDetail : Route("folder/{id}") {
        const val ARG_ID = "id"
        fun create(id: Long) = "folder/$id"
    }
}
