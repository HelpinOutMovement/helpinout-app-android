package org.helpinout.billonlights.model.database.entity

import org.json.JSONArray

class AddData {
    var activity_type: Int = 0
    var activity_uuid: String = ""
    var date_time: String = ""
    var address: String = ""
    var activity_category: Int = 0
    var activity_count: Int = 0
    var geo_location: String? = null
    var pay: Int = 0
    var offerer: String? = ""
    var requester: String? = ""
    var conditions: String? = ""
    var selfHelp: Int = 0
    var qty: String? = ""
    var activity_detail = ArrayList<ActivityDetail>()
    var activity_detail_string = JSONArray()
}

class SuggestionRequest {
    var activity_type: Int = 0
    var activity_category: Int = 0
    var activity_uuid: String = ""
    var latitude: Double = 0.0
    var longitude: Double = 0.0
    var accuracy: String = ""
}

class ActivityDetail {
    var detail: String? = ""
    var qty: String? = ""
    var id: Int = 0
    var volunters_required: Int? = 0
    var volunters_detail: String? = ""
    var volunters_quantity: String? = ""
    var technical_personal_required: Int = 0
    var technical_personal_detail: String? = ""
    var technical_personal_quantity: String? = ""
    var status: Int = 0
}





