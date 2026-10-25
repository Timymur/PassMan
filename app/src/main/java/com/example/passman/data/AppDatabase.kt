package com.example.passman.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.passman.data.dao.PasswordDao
import com.example.passman.data.model.PasswordEntry
import net.sqlcipher.database.SupportFactory


@Database(entities = [PasswordEntry::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase(){
    abstract fun passwordDao(): PasswordDao

    companion object{

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context, passphrase: ByteArray): AppDatabase{
            return INSTANCE ?: synchronized(this){
                val factory = SupportFactory(passphrase)
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "passman.db"
                )
                    .openHelperFactory(factory)
                    .build()
                INSTANCE = instance
                instance
            }
        }

    }
}