package com.triline.billionlights.view.activity

import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import com.triline.billionlights.R
import com.triline.billionlights.model.database.entity.AskForHelpItem
import com.triline.billionlights.utils.*
import com.triline.billionlights.view.adapters.AskForHelpAdapter
import com.triline.billionlights.view.view.ItemOffsetDecoration
import com.triline.billionlights.viewmodel.HomeViewModel
import com.triline.billionlights.viewmodel.OfferViewModel
import kotlinx.android.synthetic.main.activity_help.*
import org.jetbrains.anko.startActivity


class AskForHelpActivity : LocationActivity() {

    private var itemList = ArrayList<AskForHelpItem>()
    private lateinit var adapter: AskForHelpAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        changeAppLanguage(preferencesService.defaultLanguage)
        checkLocationPermission()
        checkOfferList()
        mRecyclerView
    }

    private fun checkOfferList() {
        val viewModel = ViewModelProvider(this).get(OfferViewModel::class.java)
        viewModel.getUserRequestOfferList(helpType.toString()).observe(this, Observer {
            Log.d("", "")
        })
    }

    private val mRecyclerView by lazy {
        recycler_view.itemAnimator = DefaultItemAnimator()
        recycler_view.setHasFixedSize(true)
        val layoutManager = GridLayoutManager(this@AskForHelpActivity, 2)
        recycler_view.layoutManager = layoutManager

        adapter = AskForHelpAdapter(itemList, onItemClick = { item -> onItemClick(item) })
        val itemDecorator = ItemOffsetDecoration(this@AskForHelpActivity, R.dimen.item_offset)
        recycler_view.addItemDecoration(itemDecorator)
        recycler_view.adapter = adapter
        loadAskForHelpList()
    }

    @Synchronized
    private fun loadAskForHelpList() {
        val viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        viewModel.getAskForHelpItems(this).observe(this, Observer { list ->
            progress_bar.hide()
            list?.let {
                itemList.clear()
                itemList.addAll(list)
            }
            adapter.notifyDataSetChanged()
        })
    }

    private fun onItemClick(item: AskForHelpItem) {
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