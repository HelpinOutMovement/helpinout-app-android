package org.helpinout.billonlights.service

import com.avneesh.crashreporter.CrashReporter
import com.google.firebase.iid.FirebaseInstanceId
import org.helpinout.billonlights.model.dagger.PreferencesService
import org.helpinout.billonlights.model.database.AppDatabase
import org.helpinout.billonlights.model.database.entity.*
import org.helpinout.billonlights.model.retrofit.NetworkApiProvider
import org.helpinout.billonlights.utils.CATEGORY_AMBULANCE
import org.helpinout.billonlights.utils.CATEGORY_PEOPLE
import org.helpinout.billonlights.utils.HELP_TYPE_OFFER
import org.helpinout.billonlights.utils.HELP_TYPE_REQUEST
import org.helpinout.billonlights.utils.Utils.Companion.currentDateTime
import org.json.JSONArray
import org.json.JSONObject

class LocationService(private val preferencesService: PreferencesService, private val service: NetworkApiProvider, private val db: AppDatabase) {


    suspend fun getNewAddActivityResult(body: AddData, address: String): ActivityResponses {
        val response = service.makeCall {
            it.networkApi.getActivityNewAddResponseAsync(createAddActivityRequest(body))
        }
        if (response.status == 1 && response.data != null) saveRequestToDatabase(body, response, address)
        return response
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
                body.activity_detail_string = getDetail(body)
                bodyJson.put("activity_uuid", body.activity_uuid)
                bodyJson.put("activity_type", body.activity_type)
                bodyJson.put("geo_location", preferencesService.latitude.toString() + "," + preferencesService.longitude)
                bodyJson.put("geo_accuracy", preferencesService.gpsAccuracy)
                bodyJson.put("address", body.address)
                bodyJson.put("self_else", body.selfHelp)

                bodyJson.put("activity_category", body.activity_category)
                bodyJson.put("activity_count", body.activity_count)
                bodyJson.put("activity_detail", body.activity_detail_string)

                if (body.activity_type == HELP_TYPE_OFFER) bodyJson.put("offer_note", body.conditions)

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

    private fun getDetail(addData: AddData): JSONArray {
        val jsonArray = JSONArray()
        addData.activity_count = addData.activity_detail.size
        addData.activity_detail.forEach { item ->
            val jsonObj = JSONObject()
            jsonObj.put("detail", item.detail)
            jsonObj.put("qty", item.qty)
            jsonArray.put(jsonObj)
        }
        return jsonArray
    }

    private fun saveRequestToDatabase(addData: AddData, first: ActivityResponses?, address: String) {
        first?.data?.let { item ->
            val addItemList = ArrayList<AddCategoryDbItem>()
            var itemDetail = ""
            item.activity_detail?.forEachIndexed { index, detail ->
                if (!detail.detail.isNullOrEmpty()) {
                    itemDetail += detail.detail
                }
                if (detail.quantity != null) {
                    itemDetail += " (" + detail.quantity + ")"
                }
                if (item.activity_detail!!.size - 1 != index) {
                    itemDetail += "<br/>"
                }
            }
            val categoryItem = AddCategoryDbItem()
            categoryItem.activity_type = addData.activity_type
            categoryItem.detail = itemDetail
            categoryItem.activity_uuid = addData.activity_uuid
            categoryItem.date_time = addData.date_time
            categoryItem.activity_category = addData.activity_category
            categoryItem.activity_count = addData.activity_count
            categoryItem.geo_location = addData.geo_location
            categoryItem.conditions = addData.conditions
            categoryItem.address = address
            categoryItem.status = 1
            categoryItem.self_else = addData.selfHelp
            categoryItem.pay = addData.pay

            addItemList.add(categoryItem)
            saveFoodItemToDatabase(addItemList)
        }
    }

    private fun saveFoodItemToDatabase(addItemList: ArrayList<AddCategoryDbItem>): Boolean {
        val items = addItemList.toTypedArray()
        val count = db.getAddItemDao().insertMultipleRecords(*items)
        return count.isNotEmpty()
    }


    suspend fun getNewSuggestionResult(body: SuggestionRequest, radius: Float): ActivityResponses {
        val response = service.makeCall {
            it.networkApi.getActivitySuggestionResponseAsync(createNewSuggestionRequest(body, radius))
        }

        response.data?.let { it ->
            it.offers?.let { detailList ->
                detailList.forEach { detailItem ->
                    try {
                        detailItem.user_detail?.distance = detailItem.distance
                        try {
                            var detail = ""

                            when (detailItem.activity_category) {
                                CATEGORY_AMBULANCE -> {
                                    detailItem.activity_detail?.forEachIndexed { _, it ->
                                        if (!it.quantity.isNullOrEmpty()) {
                                            detail += it.quantity
                                        }
                                    }
                                }
                                CATEGORY_PEOPLE -> {
                                    detailItem.activity_detail?.forEachIndexed { _, it ->

                                        if (!it.volunters_detail.isNullOrEmpty()) {
                                            detail += it.volunters_detail?.take(30)

                                        }
                                        if (!it.volunters_quantity.isNullOrEmpty()) {
                                            detail += " (" + it.volunters_quantity + ")"

                                        }
                                        if (!it.technical_personal_detail.isNullOrEmpty()) {
                                            if (detail.isNotEmpty()) {
                                                detail += "<br/>"
                                            }

                                            if (!it.technical_personal_detail.isNullOrEmpty()) {
                                                detail += it.technical_personal_detail?.take(30)
                                            }
                                            if (!it.technical_personal_quantity.isNullOrEmpty()) {
                                                detail += " (" + it.technical_personal_quantity + ")"
                                            }
                                        }

                                    }
                                }
                                else -> {
                                    detailItem.activity_detail?.forEachIndexed { index, it ->
                                        if (!it.detail.isNullOrEmpty()) {
                                            detail += it.detail?.take(30)
                                        }
                                        if (!it.quantity.isNullOrEmpty()) {
                                            detail += " (" + it.quantity + ")"
                                        }


                                        if (detailItem.activity_detail!!.size - 1 != index) {
                                            detail += "<br/>"
                                        }
                                    }
                                }
                            }
                            detailItem.user_detail?.detail = detail

                        } catch (e: Exception) {

                        }

                    } catch (e: Exception) {
                    }
                }
            }
            it.requests?.let { detailList ->
                detailList.forEach { detailItem ->
                    try {
                        detailItem.user_detail?.distance = detailItem.distance
                        try {
                            var detail = ""

                            when (detailItem.activity_category) {
                                CATEGORY_AMBULANCE -> {
                                    detailItem.activity_detail?.forEachIndexed { _, it ->
                                        if (!it.quantity.isNullOrEmpty()) {
                                            detail += it.quantity
                                        }
                                    }
                                }
                                CATEGORY_PEOPLE -> {
                                    detailItem.activity_detail?.forEachIndexed { _, it ->

                                        if (!it.volunters_detail.isNullOrEmpty()) {
                                            detail += it.volunters_detail?.take(30)

                                        }
                                        if (!it.volunters_quantity.isNullOrEmpty()) {
                                            detail += " (" + it.volunters_quantity + ")"

                                        }
                                        if (!it.technical_personal_detail.isNullOrEmpty()) {
                                            if (detail.isNotEmpty()) {
                                                detail += "<br/>"
                                            }

                                            if (!it.technical_personal_detail.isNullOrEmpty()) {
                                                detail += it.technical_personal_detail?.take(30)
                                            }
                                            if (!it.technical_personal_quantity.isNullOrEmpty()) {
                                                detail += " (" + it.technical_personal_quantity + ")"
                                            }
                                        }

                                    }
                                }
                                else -> {
                                    detailItem.activity_detail?.forEachIndexed { index, it ->
                                        if (!it.detail.isNullOrEmpty()) {
                                            detail += it.detail?.take(30)
                                        }
                                        if (!it.quantity.isNullOrEmpty()) {
                                            detail += " (" + it.quantity + ")"
                                        }
                                        if (detailItem.activity_detail!!.size - 1 != index) {
                                            detail += "<br/>"
                                        }
                                    }
                                }
                            }
                            detailItem.user_detail?.detail = detail
                        } catch (e: Exception) {

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

    suspend fun getRequesterSummary(radius: Float): OfferHelpResponses {
        return service.makeCall { it.networkApi.getRequestSummaryResponseAsync(createRequesterSummaryRequest(radius)) }
    }

    private fun createRequesterSummaryRequest(radius: Float): String {
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

    suspend fun getUserCurrentLocationResult(radius: Float): LocationSuggestionResponses {
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


    suspend fun getPeopleResponse(peopleHelp: AddData, activityDetail: ActivityDetail): ActivityResponses {
        val response = service.makeCall { it.networkApi.getActivityNewAddResponseAsync(createPeopleRequest(peopleHelp)) }
        savePeopleRequestToDatabase(peopleHelp, activityDetail, response)
        return response
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
                bodyJson.put("self_else", peopleHelp.selfHelp)
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

    private fun savePeopleRequestToDatabase(peopleHelp: AddData, activityDetail: ActivityDetail, first: ActivityResponses?) {
        first?.data?.let { item ->
            val addItemList = ArrayList<AddCategoryDbItem>()

            val singleItem = AddCategoryDbItem()
            singleItem.activity_type = peopleHelp.activity_type
            singleItem.activity_uuid = peopleHelp.activity_uuid
            singleItem.date_time = peopleHelp.date_time
            singleItem.activity_category = peopleHelp.activity_category
            singleItem.activity_count = peopleHelp.activity_count
            singleItem.geo_location = peopleHelp.geo_location
            singleItem.address = peopleHelp.address
            singleItem.conditions = peopleHelp.conditions
            singleItem.pay = peopleHelp.pay
            var detail = ""

            if (!activityDetail.volunters_detail.isNullOrEmpty() || !activityDetail.volunters_quantity.isNullOrEmpty()) {
                detail += "<b> %1s </b><br/>"
                detail += activityDetail.volunters_detail?.take(30) + " (" + activityDetail.volunters_quantity + ")"

            }

            if (!activityDetail.technical_personal_detail.isNullOrEmpty() || !activityDetail.technical_personal_quantity.isNullOrEmpty()) {
                if (detail.isNotEmpty()) {
                    detail += "<br/>"
                }
                detail += "<b> %2s </b><br/>"
                detail += activityDetail.technical_personal_detail?.take(30) + " (" + activityDetail.technical_personal_quantity + ")"
            }

            singleItem.detail = detail
            singleItem.volunters_required = 0
            singleItem.volunters_detail = activityDetail.volunters_detail
            singleItem.volunters_quantity = activityDetail.volunters_quantity
            singleItem.technical_personal_required = activityDetail.technical_personal_required
            singleItem.technical_personal_detail = activityDetail.technical_personal_detail
            singleItem.technical_personal_quantity = activityDetail.technical_personal_quantity
            singleItem.status = 1

            addItemList.add(singleItem)
            saveFoodItemToDatabase(addItemList)
        }
    }

    suspend fun getAmbulanceHelpResponse(ambulanceHelp: AddData, suggestionData: SuggestionRequest): ServerResponse {
        val response = service.makeCall { it.networkApi.getActivityAmbulancesResponseAsync(createAmbulanceRequest(ambulanceHelp)) }
        saveRequestToDatabase(ambulanceHelp, suggestionData)
        return response
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
                if (ambulance.activity_type == HELP_TYPE_OFFER) bodyJson.put("offer_note", ambulance.conditions)
                else bodyJson.put("request_note", ambulance.conditions)
                bodyJson.put("self_else", ambulance.selfHelp)
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

    private fun saveRequestToDatabase(ambulanceHelp: AddData, suggestionData: SuggestionRequest) {
        val addItemList = ArrayList<AddCategoryDbItem>()
        val item = AddCategoryDbItem()
        item.activity_type = ambulanceHelp.activity_type
        item.activity_uuid = ambulanceHelp.activity_uuid
        item.date_time = ambulanceHelp.date_time
        item.activity_category = ambulanceHelp.activity_category
        item.activity_count = ambulanceHelp.activity_count
        item.geo_location = ambulanceHelp.geo_location
        item.address = ambulanceHelp.address
        item.qty = ambulanceHelp.qty ?: ""
        item.conditions = ambulanceHelp.conditions
        item.status = 1
        item.pay = ambulanceHelp.pay

        suggestionData.activity_type = ambulanceHelp.activity_type
        suggestionData.latitude = preferencesService.latitude
        suggestionData.longitude = preferencesService.longitude
        suggestionData.accuracy = preferencesService.gpsAccuracy
        addItemList.add(item)
        saveFoodItemsToDb(addItemList)
    }

    internal fun saveFoodItemsToDb(addItemList: ArrayList<AddCategoryDbItem>): Boolean {
        val items = addItemList.toTypedArray()
        val count = db.getAddItemDao().insertMultipleRecords(*items)
        return count.isNotEmpty()
    }
}