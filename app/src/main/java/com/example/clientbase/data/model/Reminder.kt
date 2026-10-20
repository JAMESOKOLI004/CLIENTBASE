package com.example.clientbase.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reminders")
data class Reminder(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val customerId: Long,
    val customerName: String,
    val title: String,
    val dueMillis: Long,
    val isCompleted: Boolean = false,
    val priority: String = "Medium" // "High", "Medium", "Low"
)
