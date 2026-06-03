package com.mensis.app.ai

import com.google.ai.edge.litertlm.Content
import com.google.ai.edge.litertlm.Contents
import com.google.ai.edge.litertlm.ConversationConfig
import com.google.ai.edge.litertlm.Engine
import com.google.ai.edge.litertlm.EngineConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.io.File

/**
 * Wrapper attorno a LiteRT-LM (Qwen2.5-1.5B .litertlm). L'engine viene inizializzato una
 * sola volta (operazione lenta) e riusato. Inferenza su thread di background.
 */
class LocalLlm(private val modelFile: File) {

    private val mutex = Mutex()
    @Volatile private var engine: Engine? = null
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    val modelPath: String get() = modelFile.absolutePath
    fun isModelPresent(): Boolean =
        modelFile.exists() && modelFile.length() > 100_000_000L && modelFile.canRead()

    /** Carica il modello in memoria (idempotente). Da chiamare all'apertura della chat. */
    fun ensureLoaded() {
        scope.launch {
            mutex.withLock {
                if (engine == null && isModelPresent()) {
                    runCatching {
                        Engine(EngineConfig(modelPath = modelFile.absolutePath)).also {
                            it.initialize()
                            engine = it
                        }
                    }
                }
            }
        }
    }

    /** Scarica il modello dalla memoria. Da chiamare alla chiusura della chat: l'app resta reattiva. */
    fun release() {
        scope.launch {
            mutex.withLock {
                engine?.close()
                engine = null
            }
        }
    }

    suspend fun ask(systemInstruction: String, prompt: String): String = withContext(Dispatchers.Default) {
        mutex.withLock {
            val eng = engine ?: Engine(EngineConfig(modelPath = modelFile.absolutePath)).also {
                it.initialize()
                engine = it
            }
            eng.createConversation(
                ConversationConfig(systemInstruction = Contents.of(systemInstruction))
            ).use { conv ->
                val reply = conv.sendMessage(prompt)
                reply.contents.contents
                    .filterIsInstance<Content.Text>()
                    .joinToString("") { it.text }
                    .trim()
            }
        }
    }

    fun close() {
        engine?.close()
        engine = null
    }
}
