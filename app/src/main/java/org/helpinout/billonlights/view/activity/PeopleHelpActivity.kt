package org.helpinout.billonlights.view.activity

import android.app.ProgressDialog
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.avneesh.crashreporter.CrashReporter
import kotlinx.android.synthetic.main.activity_people_help.*
import org.helpinout.billonlights.R
import org.helpinout.billonlights.model.database.entity.ActivityDetail
import org.helpinout.billonlights.model.database.entity.AddData
import org.helpinout.billonlights.model.database.entity.SuggestionRequest
import org.helpinout.billonlights.utils.*
import org.helpinout.billonlights.viewmodel.OfferViewModel
import org.jetbrains.anko.indeterminateProgressDialog

class PeopleHelpActivity : BaseActivity(), View.OnClickListener {
    private var dialog: ProgressDialog? = null
    private var peopleHelp = AddData()
    private var selfHelp: Int = 0
    private var activityDetail = ActivityDetail()
    private val suggestionData = SuggestionRequest()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initTitle()
        peopleHelp.activity_type = helpType
        peopleHelp.activity_uuid = getUuid()
        peopleHelp.activity_category = 2

        peopleHelp.date_time = currentDateTime()
        selfHelp = intent.getIntExtra(SELF_ELSE, 0)
        peopleHelp.geo_location = preferencesService.latitude.toString() + "," + preferencesService.longitude.toString()

        peopleHelp.activity_detail.add(activityDetail)
        chk_volunteers.setOnClickListener(this)
        chk_technical_personnel.setOnClickListener(this)
        we_can_pay.setOnClickListener(this)
        we_can_not_pay.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
            return
        }
        mLastClickTime = SystemClock.elapsedRealtime()
        when (v) {
            chk_volunteers -> {
                edt_volunteers.isEnabled = chk_volunteers.isChecked
                edt_qty1.isEnabled = chk_volunteers.isChecked
                activityDetail.volunters_required = if (chk_volunteers.isChecked) 1 else 0
            }
            chk_technical_personnel -> {
                edt_technical_personnel.isEnabled = chk_technical_personnel.isChecked
                edt_qty2.isEnabled = chk_technical_personnel.isChecked
            }
            we_can_pay -> {
                peopleHelp.pay = 1
                sendDataToServer()
            }
            we_can_not_pay -> {
                peopleHelp.pay = 0
                sendDataToServer()
            }
        }
    }

    private fun createSuggestionData() {
        suggestionData.activity_category = 2
        suggestionData.activity_type = helpType
        suggestionData.latitude = preferencesService.latitude
        suggestionData.longitude = preferencesService.longitude
        suggestionData.accuracy = preferencesService.gpsAccuracy
    }

    private fun sendDataToServer() {
        dialog = indeterminateProgressDialog(R.string.alert_msg_please_wait)
        dialog?.show()
        activityDetail.volunters_required = if (chk_volunteers.isChecked) 1 else 0
        activityDetail.volunters_detail = edt_volunteers.text.toString()
        activityDetail.volunters_quantity = edt_qty1.text.toString()
        peopleHelp.address = getAddress(preferencesService.latitude, preferencesService.longitude)
        activityDetail.technical_personal_required = if (chk_technical_personnel.isChecked) 1 else 0
        activityDetail.technical_personal_detail = edt_technical_personnel.text.toString()
        activityDetail.technical_personal_quantity = edt_qty2.text.toString()
        peopleHelp.address = getAddress(preferencesService.latitude, preferencesService.longitude)
        peopleHelp.selfHelp = selfHelp
        createSuggestionData()

        val viewModel = ViewModelProvider(this).get(OfferViewModel::class.java)
        viewModel.sendPeopleHelp(peopleHelp, activityDetail).observe(this, Observer {
            dialog?.dismiss()
            if (it.first != null) {
                askForConfirmation(peopleHelp.activity_uuid, suggestionData)
            } else {
                if (!isNetworkAvailable()) {
                    toastError(R.string.toast_error_internet_issue)
                } else toastError(it.second)

                CrashReporter.logCustomLogs(it.second)
            }
        })
    }

    override fun getLayout(): Int {
        return R.layout.activity_people_help
    }
}
