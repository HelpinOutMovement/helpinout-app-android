package org.helpinout.billonlights.view.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.telephony.TelephonyManager
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.view.View
import kotlinx.android.synthetic.main.activity_login.*
import org.helpinout.billonlights.R
import org.helpinout.billonlights.utils.*
import org.jetbrains.anko.startActivityForResult


class LoginActivity : BaseActivity(), View.OnClickListener {

    private val registrationCode = 32
    private val loginCode = 33
    private var numberMaxLength: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        country_code_picker.setOnCountryChangeListener {
            setMobileNumberLength()
        }
        if (isSimInserted()) {
            val telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            val code = telephonyManager.networkCountryIso
            country_code_picker.setDefaultCountryUsingNameCode(code)
            setMobileNumberLength()
        } else {
            country_code_picker.setCountryForPhoneCode(91)
            setMobileNumberLength()
        }

        btn_login.setOnClickListener(this)
        tv_terms_service.setOnClickListener(this)
        tv_privacy_policy.setOnClickListener(this)
    }

    private fun setMobileNumberLength() {
        val countryCode = country_code_picker.selectedCountryCode
        val maxLength = countryCode.getMobileNumberLength()
        numberMaxLength = maxLength
        edt_mobile.filters = arrayOf<InputFilter>(LengthFilter(maxLength))
    }

    override fun onClick(v: View?) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < DOUBLE_CLICK_TIME) {
            return
        }
        mLastClickTime = SystemClock.elapsedRealtime()

        when (v) {
            btn_login -> {
                if (validate()) {
                    hideKeyboard()
                    val code = country_code_picker.selectedCountryCodeWithPlus
                    val mobileNo = edt_mobile.text.toString()
                    preferencesService.countryCode = code
                    preferencesService.mobileNumber = mobileNo
                    startActivityForResult<SMSVerificationActivity>(registrationCode)
                    overridePendingTransition(R.anim.enter, R.anim.exit)
                }
            }
            tv_terms_service -> {
                openUrl(TERMS_OF_SERVICE)
            }
            tv_privacy_policy -> {
                openUrl(PRIVACY_POLICY)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                registrationCode -> {
                    finishWithFade()
                }
                loginCode -> {
                    finishWithFade()
                }
            }
        }
    }

    private fun validate(): Boolean {
        if (!isNetworkAvailable()) {
            toastError(R.string.toast_error_internet_issue)
            return false
        }
        if (edt_mobile.text.isNullOrEmpty()) {
            toastError(R.string.toast_error_phone_number)
            edt_mobile.requestFocus()
            return false
        }
        if (edt_mobile.text.toString().length < numberMaxLength) {
            toastError(R.string.toast_error_invalid_phone_number)
            edt_mobile.requestFocus()
            return false
        }
        return true
    }

    override fun getLayout(): Int {
        return R.layout.activity_login
    }
}