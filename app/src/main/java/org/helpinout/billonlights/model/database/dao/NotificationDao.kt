package org.helpinout.billonlights.model.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import org.helpinout.billonlights.model.database.entity.NotificationItem

/**
 * Created by akumar29 on 6/21/2018.
 */
@Dao
interface NotificationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMultipleRecords(vararg records: NotificationItem): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertItems(record: NotificationItem): Long

    @Query("delete from notification_items WHERE activity_type=:activityType")
    fun deleteAll(activityType: Int)

    @Query("delete from notification_items WHERE activity_uuid=:formUuid")
    fun deleteActivity(formUuid: String)

    @Query("UPDATE notification_items SET seen =:seen WHERE activity_type=:activity_type and parent_uuid=:parent_uuid and mapping_initiator=:initiator")
    fun updateActivity(activity_type: Int, parent_uuid: String, seen: Int, initiator: Int)

    @Query("UPDATE notification_items SET seen =:seen WHERE activity_type=:activity_type and parent_uuid=:parent_uuid and activity_uuid=:activity_uuid and mapping_initiator=:initiator")
    fun updateActivity(activity_type: Int, parent_uuid: String, activity_uuid: String, seen: Int, initiator: Int)

    @Query("Select * from notification_items where seen=0 and activity_type =:activity_type")
    fun getNotificationItems(activity_type: Int): List<NotificationItem>

    @Query("Select * from notification_items where activity_type =:activity_type and parent_uuid=:parent_uuid and activity_uuid=:activity_uuid and mapping_initiator=:mapping_initiator")
    fun getNotificationItems(activity_type: Int, parent_uuid: String, activity_uuid: String, mapping_initiator: Int): NotificationItem?

}