package com.example.passman.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.passman.data.model.PasswordEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface PasswordDao{
    @Query("SELECT * FROM password_entries ORDER BY title ASC")
    fun getAllEntries(): Flow<List<PasswordEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntry(entry: PasswordEntry): Long

    @Update
    suspend fun updateEntry(entry: PasswordEntry)

    @Delete
    suspend fun deleteEntry(entry: PasswordEntry)
}