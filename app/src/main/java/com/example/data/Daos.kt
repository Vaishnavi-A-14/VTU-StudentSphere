package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CgpaDao {
    @Query("SELECT * FROM saved_cgpa ORDER BY timestamp DESC")
    fun getAll(): Flow<List<SavedCgpa>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(cgpa: SavedCgpa)

    @Query("DELETE FROM saved_cgpa WHERE id = :id")
    suspend fun delete(id: Int)
}

@Dao
interface StudyTaskDao {
    @Query("SELECT * FROM study_tasks ORDER BY dueDate ASC")
    fun getAll(): Flow<List<StudyTask>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: StudyTask)

    @Delete
    suspend fun delete(task: StudyTask)
}

@Dao
interface ChatDoubtDao {
    @Query("SELECT * FROM chat_doubts ORDER BY timestamp ASC")
    fun getAll(): Flow<List<ChatDoubt>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(chat: ChatDoubt)

    @Query("DELETE FROM chat_doubts")
    suspend fun clearAll()
}

@Dao
interface OfflineNoteDao {
    @Query("SELECT * FROM offline_notes")
    fun getAll(): Flow<List<OfflineNote>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(notes: List<OfflineNote>)

    @Query("UPDATE offline_notes SET isDownloaded = :isDownloaded, localFilePath = :localPath WHERE id = :id")
    suspend fun updateDownloadState(id: String, isDownloaded: Boolean, localPath: String?)
}
