package org.helpinout.billonlights.service

import com.avneesh.crashreporter.CrashReporter
import com.google.firebase.iid.FirebaseInstanceId
import org.helpinout.billonlights.model.dagger.PreferencesService
import org.helpinout.billonlights.model.database.AppDatabase
import org.helpinout.billonlights.model.database.entity.*
import org.helpinout.billonlights.model.retrofit.NetworkApiProvider
import org.helpinout.billonlights.utils.HELP_TYPE_OFFER
import org.helpinout.billonlights.utils.HELP_TYPE_REQUEST
import org.helpinout.billonlights.utils.Utils.Companion.currentDateTime
import org.helpinout.billonlights.utils.getIcon
import org.json.JSONArray
import org.json.JSONObject

class LocationService(private val preferencesService: PreferencesService, private val service: NetworkApiProvider, private val db: AppDatabase) {

    suspend fun getNewAddActivityResult(body: AddData): ActivityResponses {
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
                mainData.put("data", bodyJson)
            } catch (e: Exception) {
                CrashReporter.logException(e)
            }
        } catch (e: Exception) {
            CrashReporter.logException(e)
        }
        return mainData.toString()
    }

    suspend fun sendOfferRequests(activity_type: Int, activity_uuid: String, list: List<ActivityAddDetail>): ActivityResponses {
        return service.makeCall { it.networkApi.sendOfferRequestsAsync(createOffererRequester(activity_type, activity_uuid, list)) }
    }

    private fun createOffererRequester(activity_type: Int, activity_uuid: String, list: List<ActivityAddDetail>): String {
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
                bodyJson.put("activity_type", activity_type)
                bodyJson.put("activity_uuid", activity_uuid)
                if (activity_type == HELP_TYPE_REQUEST) {
                    val jsonArray = JSONArray()
                    list.forEach {
                        val user = JSONObject()
                        user.put("activity_uuid", it.activity_uuid)
                        jsonArray.put(user)
                    }
                    bodyJson.put("offerer", jsonArray)
                } else {
                    val jsonArray = JSONArray()
                    list.forEach {
                        val user = JSONObject()
                        user.put("activity_uuid", it.activity_uuid)
                        jsonArray.put(user)
                    }
                    bodyJson.put("requester", jsonArray)
                }
                mainData.put("data", bodyJson)
            } catch (e: Exception) {
                CrashReporter.logException(e)
            }
        } catch (e: Exception) {
            CrashReporter.logException(e)
        }
        return mainData.toString()
    }

    suspend fun getNewSuggestionResult(body: SuggestionRequest): ActivityResponses {
        return service.makeCall {
            it.networkApi.getActivitySuggestionResponseAsync(createNewSuggestionRequest(body))
        }
    }

    private fun createNewSuggestionRequest(body: SuggestionRequest): String {
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
                bodyJson.put("activity_type", body.activity_type)
                bodyJson.put("activity_uuid", body.activity_uuid)
                bodyJson.put("geo_location", body.latitude + "," + body.longitude)
                bodyJson.put("geo_accuracy", body.accuracy)
                mainData.put("data", bodyJson)
            } catch (e: Exception) {
                CrashReporter.logException(e)
            }
        } catch (e: Exception) {
            CrashReporter.logException(e)
        }
        return mainData.toString()
    }

    suspend fun getRequesterSummary(): OfferHelpResponses {
        return service.makeCall { it.networkApi.getRequestSummaryResponseAsync(createRequesterSummaryRequest()) }
    }

    private fun createRequesterSummaryRequest(): String {
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
                mainData.put("data", bodyJson)
            } catch (e: Exception) {
                CrashReporter.logException(e)
            }
        } catch (e: Exception) {
            CrashReporter.logException(e)
        }
        return mainData.toString()
    }

    suspend fun deleteMappingFromServer(item: AddCategoryDbItem, activityType: Int): String {
        return service.makeCall {
            it.networkApi.getMappingDeleteResponseAsync(createMappingDeleteRequest(item, activityType))
        }
    }

    private fun createMappingDeleteRequest(item: AddCategoryDbItem, activityType: Int): String {
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
                bodyJson.put("activity_uuid", item.parent_uuid)
                bodyJson.put("activity_type", activityType)

                val jsonArray = JSONArray()
                val json = JSONObject()
                json.put("activity_uuid", item.activity_uuid)
                jsonArray.put(json)

                if (item.activity_type == HELP_TYPE_REQUEST) {
                    bodyJson.put("requester", jsonArray)
                } else {
                    bodyJson.put("offerer", jsonArray)

                }
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
            it.networkApi.getActivityDeleteResponseAsync(createActivityDeleteRequest(uuid, activityType))
        }
    }

    private fun createActivityDeleteRequest(uuid: String, activityType: Int): String {
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

    suspend fun makeRating(item: AddCategoryDbItem, activityType: Int, rating: String, recommendOther: Int, comments: String): String {
        return service.makeCall {
            it.networkApi.getMappingRatingResponseAsync(createRatingRequest(item, activityType, rating, recommendOther, comments))
        }
    }

    private fun createRatingRequest(item: AddCategoryDbItem, activityType: Int, rating: String, recommendOther: Int, comments: String): String {
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

                bodyJson.put("activity_uuid", item.parent_uuid)
                bodyJson.put("activity_type", activityType)

                val jsonArray = JSONArray()
                val json = JSONObject()
                json.put("activity_uuid", item.activity_uuid)

                val rateReportJson = JSONObject()
                rateReportJson.put("rating", rating)
                rateReportJson.put("recommend_other", recommendOther)
                rateReportJson.put("comments", comments)

                json.put("rate_report", rateReportJson)

                jsonArray.put(json)

                if (item.activity_type == HELP_TYPE_REQUEST) {
                    bodyJson.put("requester", jsonArray)
                } else {
                    bodyJson.put("offerer", jsonArray)

                }
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

    suspend fun makeCallTracking(item: AddCategoryDbItem, activityType: Int): String {
        return service.makeCall {
            it.networkApi.getCallInitiateResponseAsync(createMappingDeleteRequest(item, activityType))
        }
    }

    suspend fun getUserRequestsOfferList(activityType: String): ActivityResponses {
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

    fun getMyRequestsOrOffers(offerType: Int, initiator: Int): List<AddCategoryDbItem> {
        val finalRequest = ArrayList<AddCategoryDbItem>()
        val requestList = db.getAddItemDao().getMyRequestsOrOffers(offerType)
        var type = offerType
        type = if (offerType == HELP_TYPE_OFFER) {
            HELP_TYPE_REQUEST
        } else {
            HELP_TYPE_OFFER
        }
        val mappingList = db.getMappingDao().getMyRequestsOrOffers(type)

        requestList.forEach { item ->

            val initiatorMappingList = mappingList.filter { it.request_mapping_initiator == initiator }

            val mapping = mappingList.filter { it.parent_uuid == item.activity_uuid }

            if (mapping.isNotEmpty()) {
                item.isMappingExist = true
            }

            if (initiatorMappingList.isEmpty()) {
                item.icon = item.activity_category.getIcon()
                finalRequest.add(item)
            } else {
                val newList = ArrayList<AddCategoryDbItem>()
                initiatorMappingList.forEach { t ->
                    val m = AddCategoryDbItem()
                    m.name = t.first_name + " " + t.last_name
                    m.activity_type = t.activity_type!!
                    m.parent_uuid = t.parent_uuid
                    m.activity_uuid = t.activity_uuid ?: ""
                    m.date_time = t.date_time ?: ""
                    m.activity_category = t.activity_category ?: 0
                    m.activity_count = 0
                    m.geo_location = t.geo_location
                    m.mobile_no = t.mobile_no
                    newList.add(m)
                }
                finalRequest.addAll(newList)
            }
        }

        return finalRequest
    }

    suspend fun getPeopleResponse(peopleHelp: AddData): ActivityResponses {
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
        return service.makeCall { it.networkApi.getActivityAmbulancesResponseAsync(createAmbulanceRequest(ambulanceHelp)) }
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

    fun saveFoodItemsToDb(addItemList: ArrayList<AddCategoryDbItem>): Boolean {
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

    fun saveMappingToDb(mappingList: ArrayList<MappingDetail>): Boolean {
        return try {
            val typeArray = mappingList.toTypedArray()
            val count = db.getMappingDao().insertMappingRecord(*typeArray)
            count.isNotEmpty()
        } catch (e: Exception) {
            false
        }

    }

    fun deleteMappingFromDb(item: AddCategoryDbItem): Boolean {
        return try {
            db.getMappingDao().deleteMapping(item.activity_uuid, item.parent_uuid!!)
            true
        } catch (e: Exception) {
            false
        }
    }

}