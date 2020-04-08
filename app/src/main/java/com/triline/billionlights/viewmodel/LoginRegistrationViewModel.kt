package com.triline.billionlights.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.avneesh.crashreporter.CrashReporter
import com.triline.billionlights.model.BillionLightsApplication
import com.triline.billionlights.model.database.entity.LoginResponse
import com.triline.billionlights.model.database.entity.Registration
import com.triline.billionlights.service.LoginService
import com.triline.billionlights.utils.getStringException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import javax.inject.Inject


class LoginRegistrationViewModel(application: Application) : AndroidViewModel(application) {

    @Inject
    lateinit var loginService: LoginService

    init {
        (application as BillionLightsApplication).getAppComponent().inject(this)
    }

    fun getLoginResult(countryCode: String, mobileNumber: String): MutableLiveData<Pair<LoginResponse?, String>> {
        val loginResponse = MutableLiveData<Pair<LoginResponse?, String>>()
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = loginService.verifyExistingUserResult(countryCode, mobileNumber)
                loginResponse.postValue(Pair(response, ""))
            } catch (e: Exception) {
                loginResponse.postValue(Pair(null, e.getStringException()))
                CrashReporter.logException(e)
            }
        }
        return loginResponse
    }

    fun getRegistrationResult(registration: Registration): MutableLiveData<Pair<LoginResponse?, String>> {
        val registrationResponse = MutableLiveData<Pair<LoginResponse?, String>>()
        GlobalScope.launch(Dispatchers.IO) {
            try {
                registrationResponse.postValue(Pair(loginService.getRegistrationResult(registration), ""))
            } catch (e: Exception) {
                registrationResponse.postValue(Pair(null, e.getStringException()))
            }
        }
        return registrationResponse
    }

}