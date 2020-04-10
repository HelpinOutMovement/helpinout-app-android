package org.helpinout.billonlights.model.database.entity

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class ServerResponse {
    var status: Int = -1
    var message: String? = null
}

class RegistrationResponse {
    var status: Int = -1
    var message: String? = null
    var data: Data? = null
}

class LoginResponse {
    var status: Int = -1
    var message: String? = null

    @SerializedName("data")
    @Expose
    var data: RegistrationData? = null
}

class Data {
    var app_id: String? = null
    var user_id: String? = null
}


class RegistrationData {
    var user_id: Int? = null
    var app_id: String? = null
    var user_detail: Registration? = null
}
