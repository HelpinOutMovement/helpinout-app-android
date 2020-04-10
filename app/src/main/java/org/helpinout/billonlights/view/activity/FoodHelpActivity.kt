package org.helpinout.billonlights.view.activity

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.view.View
import android.widget.LinearLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
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
    private val suggestionData = SuggestionData()
    private val showMapCode: Int = 43

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addData.activity_uuid = getUuid()
        addData.activity_type = helpType
        addData.date_time = currentDateTime()
        addData.geo_location = preferencesService.latitude + "," + preferencesService.longitude
        when (intent.getIntExtra(CATEGORY_TYPE, 0)) {
            CATEGORY_FOOD -> {
                addData.activity_category = 1
                suggestionData.activityCategory = 1
                logo.setImageResource(R.drawable.ic_food)
            }
            CATEGORY_SHELTER -> {
                addData.activity_category = 3
                suggestionData.activityCategory = 3
                logo.setImageResource(R.drawable.ic_shelter)
            }
            CATEGORY_MED_PPE -> {
                addData.activity_category = 4
                suggestionData.activityCategory = 4
                logo.setImageResource(R.drawable.ic_mask)
            }
            CATEGORY_TESTING -> {
                addData.activity_category = 5
                suggestionData.activityCategory = 5
                logo.setImageResource(R.drawable.ic_testing)
            }
            CATEGORY_MEDICINES -> {
                addData.activity_category = 6
                suggestionData.activityCategory = 6
                logo.setImageResource(R.drawable.ic_medicines)
            }
            CATEGORY_MEDICAL_EQUIPMENT -> {
                addData.activity_category = 8
                suggestionData.activityCategory = 8
                logo.setImageResource(R.drawable.ic_medical)
            }
            CATEGORY_OTHERS -> {
                addData.activity_category = 0
                suggestionData.activityCategory = 0
                logo.setImageResource(R.drawable.ic_other)
            }
        }

        initTitle()

        add_more.setOnClickListener(this)
        we_can_pay.setOnClickListener(this)
        we_can_not_pay.setOnClickListener(this)
        layout_items.addView(getFoodLayout(layout_items))
        addData.address = getAddress(preferencesService.latitude.toDouble(), preferencesService.longitude.toDouble())
    }

    private fun getFoodLayout(layoutMain: LinearLayout): View {
        val layout = layoutInflater.inflate(R.layout.item_food, null)
        val activityDetail = ActivityDetail()
        if (layoutMain.childCount > 0) layout.iv_expend_collapse.show()
        layout.iv_expend_collapse.setOnClickListener {
            addData.activity_detail.remove(activityDetail)
            suggestionData.activity_detail.remove(activityDetail)
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
        suggestionData.activity_detail.add(activityDetail)
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
                addData.activity_detail_string = getDetail()
                addData.conditions = edt_conditions.text.toString()
                suggestionData.activityCount = suggestionData.activity_detail.size

                suggestionData.latitude = preferencesService.latitude
                suggestionData.longitude = preferencesService.longitude
                suggestionData.accuracy = preferencesService.gpsAccuracy
                suggestionData.activity_detail_string = getDetail()
                sendDataOnMap()
            }
            we_can_not_pay -> {
                hideKeyboard()
                addData.pay = 0
                addData.activity_detail_string = getDetail()
                addData.conditions = edt_conditions.text.toString()

                suggestionData.activityCount = suggestionData.activity_detail.size
                suggestionData.latitude = preferencesService.latitude
                suggestionData.longitude = preferencesService.longitude
                suggestionData.accuracy = preferencesService.gpsAccuracy
                suggestionData.activity_detail_string = getDetail()
                sendDataOnMap()
            }
        }
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

    private fun sendDataOnMap() {

        dialog = indeterminateProgressDialog(R.string.alert_msg_please_wait)
        dialog?.show()
        val viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        viewModel.addActivity(addData).observe(this, Observer {
            if (it.first != null) {
                saveRequestToDatabase(it.first)
            } else {
                dialog?.dismiss()
            }
        })
    }

    private fun saveRequestToDatabase(first: AddDataResponses?) {
        first?.data?.let { item ->
            val addItemList = ArrayList<AddItem>()
            var itemDetail = ""

            item.activity_detail?.forEach { detail ->
                itemDetail += detail.detail + "(" + detail.activity_count + "),"
            }
            val singleItem = AddItem()
            singleItem.activity_type = addData.activity_type
            singleItem.detail = itemDetail
            singleItem.activity_uuid = addData.activity_uuid
            singleItem.date_time = addData.date_time
            singleItem.activity_category = addData.activity_category
            singleItem.activity_count = addData.activity_count
            singleItem.geo_location = addData.geo_location
            singleItem.qty = addData.qty
            singleItem.address = getAddress(preferencesService.latitude.toDouble(), preferencesService.longitude.toDouble())
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
        startActivityForResult<HelpProviderRequestersActivity>(showMapCode, HELP_TYPE to helpType, CATEGORY_TYPE to intent.getIntExtra(CATEGORY_TYPE, 0))
        finishWithFade()
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

    override fun getLayout(): Int {
        return R.layout.activity_food_help
    }
}
