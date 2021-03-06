package org.helpinout.billonlights.model.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

class Mapping {
    var request_detail: ActivityAddDetail? = null
    var offer_detail: ActivityAddDetail? = null
    var status: Int? = null
    var mapping_initiator: Int? = 0
    var mapping_time: String = ""
    var distance: String? = ""
}

@Entity(tableName = "mapping", indices = [Index(value = ["parent_uuid", "activity_uuid", "mapping_initiator"], unique = true)])
class MappingDetail {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
    var country_code: String = ""
    var mobile_no: String = ""
    var first_name: String = ""
    var last_name: String? = ""
    var profile_name: String? = ""
    var mobile_no_visibility: Int = 0
    var user_type: Int? = 0
    var org_name: String? = ""
    var org_type: Int? = null
    var org_division: String? = ""
    var date_time: String? = ""
    var activity_type: Int? = 0
    var rating_avg: Float? = 0F
    var rating_count: Int? = 0
    var activity_category: Int? = 0
    var geo_location: String? = ""
    var parent_uuid: String? = ""
    var activity_uuid: String? = ""
    var offer_note: String? = ""
    var mapping_initiator: Int? = 0
    var detail: String? = ""
    var pay: Int = 0
    var self_else: Int = 0
    var distance: String? = null //this is for calculate distance
}
