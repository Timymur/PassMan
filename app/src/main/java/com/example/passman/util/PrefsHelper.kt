package com.example.passman.util

import android.content.Context
import androidx.core.content.edit


object PrefsHelper{
    private const val PREFS_NAME = "passman_prefs"
    private const val KEY_MASTER_PASSWORD = "master-password"

    fun saveMasterPassword(context: Context, password: String){
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit { putString(KEY_MASTER_PASSWORD, password) }
    }

    fun getMasterPassword(context: Context): String?{
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_MASTER_PASSWORD, null)
    }

    fun clearMasterPassword(context: Context){
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit { remove(KEY_MASTER_PASSWORD) }
    }

}