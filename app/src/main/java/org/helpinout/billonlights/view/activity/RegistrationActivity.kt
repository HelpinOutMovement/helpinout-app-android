package org.helpinout.billonlights.view.activity

import android.os.Bundle
import android.os.SystemClock
import android.text.Editable
import android.view.View
import android.widget.AdapterView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.activity_register.*
import org.helpinout.billonlights.R
import org.helpinout.billonlights.model.database.entity.Registration
import org.helpinout.billonlights.utils.*
import org.helpinout.billonlights.view.adapters.CustomStringAdapter
import org.helpinout.billonlights.view.view.HelpInTextWatcher
import org.helpinout.billonlights.view.view.SpinnerSelector
import org.helpinout.billonlights.viewmodel.LoginRegistrationViewModel
import org.jetbrains.anko.indeterminateProgressDialog
import org.jetbrains.anko.startActivity


class RegistrationActivity : BaseActivity(), View.OnClickListener {

    private var isUpdate: Boolean = false
    private var registration = Registration()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.title = getString(R.string.update_profile)
        chk_as_organization.setOnCheckedChangeListener { _, b ->
            if (b) {
                til_org_name.show()
                spinner_org_type.show()
                til_unit_division.show()
                tv_help_in_profile_name.text = getProfileName()
            } else {
                til_org_name.hide()
                spinner_org_type.hide()
                til_unit_division.hide()
                tv_help_in_profile_name.text = getProfileName()
            }
        }
        tv_help_in_profile_name.text = getString(R.string.help_in_out_profile, "")

        first_name.addTextChangedListener(object : HelpInTextWatcher() {
            override fun afterTextChanged(s: Editable?) {
                if (!chk_as_organization.isChecked) {
                    tv_help_in_profile_name.text = getProfileName()
                }
            }
        })

        last_name.addTextChangedListener(object : HelpInTextWatcher() {
            override fun afterTextChanged(s: Editable?) {
                if (!chk_as_organization.isChecked) {
                    val name = first_name.text.toString().take(1) + ". " + s
                    tv_help_in_profile_name.text = getProfileName()
                }
            }
        })

        edt_org_name.addTextChangedListener(object : HelpInTextWatcher() {
            override fun afterTextChanged(s: Editable?) {
                tv_help_in_profile_name.text = getProfileName()
            }
        })

        spinner_org_type.onItemSelectedListener = object : SpinnerSelector() {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position != 0) {
                    registration.org_type = resources.getIntArray(R.array.org_types_index)[position]
                }
            }
        }
        spinner_org_type.adapter = CustomStringAdapter(this, values = resources.getStringArray(R.array.org_types))

        btn_login.setOnClickListener(this)

        isUpdate = intent.getBooleanExtra(UPDATE_PROFILE, false)

        if (isUpdate) {
            layout_toolbar.show()
            tv_register.text = getString(R.string.update_profile)
            btn_login.text = getString(R.string.update_profile)
            val viewModel = ViewModelProvider(this).get(LoginRegistrationViewModel::class.java)
            viewModel.getSavedRegistration().observe(this, Observer {
                it?.let {
                    registration = it
                    fillValues()
                }
            })
        }
    }

    private fun getProfileName(): String {
        val name = if (chk_as_organization.isChecked) {
            edt_org_name.text.toString()
        } else {
            if (last_name.text.toString().isEmpty()) {
                first_name.text.toString().take(1)
            } else first_name.text.toString().take(1) + ". " + last_name.text
        }
        registration.profile_name = name
        return getString(R.string.help_in_out_profile) + " <b>" + name + "</b>"
    }

    private fun fillValues() {
        first_name.setText(registration.first_name)
        last_name.setText(registration.last_name)
        tv_help_in_profile_name.text = getProfileName()
        chk_number_visible.isChecked = registration.mobile_no_visibility == 1
        chk_as_organization.isChecked = !registration.org_name.isNullOrEmpty()
        if (!registration.org_name.isNullOrEmpty()) {
            til_org_name.show()
            til_unit_division.show()
            spinner_org_type.show()
            edt_org_name.setText(registration.org_name)
            edt_unit_division.setText(registration.org_division)
            spinner_org_type.setSelection(getOrgTypeIndex(registration.org_type))
        }
    }

    private fun getOrgTypeIndex(type: Int?): Int {
        resources.getIntArray(R.array.org_types_index).forEachIndexed { index, i ->
            if (i == type) return index
        }
        return 0
    }

    override fun onClick(v: View?) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < DOUBLE_CLICK_TIME) {
            return
        }
        mLastClickTime = SystemClock.elapsedRealtime()

        if (v == btn_login) {
            if (validate()) {
                hideKeyboard()
                if (isUpdate) updateUser()
                else registerUser()
            }
        }
    }


    private fun registerUser() {
        val dialog = indeterminateProgressDialog(R.string.alert_msg_please_wait)
        dialog.setCancelable(false)
        dialog.show()
        preferencesService.orgName = registration.org_name ?: ""
        val viewModel = ViewModelProvider(this).get(LoginRegistrationViewModel::class.java)
        viewModel.getRegistrationResult(registration).observe(this, Observer {
            dialog.dismiss()
            if (it.first != null) {
                val loginResponse = it.first
                if (loginResponse?.status == 1) {
                    preferencesService.appId = loginResponse.data?.app_id ?: ""
                    saveRegistrationToDatabase(registration)
                } else {
                    toastError(loginResponse?.message ?: "")
                }
            } else {
                if (!isNetworkAvailable()) {
                    toastError(R.string.toast_error_internet_issue)
                } else toastError(it.second)
            }
        })
    }

    private fun updateUser() {
        val dialog = indeterminateProgressDialog(R.string.alert_msg_please_wait)
        dialog.setCancelable(false)
        dialog.show()

        val viewModel = ViewModelProvider(this).get(LoginRegistrationViewModel::class.java)
        preferencesService.orgName = registration.org_name ?: ""

        viewModel.getUpdateProfileResult(registration).observe(this, Observer {
            dialog.dismiss()
            if (it.first != null) {
                val loginResponse = it.first
                if (loginResponse?.status == 1) {
                    saveRegistrationToDatabase(registration, true)
                } else {
                    toastError(loginResponse?.message ?: "")
                }
            } else {
                if (!isNetworkAvailable()) {
                    toastError(R.string.toast_error_internet_issue)
                } else toastError(it.second)
            }
        })
    }

    private fun saveRegistrationToDatabase(registration: Registration, isUpdate: Boolean = false) {
        val viewModel = ViewModelProvider(this).get(LoginRegistrationViewModel::class.java)
        viewModel.saveRegistration(registration).observe(this, Observer {
            if (it) {
                if (isUpdate) {
                    toastSuccess(R.string.toast_success_update_success)
                } else {
                    toastSuccess(R.string.toast_success_registration_success)
                    preferencesService.step = HOME_STEP
                    startActivity<HomeActivity>()
                }
                overridePendingTransition(R.anim.enter, R.anim.exit)
                finishWithFade()
            }
        })
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
            registration.mobile_no_visibility = if (chk_number_visible.isChecked) 1 else 0
            registration.first_name = first_name.text.toString()
            registration.last_name = last_name.text.toString()
            registration.org_name = ""
            registration.org_type = null
            registration.org_division = ""
            registration.user_type = 1
            registration.mobile_no = preferencesService.mobileNumber
            registration.country_code = preferencesService.countryCode


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

            registration.mobile_no_visibility = if (chk_number_visible.isChecked) 1 else 0
            registration.first_name = first_name.text.toString()
            registration.last_name = last_name.text.toString()
            registration.org_type = resources.getIntArray(R.array.org_types_index)[spinner_org_type.selectedItemPosition]
            registration.org_name = edt_org_name.text.toString()
            registration.user_type = 2
            registration.org_division = edt_unit_division.text.toString()
            registration.mobile_no = preferencesService.mobileNumber
            registration.country_code = preferencesService.countryCode
        }
        return true
    }

    override fun getLayout(): Int {
        return R.layout.activity_register
    }
}