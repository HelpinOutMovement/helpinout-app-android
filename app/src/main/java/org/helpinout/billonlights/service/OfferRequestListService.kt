package org.helpinout.billonlights.service

import android.app.Application
import android.content.Context
import com.avneesh.crashreporter.CrashReporter
import com.google.firebase.iid.FirebaseInstanceId
import org.helpinout.billonlights.model.dagger.PreferencesService
import org.helpinout.billonlights.model.database.AppDatabase
import org.helpinout.billonlights.model.database.entity.*
import org.helpinout.billonlights.model.retrofit.NetworkApiProvider
import org.helpinout.billonlights.utils.*
import org.json.JSONArray
import org.json.JSONObject
import timber.log.Timber

class OfferRequestListService(private val preferencesService: PreferencesService, private val service: NetworkApiProvider, private val db: AppDatabase, private val app: Application) {


    fun getMyRequestsOrOffers(offerType: Int, initiator: Int, context: Context): ArrayList<AddCategoryDbItem> {

        val finalRequest = ArrayList<AddCategoryDbItem>()
        val requestList = db.getAddItemDao().getMyRequestsOrOffers(offerType)

        val mappingList = db.getMappingDao().getMyRequestsOrOffers(offerType)

        val notificationList = db.getNotificationDao().getNotificationItems(offerType)

        requestList.forEach { item ->
            val mapping = mappingList.filter { it.parent_uuid == item.activity_uuid }
            finalRequest.add(item)
            item.offersReceived = mapping.filter { it.mapping_initiator != initiator }.size

            val notificationItem = notificationList.find { it.parent_uuid == item.activity_uuid && it.mapping_initiator != initiator && it.activity_type == offerType }

            notificationItem?.let {
                item.show_notification = 1
            }

            item.requestSent = mapping.filter { it.mapping_initiator == initiator }.size
        }
        finalRequest.forEach { item ->
            item.name = context.getString(item.activity_category.getName())
            item.icon = item.activity_category.getIcon()
        }
        return finalRequest

    }

    suspend fun sendOfferRequests(radius: Float, lat: Double, longitude: Double, isSendToAll: Int, activity_type: Int, activity_uuid: String, list: List<ActivityAddDetail>): ActivityResponses {
        val response = service.makeCall { it.networkApi.sendOfferRequestsAsync(createOffererRequester(radius, lat, longitude, isSendToAll, activity_type, activity_uuid, list)) }
        parseResponse(response)
        return response
    }

    private fun parseResponse(responses: ActivityResponses) {
        if (responses.data != null) {
            responses.data!!.mapping?.forEach { mapping ->
                if (mapping.offer_detail != null) {
                    mapping.offer_detail?.user_detail?.parent_uuid = responses.data!!.activity_uuid
                    mapping.offer_detail?.user_detail?.activity_type = responses.data!!.activity_type
                    mapping.offer_detail?.user_detail?.activity_uuid = mapping.offer_detail?.activity_uuid
                    mapping.offer_detail?.user_detail?.activity_category = mapping.offer_detail?.activity_category
                    mapping.offer_detail?.user_detail?.date_time = mapping.mapping_time
                    mapping.offer_detail?.user_detail?.geo_location = mapping.offer_detail?.geo_location
                    mapping.offer_detail?.user_detail?.offer_note = mapping.offer_detail?.offer_note
                    mapping.offer_detail?.user_detail?.mapping_initiator = mapping.mapping_initiator
                    mapping.offer_detail?.user_detail?.date_time = mapping.mapping_time
                    mapping.offer_detail?.user_detail?.self_else = mapping.offer_detail?.self_else ?: 0
                    mapping.offer_detail?.user_detail?.pay = mapping.offer_detail!!.pay
                    mapping.offer_detail?.user_detail?.distance = mapping.distance
                    setOfferDetail(mapping)
                } else if (mapping.request_detail != null) {
                    mapping.request_detail?.user_detail?.parent_uuid = responses.data!!.activity_uuid
                    mapping.request_detail?.user_detail?.activity_type = responses.data!!.activity_type
                    mapping.request_detail?.user_detail?.activity_uuid = mapping.request_detail?.activity_uuid
                    mapping.request_detail?.user_detail?.activity_category = mapping.request_detail?.activity_category
                    mapping.request_detail?.user_detail?.date_time = mapping.mapping_time
                    mapping.request_detail?.user_detail?.geo_location = mapping.request_detail?.geo_location
                    mapping.request_detail?.user_detail?.offer_note = mapping.request_detail?.request_note
                    mapping.request_detail?.user_detail?.mapping_initiator = mapping.mapping_initiator
                    mapping.request_detail?.user_detail?.date_time = mapping.mapping_time
                    mapping.request_detail?.user_detail?.self_else = mapping.request_detail?.self_else ?: 0
                    mapping.request_detail?.user_detail?.pay = mapping.request_detail!!.pay
                    mapping.request_detail?.user_detail?.distance = mapping.distance
                    setRequestDetail(mapping)
                }
            }
            if (!responses.data!!.mapping.isNullOrEmpty()) {
                val mappingList = ArrayList<MappingDetail>()
                responses.data!!.mapping?.forEach {
                    if (it.offer_detail != null) {
                        mappingList.add(it.offer_detail!!.user_detail!!)
                    } else {
                        mappingList.add(it.request_detail!!.user_detail!!)
                    }
                }
                saveMappingToDb(mappingList)
            }
        }
    }

    private fun createOffererRequester(radius: Float, lat: Double, longitude: Double, isSendToAll: Int, activity_type: Int, activity_uuid: String, list: List<ActivityAddDetail>): String {
        val mainData = JSONObject()
        try {
            mainData.put("app_id", preferencesService.appId)
            mainData.put("imei_no", preferencesService.imeiNumber)
            mainData.put("app_version", preferencesService.appVersion)
            mainData.put("date_time", Utils.currentDateTime())
            val bodyJson = JSONObject()
            try {
                FirebaseInstanceId.getInstance().token?.let {
                    preferencesService.firebaseId = FirebaseInstanceId.getInstance().token!!
                }
                bodyJson.put("activity_type", activity_type)
                bodyJson.put("activity_uuid", activity_uuid)

                if (isSendToAll == 1) {
                    bodyJson.put("all_requester", 1)
                    bodyJson.put("radius", radius)
                    bodyJson.put("geo_location", "$lat,$longitude")

                } else {
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

    suspend fun getUserRequestsOfferList(context: Context, activityType: Int, activity_uuid: String = ""): ActivityResponses {
        val response = service.makeCall {
            it.networkApi.getUserRequestOfferListResponseAsync(createOfferRequest(activityType, activity_uuid))
        }
        try {
            val offers = response.data?.offers
            val requests = response.data?.requests
            if (!offers.isNullOrEmpty()) {
                insertItemToDatabase(context, offers, preferencesService.offerFirstTime, true)
                preferencesService.offerFirstTime = false
            }
            if (!requests.isNullOrEmpty()) {
                insertItemToDatabase(context, requests, preferencesService.requestFirstTime, false)
                preferencesService.requestFirstTime = false
            }
        } catch (e: Exception) {

        }
        return response
    }

    private fun createOfferRequest(activityType: Int, activity_uuid: String = ""): String {
        val mainData = JSONObject()
        try {
            mainData.put("app_id", preferencesService.appId)
            mainData.put("imei_no", preferencesService.imeiNumber)
            mainData.put("app_version", preferencesService.appVersion)
            mainData.put("date_time", Utils.currentDateTime())
            val bodyJson = JSONObject()
            try {
                FirebaseInstanceId.getInstance().token?.let {
                    preferencesService.firebaseId = FirebaseInstanceId.getInstance().token!!
                }
                if (activity_uuid.isNotEmpty()) {
                    bodyJson.put("activity_uuid", activity_uuid)
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

    private fun insertItemToDatabase(context: Context, offers: List<ActivityAddDetail>, isFirstTime: Boolean, isOffer: Boolean) {
        val addDataList = ArrayList<AddCategoryDbItem>()
        val notificationDb = db.getNotificationDao()
        offers.forEach { offer ->
            try {
                val item = AddCategoryDbItem()
                item.activity_type = offer.activity_type
                item.activity_uuid = offer.activity_uuid

                var itemDetail = ""
                offer.activity_detail?.forEachIndexed { index, it ->

                    when (offer.activity_category) {
                        CATEGORY_AMBULANCE, CATEGORY_MEDICAL_VOLUNTEERS, CATEGORY_MEDICAL_FRUITS_VEGETABLES, CATEGORY_MEDICAL_TRANSPORT, CATEGORY_MEDICAL_ANIMAL_SUPPORT, CATEGORY_MEDICAL_GIVEAWAYS, CATEGORY_MEDICAL_PAID_WORK -> {
                            itemDetail = it.detail ?: ""
                        }
                        else -> {
                            if (!it.detail.isNullOrEmpty()) {
                                itemDetail += it.detail?.take(30)
                            }
                            if (!it.quantity.isNullOrEmpty()) {
                                itemDetail += " (" + it.quantity + ")"
                            }

                            if (offer.activity_detail!!.size - 1 != index) {
                                itemDetail += "<br/>"
                            }
                        }
                    }
                }

                offer.mapping?.forEach { mapping ->
                    if (mapping.offer_detail != null) {

                        mapping.offer_detail?.user_detail?.parent_uuid = offer.activity_uuid
                        mapping.offer_detail?.user_detail?.activity_type = offer.activity_type
                        mapping.offer_detail?.user_detail?.activity_uuid = mapping.offer_detail?.activity_uuid
                        mapping.offer_detail?.user_detail?.activity_category = mapping.offer_detail?.activity_category
                        mapping.offer_detail?.user_detail?.date_time = mapping.mapping_time
                        mapping.offer_detail?.user_detail?.geo_location = mapping.offer_detail?.geo_location
                        mapping.offer_detail?.user_detail?.offer_note = mapping.offer_detail?.offer_note
                        mapping.offer_detail?.user_detail?.mapping_initiator = mapping.mapping_initiator
                        mapping.offer_detail?.user_detail?.pay = mapping.offer_detail!!.pay
                        mapping.offer_detail?.user_detail?.self_else = mapping.offer_detail!!.self_else
                        mapping.offer_detail?.user_detail?.distance = mapping.distance

                        if (isFirstTime) {
                            val notificationItem = NotificationItem(offer.activity_type, offer.activity_uuid, SEEN_YES)
                            notificationItem.activity_uuid = mapping.offer_detail?.activity_uuid ?: ""
                            notificationItem.mapping_initiator = mapping.mapping_initiator ?: 0
                            notificationDb.insertItems(notificationItem)
                        } else {
                            val singleItem = notificationDb.getNotificationItems(offer.activity_type, offer.activity_uuid, mapping.offer_detail?.activity_uuid ?: "", mapping.mapping_initiator ?: 0)
                            if (singleItem == null) {
                                val notificationItem = NotificationItem(offer.activity_type, offer.activity_uuid, SEEN_NO)
                                notificationItem.activity_uuid = mapping.offer_detail?.activity_uuid ?: ""
                                notificationItem.mapping_initiator = mapping.mapping_initiator ?: 0
                                notificationDb.insertItems(notificationItem)
                            }
                        }
                        setOfferDetail(mapping)
                    } else if (mapping.request_detail != null) {
                        mapping.request_detail?.user_detail?.parent_uuid = offer.activity_uuid
                        mapping.request_detail?.user_detail?.activity_type = offer.activity_type
                        mapping.request_detail?.user_detail?.activity_uuid = mapping.request_detail?.activity_uuid
                        mapping.request_detail?.user_detail?.activity_category = mapping.request_detail?.activity_category
                        mapping.request_detail?.user_detail?.date_time = mapping.mapping_time
                        mapping.request_detail?.user_detail?.geo_location = mapping.request_detail?.geo_location
                        mapping.request_detail?.user_detail?.offer_note = mapping.request_detail?.request_note
                        mapping.request_detail?.user_detail?.mapping_initiator = mapping.mapping_initiator
                        mapping.request_detail?.user_detail?.pay = mapping.request_detail!!.pay
                        mapping.request_detail?.user_detail?.self_else = mapping.request_detail!!.self_else
                        mapping.request_detail?.user_detail?.distance = mapping.distance

                        if (isFirstTime) {
                            val notificationItem = NotificationItem(offer.activity_type, offer.activity_uuid, SEEN_YES)
                            notificationItem.activity_uuid = mapping.request_detail?.activity_uuid ?: ""
                            notificationItem.mapping_initiator = mapping.mapping_initiator ?: 0
                            notificationDb.insertItems(notificationItem)
                        } else {
                            val singleItem = notificationDb.getNotificationItems(offer.activity_type, offer.activity_uuid, mapping.request_detail?.activity_uuid ?: "", mapping.mapping_initiator ?: 0)
                            if (singleItem == null) {
                                val notificationItem = NotificationItem(offer.activity_type, offer.activity_uuid, SEEN_NO)
                                notificationItem.activity_uuid = mapping.request_detail?.activity_uuid ?: ""
                                notificationItem.mapping_initiator = mapping.mapping_initiator ?: 0
                                notificationDb.insertItems(notificationItem)
                            }
                        }

                        setRequestDetail(mapping)
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
                db.getMappingDao().deleteMapping(offer.activity_uuid)
                if (mappingList.isNotEmpty()) {
                    saveMappingToDb(mappingList)
                }

                item.detail = itemDetail
                item.activity_uuid = offer.activity_uuid
                item.date_time = offer.date_time
                item.activity_category = offer.activity_category
                item.activity_count = offer.activity_count
                item.geo_location = offer.geo_location
                item.address = context.getAddress(preferencesService.latitude, preferencesService.longitude)
                item.status = 1
                item.self_else = offer.self_else
                item.conditions = if (isOffer) offer.offer_note else offer.request_note

                item.pay = offer.pay
                addDataList.add(item)
            } catch (e: Exception) {
                Timber.d("")
            }
        }
        addDataList.reverse()
        saveFoodItemsToDb(addDataList)
    }

    internal fun saveFoodItemsToDb(addItemList: ArrayList<AddCategoryDbItem>): Boolean {
        val items = addItemList.toTypedArray()
        val count = db.getAddItemDao().insertMultipleRecords(*items)

        return count.isNotEmpty()
    }

    private fun setOfferDetail(mapping: Mapping) {
        try {
            var detail = ""
            mapping.offer_detail?.activity_detail?.forEachIndexed { index, it ->
                when (mapping.offer_detail?.activity_category) {
                    CATEGORY_AMBULANCE, CATEGORY_MEDICAL_VOLUNTEERS, CATEGORY_MEDICAL_FRUITS_VEGETABLES, CATEGORY_MEDICAL_TRANSPORT, CATEGORY_MEDICAL_ANIMAL_SUPPORT, CATEGORY_MEDICAL_GIVEAWAYS, CATEGORY_MEDICAL_PAID_WORK -> {
                        detail = it.detail ?: ""
                    }
                    else -> {
                        if (!it.detail.isNullOrEmpty()) {
                            detail += it.detail?.take(30)
                        }
                        if (!it.quantity.isNullOrEmpty()) {
                            detail += " (" + it.quantity + ")"
                        }
                        if (mapping.offer_detail?.activity_detail!!.size - 1 != index) {
                            detail += "<br/>"
                        }
                    }
                }
            }

            mapping.offer_detail?.user_detail?.detail = detail
        } catch (e: Exception) {
            Timber.d("")
        }
    }

    private fun setRequestDetail(mapping: Mapping) {
        try {
            var detail = ""

            mapping.request_detail?.activity_detail?.forEachIndexed { index, it ->
                when (mapping.request_detail?.activity_category) {
                    CATEGORY_AMBULANCE, CATEGORY_MEDICAL_VOLUNTEERS, CATEGORY_MEDICAL_FRUITS_VEGETABLES, CATEGORY_MEDICAL_TRANSPORT, CATEGORY_MEDICAL_ANIMAL_SUPPORT, CATEGORY_MEDICAL_GIVEAWAYS, CATEGORY_MEDICAL_PAID_WORK -> {
                        detail = it.detail ?: ""
                    }
                    else -> {
                        if (!it.detail.isNullOrEmpty()) {
                            detail += it.detail?.take(30)
                        }
                        if (!it.quantity.isNullOrEmpty()) {
                            detail += " (" + it.quantity + ")"
                        }
                        if (mapping.request_detail?.activity_detail!!.size - 1 != index) {
                            detail += "<br/>"
                        }
                    }
                }
            }

            mapping.request_detail?.user_detail?.detail = detail
        } catch (e: Exception) {
            Timber.d("")
        }
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

    suspend fun getNewMatches(activityType: Int): NewMatchResponses {
        return service.makeCall {
            it.networkApi.getNewMatchesAsync(createNewMatchesRequest(activityType))
        }
    }

    private fun createNewMatchesRequest(activityType: Int): String {
        val mainData = JSONObject()
        try {
            mainData.put("app_id", preferencesService.appId)
            mainData.put("imei_no", preferencesService.imeiNumber)
            mainData.put("app_version", preferencesService.appVersion)
            mainData.put("date_time", Utils.currentDateTime())
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

    suspend fun sendEmailToServer(email: String): ServerResponse {
        return service.makeCall {
            it.networkApi.getEmailResponseAsync(createCreateMailRequest(email))
        }
    }

    private fun createCreateMailRequest(email: String): String {
        val mainData = JSONObject()
        try {
            mainData.put("app_id", preferencesService.appId)
            mainData.put("imei_no", preferencesService.imeiNumber)
            mainData.put("app_version", preferencesService.appVersion)
            mainData.put("date_time", Utils.currentDateTime())
            val bodyJson = JSONObject()
            try {
                FirebaseInstanceId.getInstance().token?.let {
                    preferencesService.firebaseId = FirebaseInstanceId.getInstance().token!!
                }
                bodyJson.put("email_address", email)
                mainData.put("data", bodyJson)
            } catch (e: Exception) {
                CrashReporter.logException(e)
            }
        } catch (e: Exception) {
            CrashReporter.logException(e)
        }
        return mainData.toString()
    }

    fun deleteActivityFromDb(activityUuid: String) {
        db.getMappingDao().deleteMapping(activityUuid)
    }
}