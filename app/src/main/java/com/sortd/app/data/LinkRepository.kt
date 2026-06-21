package com.sortd.app.data

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LinkRepository @Inject constructor(
    private val dao: SavedLinkDao,
    private val fetcher: LinkMetadataFetcher
) {
    fun observeAll(): Flow<List<SavedLink>> = dao.observeAll()

    suspend fun saveLink(rawUrl: String): Result<Long> {
        val url = normalize(rawUrl) ?: return Result.failure(IllegalArgumentException("Invalid URL"))
        val meta = fetcher.fetch(url)
        val id = dao.insert(
            SavedLink(
                url = url,
                title = meta.title,
                imageUrl = meta.imageUrl,
                savedAt = System.currentTimeMillis()
            )
        )
        return Result.success(id)
    }

    suspend fun delete(id: Long) = dao.delete(id)

    private fun normalize(raw: String): String? {
        val trimmed = raw.trim()
        if (trimmed.isBlank()) return null
        val withScheme = if (trimmed.startsWith("http://") || trimmed.startsWith("https://")) trimmed
        else "https://$trimmed"
        return runCatching { java.net.URL(withScheme).toString() }.getOrNull()
    }
}
