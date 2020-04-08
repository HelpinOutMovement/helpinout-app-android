package com.triline.billionlights.view.activity

import android.location.Location
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import android.widget.AdapterView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.triline.billionlights.R
import com.triline.billionlights.model.database.entity.Registration
import com.triline.billionlights.utils.*
import com.triline.billionlights.view.view.SpinnerSelector
import com.triline.billionlights.viewmodel.LoginRegistrationViewModel
import kotlinx.android.synthetic.main.activity_register.*
import org.jetbrains.anko.indeterminateProgressDialog
import org.jetbrains.anko.startActivity

class RegistrationActivity : LocationActivity(), View.OnClickListener {

    private var registration = Registration()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkLocationPermission()
        chk_as_organization.setOnCheckedChangeListener { _, b ->
            if (b) {
                til_org_name.show()
                spinner_org_type.show()
                til_unit_division.show()
            } else {
                til_org_name.hide()
                spinner_org_type.hide()
                til_unit_division.hide()
            }
        }

        spinner_org_type.onItemSelectedListener = object : SpinnerSelector() {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                registration.orgType = spinner_org_type.selectedItem as String
            }
        }
        btn_login.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < DOUBLE_CLICK_TIME) {
            return
        }
        mLastClickTime = SystemClock.elapsedRealtime()

        if (v == btn_login) {
            if (validate()) {
                hideKeyboard()
                val dialog = indeterminateProgressDialog(R.string.alert_msg_please_wait)
                dialog.setCancelable(false)
                dialog.show()

                val viewModel = ViewModelProvider(this).get(LoginRegistrationViewModel::class.java)
                viewModel.getRegistrationResult(registration).observe(this, Observer {
                    dialog.dismiss()
                    if (it.first != null) {
                        val loginResponse = it.first
                        if (loginResponse?.status == 1) {
                            toastSuccess(R.string.toast_success_registrtion_success)
                            preferencesService.appId = loginResponse.data?.app_id ?: ""
                            preferencesService.step = HOME_STEP
                            startActivity<HomeActivity>()
                            overridePendingTransition(R.anim.enter, R.anim.exit)
                            finishWithFade()
                        } else {
                            toastError(loginResponse?.message ?: "")
                        }
                    } else {
                        toastError(it.second)
                    }
                })
            }
        }
    }

    override fun onLocationChanged(location: Location?) {
        location?.let {
            preferencesService.latitude = it.latitude.toString()
            preferencesService.longitude = it.longitude.toString()
            preferencesService.gpsAccuracy = it.accuracy.toString()
            stopLocationUpdate()
        }
    }

    private fun validate(): Boolean {
        if (!chk_as_organization.isChecked) {
            if (first_name.text.isNullOrEmpty()) {
                toastError(R.string.toast_error_please_enter_first_name)
                first_name.requestFocus()
                return false
            }
            if (last_name.text.isNullOrEmpty()) {
                toastError(R.string.toast_error_please_enter_last_name)
                last_name.requestFocus()
                return false
            }
            registration.numberVisible = chk_number_visible.isChecked
            registration.first_name = first_name.text.toString()
            registration.last_name = last_name.text.toString()
            registration.orgName = ""
            registration.orgType = ""
            registration.unitDivision = ""
            registration.mobileNumber = preferencesService.mobileNumber

        } else {
            if (first_name.text.isNullOrEmpty()) {
                toastError(R.string.toast_error_please_enter_first_name)
                first_name.requestFocus()
                return false
            }
            if (last_name.text.isNullOrEmpty()) {
                toastError(R.string.toast_error_please_enter_last_name)
                last_name.requestFocus()
                return false
            }

            if (edt_org_name.text.isNullOrEmpty()) {
                toastError(R.string.toast_error_please_enter_organization_name)
                edt_org_name.requestFocus()
                return false
            }

            if (spinner_org_type.selectedItemPosition == 0) {
                toastError(R.string.toast_error_please_choose_org_type)
                spinner_org_type.requestFocus()
                return false
            }
            if (edt_unit_division.text.isNullOrEmpty()) {
                toastError(R.string.toast_error_please_enter_unit_division)
                edt_unit_division.requestFocus()
                return false
            }

            registration.numberVisible = chk_number_visible.isChecked
            registration.first_name = first_name.text.toString()
            registration.last_name = last_name.text.toString()
            registration.orgType = spinner_org_type.selectedItem as String
            registration.orgName = edt_org_name.text.toString()
            registration.unitDivision = edt_unit_division.text.toString()
            registration.mobileNumber = preferencesService.mobileNumber
        }
        return true
    }

    override fun getLayout(): Int {
        return R.layout.activity_register
    }

    override fun onPermissionAllow() {
        buildGoogleApiClient()
    }
}