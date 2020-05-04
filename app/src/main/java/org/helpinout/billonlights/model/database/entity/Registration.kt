package org.helpinout.billonlights.model.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey


@Entity(tableName = "registration", indices = [Index(value = ["mobile_no"], unique = true)])
class Registration {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
    var country_code: String = ""
    var mobile_no: String = ""
    var first_name: String = ""
    var last_name: String? = ""
    var mobile_no_visibility: Int = 0
    var user_type: Int? = 0
    var org_name: String? = ""
    var org_type: Int? = null
    var profile_name: String = ""
    var org_division: String? = ""
}