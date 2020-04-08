package com.triline.billionlights.model.database.entity

import org.json.JSONArray

open class Offer

data class OfferReceived(var name: String = "", var detail: String, var mobile: String, var icon: Int) : Offer()

data class RequestSent(var name: String = "", var detail: String, var icon: Int) : Offer()


data class AddData(var uuid: String = "", var activityType: Int = 0, var address: String = "", var activityCategory: Int = 0, var activityCount: Int = 0, var offerer: String? = "", var requester: String? = "", var pay: Int = 0, var conditions: String? = "") {
    var activity_detail = ArrayList<ActivityDetail>()
    var activity_detail_string = JSONArray()
}

data class SuggestionData(var activityType: Int = 0, var activityCategory: Int = 0, var activityCount: Int = 0, var latitude: String = "", var longitude: String = "", var accuracy: String = "") {
    var activity_detail = ArrayList<ActivityDetail>()
    var activity_detail_string = JSONArray()
}

data class ActivityDetail(var detail: String? = "", var qty: String? = "")

data class PlaceData(var latitude: Double? = null, var longnitude: Double? = null, var location: String? = "", var name: String? = null)

class BottomHelp(val category: Int, var name: String, val rating: Float, var description: String)
