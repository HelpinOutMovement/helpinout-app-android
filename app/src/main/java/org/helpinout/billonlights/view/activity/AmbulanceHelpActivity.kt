package org.helpinout.billonlights.view.activity

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.activity_ambulance_help.*
import org.helpinout.billonlights.R
import org.helpinout.billonlights.model.database.entity.AddData
import org.helpinout.billonlights.model.database.entity.AddItem
import org.helpinout.billonlights.utils.*
import org.helpinout.billonlights.view.fragments.BottomSheetsRequestConfirmationFragment
import org.helpinout.billonlights.viewmodel.OfferViewModel
import org.jetbrains.anko.indeterminateProgressDialog
import timber.log.Timber


class AmbulanceHelpActivity : BaseActivity(), View.OnClickListener {
    private val ambulanceHelp = AddData()
    private var dialog: ProgressDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ambulanceHelp.activity_category = 7
        ambulanceHelp.activity_type = helpType
        ambulanceHelp.activity_uuid = getUuid()
        ambulanceHelp.date_time = currentDateTime()
        ambulanceHelp.geo_location = preferencesService.latitude + "," + preferencesService.longitude
        initTitle()
        we_can_pay.setOnClickListener(this)
        we_can_not_pay.setOnClickListener(this)
    }

    override fun getLayout(): Int {
        return R.layout.activity_ambulance_help
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
        ambulanceHelp.address = getAddress(preferencesService.latitude.toDouble(), preferencesService.longitude.toDouble())
        val viewModel = ViewModelProvider(this).get(OfferViewModel::class.java)
        viewModel.sendAmbulanceHelp(ambulanceHelp).observe(this, Observer {
            if (it.first != null && it.first?.status == 1) {
                saveRequestToDatabase()
            } else {
                dialog?.dismiss()
                // askForConfirmation()
            }
        })
    }

    private fun saveRequestToDatabase() {
        val addItemList = ArrayList<AddItem>()

        val singleItem = AddItem()
        singleItem.activity_type = ambulanceHelp.activity_type
        singleItem.activity_uuid = ambulanceHelp.activity_uuid
        singleItem.date_time = ambulanceHelp.date_time
        singleItem.activity_category = ambulanceHelp.activity_category
        singleItem.activity_count = ambulanceHelp.activity_count
        singleItem.geo_location = ambulanceHelp.geo_location
        singleItem.address = ambulanceHelp.address
        singleItem.qty = ambulanceHelp.qty
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
    }

    private fun askForConfirmation() {
        val deleteDialog = BottomSheetsRequestConfirmationFragment(helpType, onYesClick = { onYesClick() }, onNoClick = { onNoClick() })
        deleteDialog.show(supportFragmentManager, null)
    }

    private fun onYesClick() {
        Timber.d("")
    }

    private fun onNoClick() {
        if (helpType == HELP_TYPE_REQUEST) {
            val intent = Intent(baseContext!!, HomeActivity::class.java)
            intent.putExtra(SELECTED_INDEX, 1)
            intent.putExtra(PAGER_INDEX, 1)
            startActivity(intent)
        } else {
            val intent = Intent(baseContext!!, HomeActivity::class.java)
            intent.putExtra(SELECTED_INDEX, 2)
            intent.putExtra(PAGER_INDEX, 1)
            startActivity(intent)
        }
        finishWithFade()
    }

}
