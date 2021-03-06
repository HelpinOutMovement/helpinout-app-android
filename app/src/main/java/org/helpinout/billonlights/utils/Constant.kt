package org.helpinout.billonlights.utils

import org.helpinout.billonlights.R

const val CALENDER_FORMAT = "yyyy-MM-dd hh:mm:ss"
const val TIME_AGO_FORMAT = "yyyy-MM-dd HH:mm:ss"

const val INTERNET_ERROR1 = "java.net.ConnectException: Failed to connect to"
const val INTERNET_ERROR2 = "java.net.UnknownHostException: Unable to resolve host"
const val INTERNET_ERROR_3 = "java.net.SocketException"
const val INTERNET_ERROR_4 = "javax.net.ssl.sslexception"

const val SERVER_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ"
const val APP_INSTALL_FORMAT = "dd-MM-yyyy hh:mm a"
const val TERMS_OF_SERVICE = "https://helpinout.org/docs/terms_of_service.html"
const val PRIVACY_POLICY = "https://helpinout.org/docs/privacy.html"
const val FEEDBACK_URL = "https://docs.google.com/forms/d/e/1FAIpQLScCCGSQpN9CJLV4uLXJ34ZmB5lrH3Ex_IJkfxjI_an0BOAWuQ/viewform"
const val SECOND_MILLIS = 1000
const val MINUTE_MILLIS = 60 * SECOND_MILLIS
const val HOUR_MILLIS = 60 * MINUTE_MILLIS
const val DAY_MILLIS = 24 * HOUR_MILLIS
const val DISPLAY_DATE_FORMAT = "dd MMMM yyyy, h:mm a"
const val UPDATE_INTERVAL_IN_MILLISECONDS: Long = 10000
const val FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2
const val REQUEST_APP_SETTINGS = 101
const val DOUBLE_CLICK_TIME = 1000
const val AUTOCOMPLETE_REQUEST_CODE = 42
const val HELP_TYPE = "HelpType"
const val SELF_ELSE = "self_else"
const val SUGGESTION_DATA = "SuggestionData"
const val UPDATE_PROFILE = "UpdateProfile"
const val UPDATE_LANGAUGE = "UpdateLanguage"
const val OFFER_TYPE = "OfferType"
const val INITIATOR = "initiator"
const val LOCATION = "location"
const val DATA_REFRESH = "DataRefresh"
const val BEDGE_REFRESH = "BedgeRefresh"
const val RADIUS = "Radius"
const val LANGUAGE_STEP = 0
const val REGISTRATION_STEP = 3
const val HOME_STEP = 4
const val WEB_URL = "WebUrl"
const val ENGLISH = 1
const val HINDI = 2
const val KANNAD = 3
const val TAMIL = 4
const val TELUGU = 5
const val MARATHI = 4
const val GUJRATI = 5
const val RUSSIAN = 6
const val ORIYA = 7
const val ALLOW_NUMBER1 = "+917303767448" //
const val ALLOW_NUMBER2 = "+918800579215"


const val ENGLISH_CODE = "en"
const val HINDI_CODE = "hi"
const val KANNAD_CODE = "kn"
const val MARATHI_CODE = "mr"
const val GUJRATI_CODE = "gu"
const val TAMIL_CODE = "ta"
const val ORIYA_CODE = "or"
const val TELUGU_CODE = "te"
const val RUSSIAN_CODE = "ru"

val languageList = listOf(R.id.nav_english, R.id.nav_hindi, R.id.nav_kannad, R.id.nav_marathi, R.id.nav_gujrati, R.id.nav_tamil, R.id.nav_oriya, R.id.nav_telugu, R.id.nav_russian)
val languageCode = listOf(ENGLISH_CODE, HINDI_CODE, KANNAD_CODE, MARATHI_CODE, GUJRATI_CODE, TAMIL_CODE, ORIYA_CODE, TELUGU_CODE, RUSSIAN_CODE)


const val CATEGORY_TYPE = "Item_Type"
const val CATEGORY_OTHERS = 0
const val CATEGORY_FOOD = 1
const val CATEGORY_SHELTER = 3
const val CATEGORY_MED_PPE = 4
const val CATEGORY_TESTING = 5
const val CATEGORY_MEDICINES = 6
const val CATEGORY_AMBULANCE = 7
const val CATEGORY_MEDICAL_EQUIPMENT = 8

const val CATEGORY_MEDICAL_VOLUNTEERS = 9
const val CATEGORY_MEDICAL_FRUITS_VEGETABLES = 10
const val CATEGORY_MEDICAL_TRANSPORT = 11
const val CATEGORY_MEDICAL_ANIMAL_SUPPORT = 12
const val CATEGORY_MEDICAL_GIVEAWAYS = 13
const val CATEGORY_MEDICAL_PAID_WORK = 14


const val SELECTED_INDEX = "SelectedIndex"

const val HELP_TYPE_REQUEST = 1
const val HELP_TYPE_OFFER = 2

const val SEEN_YES = 1
const val SEEN_NO = 0

//notification constants
const val TITLE = "title"
const val ACTIVITY_TYPE = "activity_type"
const val ACTION = "action"
const val ACTIVITY_UUID = "activity_uuid"
const val SENDER_NAME = "sender_name"
const val FROM_NOTIFICATION = "FromNotification"

const val NOTIFICATION_REQUEST_CANCELLED = 3
const val NOTIFICATION_OFFER_CANCELLED = 4