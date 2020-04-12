package org.helpinout.billonlights.model.database.entity

import org.json.JSONArray


//open class Offer

//data class OfferReceived(var name: String = "", var detail: String, var mobile: String, var icon: Int) : Offer()


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
    var qty: String? = ""
    var activity_detail = ArrayList<ActivityDetail>()
    var activity_detail_string = JSONArray()
}

class SuggestionRequest {
    var activity_type: Int = 0
    var activity_category: Int = 0
    var activity_uuid: String = ""
    var latitude: String = ""
    var longitude: String = ""
    var accuracy: String = ""
}

class ActivityDetail {
    var detail: String? = ""
    var qty: String? = ""
    var id: Int = 0
    var request_help_id: Int? = 0
    var volunters_required: Int? = 0
    var volunters_detail: String? = ""
    var volunters_quantity: String? = ""
    var technical_personal_required: Int = 0
    var technical_personal_detail: String? = ""
    var technical_personal_quantity: String? = ""
    var status: Int = 0
}

data class PlaceData(var latitude: Double? = null, var longnitude: Double? = null, var location: String? = "", var name: String? = null)





