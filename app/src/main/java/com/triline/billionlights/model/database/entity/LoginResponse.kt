package com.triline.billionlights.model.database.entity

class LoginResponse {
    var status: Int = -1
    var message: String? = null
    var data: Data? = null
}

class Data {
    var app_id: String? = null
    var user_id: String? = null
}