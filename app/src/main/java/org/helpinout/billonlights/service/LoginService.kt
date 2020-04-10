package org.helpinout.billonlights.service

import android.os.Build
import com.avneesh.crashreporter.CrashReporter
import com.google.firebase.iid.FirebaseInstanceId
import org.helpinout.billonlights.model.dagger.PreferencesService
import org.helpinout.billonlights.model.database.AppDatabase
import org.helpinout.billonlights.model.database.entity.LoginResponse
import org.helpinout.billonlights.model.database.entity.Registration
import org.helpinout.billonlights.model.database.entity.RegistrationResponse
import org.helpinout.billonlights.model.retrofit.NetworkApiProvider
import org.helpinout.billonlights.utils.Utils.Companion.currentDateTime
import org.helpinout.billonlights.utils.Utils.Companion.getTimeZone
import org.helpinout.billonlights.utils.Utils.Companion.getTimeZoneString
import org.json.JSONObject

class LoginService(private val preferencesService: PreferencesService, private val service: NetworkApiProvider, private val db: AppDatabase) {


    suspend fun verifyExistingUserResult(countryCode: String, mobileNumber: String): LoginResponse {
        return service.makeCall {
            it.networkApi.verifyExistingUserAsync(createLoginRequest(countryCode, mobileNumber))
        }
    }

    private fun createLoginRequest(countryCode: String, mobileNumber: String): String {
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
                bodyJson.put("app_id", preferencesService.appId)
                bodyJson.put("imei_no", preferencesService.imeiNumber)
                bodyJson.put("os_type", "Android")
                bodyJson.put("manufacturer_name", Build.MANUFACTURER + " " + Build.MODEL)
                bodyJson.put("os_version", Build.VERSION.SDK_INT.toString())
                bodyJson.put("firebase_token", preferencesService.firebaseId)
                bodyJson.put("app_version", preferencesService.appVersion)
                bodyJson.put("time_zone", getTimeZoneString())
                bodyJson.put("date_time", currentDateTime())
                bodyJson.put("country_code", countryCode)
                bodyJson.put("mobile_no", mobileNumber)
                mainData.put("data", bodyJson)
            } catch (e: Exception) {
                CrashReporter.logException(e)
            }
        } catch (e: Exception) {
            CrashReporter.logException(e)
        }
        return mainData.toString()
    }


    suspend fun getRegistrationResult(registration: Registration): RegistrationResponse {
        return service.makeCall {
            it.networkApi.getRegistrationResponseAsync(createRegistrationRequest(registration))
        }
    }

    suspend fun getUpdateProfileResult(registration: Registration): RegistrationResponse {
        return service.makeCall {
            it.networkApi.getProfileUpdateResponseAsync(createRegistrationRequest(registration))
        }
    }

    fun saveRegistrationToDb(registration: Registration): Boolean {
        val count = db.getRegistrationDao().insertRegistrationRecord(registration)
        return count.toInt() != 0
    }

    private fun createRegistrationRequest(registration: Registration): String {
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

                bodyJson.put("imei_no", preferencesService.imeiNumber)
                bodyJson.put("os_type", "Android")
                bodyJson.put("manufacturer_name", Build.MANUFACTURER + " " + Build.MODEL)
                bodyJson.put("os_version", Build.VERSION.SDK_INT.toString())
                bodyJson.put("firebase_token", preferencesService.firebaseId)
                bodyJson.put("app_version", preferencesService.appVersion)
                bodyJson.put("time_zone", getTimeZone())
                bodyJson.put("country_code", preferencesService.countryCode)
                bodyJson.put("mobile_no", preferencesService.mobileNumber)
                bodyJson.put("first_name", registration.first_name)
                bodyJson.put("time_zone", getTimeZoneString())
                bodyJson.put("last_name", registration.last_name)
                bodyJson.put("mobile_no_visibility", registration.mobile_no_visibility)
                bodyJson.put("user_type", if (registration.org_type == 0) 1 else 2)
                bodyJson.put("org_name", registration.org_name)
                bodyJson.put("org_type", registration.org_type)
                bodyJson.put("org_division", registration.org_division)
                mainData.put("data", bodyJson)

            } catch (e: Exception) {
                CrashReporter.logException(e)
            }
        } catch (e: Exception) {
            CrashReporter.logException(e)
        }
        return mainData.toString()
    }

    fun getSavedRegistration(): Registration? {
        return db.getRegistrationDao().getRegistrationRecord()
    }

}