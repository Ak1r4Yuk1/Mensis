package com.mensis.app.academy

enum class ContentType { ARTICLE, VIDEO }
enum class AcademyMode { CYCLE, PREGNANCY }

/**
 * Un contenuto dell'Academy. Per gli articoli il corpo è Markdown incluso nell'app
 * (leggero, sempre offline). Per i video si tiene solo l'ID YouTube + l'anteprima in
 * streaming, così l'APK resta piccolo.
 */
data class AcademyContent(
    val id: String,
    val type: ContentType,
    val mode: AcademyMode,
    val category: String,
    /** Fasi del ciclo per cui il contenuto è "in evidenza" (vuoto = tutte / sempre). */
    val phases: List<String> = emptyList(),
    val title: String,
    val subtitle: String,
    val durationMinutes: Int,
    val bodyMarkdown: String? = null,
    val videoId: String? = null
) {
    val thumbnailUrl: String?
        get() = videoId?.let { "https://i.ytimg.com/vi/$it/hqdefault.jpg" }
}
