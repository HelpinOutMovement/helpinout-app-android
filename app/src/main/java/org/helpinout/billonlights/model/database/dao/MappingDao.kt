package org.helpinout.billonlights.model.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import org.helpinout.billonlights.model.database.entity.MappingDetail

/**
 * Created by akumar29 on 6/21/2018.
 */
@Dao
interface MappingDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMappingRecord(vararg record: MappingDetail): List<Long>

    @Query("Select * from mapping where activity_type =:offerType")
    fun getMyRequestsOrOffers(offerType: Int): List<MappingDetail>
}