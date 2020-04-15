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

    @Query("Select * from mapping where activity_type =:offerType and request_mapping_initiator!=:initiator")
    fun getMyRequestsOrOffers(offerType: Int, initiator: Int): List<MappingDetail>


    @Query("Select * from mapping where activity_type =:offerType and request_mapping_initiator=:initiator and parent_uuid=:activity_uuid")
    fun getMyRequestsOrOffersByUuid(offerType: Int, initiator: Int, activity_uuid: String): List<MappingDetail>


    @Query("delete from mapping WHERE activity_uuid=:activity_uuid and parent_uuid=:parentUUid")
    fun deleteMapping(activity_uuid: String, parentUUid: String)
}