package com.example.taskmanager111.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String? = null,
    val priority: Priority,
    val dueDate: Long,
    val isCompleted: Boolean = false,
    val order: Int = 0
)

enum class Priority {
    LOW, MEDIUM, HIGH
}