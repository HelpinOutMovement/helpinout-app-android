package com.triline.billionlights.view.activity

import android.app.Activity
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import com.google.android.gms.common.api.Status
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.triline.billionlights.R
import com.triline.billionlights.model.database.entity.BottomHelp
import com.triline.billionlights.model.database.entity.PlaceData
import com.triline.billionlights.utils.*
import com.triline.billionlights.view.adapters.BottomSheetHelpAdapter
import com.triline.billionlights.view.view.DividerItemDecoration
import com.triline.billionlights.view.view.ItemOffsetDecoration
import com.triline.billionlights.viewmodel.HomeViewModel
import com.triline.billionlights.viewmodel.OfferViewModel
import kotlinx.android.synthetic.main.activity_help_map.*
import kotlinx.android.synthetic.main.bottom_sheet_help_provider_requester.*
import kotlinx.android.synthetic.main.layout_map_toolbar.*
import org.json.JSONObject
import java.util.*

class HelpProviderRequestersActivity : LocationActivity(), OnMapReadyCallback {
    private var mMap: GoogleMap? = null
    private var location: Location? = null

    private var bottomItemList = ArrayList<BottomHelp>()
    private lateinit var bottomAdapter: BottomSheetHelpAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkLocationPermission()
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        iv_menu.setImageResource(R.drawable.ic_arrow_back)
        iv_menu.setOnClickListener {
            onBackPressed()
        }
        tv_change.setOnClickListener {
            startLocationPicker()
        }
        val bottomSheetBehavior = BottomSheetBehavior.from(bottom_sheet)
        iv_expend_collapse.setOnClickListener {
            if (bottomSheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
                iv_expend_collapse.setImageResource(R.drawable.ic_expand_less)
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED)
            } else {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED)
                iv_expend_collapse.setImageResource(R.drawable.ic_expand_more)
            }
        }
        mRecyclerView
    }

    private val mRecyclerView by lazy {
        recycler_view.itemAnimator = DefaultItemAnimator()
        recycler_view.setHasFixedSize(true)
        val divider = ContextCompat.getDrawable(this, R.drawable.line_divider)
        recycler_view.addItemDecoration(DividerItemDecoration(divider!!, 10, -10))
        bottomAdapter = BottomSheetHelpAdapter(bottomItemList, onItemClick = { item -> onItemClick(item) })
        val itemDecorator = ItemOffsetDecoration(this, R.dimen.item_offset)
        recycler_view.addItemDecoration(itemDecorator)
        recycler_view.adapter = bottomAdapter
        loadBottomList()
    }

    private fun onItemClick(item: BottomHelp) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < DOUBLE_CLICK_TIME) {
            return
        }
    }

    private fun loadBottomList() {
        val viewModel = ViewModelProvider(this).get(OfferViewModel::class.java)
        viewModel.getBottomSheetItem().observe(this, Observer { list ->
            list?.let {
                bottomItemList.clear()
                bottomItemList.addAll(list)
            }
            bottomAdapter.notifyDataSetChanged()
        })
    }

    private fun startLocationPicker() {
        val fields: List<Place.Field> = listOf(Place.Field.ID, Place.Field.ADDRESS, Place.Field.NAME, Place.Field.LAT_LNG)
        val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields).build(this)
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    data?.let {
                        val place = Autocomplete.getPlaceFromIntent(data)
                        val latLing = place.latLng
                        latLing?.let {
                            mMap?.clear()
//                            mMap?.addMarker(MarkerOptions().position(latLing).title(place.address))
                            mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLing, 12.0f))
                            findRequesterOrProviders(it.latitude, it.longitude)
                        }
                        tv_current_address.text = place.address
                    }
                }
                AutocompleteActivity.RESULT_ERROR -> {
                    val status: Status = Autocomplete.getStatusFromIntent(data!!)
                    Log.i("TAG", status.statusMessage)
                }
                Activity.RESULT_CANCELED -> {
                    // The user canceled the operation.
                }
            }
        }
    }

    override fun onPermissionAllow() {
        buildGoogleApiClient()
    }

    override fun onLocationChanged(location: Location?) {
        this.location = location
        if (location != null && mMap != null) {
            updateLocation()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        if (location != null && mMap != null) {
            updateLocation()
        }
    }

    private fun updateLocation() {
        location?.let { loc ->
            mMap?.let {
                mMap!!.clear()
                val currentLocation = LatLng(loc.latitude, loc.longitude)
//                it.addMarker(MarkerOptions().position(currentLocation).title(getString(R.string.current_location)))
                it.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 14.0f))

                mMap?.setOnCameraIdleListener {
                    val midLatLng = mMap!!.cameraPosition.target
                    current_map_pin.show()
                    tv_current_address.text = getAddress(midLatLng.latitude, midLatLng.longitude)
                }

                tv_current_address.text = getAddress(loc.latitude, loc.longitude)
                stopLocationUpdate()
                findRequesterOrProviders(loc.latitude, loc.longitude)
            }
        }
    }

    private fun findRequesterOrProviders(latitude: Double, longitude: Double) {
        val url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?key=$PLACE_SEARCH_KEY&location=$latitude,$longitude&sensor=true&radius=1000&types=gas_station"

        val nearestPumpViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        nearestPumpViewModel.getNearestDestination(url)!!.observe(this, Observer {
            try {
                val json = JSONObject(it!!)
                finsHelperRequester(json, if (helpType == OFFER_HELP) R.drawable.ic_help_provider else R.drawable.ic_help_requester)
            } catch (e: Exception) {
                Log.d("", "")
            }
        })
    }

    private fun finsHelperRequester(json: JSONObject, icon: Int) {
        val nearestPumpViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        nearestPumpViewModel.parseDestination(json)!!.observe(this, Observer {
            try {
                showPinsOnMap(it!!, icon)
            } catch (e: Exception) {
                Log.d("", "")
            }
        })
    }

    private fun showPinsOnMap(placeData: ArrayList<PlaceData>, icon: Int) {
        placeData.forEach {
            createMarker(it.latitude!!, it.longnitude!!, it.name, it.location, icon)
        }
    }

    private fun createMarker(latitude: Double, longitude: Double, title: String?, snippet: String?, iconResID: Int) {
        mMap?.addMarker(MarkerOptions().position(LatLng(latitude, longitude)).anchor(0.5f, 0.5f).title(title).snippet(snippet).icon(BitmapDescriptorFactory.fromResource(iconResID)))
    }

    override fun getLayout(): Int {
        return R.layout.activity_help_map
    }
}
