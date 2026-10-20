package com.example.clientbase.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "customers")
data class Customer(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val company: String,
    val email: String,
    val phone: String,
    val address: String,
    val tags: String, // Comma separated e.g. "Enterprise, VIP"
    val status: String = "Active", // "Active", "Lead", "Opportunity", "Inactive"
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val lastContactedAt: Long = System.currentTimeMillis()
) {
    fun getTagList(): List<String> = tags.split(",")
        .map { it.trim() }
        .filter { it.isNotEmpty() }
}
