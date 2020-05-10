package org.helpinout.billonlights.model.database.entity


class LocationSuggestionResponses {
    var status: Int = 0
    var message: String = ""
    var data: LocationData? = null
}

class LocationData {
    var my_requests_match: Int = 0
    var my_offers_match: Int = 0
}
class ActivityResponses {
    var status: Int = 0
    var message: String = ""
    var data: AddDataData? = null
}

class AddDataData {
    var offers: List<ActivityAddDetail>? = null
    var requests: List<ActivityAddDetail>? = null
    var activity_detail: List<ActivityAddDetail>? = null// this item for single food items
    var mapping: List<Mapping>? = null// for help provider activity do not delete
    var activity_uuid: String = ""
    var activity_type: Int? = 0
    var date_time: String = ""
}

class ActivityAddDetail {
    var activity_type: Int = 0
    var activity_uuid: String = ""
    var date_time: String = ""
    var activity_category: Int = 0
    var activity_count: Int = 0
    var geo_location: String? = ""
    var offer_note: String? = ""
    var request_note: String? = ""
    var isSelected: Boolean = false// for checked and unchecked
    var isEnable: Boolean = true
    var detail: String? = null
    var quantity: Int? = null
    var activity_detail: List<ActivityDetailSubItem>? = null
    var mapping: List<Mapping>? = null
    var user_detail: MappingDetail? = null
    var self_else:Int=0
    var pay: Int = 0
}

class ActivityDetailSubItem {
    var detail: String? = ""
    var quantity: String? = ""
    var volunters_required: Int? = 0
    var volunters_detail: String? = ""
    var volunters_quantity: String? = ""
    var technical_personal_required: Int = 0
    var technical_personal_detail: String? = ""
    var technical_personal_quantity: String? = ""
}