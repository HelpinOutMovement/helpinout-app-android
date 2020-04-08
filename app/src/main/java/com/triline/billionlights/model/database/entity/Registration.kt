package com.triline.billionlights.model.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey


@Entity(tableName = "registration", indices = [Index(value = ["mobileNumber"], unique = true)])
class Registration(
    var first_name: String = "",
    var last_name: String? = "",
    var mobileNumber: String = "",
    var orgName: String? = "",
    var orgType: String = "",
    var unitDivision: String? = "",
    var numberVisible: Boolean = false
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}