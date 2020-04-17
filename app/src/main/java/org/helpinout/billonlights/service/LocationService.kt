package org.helpinout.billonlights.service

import android.content.Context
import com.avneesh.crashreporter.CrashReporter
import com.google.firebase.iid.FirebaseInstanceId
import org.helpinout.billonlights.model.dagger.PreferencesService
import org.helpinout.billonlights.model.database.AppDatabase
import org.helpinout.billonlights.model.database.entity.*
import org.helpinout.billonlights.model.retrofit.NetworkApiProvider
import org.helpinout.billonlights.utils.*
import org.helpinout.billonlights.utils.Utils.Companion.currentDateTime
import org.json.JSONArray
import org.json.JSONObject
import timber.log.Timber

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
                bodyJson.put("geo_location", preferencesService.latitude.toString() + "," + preferencesService.longitude)
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

    suspend fun getNewSuggestionResult(body: SuggestionRequest, radius: Float): ActivityResponses {
        val response = service.makeCall {
            it.networkApi.getActivitySuggestionResponseAsync(createNewSuggestionRequest(body, radius))
        }

        response.data?.let { it ->
            it.offers?.let { detailList ->
                detailList.forEach { detail ->
                    try {
                        val destinationLatLong = detail.geo_location?.split(",")
                        if (!destinationLatLong.isNullOrEmpty()) {
                            val lat1 = preferencesService.latitude
                            val long1 = preferencesService.longitude
                            val lat2 = destinationLatLong[0].toDouble()
                            val long2 = destinationLatLong[1].toDouble()
                            detail.user_detail?.distance = Utils.getDistance(lat1, long1, lat2, long2)
                        }
                    } catch (e: Exception) {
                    }
                }
            }
            it.requests?.let { detailList ->
                detailList.forEach { detail ->
                    try {
                        val destinationLatLong = detail.geo_location?.split(",")
                        if (!destinationLatLong.isNullOrEmpty()) {
                            val lat1 = preferencesService.latitude
                            val long1 = preferencesService.longitude
                            val lat2 = destinationLatLong[0].toDouble()
                            val long2 = destinationLatLong[1].toDouble()
                            detail.user_detail?.distance = Utils.getDistance(lat1, long1, lat2, long2)
                        }
                    } catch (e: Exception) {
                    }
                }
            }
        }
        return response
    }

    private fun createNewSuggestionRequest(body: SuggestionRequest, radius: Float): String {
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
                bodyJson.put("geo_location", body.latitude.toString() + "," + body.longitude)
                bodyJson.put("geo_accuracy", body.accuracy)
                bodyJson.put("radius", radius)
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
                bodyJson.put("geo_location", preferencesService.latitude.toString() + "," + preferencesService.longitude)
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

    suspend fun getUserCurrentLocationResult(radius: Float): ActivityResponses {
        return service.makeCall { it.networkApi.getUserLocationResponseAsync(createLocationRequest(radius)) }
    }

    private fun createLocationRequest(radius: Float): String {
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
                bodyJson.put("geo_location", preferencesService.latitude.toString() + "," + preferencesService.longitude)
                bodyJson.put("geo_accuracy", preferencesService.gpsAccuracy)
                bodyJson.put("radius", radius)

                mainData.put("data", bodyJson)
            } catch (e: Exception) {
                CrashReporter.logException(e)
            }
        } catch (e: Exception) {
            CrashReporter.logException(e)
        }
        return mainData.toString()
    }

    suspend fun deleteMappingFromServer(parent_uuid: String?, activity_uuid: String, activityType: Int): String {
        return service.makeCall {
            it.networkApi.getMappingDeleteResponseAsync(createMappingDeleteRequest(parent_uuid, activity_uuid, activityType))
        }
    }

    private fun createMappingDeleteRequest(parent_uuid: String?, activity_uuid: String, activityType: Int): String {
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
                bodyJson.put("activity_uuid", parent_uuid)
                bodyJson.put("activity_type", activityType)

                val jsonArray = JSONArray()
                val json = JSONObject()
                json.put("activity_uuid", activity_uuid)
                jsonArray.put(json)

                if (activityType == HELP_TYPE_REQUEST) {
                    bodyJson.put("offerer", jsonArray)
                } else {
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

    suspend fun makeRating(parent_uuid: String?, activity_uuid: String, activityType: Int, rating: String, recommendOther: Int, comments: String): String {
        return service.makeCall {
            it.networkApi.getMappingRatingResponseAsync(createRatingRequest(parent_uuid, activity_uuid, activityType, rating, recommendOther, comments))
        }
    }

    private fun createRatingRequest(parent_uuid: String?, activity_uuid: String, activityType: Int, rating: String, recommendOther: Int, comments: String): String {
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

                bodyJson.put("activity_uuid", parent_uuid)
                bodyJson.put("activity_type", activityType)

                val jsonArray = JSONArray()
                val json = JSONObject()
                json.put("activity_uuid", activity_uuid)

                val rateReportJson = JSONObject()
                rateReportJson.put("rating", rating)
                rateReportJson.put("recommend_other", recommendOther)
                rateReportJson.put("comments", comments)

                json.put("rate_report", rateReportJson)

                jsonArray.put(json)

                if (activityType == HELP_TYPE_REQUEST) {
                    bodyJson.put("offerer", jsonArray)
                } else {
                    bodyJson.put("requester", jsonArray)
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

    suspend fun makeCallTracking(parent_uuid: String?, activity_uuid: String, activityType: Int): String {
        return service.makeCall {
            it.networkApi.getCallInitiateResponseAsync(createMappingDeleteRequest(parent_uuid, activity_uuid, activityType))
        }
    }

    suspend fun getUserRequestsOfferList(context: Context, activityType: Int): ActivityResponses {
        val response = service.makeCall {
            it.networkApi.getUserRequestOfferListResponseAsync(createOfferRequest(activityType))
        }
        try {
            val offers = response.data?.offers
            val requests = response.data?.requests
            if (!offers.isNullOrEmpty()) {
                insertItemToDatabase(context, offers)
            }
            if (!requests.isNullOrEmpty()) {
                insertItemToDatabase(context, requests)
            }
        } catch (e: Exception) {

        }
        return response
    }

    private fun insertItemToDatabase(context: Context, offers: List<ActivityAddDetail>) {
        val addDataList = ArrayList<AddCategoryDbItem>()
        offers.forEach { offer ->
            try {
                val item = AddCategoryDbItem()
                item.activity_type = offer.activity_type
                item.activity_uuid = offer.activity_uuid

                var itemDetail = ""
                offer.activity_detail?.forEachIndexed { index, it ->

                    if (offer.activity_category == CATEGORY_PEOPLE) {
                        //for people
                        item.volunters_required = it.volunters_required
                        item.volunters_detail = it.volunters_detail
                        item.volunters_quantity = it.volunters_quantity
                        item.technical_personal_required = it.technical_personal_required
                        item.technical_personal_detail = it.technical_personal_detail
                        item.technical_personal_quantity = it.technical_personal_quantity

                        if (!it.volunters_detail.isNullOrEmpty() || !it.volunters_quantity.isNullOrEmpty()) {
                            itemDetail += it.volunters_detail + "(" + it.volunters_quantity + ")"

                        }
                        if (!it.technical_personal_detail.isNullOrEmpty()) {
                            if (itemDetail.isNotEmpty()) {
                                itemDetail += ","
                            }
                            itemDetail += it.technical_personal_detail + "(" + it.technical_personal_quantity + ")"
                        }

                    } else if (offer.activity_category == CATEGORY_AMBULANCE) {
                        item.qty = it.quantity
                        itemDetail = ""
                    } else {
                        itemDetail += it.detail + "(" + it.quantity + ")"
                        if (offer.activity_detail!!.size - 1 != index) {
                            itemDetail += ","
                        }
                    }
                }

                offer.mapping?.forEach { mapping ->
                    if (mapping.offer_detail != null) {
                        mapping.offer_detail?.user_detail?.parent_uuid = offer.activity_uuid
                        mapping.offer_detail?.user_detail?.activity_type = offer.activity_type
                        mapping.offer_detail?.user_detail?.activity_uuid = mapping.offer_detail?.activity_uuid
                        mapping.offer_detail?.user_detail?.activity_category = mapping.offer_detail?.activity_category
                        mapping.offer_detail?.user_detail?.date_time = mapping.offer_detail?.date_time
                        mapping.offer_detail?.user_detail?.geo_location = mapping.offer_detail?.geo_location
                        mapping.offer_detail?.user_detail?.offer_condition = mapping.offer_detail?.offer_condition
                        mapping.offer_detail?.user_detail?.mapping_initiator = mapping.mapping_initiator
                    } else if (mapping.request_detail != null) {
                        mapping.request_detail?.user_detail?.parent_uuid = offer.activity_uuid
                        mapping.request_detail?.user_detail?.activity_type = offer.activity_type
                        mapping.request_detail?.user_detail?.activity_uuid = mapping.request_detail?.activity_uuid
                        mapping.request_detail?.user_detail?.activity_category = mapping.request_detail?.activity_category
                        mapping.request_detail?.user_detail?.date_time = mapping.request_detail?.date_time
                        mapping.request_detail?.user_detail?.geo_location = mapping.request_detail?.geo_location
                        mapping.request_detail?.user_detail?.offer_condition = mapping.request_detail?.offer_condition
                        mapping.request_detail?.user_detail?.mapping_initiator = mapping.mapping_initiator
                    }
                }
                val mappingList = ArrayList<MappingDetail>()
                offer.mapping?.forEach {
                    if (it.offer_detail != null) {
                        mappingList.add(it.offer_detail!!.user_detail!!)
                    } else {
                        mappingList.add(it.request_detail!!.user_detail!!)
                    }
                }
                if (mappingList.isNotEmpty()) saveMappingToDb(mappingList)

                item.detail = itemDetail
                item.activity_uuid = offer.activity_uuid
                item.date_time = offer.date_time
                item.activity_category = offer.activity_category
                item.activity_count = offer.activity_count
                item.geo_location = offer.geo_location
                item.address = context.getAddress(preferencesService.latitude, preferencesService.longitude)
                item.status = 1
                addDataList.add(item)
            } catch (e: Exception) {
                Timber.d("")
            }
        }
        addDataList.reverse()
        saveFoodItemsToDb(addDataList)
    }

    private fun createOfferRequest(activityType: Int): String {
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
        val mappingList = db.getMappingDao().getMyRequestsOrOffers(offerType, initiator)
        requestList.forEach { item ->
            val mapping = mappingList.filter { it.parent_uuid == item.activity_uuid }
            if (mapping.isNotEmpty()) {
                item.isMappingExist = true
            }
            finalRequest.add(item)
            item.totalOffers = mapping.size
        }
        return finalRequest
    }


    fun getRequestDetails(offerType: Int, initiator: Int, activity_uuid: String): List<MappingDetail> {
        val response = db.getMappingDao().getMyRequestsOrOffersByUuid(offerType, initiator, activity_uuid)
        response.forEach { detail ->
            try {
                val destinationLatLong = detail.geo_location?.split(",")
                if (!destinationLatLong.isNullOrEmpty()) {
                    val lat1 = preferencesService.latitude
                    val long1 = preferencesService.longitude
                    val lat2 = destinationLatLong[0].toDouble()
                    val long2 = destinationLatLong[1].toDouble()
                    detail.distance = Utils.getDistance(lat1, long1, lat2, long2)
                }
            } catch (e: Exception) {
            }
        }
        return response
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
                bodyJson.put("geo_location", preferencesService.latitude.toString() + "," + preferencesService.longitude)
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

    fun deleteMappingFromDb(parent_uuid: String?, activity_uuid: String): Boolean {
        return try {
            db.getMappingDao().deleteMapping(activity_uuid, parent_uuid!!)
            true
        } catch (e: Exception) {
            false
        }
    }

}