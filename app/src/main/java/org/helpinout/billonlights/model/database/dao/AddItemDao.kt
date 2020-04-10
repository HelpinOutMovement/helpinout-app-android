package org.helpinout.billonlights.model.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import org.helpinout.billonlights.model.database.entity.AddItem

/**
 * Created by akumar29 on 6/21/2018.
 */
@Dao
interface AddItemDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMultipleRecords(vararg records: AddItem): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertItems(record: AddItem): Long

    @Query("delete from add_item WHERE activity_uuid=:formUuid")
    fun deleteActivity(formUuid: String)

    @Query("Select * from add_item where activity_type =:offerType")
    fun getMyRequestsOrOffers(offerType: Int): List<AddItem>
}