package org.helpinout.billonlights.view.activity

import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import kotlinx.android.synthetic.main.activity_help.*
import kotlinx.android.synthetic.main.layout_enable_location.*
import kotlinx.android.synthetic.main.layout_permission.*
import org.helpinout.billonlights.R
import org.helpinout.billonlights.model.database.entity.OfferHelpItem
import org.helpinout.billonlights.utils.*
import org.helpinout.billonlights.view.adapters.OfferHelpAdapter
import org.helpinout.billonlights.view.view.ItemOffsetDecoration
import org.helpinout.billonlights.viewmodel.HomeViewModel
import org.jetbrains.anko.startActivity


class OfferHelpActivity : LocationActivity(), View.OnClickListener {

    private var itemList = ArrayList<OfferHelpItem>()
    private lateinit var adapter: OfferHelpAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.title = getString(R.string.title_offer_help_with)
        checkLocationPermission()
        btnPermission.setOnClickListener(this)
        btnPermission.setOnClickListener(this)
        enableLocation.setOnClickListener(this)
        mRecyclerView
    }

    private val mRecyclerView by lazy {
        recycler_view.itemAnimator = DefaultItemAnimator()
        recycler_view.setHasFixedSize(true)
        adapter = OfferHelpAdapter(itemList, onItemClick = { item -> onItemClick(item) })
        val itemDecorator = ItemOffsetDecoration(this@OfferHelpActivity, R.dimen.item_offset)
        recycler_view.addItemDecoration(itemDecorator)
        recycler_view.adapter = adapter
        loadOfferHelpList()
    }

    @Synchronized
    private fun loadOfferHelpList() {
        val viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        viewModel.getOfferHelpItems(this).observe(this, Observer { list ->
            progress_bar.hide()
            list?.let {
                itemList.clear()
                itemList.addAll(list)
            }
            adapter.notifyDataSetChanged()
        })
    }

    private fun onItemClick(item: OfferHelpItem) {
        when (item.type) {
            CATEGORY_FOOD -> {
                startActivity<FoodHelpActivity>(HELP_TYPE to intent.getIntExtra(HELP_TYPE, 0), CATEGORY_TYPE to CATEGORY_FOOD)
            }
            CATEGORY_PEOPLE -> {
                startActivity<PeopleHelpActivity>(HELP_TYPE to intent.getIntExtra(HELP_TYPE, 0))
            }
            CATEGORY_SHELTER -> {
                startActivity<FoodHelpActivity>(HELP_TYPE to intent.getIntExtra(HELP_TYPE, 0), CATEGORY_TYPE to CATEGORY_SHELTER)
            }
            CATEGORY_MED_PPE -> {
                startActivity<FoodHelpActivity>(HELP_TYPE to intent.getIntExtra(HELP_TYPE, 0), CATEGORY_TYPE to CATEGORY_MED_PPE)
            }
            CATEGORY_TESTING -> {
                startActivity<FoodHelpActivity>(HELP_TYPE to intent.getIntExtra(HELP_TYPE, 0), CATEGORY_TYPE to CATEGORY_TESTING)
            }
            CATEGORY_MEDICINES -> {
                startActivity<FoodHelpActivity>(HELP_TYPE to intent.getIntExtra(HELP_TYPE, 0), CATEGORY_TYPE to CATEGORY_MEDICINES)
            }
            CATEGORY_AMBULANCE -> {
                startActivity<AmbulanceHelpActivity>(HELP_TYPE to intent.getIntExtra(HELP_TYPE, 0))
            }
            CATEGORY_MEDICAL_EQUIPMENT -> {
                startActivity<FoodHelpActivity>(HELP_TYPE to intent.getIntExtra(HELP_TYPE, 0), CATEGORY_TYPE to CATEGORY_MEDICAL_EQUIPMENT)
            }
            CATEGORY_OTHERS -> {
                startActivity<FoodHelpActivity>(HELP_TYPE to intent.getIntExtra(HELP_TYPE, 0), CATEGORY_TYPE to CATEGORY_OTHERS)
            }
        }
        overridePendingTransition(R.anim.enter, R.anim.exit)
    }


    override fun onLocationChanged(location: Location?) {
        location?.let {
            preferencesService.latitude = location.latitude
            preferencesService.longitude = location.longitude
            preferencesService.gpsAccuracy = location.accuracy.toInt().toString()
        }
    }

    override fun getLayout(): Int {
        return R.layout.activity_help
    }

    override fun onPermissionAllow() {
        buildGoogleApiClient()
        layoutPermission.hide()
    }

    override fun onPermissionCancel() {
        layoutPermission.show()
    }

    override fun onLocationOnOff(isEnable: Boolean) {
        if (isEnable) {
            layoutLocation.hide()
        } else {
            layoutLocation.show()
        }
    }

    override fun onClick(v: View?) {
        when (v) {
            btnPermission -> {
                showSetting = true
                checkLocationPermission()
            }
            enableLocation -> {
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)

            }
        }
    }
}