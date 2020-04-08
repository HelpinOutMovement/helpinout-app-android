package com.triline.billionlights.view.activity

import android.os.Bundle
import android.text.Editable
import android.view.View
import android.widget.LinearLayout
import com.triline.billionlights.R
import com.triline.billionlights.model.database.entity.ActivityDetail
import com.triline.billionlights.model.database.entity.AddData
import com.triline.billionlights.model.database.entity.SuggestionData
import com.triline.billionlights.utils.*
import com.triline.billionlights.view.view.HelpInTextWatcher
import kotlinx.android.synthetic.main.activity_food_help.*
import kotlinx.android.synthetic.main.item_food.view.*
import org.jetbrains.anko.startActivityForResult
import org.json.JSONArray
import org.json.JSONObject


class FoodHelpActivity : BaseActivity(), View.OnClickListener {
    private val maxLimit = 4
    private val addData = AddData()
    private val suggestionData = SuggestionData()
    private val showMapCode = 56

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addData.uuid = getUuid()
        when (intent.getIntExtra(ITEM_TYPE, 0)) {
            FOOD -> {
                addData.activityCategory = 1
                suggestionData.activityCategory = 1
                logo.setImageResource(R.drawable.ic_food)
            }
            SHELTER -> {
                addData.activityCategory = 3
                suggestionData.activityCategory = 3
                logo.setImageResource(R.drawable.ic_shelter)
            }
            MED_PPE -> {
                addData.activityCategory = 4
                suggestionData.activityCategory = 4
                logo.setImageResource(R.drawable.ic_mask)
            }
            TESTING -> {
                addData.activityCategory = 5
                suggestionData.activityCategory = 5
                logo.setImageResource(R.drawable.ic_testing)
            }
            MEDICINES -> {
                addData.activityCategory = 6
                suggestionData.activityCategory = 6
                logo.setImageResource(R.drawable.ic_medicines)
            }
            MEDICAL_EQUIPMENT -> {
                addData.activityCategory = 8
                suggestionData.activityCategory = 8
                logo.setImageResource(R.drawable.ic_medical)
            }
            OTHER_THINGS -> {
                addData.activityCategory = 9
                suggestionData.activityCategory = 9
                logo.setImageResource(R.drawable.ic_other)
            }
        }

        initTitle()

        add_more.setOnClickListener(this)
        we_can_pay.setOnClickListener(this)
        we_can_not_pay.setOnClickListener(this)
        layout_items.addView(getFoodLayout(layout_items))
        addData.address = getAddress(
            preferencesService.latitude.toDouble(),
            preferencesService.longitude.toDouble()
        )
    }

    private fun getFoodLayout(layoutMain: LinearLayout): View {
        val layout = layoutInflater.inflate(R.layout.item_food, null)
        val activityDetail = ActivityDetail()
        if (layoutMain.childCount > 0)
            layout.iv_expend_collapse.show()
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

        addData.activityCount = addData.activity_detail.size
        addData.activity_detail.forEach { item ->
            val jsonObj = JSONObject()
            jsonObj.put("detail", item.detail)
            jsonObj.put("qty", item.qty)
            jsonArray.put(jsonObj)
        }
        return jsonArray
    }

    private fun sendDataOnMap() {

        startActivityForResult<HelpProviderRequestersActivity>(
            showMapCode,
            HELP_TYPE to helpType,
            ITEM_TYPE to intent.getIntExtra(ITEM_TYPE, 0)
        )

//        val dialog = indeterminateProgressDialog(R.string.alert_msg_please_wait)
//        dialog.setCancelable(false)
//        dialog.show()
//        val viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
//
//        viewModel.getSuggestion(suggestionData).observe(this, Observer {
//            dialog.dismiss()
//            Log.d("", "")
//        })
//        viewModel.addActivity(addData).observe(this, Observer {
//            dialog.dismiss()
//            Log.d("", "")
//        })
    }

    override fun getLayout(): Int {
        return R.layout.activity_food_help
    }
}
