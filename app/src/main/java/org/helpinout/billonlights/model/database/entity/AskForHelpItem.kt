package org.helpinout.billonlights.model.database.entity

data class OfferHelpItem(var title: String, val type: Int, val icon: Int = 0, var nearRequest: Int = 0, var totalRequest: Int = 0)

data class LanguageItem(var title: String, val type: Int, val code: String)