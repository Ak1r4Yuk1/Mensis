package com.mensis.app

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.mensis.app.ai.LocalLlm
import com.mensis.app.data.MensisRepository
import com.mensis.app.data.SettingsRepository
import com.mensis.app.data.db.MensisDatabase
import java.io.File

/** Tiny service locator — avoids a DI framework for a small local-only app. */
object Graph {
    const val MODEL_NAME = "qwen2.5-1.5b-it_q8_ekv4096.litertlm"

    lateinit var repository: MensisRepository
        private set
    lateinit var settings: SettingsRepository
        private set
    lateinit var appContext: Context
        private set

    val engine = MensisEngine()

    val llm: LocalLlm by lazy { LocalLlm(File(appContext.getExternalFilesDir("models"), MODEL_NAME)) }

    fun provide(context: Context) {
        if (::repository.isInitialized) return
        appContext = context.applicationContext
        val db = MensisDatabase.get(appContext)
        repository = MensisRepository(db.cycleDao(), db.logDao(), db.kickDao())
        settings = SettingsRepository(appContext)
    }
}

class MensisApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Graph.provide(this)
        createReminderChannel()
    }

    private fun createReminderChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                REMINDER_CHANNEL_ID,
                "Promemoria del ciclo",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Avvisi sui prossimi eventi: mestruazione, finestra fertile, ovulazione."
            }
            getSystemService(NotificationManager::class.java)?.createNotificationChannel(channel)
        }
    }

    companion object {
        const val REMINDER_CHANNEL_ID = "mensis_reminders"
    }
}
