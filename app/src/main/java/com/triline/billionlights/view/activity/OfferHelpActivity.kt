package com.triline.billionlights.view.activity

import android.location.Location
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import com.triline.billionlights.R
import com.triline.billionlights.model.database.entity.OfferHelpItem
import com.triline.billionlights.utils.*
import com.triline.billionlights.view.adapters.OfferHelpAdapter
import com.triline.billionlights.view.view.ItemOffsetDecoration
import com.triline.billionlights.viewmodel.HomeViewModel
import kotlinx.android.synthetic.main.activity_help.*
import org.jetbrains.anko.startActivity


class OfferHelpActivity : LocationActivity() {

    private var itemList = ArrayList<OfferHelpItem>()
    private lateinit var adapter: OfferHelpAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        changeAppLanguage(preferencesService.defaultLanguage)
        checkLocationPermission()
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
            FOOD -> {
                startActivity<FoodHelpActivity>(
                    HELP_TYPE to intent.getIntExtra(HELP_TYPE, 0),
                    ITEM_TYPE to FOOD
                )
            }
            PEOPLE -> {
                startActivity<PeopleHelpActivity>(HELP_TYPE to intent.getIntExtra(HELP_TYPE, 0))
            }
            SHELTER -> {
                startActivity<FoodHelpActivity>(
                    HELP_TYPE to intent.getIntExtra(HELP_TYPE, 0),
                    ITEM_TYPE to SHELTER
                )
            }
            MED_PPE -> {
                startActivity<FoodHelpActivity>(
                    HELP_TYPE to intent.getIntExtra(HELP_TYPE, 0),
                    ITEM_TYPE to MED_PPE
                )
            }
            TESTING -> {
                startActivity<FoodHelpActivity>(
                    HELP_TYPE to intent.getIntExtra(HELP_TYPE, 0),
                    ITEM_TYPE to TESTING
                )
            }
            MEDICINES -> {
                startActivity<FoodHelpActivity>(
                    HELP_TYPE to intent.getIntExtra(HELP_TYPE, 0),
                    ITEM_TYPE to MEDICINES
                )
            }
            AMBULANCE -> {
                startActivity<AmbulanceHelpActivity>(HELP_TYPE to intent.getIntExtra(HELP_TYPE, 0))
            }
            MEDICAL_EQUIPMENT -> {
                startActivity<FoodHelpActivity>(
                    HELP_TYPE to intent.getIntExtra(HELP_TYPE, 0),
                    ITEM_TYPE to MEDICAL_EQUIPMENT
                )
            }
            OTHER_THINGS -> {
                startActivity<FoodHelpActivity>(
                    HELP_TYPE to intent.getIntExtra(HELP_TYPE, 0),
                    ITEM_TYPE to OTHER_THINGS
                )
            }
        }
        overridePendingTransition(R.anim.enter, R.anim.exit)
    }


    override fun onLocationChanged(location: Location?) {
        location?.let {
            preferencesService.latitude = location.latitude.toString()
            preferencesService.longitude = location.longitude.toString()
            preferencesService.gpsAccuracy = location.accuracy.toInt().toString()
        }
    }

    override fun getLayout(): Int {
        return R.layout.activity_help
    }

    override fun onPermissionAllow() {
        buildGoogleApiClient()
    }
}