package com.example.passman.data.repository

import com.example.passman.data.dao.PasswordDao
import com.example.passman.data.model.PasswordEntry
import kotlinx.coroutines.flow.Flow


class PasswordRepository(private val passwordDao: PasswordDao){
    val allEntries: Flow<List<PasswordEntry>> = passwordDao.getAllEntries()

    suspend fun insertEntry(entry: PasswordEntry): Long{
        return passwordDao.insertEntry(entry)

    }

    suspend fun updateEntry(entry: PasswordEntry){
        return passwordDao.updateEntry(entry)

    }

    suspend fun deleteEntry(entry: PasswordEntry){
        return passwordDao.deleteEntry(entry)

    }
}