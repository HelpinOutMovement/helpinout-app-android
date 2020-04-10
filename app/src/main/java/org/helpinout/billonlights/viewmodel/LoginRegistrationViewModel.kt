package org.helpinout.billonlights.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.avneesh.crashreporter.CrashReporter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import org.helpinout.billonlights.model.BillionLightsApplication
import org.helpinout.billonlights.model.database.entity.LoginResponse
import org.helpinout.billonlights.model.database.entity.Registration
import org.helpinout.billonlights.model.database.entity.RegistrationResponse
import org.helpinout.billonlights.service.LoginService
import org.helpinout.billonlights.utils.getStringException
import org.jetbrains.anko.doAsync
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

    fun getRegistrationResult(registration: Registration): MutableLiveData<Pair<RegistrationResponse?, String>> {
        val registrationResponse = MutableLiveData<Pair<RegistrationResponse?, String>>()
        GlobalScope.launch(Dispatchers.IO) {
            try {
                registrationResponse.postValue(Pair(loginService.getRegistrationResult(registration), ""))
            } catch (e: Exception) {
                registrationResponse.postValue(Pair(null, e.getStringException()))
            }
        }
        return registrationResponse
    }

    fun getUpdateProfileResult(registration: Registration): MutableLiveData<Pair<RegistrationResponse?, String>> {
        val registrationResponse = MutableLiveData<Pair<RegistrationResponse?, String>>()
        GlobalScope.launch(Dispatchers.IO) {
            try {
                registrationResponse.postValue(Pair(loginService.getUpdateProfileResult(registration), ""))
            } catch (e: Exception) {
                registrationResponse.postValue(Pair(null, e.getStringException()))
            }
        }
        return registrationResponse
    }

    fun getSavedRegistration(): MutableLiveData<Registration?> {
        val registrationResponse = MutableLiveData<Registration?>()
        doAsync {
            registrationResponse.postValue(loginService.getSavedRegistration())
        }
        return registrationResponse
    }

    fun saveRegistration(registration: Registration): MutableLiveData<Boolean> {
        val saveResponse = MutableLiveData<Boolean>()
        doAsync {
            saveResponse.postValue(loginService.saveRegistrationToDb(registration))
        }
        return saveResponse
    }
}