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
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_food_help.*
import kotlinx.android.synthetic.main.item_food.view.*
import org.helpinout.billonlights.R
import org.helpinout.billonlights.model.database.entity.*
import org.helpinout.billonlights.utils.*
import org.helpinout.billonlights.view.fragments.BottomSheetsRequestConfirmationFragment
import org.helpinout.billonlights.view.view.HelpInTextWatcher
import org.helpinout.billonlights.viewmodel.HomeViewModel
import org.helpinout.billonlights.viewmodel.OfferViewModel
import org.jetbrains.anko.indeterminateProgressDialog
import org.jetbrains.anko.startActivityForResult
import org.json.JSONArray
import org.json.JSONObject


class FoodHelpActivity : BaseActivity(), View.OnClickListener {
    private var dialog: ProgressDialog? = null
    private val maxLimit = 4
    private val addData = AddData()
    private val suggestionData = SuggestionRequest()
    private val showMapCode: Int = 43

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addData.activity_uuid = getUuid()
        addData.activity_type = helpType
        addData.date_time = currentDateTime()
        addData.geo_location = preferencesService.latitude.toString() + "," + preferencesService.longitude.toString()
        when (intent.getIntExtra(CATEGORY_TYPE, 0)) {
            CATEGORY_FOOD -> {
                addData.activity_category = 1
                suggestionData.activity_category = 1
                logo.setImageResource(R.drawable.ic_food)
            }
            CATEGORY_SHELTER -> {
                addData.activity_category = 3
                suggestionData.activity_category = 3
                logo.setImageResource(R.drawable.ic_shelter)
            }
            CATEGORY_MED_PPE -> {
                addData.activity_category = 4
                suggestionData.activity_category = 4
                logo.setImageResource(R.drawable.ic_mask)
            }
            CATEGORY_TESTING -> {
                addData.activity_category = 5
                suggestionData.activity_category = 5
                logo.setImageResource(R.drawable.ic_testing)
            }
            CATEGORY_MEDICINES -> {
                addData.activity_category = 6
                suggestionData.activity_category = 6
                logo.setImageResource(R.drawable.ic_medicines)
            }
            CATEGORY_MEDICAL_EQUIPMENT -> {
                addData.activity_category = 8
                suggestionData.activity_category = 8
                logo.setImageResource(R.drawable.ic_medical)
            }
            CATEGORY_OTHERS -> {
                addData.activity_category = 0
                suggestionData.activity_category = 0
                logo.setImageResource(R.drawable.ic_other)
            }
        }

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
        dialog = indeterminateProgressDialog(R.string.alert_msg_please_wait)
        dialog?.show()
        val viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        viewModel.addActivity(addData).observe(this, Observer {
            if (it.first != null) {
                saveRequestToDatabase(it.first)
            } else {
                CrashReporter.logCustomLogs(it.second)
                if (!isNetworkAvailable()) {
                    toastError(R.string.toast_error_internet_issue)
                } else toastError(it.second)
                dialog?.dismiss()
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


    private fun saveRequestToDatabase(first: ActivityResponses?) {
        first?.data?.let { item ->
            val addItemList = ArrayList<AddCategoryDbItem>()
            var itemDetail = ""
            item.activity_detail?.forEachIndexed { index, detail ->
                if (!detail.detail.isNullOrEmpty()) {
                    itemDetail += detail.detail
                }
                if (detail.quantity != null) {
                    itemDetail += "(" + detail.quantity + ")"
                }
                if (item.activity_detail!!.size - 1 != index) {
                    itemDetail += "<br/>"
                }
            }
            val categoryItem = AddCategoryDbItem()
            categoryItem.activity_type = addData.activity_type
            categoryItem.detail = itemDetail
            categoryItem.activity_uuid = addData.activity_uuid
            categoryItem.date_time = addData.date_time
            categoryItem.activity_category = addData.activity_category
            categoryItem.activity_count = addData.activity_count
            categoryItem.geo_location = addData.geo_location
            categoryItem.address = getAddress(preferencesService.latitude, preferencesService.longitude)
            categoryItem.status = 1

            addItemList.add(categoryItem)

            val viewModel = ViewModelProvider(this).get(OfferViewModel::class.java)
            viewModel.saveFoodItemToDatabase(addItemList).observe(this, Observer {
                dialog?.dismiss()
                if (it) {
                    askForConfirmation(addData.activity_uuid)
                }
            })
        } ?: kotlin.run {
            dialog?.dismiss()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == showMapCode) {
            finish()
        }
    }

    private fun askForConfirmation(activity_uuid: String) {
        val deleteDialog = BottomSheetsRequestConfirmationFragment(helpType, activity_uuid, onConfirmationYesClick = { onYesClick() }, onConfirmationNoClick = { type, uuid -> onConfirmationNoClick(type, uuid) })
        deleteDialog.show(supportFragmentManager, null)
    }

    private fun onYesClick() {
        suggestionData.activity_uuid = addData.activity_uuid
        val suggestionDataAsString = Gson().toJson(suggestionData)
        startActivityForResult<HelpProviderRequestersActivity>(showMapCode, SUGGESTION_DATA to suggestionDataAsString, HELP_TYPE to helpType)
        overridePendingTransition(R.anim.enter, R.anim.exit)
        finishWithFade()
    }

    override fun getLayout(): Int {
        return R.layout.activity_food_help
    }
}
