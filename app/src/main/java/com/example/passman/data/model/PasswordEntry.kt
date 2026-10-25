package com.example.passman.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "password_entries")
data class PasswordEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val username: String,
    val password: String,
    val notes: String = "",
    val website: String = "",
    val createdAt: Long = System.currentTimeMillis()
)