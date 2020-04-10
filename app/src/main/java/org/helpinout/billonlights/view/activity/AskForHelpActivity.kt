package org.helpinout.billonlights.view.activity

import android.location.Location
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.activity_help.*
import org.helpinout.billonlights.R
import org.helpinout.billonlights.model.database.entity.AskForHelpItem
import org.helpinout.billonlights.utils.*
import org.helpinout.billonlights.view.adapters.AskForHelpAdapter
import org.helpinout.billonlights.view.view.ItemOffsetDecoration
import org.helpinout.billonlights.viewmodel.HomeViewModel
import org.jetbrains.anko.startActivity


class AskForHelpActivity : LocationActivity() {

    private var itemList = ArrayList<AskForHelpItem>()
    private lateinit var adapter: AskForHelpAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.title = getString(R.string.title_need_help_with)
        checkLocationPermission()
        mRecyclerView
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