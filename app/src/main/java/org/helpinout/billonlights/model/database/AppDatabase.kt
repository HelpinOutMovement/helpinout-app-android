package org.helpinout.billonlights.model.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import org.helpinout.billonlights.model.database.dao.AddItemDao
import org.helpinout.billonlights.model.database.dao.RegistrationDao
import org.helpinout.billonlights.model.database.entity.AddItem
import org.helpinout.billonlights.model.database.entity.Registration


@Database(entities = [(Registration::class), (AddItem::class)], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getRegistrationDao(): RegistrationDao
    abstract fun getAddItemDao(): AddItemDao

    companion object {
        private var mInstance: AppDatabase? = null
        private var DB_NAME = "database.db"

        @Synchronized
        fun getInstance(context: Context): AppDatabase {
            if (mInstance == null) {
                mInstance = Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, DB_NAME).fallbackToDestructiveMigration().build()
            }
            return mInstance!!
        }

    }
}