package com.sortd.app.data

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LinkRepository @Inject constructor(
    private val dao: SavedLinkDao,
    private val folderDao: FolderDao,
    private val fetcher: LinkMetadataFetcher
) {
    fun observeAll(): Flow<List<SavedLink>> = dao.observeAll()
    fun observeByFolder(folderId: Long): Flow<List<SavedLink>> = dao.observeByFolder(folderId)
    fun observeFavorites(): Flow<List<SavedLink>> = dao.observeFavorites()
    fun search(query: String): Flow<List<SavedLink>> = dao.search(query)
    fun observeOne(id: Long): Flow<SavedLink?> = dao.observeOne(id)
    fun observeFolders(): Flow<List<Folder>> = folderDao.observeAll()
    fun observeFoldersWithCounts(): Flow<List<FolderWithCount>> = folderDao.observeWithCounts()

    suspend fun saveLink(rawUrl: String, folderId: Long? = null): Result<Long> {
        val url = normalize(rawUrl) ?: return Result.failure(IllegalArgumentException("Invalid URL"))
        val meta = fetcher.fetch(url)
        val id = dao.insert(
            SavedLink(
                url = url,
                title = meta.title,
                description = meta.description,
                imageUrl = meta.imageUrl,
                folderId = folderId,
                savedAt = System.currentTimeMillis()
            )
        )
        return Result.success(id)
    }

    suspend fun updateLink(link: SavedLink) = dao.update(link)
    suspend fun toggleFavorite(id: Long, isFavorite: Boolean) = dao.setFavorite(id, isFavorite)
    suspend fun moveToFolder(id: Long, folderId: Long?) = dao.setFolder(id, folderId)
    suspend fun delete(id: Long) = dao.delete(id)

    suspend fun createFolder(name: String, colorHex: String = "#7C4DFF"): Long =
        folderDao.insert(Folder(name = name.trim(), colorHex = colorHex))

    suspend fun renameFolder(id: Long, name: String) {
        val existing = folderDao.get(id) ?: return
        folderDao.update(existing.copy(name = name.trim()))
    }

    suspend fun deleteFolder(id: Long) = folderDao.delete(id)

    private fun normalize(raw: String): String? {
        val trimmed = raw.trim()
        if (trimmed.isBlank()) return null
        val withScheme = if (trimmed.startsWith("http://") || trimmed.startsWith("https://")) trimmed
        else "https://$trimmed"
        return runCatching { java.net.URL(withScheme).toString() }.getOrNull()
    }
}
