package com.triline.billionlights.model.database.entity

data class AskForHelpItem(var title: String, val type: Int, val icon: Int = 0)

data class OfferHelpItem(
    var title: String,
    val type: Int,
    val icon: Int = 0,
    var nearRequest: Int = 0,
    val totalRequest: Int
)

data class LanguageItem(var title: String, val type: Int, val code: String)