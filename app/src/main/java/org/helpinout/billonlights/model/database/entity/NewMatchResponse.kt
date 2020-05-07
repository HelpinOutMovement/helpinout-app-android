package org.helpinout.billonlights.model.database.entity

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class NewMatchResponses {
    var status: Int = 0

    var message: String? = null

    @SerializedName("data")
    @Expose
    var data: MatchesData? = null
}

class MatchesData {
    var requests: List<Request>? = null
    var offers: List<Request>? = null
}

class Request {
    var activity_uuid: String? = null
    var new_matches: Int? = null
}