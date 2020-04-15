package org.helpinout.billonlights.view.activity

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.avneesh.crashreporter.CrashReporter
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_people_help.*
import org.helpinout.billonlights.R
import org.helpinout.billonlights.model.database.entity.*
import org.helpinout.billonlights.utils.*
import org.helpinout.billonlights.view.fragments.BottomSheetsRequestConfirmationFragment
import org.helpinout.billonlights.viewmodel.OfferViewModel
import org.jetbrains.anko.indeterminateProgressDialog
import org.jetbrains.anko.startActivityForResult

class PeopleHelpActivity : BaseActivity(), View.OnClickListener {
    private var dialog: ProgressDialog? = null
    private var peopleHelp = AddData()
    private var activityDetail = ActivityDetail()
    private val suggestionData = SuggestionRequest()
    private val showMapCode: Int = 43

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initTitle()
        peopleHelp.activity_type = helpType
        peopleHelp.activity_uuid = getUuid()
        peopleHelp.activity_category = 2
        suggestionData.activity_category = 2
        peopleHelp.date_time = currentDateTime()
        peopleHelp.geo_location = preferencesService.latitude.toString() + "," + preferencesService.longitude.toString()

        peopleHelp.activity_detail.add(activityDetail)
        chk_volunteers.setOnClickListener(this)
        chk_technical_personnel.setOnClickListener(this)
        we_can_pay.setOnClickListener(this)
        we_can_not_pay.setOnClickListener(this)
    }

    override fun getLayout(): Int {
        return R.layout.activity_people_help
    }

    override fun onClick(v: View?) {
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
                peopleHelp.pay = 2
                sendDataToServer()
            }
        }
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


        suggestionData.activity_type = helpType
        suggestionData.latitude = preferencesService.latitude
        suggestionData.longitude = preferencesService.longitude
        suggestionData.accuracy = preferencesService.gpsAccuracy

        val viewModel = ViewModelProvider(this).get(OfferViewModel::class.java)
        viewModel.sendPeopleHelp(peopleHelp).observe(this, Observer {
            if (it.first != null) {
                saveRequestToDatabase(it.first)
            } else {
                dialog?.dismiss()
                CrashReporter.logCustomLogs(it.second)
            }
        })
    }

    private fun saveRequestToDatabase(first: ActivityResponses?) {
        first?.data?.let { item ->
            val addItemList = ArrayList<AddCategoryDbItem>()

            val singleItem = AddCategoryDbItem()
            singleItem.activity_type = peopleHelp.activity_type
            singleItem.activity_uuid = peopleHelp.activity_uuid
            singleItem.date_time = peopleHelp.date_time
            singleItem.activity_category = peopleHelp.activity_category
            singleItem.activity_count = peopleHelp.activity_count
            singleItem.geo_location = peopleHelp.geo_location
            singleItem.address = peopleHelp.address
            var detail = ""
            if (!activityDetail.volunters_detail.isNullOrEmpty() || !activityDetail.volunters_quantity.isNullOrEmpty()) {
                detail += activityDetail.volunters_detail + "(" + activityDetail.volunters_quantity + "),"
            }
            if (!activityDetail.technical_personal_detail.isNullOrEmpty() || !activityDetail.technical_personal_quantity.isNullOrEmpty()) {
                detail += activityDetail.technical_personal_detail + "(" + activityDetail.technical_personal_quantity + ")"
            }

            singleItem.detail = detail
            singleItem.volunters_required = 0
            singleItem.volunters_detail = activityDetail.volunters_detail
            singleItem.volunters_quantity = activityDetail.volunters_quantity
            singleItem.technical_personal_required = activityDetail.technical_personal_required
            singleItem.technical_personal_detail = activityDetail.technical_personal_detail
            singleItem.technical_personal_quantity = activityDetail.technical_personal_quantity
            singleItem.status = 1

            addItemList.add(singleItem)
            val viewModel = ViewModelProvider(this).get(OfferViewModel::class.java)
            viewModel.saveFoodItemToDatabase(addItemList).observe(this, Observer {
                dialog?.dismiss()
                if (it) {
                    toastSuccess(R.string.toast_success_data_save_success)
                    askForConfirmation()
                }
            })
        } ?: kotlin.run {
            dialog?.dismiss()
        }
    }

    private fun askForConfirmation() {
        val deleteDialog = BottomSheetsRequestConfirmationFragment(helpType, onYesClick = { onYesClick() }, onNoClick = { onNoClick() })
        deleteDialog.show(supportFragmentManager, null)
    }

    private fun onYesClick() {
        suggestionData.activity_uuid = peopleHelp.activity_uuid
        val suggestionDataAsString = Gson().toJson(suggestionData)
        startActivityForResult<HelpProviderRequestersActivity>(showMapCode, SUGGESTION_DATA to suggestionDataAsString, HELP_TYPE to helpType)
        overridePendingTransition(R.anim.enter, R.anim.exit)
        finish()
    }

    private fun onNoClick() {
        if (helpType == HELP_TYPE_REQUEST) {
            val intent = Intent(baseContext!!, HomeActivity::class.java)
            intent.putExtra(SELECTED_INDEX, 1)
            startActivity(intent)
        } else {
            val intent = Intent(baseContext!!, HomeActivity::class.java)
            intent.putExtra(SELECTED_INDEX, 2)
            startActivity(intent)
        }
        finishWithFade()
    }
}
