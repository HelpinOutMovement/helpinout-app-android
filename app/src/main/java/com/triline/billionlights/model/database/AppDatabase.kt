package com.triline.billionlights.model.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.triline.billionlights.model.database.entity.Registration


@Database(
    entities = [(Registration::class)],
    version = 1, exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    companion object {
        private var mInstance: AppDatabase? = null
        private var DB_NAME = "database.db"

        @Synchronized
        fun getInstance(context: Context): AppDatabase {
            if (mInstance == null) {
                mInstance = Room.databaseBuilder(
                    context.applicationContext, AppDatabase::class.java,
                    DB_NAME
                )
                    .fallbackToDestructiveMigration().build()
            }
            return mInstance!!
        }

    }
}