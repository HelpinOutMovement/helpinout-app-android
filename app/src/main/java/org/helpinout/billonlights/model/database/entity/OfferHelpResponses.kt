package org.helpinout.billonlights.model.database.entity

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class OfferHelpResponses {
    var status: Int = 0
    var message: String? = null

    @SerializedName("data")
    @Expose
    var data = ArrayList<OfferHelpDisplay>()
}

class OfferHelpDisplay {
    var activity_category: Int = 0
    var total: Int = 0
    var near: Int = 0
}