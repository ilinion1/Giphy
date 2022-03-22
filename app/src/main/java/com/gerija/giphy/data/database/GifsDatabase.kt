package com.gerija.giphy.data.database

import android.app.Application
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.gerija.giphy.data.api.dto.Data

@Database(entities = [Data::class], version = 2, exportSchema = false)
abstract class GifsDatabase : RoomDatabase() {

    companion object {
        private const val NAME_DB = "gifs_db"
        private val lock = Any()
        private var db: GifsDatabase? = null
        fun getInstance(application: Application): GifsDatabase {
            synchronized(lock) {
                db?.let { return it }
                val instance = Room.databaseBuilder(application, GifsDatabase::class.java, NAME_DB)
                    .fallbackToDestructiveMigration().build()
                db = instance
                return instance
            }
        }
    }

    abstract fun gifsDao(): GifsDao
}