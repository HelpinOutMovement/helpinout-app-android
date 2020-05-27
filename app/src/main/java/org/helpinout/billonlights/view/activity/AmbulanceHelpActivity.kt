package org.helpinout.billonlights.view.activity

import android.app.ProgressDialog
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.avneesh.crashreporter.CrashReporter
import kotlinx.android.synthetic.main.activity_ambulance_help.*
import org.helpinout.billonlights.R
import org.helpinout.billonlights.model.database.entity.AddData
import org.helpinout.billonlights.model.database.entity.SuggestionRequest
import org.helpinout.billonlights.utils.*
import org.helpinout.billonlights.viewmodel.OfferViewModel
import org.jetbrains.anko.indeterminateProgressDialog


class AmbulanceHelpActivity : BaseActivity(), View.OnClickListener {
    private val ambulanceHelp = AddData()
    private var dialog: ProgressDialog? = null
    private var selfHelp: Int = 0
    private val suggestionData = SuggestionRequest()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ambulanceHelp.activity_type = helpType

        val categoryType = intent.getIntExtra(CATEGORY_TYPE, 0)
        ambulanceHelp.activity_category = categoryType
        suggestionData.activity_category = categoryType
        logo.setImageResource(categoryType.getIcon())
        suggestionData.activity_category = categoryType

        ambulanceHelp.activity_uuid = getUuid()
        ambulanceHelp.date_time = currentDateTime()
        selfHelp = intent.getIntExtra(SELF_ELSE, 0)
        ambulanceHelp.geo_location = preferencesService.latitude.toString() + "," + preferencesService.longitude
        initTitle()
        if (helpType == HELP_TYPE_OFFER) {
            tv_note_to.setText(R.string.note_to_requester)
        }

        when (categoryType) {
            CATEGORY_AMBULANCE -> {
                edt_conditions.setHint(R.string.hint_conditions)
            }
            CATEGORY_MEDICAL_VOLUNTEERS -> {
                we_can_pay.invisible()
                if (helpType == HELP_TYPE_REQUEST) edt_conditions.setHint(R.string.hint_volunteers_request)
                else edt_conditions.setHint(R.string.hint_volunteers_offer)
            }
            CATEGORY_MEDICAL_FRUITS_VEGETABLES -> {
                if (helpType == HELP_TYPE_REQUEST) edt_conditions.setHint(R.string.hint_fruits_vegetables_request)
                else edt_conditions.setHint(R.string.hint_fruits_vegetables_offer)
            }
            CATEGORY_MEDICAL_TRANSPORT -> {
                if (helpType == HELP_TYPE_REQUEST) edt_conditions.setHint(R.string.hint_transport_request)
                else edt_conditions.setHint(R.string.hint_transport_offer)
            }
            CATEGORY_MEDICAL_ANIMAL_SUPPORT -> {
                if (helpType == HELP_TYPE_REQUEST) edt_conditions.setHint(R.string.hint_animal_support_request)
                else edt_conditions.setHint(R.string.hint_animal_support_offer)
            }
            CATEGORY_MEDICAL_GIVEAWAYS -> {
                we_can_pay.invisible()
                if (helpType == HELP_TYPE_REQUEST) edt_conditions.setHint(R.string.hint_giveaways_request)
                else edt_conditions.setHint(R.string.hint_giveaways_offer)
            }
            CATEGORY_MEDICAL_PAID_WORK -> {
                we_can_pay.invisible()
                if (helpType == HELP_TYPE_REQUEST) {
                    we_can_not_pay.setText(R.string.must_get_paid)
                    edt_conditions.setHint(R.string.hint_paid_work_request)
                } else {
                    we_can_not_pay.setText(R.string.we_will_pay)
                    edt_conditions.setHint(R.string.hint_paid_work_offer)
                }
            }
        }

        we_can_pay.setOnClickListener(this)
        we_can_not_pay.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
            return
        }
        mLastClickTime = SystemClock.elapsedRealtime()
        when (v) {
            we_can_pay -> {
                hideKeyboard()
                ambulanceHelp.pay = 1
                sendDataToServer()
            }
            we_can_not_pay -> {
                hideKeyboard()
                ambulanceHelp.pay = 0
                sendDataToServer()
            }
        }
    }

    private fun sendDataToServer() {
        dialog = indeterminateProgressDialog(R.string.alert_msg_please_wait)
        dialog?.show()
        ambulanceHelp.selfHelp = selfHelp
        ambulanceHelp.conditions = edt_conditions.text.toString()
        ambulanceHelp.address = getAddress(preferencesService.latitude, preferencesService.longitude)
        val viewModel = ViewModelProvider(this).get(OfferViewModel::class.java)
        viewModel.sendAmbulanceHelp(ambulanceHelp, suggestionData).observe(this, Observer {
            dialog?.dismiss()
            if (it.first != null && it.first?.status == 1) {
                askForConfirmation(ambulanceHelp.activity_uuid, suggestionData)
            } else {
                if (!isNetworkAvailable()) {
                    toastError(R.string.toast_error_internet_issue)
                }
                CrashReporter.logCustomLogs(it.second)
            }
        })
    }


    override fun getLayout(): Int {
        return R.layout.activity_ambulance_help
    }
}
