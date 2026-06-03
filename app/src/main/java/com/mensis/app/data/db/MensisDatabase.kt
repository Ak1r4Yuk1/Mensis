package com.mensis.app.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

private val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE logs ADD COLUMN lhTestResult TEXT")
    }
}

private val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS kick_sessions (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "startEpochMs INTEGER NOT NULL, count INTEGER NOT NULL, durationSeconds INTEGER NOT NULL)"
        )
    }
}

@Database(
    entities = [CycleEntity::class, LogEntity::class, KickSessionEntity::class],
    version = 3,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class MensisDatabase : RoomDatabase() {
    abstract fun cycleDao(): CycleDao
    abstract fun logDao(): LogDao
    abstract fun kickDao(): KickDao

    companion object {
        @Volatile
        private var instance: MensisDatabase? = null

        fun get(context: Context): MensisDatabase = instance ?: synchronized(this) {
            instance ?: Room.databaseBuilder(
                context.applicationContext,
                MensisDatabase::class.java,
                "mensis_v2.db"
            ).addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                .fallbackToDestructiveMigration(true)
                .build().also { instance = it }
        }
    }
}
