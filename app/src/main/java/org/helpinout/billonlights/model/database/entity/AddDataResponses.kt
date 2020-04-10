package org.helpinout.billonlights.model.database.entity

class AddDataResponses {
    var status: Int = 0
    var message: String = ""
    var data: AddDataData? = null
}

class AddDataData {

    var offers: List<ActivityAddDetail>? = null
    var requests: List<ActivityAddDetail>? = null
    var activity_detail: List<ActivityAddDetail>? = null
}

class ActivityAddDetail {
    var id: Int = 0
    var activity_type: Int = 0
    var activity_uuid: String = ""
    var date_time: String = ""
    var activity_category: Int = 0
    var activity_count: Int = 0
    var geo_location: String? = ""
    var detail: String? = null
    var quantity: Int? = null
    var request_help_id: Int = 0
    var volunters_required: Int = 0
    var volunters_detail: String? = ""
    var volunters_quantity: String? = ""
    var technical_personal_required: Int = 0
    var technical_personal_detail: String? = ""
    var technical_personal_quantity: String? = ""
    var status: Int = 0
}