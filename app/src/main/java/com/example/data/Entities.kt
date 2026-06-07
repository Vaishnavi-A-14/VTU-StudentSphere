package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_cgpa")
data class SavedCgpa(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val semesterName: String,
    val sgpa: Double,
    val cgpa: Double,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "study_tasks")
data class StudyTask(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val dueDate: String,
    val category: String, // e.g. "Math", "Physics", "Exam Study"
    val isCompleted: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "chat_doubts")
data class ChatDoubt(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val sender: String, // "user" or "assistant"
    val message: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "offline_notes")
data class OfflineNote(
    @PrimaryKey val id: String, // e.g. "notes_math_ch1"
    val title: String,
    val subject: String,
    val chapter: String,
    val semester: Int,
    val downloadUrl: String,
    val isDownloaded: Boolean = false,
    val localFilePath: String? = null
)
