package org.helpinout.billonlights.model.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import org.helpinout.billonlights.model.database.entity.Registration

/**
 * Created by akumar29 on 6/21/2018.
 */
@Dao
interface RegistrationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRegistrationRecord(record: Registration): Long

    @Query("Select * from registration limit 1")
    fun getRegistrationRecord(): Registration
}