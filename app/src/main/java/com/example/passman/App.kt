package com.example.passman

import android.app.Application
import com.example.passman.data.AppDatabase

class App : Application(){
    lateinit var database: AppDatabase
        private set

    override fun onCreate(){
        super.onCreate()

        val  passphrase = "my_super_secret_test_key_135".toByteArray() // TEST

        database = AppDatabase.getInstance(this,  passphrase)
    }
}