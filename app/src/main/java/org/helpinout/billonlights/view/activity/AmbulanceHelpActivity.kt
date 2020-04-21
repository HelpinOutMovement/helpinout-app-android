package org.helpinout.billonlights.view.activity

import android.app.ProgressDialog
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.avneesh.crashreporter.CrashReporter
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_ambulance_help.*
import org.helpinout.billonlights.R
import org.helpinout.billonlights.model.database.entity.AddCategoryDbItem
import org.helpinout.billonlights.model.database.entity.AddData
import org.helpinout.billonlights.model.database.entity.SuggestionRequest
import org.helpinout.billonlights.utils.*
import org.helpinout.billonlights.view.fragments.BottomSheetsRequestConfirmationFragment
import org.helpinout.billonlights.viewmodel.OfferViewModel
import org.jetbrains.anko.indeterminateProgressDialog
import org.jetbrains.anko.startActivityForResult


class AmbulanceHelpActivity : BaseActivity(), View.OnClickListener {
    private val ambulanceHelp = AddData()
    private var dialog: ProgressDialog? = null
    private val suggestionData = SuggestionRequest()
    private val showMapCode: Int = 43

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ambulanceHelp.activity_category = 7
        ambulanceHelp.activity_type = helpType
        suggestionData.activity_category = 7
        ambulanceHelp.activity_uuid = getUuid()
        ambulanceHelp.date_time = currentDateTime()
        ambulanceHelp.geo_location = preferencesService.latitude.toString() + "," + preferencesService.longitude
        initTitle()
        we_can_pay.setOnClickListener(this)
        we_can_not_pay.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v) {
            we_can_pay -> {
                ambulanceHelp.qty = edt_people.text.toString()
                ambulanceHelp.pay = 1
                sendDataToServer()
            }
            we_can_not_pay -> {
                ambulanceHelp.qty = edt_people.text.toString()
                ambulanceHelp.pay = 2
                sendDataToServer()
            }
        }
    }

    private fun sendDataToServer() {
        dialog = indeterminateProgressDialog(R.string.alert_msg_please_wait)
        dialog?.show()
        ambulanceHelp.address = getAddress(preferencesService.latitude, preferencesService.longitude)
        val viewModel = ViewModelProvider(this).get(OfferViewModel::class.java)
        viewModel.sendAmbulanceHelp(ambulanceHelp).observe(this, Observer {
            if (it.first != null && it.first?.status == 1) {
                saveRequestToDatabase()
            } else {
                if (!isNetworkAvailable()) {
                    toastError(R.string.toast_error_internet_issue)
                }
                dialog?.dismiss()
                CrashReporter.logCustomLogs(it.second)
            }
        })
    }

    private fun saveRequestToDatabase() {
        val addItemList = ArrayList<AddCategoryDbItem>()
        val item = AddCategoryDbItem()
        item.activity_type = ambulanceHelp.activity_type
        item.activity_uuid = ambulanceHelp.activity_uuid
        item.date_time = ambulanceHelp.date_time
        item.activity_category = ambulanceHelp.activity_category
        item.activity_count = ambulanceHelp.activity_count
        item.geo_location = ambulanceHelp.geo_location
        item.address = ambulanceHelp.address
        item.qty = ambulanceHelp.qty ?: ""
        item.status = 1


        suggestionData.activity_type = helpType
        suggestionData.latitude = preferencesService.latitude
        suggestionData.longitude = preferencesService.longitude
        suggestionData.accuracy = preferencesService.gpsAccuracy

        addItemList.add(item)

        val viewModel = ViewModelProvider(this).get(OfferViewModel::class.java)
        viewModel.saveFoodItemToDatabase(addItemList).observe(this, Observer {
            dialog?.dismiss()
            if (it) {
                askForConfirmation(ambulanceHelp.activity_uuid)
            }
        })
    }

    private fun askForConfirmation(activity_uuid: String) {
        val deleteDialog = BottomSheetsRequestConfirmationFragment(helpType, activity_uuid, onConfirmationYesClick = { onYesClick() }, onConfirmationNoClick = { type, uuid -> onConfirmationNoClick(type, uuid) })
        deleteDialog.show(supportFragmentManager, null)
    }

    private fun onYesClick() {
        suggestionData.activity_uuid = ambulanceHelp.activity_uuid
        val suggestionDataAsString = Gson().toJson(suggestionData)
        startActivityForResult<HelpProviderRequestersActivity>(showMapCode, SUGGESTION_DATA to suggestionDataAsString, HELP_TYPE to helpType)
        overridePendingTransition(R.anim.enter, R.anim.exit)
        finishWithFade()
    }


    override fun getLayout(): Int {
        return R.layout.activity_ambulance_help
    }
}
