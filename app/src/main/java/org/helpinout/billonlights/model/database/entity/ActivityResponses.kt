package org.helpinout.billonlights.model.database.entity

class ActivityResponses {
    var status: Int = 0
    var message: String = ""
    var data: AddDataData? = null
}

class AddDataData {
    var offers: List<ActivityAddDetail>? = null
    var requests: List<ActivityAddDetail>? = null
    var activity_detail: List<ActivityAddDetail>? = null// this item for single food items
    var mapping: List<Mapping>? = null
    var activity_uuid:String=""
    var activity_count:String=""
}

class ActivityAddDetail {
    var id: Int = 0
    var activity_type: Int = 0
    var activity_uuid: String = ""
    var date_time: String = ""
    var activity_category: Int = 0
    var activity_count: Int = 0
    var geo_location: String? = ""
    var offer_condition: String? = ""
    var isSelected: Boolean = false// for checked and unchecked
    var detail: String? = null
    var quantity: Int? = null
    var app_user_id: Int = 0
    var status: Int = 0
    var activity_detail: List<ActivityDetailSubItem>? = null
    var app_user_detail: MappingDetail? = null
}

class ActivityDetailSubItem {
    var detail: String? = ""
    var quantity: String? = ""
    var request_help_id: Int = 0
    var volunters_required: Int? = 0
    var volunters_detail: String? = ""
    var volunters_quantity: String? = ""
    var technical_personal_required: Int = 0
    var technical_personal_detail: String? = ""
    var technical_personal_quantity: String? = ""
    var status: Int = 0
}