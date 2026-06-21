package com.sortd.app.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import javax.inject.Inject
import javax.inject.Singleton

data class LinkMetadata(
    val title: String?,
    val description: String?,
    val imageUrl: String?
)

@Singleton
class LinkMetadataFetcher @Inject constructor() {

    suspend fun fetch(url: String): LinkMetadata = withContext(Dispatchers.IO) {
        runCatching {
            val doc = Jsoup.connect(url)
                .userAgent("Mozilla/5.0 (Android) Sortd/1.3")
                .timeout(8_000)
                .followRedirects(true)
                .get()

            val title = doc.metaContent("og:title")
                ?: doc.metaContent("twitter:title")
                ?: doc.title().ifBlank { null }

            val description = doc.metaContent("og:description")
                ?: doc.metaContent("twitter:description")
                ?: doc.metaContent("description")

            val image = doc.metaContent("og:image")
                ?: doc.metaContent("twitter:image")

            LinkMetadata(
                title = title?.trim()?.takeIf { it.isNotBlank() },
                description = description?.trim()?.takeIf { it.isNotBlank() },
                imageUrl = image?.trim()?.takeIf { it.isNotBlank() }
            )
        }.getOrElse { LinkMetadata(null, null, null) }
    }

    private fun org.jsoup.nodes.Document.metaContent(property: String): String? {
        val byProp = select("meta[property=$property]").firstOrNull()?.attr("content")
        if (!byProp.isNullOrBlank()) return byProp
        val byName = select("meta[name=$property]").firstOrNull()?.attr("content")
        return byName?.takeIf { it.isNotBlank() }
    }
}
