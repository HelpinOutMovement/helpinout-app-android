package com.triline.billionlights.service

import com.avneesh.crashreporter.CrashReporter
import com.google.firebase.iid.FirebaseInstanceId
import com.triline.billionlights.model.dagger.PreferencesService
import com.triline.billionlights.model.database.AppDatabase
import com.triline.billionlights.model.database.entity.AddData
import com.triline.billionlights.model.database.entity.SuggestionData
import com.triline.billionlights.model.retrofit.NetworkApiProvider
import com.triline.billionlights.utils.Utils.Companion.currentDateTime
import com.triline.billionlights.utils.Utils.Companion.getTimeZoneString
import org.json.JSONObject

class LocationService(private val preferencesService: PreferencesService, private val service: NetworkApiProvider, private val db: AppDatabase) {

    suspend fun getNewAddActivityResult(body: AddData): String {
        return service.makeCall {
            it.networkApi.getActivityNewAddResponseAsync(createAddActivityRequest(body))
        }
    }

    private fun createAddActivityRequest(body: AddData): String {
        val mainData = JSONObject()
        try {
            mainData.put("app_id", preferencesService.appId)
            mainData.put("imei_no", preferencesService.imeiNumber)
            mainData.put("app_version", preferencesService.appVersion)
            mainData.put("date_time", currentDateTime())
            val bodyJson = JSONObject()
            try {
                FirebaseInstanceId.getInstance().token?.let {
                    preferencesService.firebaseId = FirebaseInstanceId.getInstance().token!!
                }

                bodyJson.put("uuid", body.uuid)
                bodyJson.put("activity_type", body.activityType)
                bodyJson.put("geo_location", preferencesService.latitude + "," + preferencesService.longitude)
                bodyJson.put("geo_accuracy", preferencesService.gpsAccuracy)
                bodyJson.put("address", body.address)
                bodyJson.put("activity_category", body.activityCategory)
                bodyJson.put("activity_count", body.activityCount)
                bodyJson.put("activity_detail", body.activity_detail_string)
                bodyJson.put("offerer", body.offerer)
                bodyJson.put("requester", body.requester)
                bodyJson.put("pay", body.pay)
                bodyJson.put("time_zone", getTimeZoneString())
                mainData.put("data", bodyJson)
            } catch (e: Exception) {
                CrashReporter.logException(e)
            }
        } catch (e: Exception) {
            CrashReporter.logException(e)
        }
        return mainData.toString()
    }

    suspend fun getNewSuggestionResult(body: SuggestionData): String {
        return service.makeCall {
            it.networkApi.getActivitySuggestionResponseAsync(createNewSuggestionRequest(body))
        }
    }

    private fun createNewSuggestionRequest(body: SuggestionData): String {
        val mainData = JSONObject()
        try {
            mainData.put("app_id", preferencesService.appId)
            mainData.put("imei_no", preferencesService.imeiNumber)
            mainData.put("app_version", preferencesService.appVersion)
            mainData.put("date_time", currentDateTime())
            val bodyJson = JSONObject()
            try {
                FirebaseInstanceId.getInstance().token?.let {
                    preferencesService.firebaseId = FirebaseInstanceId.getInstance().token!!
                }
                bodyJson.put("activity_type", body.activityType)
                bodyJson.put("activity_category", body.activityCategory)
                bodyJson.put("activity_count", body.activityCount)
                bodyJson.put("activity_detail", body.activity_detail_string)
                bodyJson.put("geo_location", body.latitude + "," + body.longitude)
                bodyJson.put("geo_accuracy", body.accuracy)
                bodyJson.put("time_zone", getTimeZoneString())
                mainData.put("data", bodyJson)
            } catch (e: Exception) {
                CrashReporter.logException(e)
            }
        } catch (e: Exception) {
            CrashReporter.logException(e)
        }
        return mainData.toString()
    }

    suspend fun getUserCurrentLocationResult(): String {
        return service.makeCall { it.networkApi.getUserLocationResponseAsync(createLocationRequest()) }
    }

    private fun createLocationRequest(): String {
        val mainData = JSONObject()
        try {
            mainData.put("app_id", preferencesService.appId)
            mainData.put("imei_no", preferencesService.imeiNumber)
            mainData.put("app_version", preferencesService.appVersion)
            mainData.put("date_time", currentDateTime())
            val bodyJson = JSONObject()
            try {
                FirebaseInstanceId.getInstance().token?.let {
                    preferencesService.firebaseId = FirebaseInstanceId.getInstance().token!!
                }
                bodyJson.put("geo_location", preferencesService.latitude + "," + preferencesService.longitude)
                bodyJson.put("geo_accuracy", preferencesService.gpsAccuracy)
                bodyJson.put("time_zone", getTimeZoneString())
                mainData.put("data", bodyJson)
            } catch (e: Exception) {
                CrashReporter.logException(e)
            }
        } catch (e: Exception) {
            CrashReporter.logException(e)
        }
        return mainData.toString()
    }

    suspend fun deleteActivity(mappingId: String, uuid: String): String {
        return service.makeCall {
            it.networkApi.getMappingDeleteResponseAsync(createDeleteRequest(mappingId, uuid))
        }
    }

    private fun createDeleteRequest(mappingId: String, uuid: String): String {
        val mainData = JSONObject()
        try {
            mainData.put("app_id", preferencesService.appId)
            mainData.put("imei_no", preferencesService.imeiNumber)
            mainData.put("app_version", preferencesService.appVersion)
            mainData.put("date_time", currentDateTime())
            val bodyJson = JSONObject()
            try {
                FirebaseInstanceId.getInstance().token?.let {
                    preferencesService.firebaseId = FirebaseInstanceId.getInstance().token!!
                }
                bodyJson.put("activity_uuid", uuid)
                bodyJson.put("mapping_id", mappingId)
                mainData.put("data", bodyJson)
            } catch (e: Exception) {
                CrashReporter.logException(e)
            }
        } catch (e: Exception) {
            CrashReporter.logException(e)
        }
        return mainData.toString()
    }

    suspend fun makeRating(mappingId: String, uuid: String, rating: String, recommendOther: Int): String {
        return service.makeCall {
            it.networkApi.getMappingRatingResponseAsync(createRatingRequest(mappingId, uuid, rating, recommendOther))
        }
    }

    private fun createRatingRequest(mappingId: String, uuid: String, rating: String, recommendOther: Int): String {
        val mainData = JSONObject()
        try {
            mainData.put("app_id", preferencesService.appId)
            mainData.put("imei_no", preferencesService.imeiNumber)
            mainData.put("app_version", preferencesService.appVersion)
            mainData.put("date_time", currentDateTime())
            val bodyJson = JSONObject()
            try {
                FirebaseInstanceId.getInstance().token?.let {
                    preferencesService.firebaseId = FirebaseInstanceId.getInstance().token!!
                }
                bodyJson.put("activity_uuid", uuid)
                bodyJson.put("mapping_id", mappingId)
                bodyJson.put("rating", rating)
                bodyJson.put("recommend_other", recommendOther)
                mainData.put("data", bodyJson)
            } catch (e: Exception) {
                CrashReporter.logException(e)
            }
        } catch (e: Exception) {
            CrashReporter.logException(e)
        }
        return mainData.toString()
    }

    suspend fun makeCallTracking(mappingId: String, uuid: String): String {
        return service.makeCall {
            it.networkApi.getCallInitiateResponseAsync(createDeleteRequest(mappingId, uuid))
        }
    }

    suspend fun getUserRequestsOfferList(activityType: String): String {
        return service.makeCall {
            it.networkApi.getUserRequestOfferListResponseAsync(createOfferRequest(activityType))
        }
    }


    private fun createOfferRequest(activityType: String): String {
        val mainData = JSONObject()
        try {
            mainData.put("app_id", preferencesService.appId)
            mainData.put("imei_no", preferencesService.imeiNumber)
            mainData.put("app_version", preferencesService.appVersion)
            mainData.put("date_time", currentDateTime())
            val bodyJson = JSONObject()
            try {
                FirebaseInstanceId.getInstance().token?.let {
                    preferencesService.firebaseId = FirebaseInstanceId.getInstance().token!!
                }
                bodyJson.put("activity_type", activityType)
                mainData.put("data", bodyJson)
            } catch (e: Exception) {
                CrashReporter.logException(e)
            }
        } catch (e: Exception) {
            CrashReporter.logException(e)
        }
        return mainData.toString()
    }
}