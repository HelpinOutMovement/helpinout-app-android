package org.helpinout.billonlights.service

import android.app.Application
import com.avneesh.crashreporter.CrashReporter
import com.google.firebase.iid.FirebaseInstanceId
import org.helpinout.billonlights.model.dagger.PreferencesService
import org.helpinout.billonlights.model.database.AppDatabase
import org.helpinout.billonlights.model.database.entity.DeleteDataResponses
import org.helpinout.billonlights.model.database.entity.MappingDetail
import org.helpinout.billonlights.model.retrofit.NetworkApiProvider
import org.helpinout.billonlights.utils.HELP_TYPE_REQUEST
import org.helpinout.billonlights.utils.SEEN_YES
import org.helpinout.billonlights.utils.Utils
import org.json.JSONArray
import org.json.JSONObject

class OfferRequestDetailService(private val preferencesService: PreferencesService, private val service: NetworkApiProvider, private val db: AppDatabase, private val app: Application) {

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
        db.getNotificationDao().updateActivity(offerType,activity_uuid, SEEN_YES,initiator)

        return response
    }

    suspend fun deleteActivity(uuid: String, activityType: Int): DeleteDataResponses {
        val response = service.makeCall {
            it.networkApi.getActivityDeleteResponseAsync(createActivityDeleteRequest(uuid, activityType))
        }
        if (response.status == 1) deleteActivityFromDb(response.data?.activity_uuid)

        return response
    }


    private fun deleteActivityFromDb(activityUuid: String?): Boolean {
        activityUuid?.let {
            db.getAddItemDao().deleteActivity(it)
            db.getMappingDao().deleteMapping(activityUuid)
            return true
        }
        return false
    }

    private fun createActivityDeleteRequest(uuid: String, activityType: Int): String {
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

    suspend fun deleteMappingFromServer(parent_uuid: String?, activity_uuid: String, activityType: Int,mapping_initiator:Int): String {
        return service.makeCall {
            it.networkApi.getMappingDeleteResponseAsync(createMappingDeleteRequest(parent_uuid, activity_uuid, activityType,mapping_initiator))
        }
    }

    suspend fun makeCallTracking(parent_uuid: String?, activity_uuid: String, activityType: Int): String {
        return service.makeCall {
            it.networkApi.getCallInitiateResponseAsync(createMappingDeleteRequest(parent_uuid, activity_uuid, activityType,0))
        }
    }

    private fun createMappingDeleteRequest(parent_uuid: String?, activity_uuid: String, activityType: Int,mapping_initiator:Int): String {
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
                bodyJson.put("activity_uuid", parent_uuid)
                bodyJson.put("activity_type", activityType)

                val jsonArray = JSONArray()
                val json = JSONObject()
                json.put("activity_uuid", activity_uuid)
                json.put("mapping_initiator", mapping_initiator)
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

    fun deleteMappingFromDb(parent_uuid: String?, activity_uuid: String): Boolean {
        return try {
            db.getMappingDao().deleteMapping(activity_uuid, parent_uuid!!)
            true
        } catch (e: Exception) {
            false
        }
    }

}