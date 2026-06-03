package com.mensis.app.ai

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import com.mensis.app.Graph
import java.io.File

/**
 * Scarica automaticamente il modello AI (.litertlm) alla prima apertura, così l'utente non
 * deve copiarlo a mano. Usa il [DownloadManager] di sistema: prosegue in background, mostra
 * una notifica di avanzamento e salva il file direttamente dove il motore lo cerca
 * (getExternalFilesDir("models")/[Graph.MODEL_NAME]).
 *
 * Fonte pubblica e ungated (HuggingFace, litert-community). Il contenuto è identico al file
 * richiesto `qwen2.5-1.5b-it_q8_ekv4096.litertlm`, che è il nome con cui viene salvato.
 */
object ModelDownloader {

    const val URL =
        "https://huggingface.co/litert-community/Qwen2.5-1.5B-Instruct/resolve/main/" +
            "Qwen2.5-1.5B-Instruct_multi-prefill-seq_q8_ekv4096.litertlm"

    const val APPROX_BYTES = 1_597_931_520L

    private const val PREFS = "model_dl"
    private const val KEY_ID = "download_id"

    enum class State { ABSENT, PENDING, RUNNING, PAUSED, DONE, FAILED }

    data class Status(
        val state: State,
        val downloadedBytes: Long = 0,
        val totalBytes: Long = 0
    ) {
        val fraction: Float
            get() = if (totalBytes > 0) (downloadedBytes.toFloat() / totalBytes).coerceIn(0f, 1f) else 0f
    }

    private fun modelFile(context: Context) = File(context.getExternalFilesDir("models"), Graph.MODEL_NAME)

    fun isPresent(): Boolean = Graph.llm.isModelPresent()

    /** Avvia (o riprende) il download se il modello non è ancora completo. */
    fun ensureDownloading(context: Context): Long {
        val st = status(context)
        if (st.state == State.DONE) return -1L
        if (st.state == State.RUNNING || st.state == State.PENDING || st.state == State.PAUSED) {
            return savedId(context)
        }
        // ABSENT o FAILED → (ri)parti da capo: rimuovi eventuali residui parziali.
        runCatching { modelFile(context).delete() }

        val dm = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val request = DownloadManager.Request(Uri.parse(URL))
            .setTitle("Assistente AI di Mensis")
            .setDescription("Download del modello (≈ 1,5 GB)")
            .setDestinationInExternalFilesDir(context, "models", Graph.MODEL_NAME)
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setAllowedOverMetered(true)
            .setAllowedOverRoaming(false)
        val id = dm.enqueue(request)
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putLong(KEY_ID, id).apply()
        return id
    }

    fun status(context: Context): Status {
        val id = savedId(context)
        if (id != -1L) {
            val dm = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            queryById(dm, id)?.let { return it }
        }
        return if (isPresent()) Status(State.DONE) else Status(State.ABSENT)
    }

    private fun savedId(context: Context): Long =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getLong(KEY_ID, -1L)

    private fun queryById(dm: DownloadManager, id: Long): Status? {
        dm.query(DownloadManager.Query().setFilterById(id)).use { c ->
            if (!c.moveToFirst()) return null
            val st = c.getInt(c.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS))
            val down = c.getLong(c.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
            val total = c.getLong(c.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
            val state = when (st) {
                DownloadManager.STATUS_SUCCESSFUL -> State.DONE
                DownloadManager.STATUS_RUNNING -> State.RUNNING
                DownloadManager.STATUS_PENDING -> State.PENDING
                DownloadManager.STATUS_PAUSED -> State.PAUSED
                DownloadManager.STATUS_FAILED -> State.FAILED
                else -> State.ABSENT
            }
            return Status(state, down, if (total > 0) total else APPROX_BYTES)
        }
    }
}
