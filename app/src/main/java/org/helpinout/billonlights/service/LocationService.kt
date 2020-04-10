package org.helpinout.billonlights.service

import com.avneesh.crashreporter.CrashReporter
import com.google.firebase.iid.FirebaseInstanceId
import org.helpinout.billonlights.model.dagger.PreferencesService
import org.helpinout.billonlights.model.database.AppDatabase
import org.helpinout.billonlights.model.database.entity.*
import org.helpinout.billonlights.model.retrofit.NetworkApiProvider
import org.helpinout.billonlights.utils.HELP_TYPE_OFFER
import org.helpinout.billonlights.utils.Utils.Companion.currentDateTime
import org.helpinout.billonlights.utils.Utils.Companion.getTimeZoneString
import org.json.JSONObject
import java.util.*

class LocationService(private val preferencesService: PreferencesService, private val service: NetworkApiProvider, private val db: AppDatabase) {

    suspend fun getNewAddActivityResult(body: AddData): AddDataResponses {
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

                bodyJson.put("activity_uuid", body.activity_uuid)
                bodyJson.put("activity_type", body.activity_type)
                bodyJson.put("geo_location", preferencesService.latitude + "," + preferencesService.longitude)
                bodyJson.put("geo_accuracy", preferencesService.gpsAccuracy)
                bodyJson.put("address", body.address)
                bodyJson.put("activity_category", body.activity_category)
                bodyJson.put("activity_count", body.activity_count)
                bodyJson.put("activity_detail", body.activity_detail_string)

                if (body.activity_type == HELP_TYPE_OFFER) bodyJson.put("offer_condition", body.conditions)

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

    suspend fun deleteActivity(uuid: String, activityType: Int): DeleteDataResponses {
        return service.makeCall {
            it.networkApi.getMappingDeleteResponseAsync(createDeleteRequest(uuid, activityType))
        }
    }

    private fun createDeleteRequest(uuid: String, activityType: Int): String {
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

    suspend fun makeCallTracking(uuid: String, activityType: Int): String {
        return service.makeCall {
            it.networkApi.getCallInitiateResponseAsync(createDeleteRequest(uuid, activityType))
        }
    }

    suspend fun getUserRequestsOfferList(activityType: String): AddDataResponses {
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

    fun getMyRequestsOrOffers(offerType: Int): List<AddItem> {
        return db.getAddItemDao().getMyRequestsOrOffers(offerType)
    }

    suspend fun getPeopleResponse(peopleHelp: AddData): AddDataResponses {
        return service.makeCall { it.networkApi.getActivityNewAddResponseAsync(createPeopleRequest(peopleHelp)) }
    }

    private fun createPeopleRequest(peopleHelp: AddData): String {
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
                bodyJson.put("activity_uuid", peopleHelp.activity_uuid)
                bodyJson.put("activity_type", peopleHelp.activity_type)
                bodyJson.put("activity_category", peopleHelp.activity_category)

                val activity_detailjson = JSONObject()


                activity_detailjson.put("volunters_required", peopleHelp.activity_detail[0].volunters_required)
                activity_detailjson.put("volunters_detail", peopleHelp.activity_detail[0].volunters_detail)
                activity_detailjson.put("volunters_quantity", peopleHelp.activity_detail[0].volunters_quantity)
                activity_detailjson.put("technical_personal_required", peopleHelp.activity_detail[0].technical_personal_required)
                activity_detailjson.put("technical_personal_detail", peopleHelp.activity_detail[0].technical_personal_detail)
                activity_detailjson.put("technical_personal_quantity", peopleHelp.activity_detail[0].technical_personal_quantity)

                bodyJson.put("activity_detail", activity_detailjson)

                bodyJson.put("address", peopleHelp.address)
                bodyJson.put("pay", peopleHelp.pay)
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

    suspend fun getAmbulanceHelpResponse(ambulanceHelp: AddData): ServerResponse {
        return service.makeCall { it.networkApi.getActivityAmbulancesponseAsync(createAmbulanceRequest(ambulanceHelp)) }
    }

    private fun createAmbulanceRequest(ambulance: AddData): String {
        val mainData = JSONObject()
        try {
            mainData.put("app_id", preferencesService.appId)
            mainData.put("imei_no", preferencesService.imeiNumber)
            mainData.put("app_version", preferencesService.appVersion)
            mainData.put("date_time", ambulance.date_time)
            val bodyJson = JSONObject()
            try {
                FirebaseInstanceId.getInstance().token?.let {
                    preferencesService.firebaseId = FirebaseInstanceId.getInstance().token!!
                }
                bodyJson.put("activity_uuid", ambulance.activity_uuid)
                bodyJson.put("activity_type", ambulance.activity_type)
                bodyJson.put("address", ambulance.address)
                bodyJson.put("activity_category", ambulance.activity_category)

                val activity_detailjson = JSONObject()
                activity_detailjson.put("qty", ambulance.qty)
                bodyJson.put("activity_detail", activity_detailjson)

                bodyJson.put("qty", ambulance.qty)
                bodyJson.put("geo_location", ambulance.geo_location)
                bodyJson.put("geo_accuracy", preferencesService.gpsAccuracy)
                bodyJson.put("time_zone", getTimeZoneString())
                bodyJson.put("pay", ambulance.pay)

                mainData.put("data", bodyJson)
            } catch (e: Exception) {
                CrashReporter.logException(e)
            }
        } catch (e: Exception) {
            CrashReporter.logException(e)
        }
        return mainData.toString()
    }

    fun saveFoodItemsToDb(addItemList: ArrayList<AddItem>): Boolean {
        val items = addItemList.toTypedArray()
        val count = db.getAddItemDao().insertMultipleRecords(*items)
        return count.isNotEmpty()
    }

    fun deleteActivityFromDb(activityUuid: String?): Boolean {
        activityUuid?.let {
            db.getAddItemDao().deleteActivity(it)
            return true
        }
        return false
    }
}