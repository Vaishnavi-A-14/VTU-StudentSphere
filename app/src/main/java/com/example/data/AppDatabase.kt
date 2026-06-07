package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [SavedCgpa::class, StudyTask::class, ChatDoubt::class, OfflineNote::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun cgpaDao(): CgpaDao
    abstract fun studyTaskDao(): StudyTaskDao
    abstract fun chatDoubtDao(): ChatDoubtDao
    abstract fun offlineNoteDao(): OfflineNoteDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "vtu_studentsphere_db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
