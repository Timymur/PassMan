package com.example.passman.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.passman.data.model.PasswordEntry
import com.example.passman.data.repository.PasswordRepository
import kotlinx.coroutines.launch
import androidx.lifecycle.LiveData

class PasswordViewModel(private val repository: PasswordRepository) : ViewModel(){

    val allEntries: LiveData<List<PasswordEntry>> = repository.allEntries.asLiveData()

    fun insertEntry(entry: PasswordEntry) = viewModelScope.launch{
        repository.insertEntry(entry)
    }

    fun updateEntry(entry: PasswordEntry) = viewModelScope.launch{
        repository.updateEntry(entry)
    }

    fun deleteEntry(entry: PasswordEntry) = viewModelScope.launch{
        repository.deleteEntry(entry)
    }
}