package org.helpinout.billonlights.model.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import org.helpinout.billonlights.utils.SEEN_YES

@Entity(tableName = "notification_items", indices = [Index(value = ["activity_type", "mapping_initiator", "parent_uuid", "activity_uuid"], unique = true)])
class NotificationItem(var activity_type: Int = 0, var parent_uuid: String = "", var seen: Int = SEEN_YES) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    var mapping_initiator: Int = 0
    var activity_uuid: String? = ""


}