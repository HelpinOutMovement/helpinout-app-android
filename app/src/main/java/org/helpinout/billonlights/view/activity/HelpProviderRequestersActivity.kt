package org.helpinout.billonlights.view.activity

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import com.avneesh.crashreporter.CrashReporter
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_help_map.*
import kotlinx.android.synthetic.main.bottom_sheet_help_provider_requester.*
import kotlinx.android.synthetic.main.layout_enable_location.*
import kotlinx.android.synthetic.main.layout_map_toolbar.*
import kotlinx.android.synthetic.main.layout_permission.*
import org.helpinout.billonlights.R
import org.helpinout.billonlights.model.database.entity.ActivityAddDetail
import org.helpinout.billonlights.model.database.entity.Mapping
import org.helpinout.billonlights.model.database.entity.MappingDetail
import org.helpinout.billonlights.model.database.entity.SuggestionRequest
import org.helpinout.billonlights.utils.*
import org.helpinout.billonlights.view.adapters.BottomSheetHelpAdapter
import org.helpinout.billonlights.view.view.DividerItemDecoration
import org.helpinout.billonlights.view.view.ItemOffsetDecoration
import org.helpinout.billonlights.viewmodel.HomeViewModel
import org.helpinout.billonlights.viewmodel.OfferViewModel
import org.jetbrains.anko.indeterminateProgressDialog
import org.jetbrains.anko.startActivity
import timber.log.Timber

class HelpProviderRequestersActivity : LocationActivity(), OnMapReadyCallback, View.OnClickListener {
    private var mapFragment: SupportMapFragment? = null
    private var dialog: ProgressDialog? = null
    private var suggestionData: SuggestionRequest? = null
    private var mMap: GoogleMap? = null
    private var location: Location? = null
    private var bottomItemList = ArrayList<ActivityAddDetail>()
    private var bottomAdapter: BottomSheetHelpAdapter? = null
    private var mapPadding: Int = 0
    private var builder: LatLngBounds.Builder = LatLngBounds.Builder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkLocationPermission()
        mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment?.getMapAsync(this)
        iv_menu.setImageResource(R.drawable.ic_arrow_back)
        iv_menu.setOnClickListener {
            goToRequestDetailScreen()
        }
        tv_change.setOnClickListener {
            startLocationPicker()
        }
        layout_peek.setOnClickListener(this)
        if (helpType == HELP_TYPE_OFFER) {
            tv_title.setText(R.string.select_help_requester)
            tv_comment.setText(R.string.phone_number_will_be_send_to_requester)
            button_continue.setText(R.string.send_offer)
            button_continue.setBackgroundResource(R.drawable.button_background)
        }
        button_continue.setOnClickListener(this)
        btnPermission.setOnClickListener(this)
        enableLocation.setOnClickListener(this)
        bottom_sheet.hide()
        mRecyclerView
    }

    private fun loadSuggestionData() {
        val data = intent.getStringExtra(SUGGESTION_DATA)!!
        suggestionData = Gson().fromJson(data, SuggestionRequest::class.java)
        iv_item.setImageResource(suggestionData!!.activity_category.getIcon())
        val viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        viewModel.getSuggestion(suggestionData!!).observe(this, Observer {
            it.first?.let { res ->
                if (helpType == HELP_TYPE_REQUEST) {
                    if (!res.data?.offers.isNullOrEmpty()) {
                        bottomItemList.clear()
                        bottomItemList.addAll(res.data?.offers!!)
                        bottomAdapter?.notifyDataSetChanged()
                    }
                } else {
                    if (!res.data?.requests.isNullOrEmpty()) {
                        bottomItemList.clear()
                        bottomItemList.addAll(res.data?.requests!!)
                        bottomAdapter?.notifyDataSetChanged()
                    }
                }
                if (helpType == HELP_TYPE_OFFER) {
                    tv_no_help_provider.setText(R.string.no_help_requeter)
                }
                tv_comment.visibleIf(bottomItemList.isNotEmpty())
                if (bottomItemList.isEmpty()) {
                    button_continue.setText(R.string.btn_continue)
                }
                tv_no_help_provider.visibleIf(bottomItemList.isEmpty())
                recycler_view.inVisibleIf(bottomItemList.isEmpty())

                mMap?.let {
                    it.clear()
                    showPinsOnMap(bottomItemList, if (helpType == HELP_TYPE_REQUEST) R.drawable.ic_help_provider else R.drawable.ic_help_requester)
                    builder = LatLngBounds.Builder()
                    fitMap()
                }
            } ?: kotlin.run {
                toastError(it.second)
            }
            bottom_sheet.show()
        })
    }

    private val mRecyclerView by lazy {
        recycler_view.itemAnimator = DefaultItemAnimator()
        recycler_view.setHasFixedSize(true)
        val divider = ContextCompat.getDrawable(this, R.drawable.line_divider)
        recycler_view.addItemDecoration(DividerItemDecoration(divider!!, 10, -10))
        bottomAdapter = BottomSheetHelpAdapter(bottomItemList)
        val itemDecorator = ItemOffsetDecoration(this, R.dimen.item_offset)
        recycler_view.addItemDecoration(itemDecorator)
        recycler_view.adapter = bottomAdapter
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
                            suggestionData?.latitude = latLing.latitude
                            suggestionData?.longitude = latLing.longitude

                            mMap?.clear()
                            mMap?.addMarker(MarkerOptions().position(latLing).title(place.address))
                            builder = LatLngBounds.Builder()
                            fitMap()
//                            mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLing, 14.0f))
                        }
                        tv_current_address.text = place.address
                    }
                }
                AutocompleteActivity.RESULT_ERROR -> {
                }
                Activity.RESULT_CANCELED -> {
                }
            }
        }
    }

    private fun fitMap() {
        val bounds = builder.build()
        val cu: CameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, mapPadding)
        mMap?.moveCamera(cu)
    }

    override fun onPermissionAllow() {
        buildGoogleApiClient()
        layoutPermission.hide()
        bottom_sheet.show()
    }

    override fun onPermissionCancel() {
        layoutPermission.show()
        bottom_sheet.hide()
    }

    override fun onLocationOnOff(isEnable: Boolean) {
        if (isEnable) {
            layoutLocation.hide()
            bottom_sheet.show()
            checkLocationPermission()
        } else {
            layoutLocation.show()
            bottom_sheet.hide()
        }
    }

    override fun onLocationChanged(location: Location?) {
        this.location = location
        if (location != null && mMap != null) {
            updateLocation()
        }
    }

    override fun onBackPressed() {
        goToRequestDetailScreen()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val latlong = LatLng(preferencesService.latitude, preferencesService.longitude)
        tv_current_address?.text = getAddress(preferencesService.latitude, preferencesService.longitude)
        mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(latlong, 14.0f))
        if (location != null && mMap != null) {
            updateLocation()
        }
    }

    private fun updateLocation() {
        location?.let { loc ->
            mMap?.let {
                mMap?.isMyLocationEnabled = true
                mMap!!.clear()
                changeMyLocationButton()
                mMap?.setOnCameraIdleListener {
                    val midLatLng = mMap!!.cameraPosition.target
                    tv_current_address.text = getAddress(midLatLng.latitude, midLatLng.longitude)
                    suggestionData?.latitude = midLatLng.latitude
                    suggestionData?.longitude = midLatLng.longitude
                    loadSuggestionData()
                }
                tv_current_address.text = getAddress(loc.latitude, loc.longitude)
                stopLocationUpdate()
                showPinsOnMap(bottomItemList, if (helpType == HELP_TYPE_REQUEST) R.drawable.ic_help_provider else R.drawable.ic_help_requester)
            }
        }
    }

    private fun changeMyLocationButton() {
        try {
            val locationButton = (mapFragment?.view?.findViewById<View>("1".toInt())?.parent as View).findViewById<View>("2".toInt())
            val rlp = locationButton.layoutParams as RelativeLayout.LayoutParams
            val ruleList = rlp.rules
            for (i in ruleList.indices) {
                rlp.removeRule(i)
            }
            rlp.bottomMargin = 100
            rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE)
            rlp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE)
        } catch (e: Exception) {

        }
    }

    private fun showPinsOnMap(placeData: ArrayList<ActivityAddDetail>, icon: Int) {
        placeData.forEach { detail ->
            try {
                val geoLocation = detail.geo_location!!.split(",")
                val latitude = geoLocation[0].toDouble()
                val longitude = geoLocation[1].toDouble()
                val name = detail.user_detail!!.first_name + " " + detail.user_detail!!.last_name
                createMarker(latitude, longitude, name, name, icon)
            } catch (e: Exception) {
                Timber.d("")
            }
        }
    }

    private fun createMarker(latitude: Double, longitude: Double, title: String?, snippet: String?, iconResID: Int) {
        val marker = MarkerOptions().position(LatLng(latitude, longitude))
        builder.include(marker.position)
        mMap?.addMarker(MarkerOptions().position(LatLng(latitude, longitude)).anchor(0.5f, 0.5f).title(title).snippet(snippet).icon(BitmapDescriptorFactory.fromResource(iconResID)))
    }


    override fun onClick(v: View?) {
        when (v) {
            button_continue -> {
                if (bottomItemList.isEmpty()) {
                    val intent = Intent(baseContext!!, HomeActivity::class.java)
                    intent.putExtra(SELECTED_INDEX, 1)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                } else {
                    if (bottomAdapter!!.getCheckedItemsList().isEmpty()) {
                        toastError(R.string.please_select_provider_helper)
                    } else sendRequestOffersToServer()
                }
            }
            layout_peek -> {
                val bottomSheetBehavior = BottomSheetBehavior.from(bottom_sheet)
                if (bottomSheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
                    iv_expend_collapse.setImageResource(R.drawable.ic_expand_less)
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED)
                } else {
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                    iv_expend_collapse.setImageResource(R.drawable.ic_expand_more)
                }
            }
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

    private fun sendRequestOffersToServer() {
        dialog = indeterminateProgressDialog(R.string.alert_msg_please_wait)
        dialog?.show()
        val list = bottomAdapter?.getCheckedItemsList() ?: listOf()
        val viewModel = ViewModelProvider(this).get(OfferViewModel::class.java)
        viewModel.sendOfferRequesterToServer(suggestionData!!.activity_type, suggestionData!!.activity_uuid, list).observe(this, Observer {
            if (it.first != null) {
                if (it.first!!.data != null) {
                    it.first!!.data!!.mapping?.forEach { mapping ->

                        if (mapping.offer_detail != null) {
                            mapping.offer_detail?.user_detail?.parent_uuid = it.first!!.data!!.activity_uuid
                            mapping.offer_detail?.user_detail?.activity_type = it.first!!.data!!.activity_type
                            mapping.offer_detail?.user_detail?.activity_uuid = mapping.offer_detail?.activity_uuid
                            mapping.offer_detail?.user_detail?.activity_category = mapping.offer_detail?.activity_category
                            mapping.offer_detail?.user_detail?.date_time = mapping.offer_detail?.date_time
                            mapping.offer_detail?.user_detail?.geo_location = mapping.offer_detail?.geo_location
                            mapping.offer_detail?.user_detail?.offer_condition = mapping.offer_detail?.offer_condition
                            mapping.offer_detail?.user_detail?.request_mapping_initiator = mapping.request_mapping_initiator
                        } else if (mapping.request_detail != null) {
                            mapping.request_detail?.user_detail?.parent_uuid = it.first!!.data!!.activity_uuid
                            mapping.request_detail?.user_detail?.activity_type = it.first!!.data!!.activity_type
                            mapping.request_detail?.user_detail?.activity_uuid = mapping.request_detail?.activity_uuid
                            mapping.request_detail?.user_detail?.activity_category = mapping.request_detail?.activity_category
                            mapping.request_detail?.user_detail?.date_time = mapping.request_detail?.date_time
                            mapping.request_detail?.user_detail?.geo_location = mapping.request_detail?.geo_location
                            mapping.request_detail?.user_detail?.offer_condition = mapping.request_detail?.offer_condition
                            mapping.request_detail?.user_detail?.request_mapping_initiator = mapping.request_mapping_initiator
                        }
                    }
                    if (it.first!!.data!!.mapping.isNullOrEmpty()) {
                        toastError(R.string.toast_error_some_error)
                        finish()
                    } else {
                        saveMappingToDataBase(it.first!!.data!!.mapping)
                        toastSuccess(if (helpType == HELP_TYPE_REQUEST) R.string.request_send_success else R.string.offer_send_success)
                    }
                } else {
                    dialog?.dismiss()
                }
            } else {
                if (!isNetworkAvailable()) {
                    toastError(R.string.toast_error_internet_issue)
                } else toastError(it.second)
                dialog?.dismiss()
                CrashReporter.logCustomLogs(it.second)
            }
        })

    }

    private fun saveMappingToDataBase(mapping: List<Mapping>?) {
        val mappingList = ArrayList<MappingDetail>()
        mapping?.forEach {
            if (it.offer_detail != null) {
                mappingList.add(it.offer_detail!!.user_detail!!)
            } else {
                mappingList.add(it.request_detail!!.user_detail!!)
            }
        }
        val viewModel = ViewModelProvider(this).get(OfferViewModel::class.java)
        viewModel.saveMapping(mappingList).observe(this, Observer {
            dialog?.dismiss()
            if (it) {
                startActivity<RequestDetailActivity>(OFFER_TYPE to helpType, INITIATOR to helpType, HELP_TYPE to helpType, ACTIVITY_UUID to (suggestionData?.activity_uuid ?: ""))
                overridePendingTransition(R.anim.enter, R.anim.exit)
                val returnIntent = Intent()
                setResult(Activity.RESULT_OK, returnIntent)
                finish()
            }
        })
    }

    private fun goToRequestDetailScreen() {
        if (helpType == HELP_TYPE_REQUEST) {
            val intent = Intent(baseContext!!, HomeActivity::class.java)
            intent.putExtra(SELECTED_INDEX, 1)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        } else {
            val intent = Intent(baseContext!!, HomeActivity::class.java)
            intent.putExtra(SELECTED_INDEX, 2)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        }
        finishWithSlideAnimation()
    }

    override fun getLayout(): Int {
        return R.layout.activity_help_map
    }

}
