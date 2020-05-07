package org.helpinout.billonlights.model.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "add_item", indices = [Index(value = ["activity_uuid"], unique = true)])
class AddCategoryDbItem {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    var name: String = ""
    var detail: String? = ""
    var icon: Int = 0
    var activity_type: Int = 0
    var parent_uuid: String? = ""
    var activity_uuid: String = ""
    var date_time: String = ""
    var activity_category: Int = 0
    var activity_count: Int = 0
    var geo_location: String? = ""
    var volunters_required: Int? = 0
    var volunters_detail: String? = ""
    var volunters_quantity: String? = ""
    var technical_personal_required: Int = 0
    var technical_personal_detail: String? = ""
    var technical_personal_quantity: String? = ""
    var offersReceived: Int? = 0
    var requestSent: Int? = 0
    var status: Int = 0
    var mobile_no: String? = ""
    var address: String = ""
    var qty: String? = ""
    var newMatchesCount: Int? = 0
    var pay: Int = 0
}