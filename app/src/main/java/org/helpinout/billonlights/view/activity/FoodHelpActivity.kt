package org.helpinout.billonlights.view.activity

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.view.View
import android.widget.LinearLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.avneesh.crashreporter.CrashReporter
import kotlinx.android.synthetic.main.activity_food_help.*
import kotlinx.android.synthetic.main.item_food.view.*
import org.helpinout.billonlights.R
import org.helpinout.billonlights.model.database.entity.ActivityDetail
import org.helpinout.billonlights.model.database.entity.AddData
import org.helpinout.billonlights.model.database.entity.SuggestionRequest
import org.helpinout.billonlights.utils.*
import org.helpinout.billonlights.view.view.HelpInTextWatcher
import org.helpinout.billonlights.viewmodel.HomeViewModel
import org.jetbrains.anko.indeterminateProgressDialog
import org.json.JSONArray
import org.json.JSONObject


class FoodHelpActivity : BaseActivity(), View.OnClickListener {
    private var dialog: ProgressDialog? = null
    private val maxLimit = 4
    private val addData = AddData()
    private val suggestionData = SuggestionRequest()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addData.activity_uuid = getUuid()
        addData.activity_type = helpType
        addData.date_time = currentDateTime()
        addData.geo_location = preferencesService.latitude.toString() + "," + preferencesService.longitude.toString()

        val categoryType = intent.getIntExtra(CATEGORY_TYPE, 0)
        addData.activity_category = categoryType
        suggestionData.activity_category = categoryType
        logo.setImageResource(categoryType.getIcon())
        initTitle()
        add_more.setOnClickListener(this)
        we_can_pay.setOnClickListener(this)
        we_can_not_pay.setOnClickListener(this)
        layout_items.addView(getFoodLayout(layout_items))
        addData.address = getAddress(preferencesService.latitude, preferencesService.longitude)
    }

    private fun getFoodLayout(layoutMain: LinearLayout): View {
        val layout = layoutInflater.inflate(R.layout.item_food, null)
        val activityDetail = ActivityDetail()
        if (layoutMain.childCount > 0) layout.iv_expend_collapse.show()
        layout.iv_expend_collapse.setOnClickListener {
            addData.activity_detail.remove(activityDetail)
            layoutMain.removeView(layout)
            add_more.show()
        }
        layout.edt_item.addTextChangedListener(object : HelpInTextWatcher() {
            override fun afterTextChanged(s: Editable?) {
                activityDetail.detail = s.toString()
            }
        })
        layout.edt_qty.addTextChangedListener(object : HelpInTextWatcher() {
            override fun afterTextChanged(s: Editable?) {
                activityDetail.qty = s.toString()
            }
        })
        addData.activity_detail.add(activityDetail)
        return layout
    }

    override fun onClick(view: View?) {
        when (view) {
            add_more -> {
                layout_items.addView(getFoodLayout(layout_items))
                if (layout_items.childCount >= maxLimit) {
                    add_more.hide()
                }
            }
            we_can_pay -> {
                hideKeyboard()
                addData.pay = 1
                sendData()
            }
            we_can_not_pay -> {
                hideKeyboard()
                addData.pay = 0
                sendData()
            }
        }
    }

    private fun sendData() {
        addData.activity_detail_string = getDetail()
        addData.conditions = edt_conditions.text.toString()
        suggestionData.activity_type = helpType
        suggestionData.latitude = preferencesService.latitude
        suggestionData.longitude = preferencesService.longitude
        suggestionData.accuracy = preferencesService.gpsAccuracy
        val address = getAddress(preferencesService.latitude, preferencesService.longitude)
        dialog = indeterminateProgressDialog(R.string.alert_msg_please_wait)
        dialog?.show()
        val viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        viewModel.addActivity(addData, address).observe(this, Observer {
            dialog?.dismiss()
            if (it.first != null) {
                askForConfirmation(addData.activity_uuid, suggestionData)
            } else {
                CrashReporter.logCustomLogs(it.second)
                if (!isNetworkAvailable()) {
                    toastError(R.string.toast_error_internet_issue)
                } else toastError(it.second)

            }
        })
    }

    private fun getDetail(): JSONArray {
        val jsonArray = JSONArray()
        addData.activity_count = addData.activity_detail.size
        addData.activity_detail.forEach { item ->
            val jsonObj = JSONObject()
            jsonObj.put("detail", item.detail)
            jsonObj.put("qty", item.qty)
            jsonArray.put(jsonObj)
        }
        return jsonArray
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == showMapCode) {
            val returnIntent = Intent()
            setResult(Activity.RESULT_OK, returnIntent)
            finishWithSlideAnimation()
        }
    }

    override fun getLayout(): Int {
        return R.layout.activity_food_help
    }
}
