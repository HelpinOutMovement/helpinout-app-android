package org.helpinout.billonlights.model.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import org.helpinout.billonlights.model.database.dao.AddItemDao
import org.helpinout.billonlights.model.database.dao.MappingDao
import org.helpinout.billonlights.model.database.dao.RegistrationDao
import org.helpinout.billonlights.model.database.entity.AddCategoryDbItem
import org.helpinout.billonlights.model.database.entity.MappingDetail
import org.helpinout.billonlights.model.database.entity.Registration


@Database(entities = [(Registration::class), (AddCategoryDbItem::class), (MappingDetail::class)], version = 4, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getRegistrationDao(): RegistrationDao
    abstract fun getAddItemDao(): AddItemDao
    abstract fun getMappingDao(): MappingDao

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