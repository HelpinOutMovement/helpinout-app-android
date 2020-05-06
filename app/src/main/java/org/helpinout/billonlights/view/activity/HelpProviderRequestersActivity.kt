package org.helpinout.billonlights.view.activity

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.DefaultItemAnimator
import com.avneesh.crashreporter.CrashReporter
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.Gson
import com.google.maps.android.SphericalUtil
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
    private lateinit var location: Location
    private var bottomItemList = ArrayList<ActivityAddDetail>()
    private var bottomAdapter: BottomSheetHelpAdapter? = null
    private var radius: Float = 0.0F
    private var zoomLevel = 10.7F

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkLocationPermission()
        val data = intent.getStringExtra(SUGGESTION_DATA)!!

        location = Location("")
        suggestionData = Gson().fromJson(data, SuggestionRequest::class.java)

        zoomLevel = preferencesService.zoomLevel

        location.latitude = suggestionData?.latitude ?: preferencesService.latitude
        location.longitude = suggestionData?.longitude ?: preferencesService.longitude

        mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment?.getMapAsync(this)

        iv_menu.setImageResource(R.drawable.ic_arrow_back)
        iv_menu.setOnClickListener {
            goToRequestDetailScreen()
        }
        tv_change.hide()

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
        chk_all.setOnClickListener(this)
        bottom_sheet.hide()
        iv_item.setImageResource(suggestionData!!.activity_category.getIcon())
        mRecyclerView
    }

    private fun loadSuggestionData() {
        progressBar.show()
        val viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        viewModel.getSuggestion(suggestionData!!, radius).observe(this, Observer {
            it.first?.let { res ->
                progressBar.hide()
                preferencesService.zoomLevel = mMap?.cameraPosition?.zoom ?: 10F
                if (helpType == HELP_TYPE_REQUEST) {

                    if (!res.data?.offers.isNullOrEmpty()) {
                        bottomItemList.clear()

                        val list = res.data?.offers!!.sortedBy { it.user_detail?.distance?.toDouble() }
                        bottomItemList.addAll(list)
                        bottomAdapter?.notifyDataSetChanged()
                    } else {
                        bottomItemList.clear()
                        bottomAdapter?.notifyDataSetChanged()
                    }
                } else {
                    if (!res.data?.requests.isNullOrEmpty()) {
                        bottomItemList.clear()
                        val list = res.data?.requests!!.sortedBy { it.user_detail?.distance?.toDouble() }
                        bottomItemList.addAll(list)
                        bottomAdapter?.notifyDataSetChanged()
                    } else {
                        bottomItemList.clear()
                        bottomAdapter?.notifyDataSetChanged()
                    }
                }
                if (helpType == HELP_TYPE_OFFER) {
                    tv_no_help_provider.setText(R.string.no_help_requeter)
                }
                tv_comment.visibleIf(bottomItemList.isNotEmpty())
                if (bottomItemList.isEmpty()) {
                    button_continue.setText(R.string.btn_continue)
                } else if (helpType == HELP_TYPE_REQUEST) {
                    button_continue.setText(R.string.send_request)
                } else if (helpType == HELP_TYPE_OFFER) {
                    button_continue.setText(R.string.send_offer)
                }

                chk_all.visibleIf(helpType == HELP_TYPE_OFFER && bottomItemList.isNotEmpty())
                divider2.visibleIf(helpType == HELP_TYPE_OFFER && bottomItemList.isNotEmpty())

                tv_no_help_provider.visibleIf(bottomItemList.isEmpty())
                recycler_view.goneIf(bottomItemList.isEmpty())

                mMap?.let {
                    mMap!!.clear()
                    val latLng = LatLng(location.latitude, location.longitude)
                    val address = getAddress(location.latitude, location.longitude)
                    showCurrentLocation(latLng, address)
                    showPinsOnMap(bottomItemList, if (helpType == HELP_TYPE_REQUEST) R.drawable.ic_help_provider else R.drawable.ic_help_requester)
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
        bottomAdapter = BottomSheetHelpAdapter(bottomItemList, onCheckedChange = { onCheckedChange() })
        val itemDecorator = ItemOffsetDecoration(this, R.dimen.item_offset)
        recycler_view.addItemDecoration(itemDecorator)
        recycler_view.adapter = bottomAdapter
    }

    private fun onCheckedChange() {
        val isAllChecked = bottomAdapter?.isAllItemChecked() ?: false
        chk_all.isChecked = isAllChecked
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

    }

    override fun onBackPressed() {
        goToRequestDetailScreen()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap?.uiSettings?.isZoomControlsEnabled = true
        mMap?.uiSettings?.setAllGesturesEnabled(true)
        val latLng = LatLng(location.latitude, location.longitude)
        tv_toolbar_address?.text = getAddress(location.latitude, location.longitude)
        mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel))
        if (mMap != null) {
            try {
                updateLocation()
            } catch (e: Exception) {
                Timber.d("")
            }
        }
    }

    private fun updateLocation() {
        location.let { loc ->
            mMap?.let {
                mMap!!.clear()
                val latlng = LatLng(loc.latitude, loc.longitude)
                mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, zoomLevel))
                detectRadius()
                stopLocationUpdate()
            }
        }
    }

    private fun detectRadius() {
        mMap?.setOnCameraIdleListener {
            mMap?.let {
                val visibleRegion = it.projection.visibleRegion
                val midLatLng = mMap!!.cameraPosition.target
                suggestionData?.latitude = midLatLng.latitude
                suggestionData?.longitude = midLatLng.longitude
                val farRight: LatLng = visibleRegion.farRight
                val farLeft: LatLng = visibleRegion.farLeft
                radius = (SphericalUtil.computeDistanceBetween(farLeft, farRight) / 2).toFloat()
                loadSuggestionData()
            }
        }
    }

    private fun showPinsOnMap(placeData: ArrayList<ActivityAddDetail>, icon: Int) {
        placeData.forEach { detail ->
            try {
                val geoLocation = detail.geo_location!!.split(",")
                val latitude = geoLocation[0].toDouble()
                val longitude = geoLocation[1].toDouble()
                val name = detail.user_detail!!.profile_name
                createMarker(latitude, longitude, name, name, icon)
            } catch (e: Exception) {
                Timber.d("")
            }
        }
    }

    private fun createMarker(latitude: Double, longitude: Double, title: String?, snippet: String?, iconResID: Int) {
        val marker = MarkerOptions().position(LatLng(latitude, longitude)).title(title).icon(BitmapDescriptorFactory.fromResource(iconResID))
        mMap?.addMarker(marker)
    }

    private fun showCurrentLocation(latLng: LatLng, title: String) {
        tv_toolbar_address.text = title
        val markerOptions = MarkerOptions()
        markerOptions.position(latLng)
        markerOptions.title(title)
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
        mMap?.addMarker(markerOptions)
    }

    override fun onClick(v: View?) {
        when (v) {
            button_continue -> {
                if (bottomItemList.isEmpty()) {
                    goToRequestDetailScreen()
                } else {
                    if (bottomAdapter!!.getCheckedItemsList().isEmpty()) {
                        toastError(R.string.please_select_provider_helper)
                    } else {
                        sendRequestOffersToServer()
                    }
                }
            }
            layout_peek -> {
                val bottomSheetBehavior = BottomSheetBehavior.from(bottom_sheet)
                if (bottomSheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
                    iv_expend_collapse.setImageResource(R.drawable.ic_expand_more)
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED)
                } else {
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                    iv_expend_collapse.setImageResource(R.drawable.ic_expand_less)
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
            chk_all -> {
                bottomAdapter?.toggleCheckBox(chk_all.isChecked)
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
                            mapping.offer_detail?.user_detail?.offer_note = mapping.offer_detail?.offer_note
                            mapping.offer_detail?.user_detail?.mapping_initiator = mapping.mapping_initiator
                            mapping.offer_detail?.user_detail?.pay= mapping.offer_detail!!.pay
                            setOfferDetail(mapping)
                        } else if (mapping.request_detail != null) {
                            mapping.request_detail?.user_detail?.parent_uuid = it.first!!.data!!.activity_uuid
                            mapping.request_detail?.user_detail?.activity_type = it.first!!.data!!.activity_type
                            mapping.request_detail?.user_detail?.activity_uuid = mapping.request_detail?.activity_uuid
                            mapping.request_detail?.user_detail?.activity_category = mapping.request_detail?.activity_category
                            mapping.request_detail?.user_detail?.date_time = mapping.request_detail?.date_time
                            mapping.request_detail?.user_detail?.geo_location = mapping.request_detail?.geo_location
                            mapping.request_detail?.user_detail?.offer_note = mapping.request_detail?.request_note
                            mapping.request_detail?.user_detail?.mapping_initiator = mapping.mapping_initiator
                            mapping.request_detail?.user_detail?.pay= mapping.request_detail!!.pay
                            setRequestDetail(mapping)
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

    private fun setRequestDetail(mapping: Mapping) {
        try {
            var detail = ""

            if (mapping.request_detail?.activity_category == CATEGORY_AMBULANCE) {
                mapping.request_detail?.activity_detail?.forEachIndexed { index, it ->
                    mapping.offer_detail?.user_detail?.offer_note = mapping.request_detail?.request_note
                }
            } else if (mapping.request_detail?.activity_category == CATEGORY_PEOPLE) {

                mapping.request_detail?.activity_detail?.forEachIndexed { index, it ->
                    if (!it.volunters_detail.isNullOrEmpty()) {
                        detail += it.volunters_detail?.take(30)
                    }
                    if (!it.volunters_quantity.isNullOrEmpty()) {
                        detail += " (" + it.volunters_quantity + ")"
                    }

                    if (!it.technical_personal_detail.isNullOrEmpty()) {
                        if (detail.isNotEmpty()) {
                            detail += "<br/>"
                        }
                        detail += it.technical_personal_detail?.take(30)
                    }
                    if (!it.technical_personal_quantity.isNullOrEmpty()) {
                        detail += " (" + it.technical_personal_quantity + ")"
                    }
                }

            } else {
                mapping.request_detail?.activity_detail?.forEachIndexed { index, it ->
                    if (!it.detail.isNullOrEmpty()) {
                        detail += it.detail?.take(30)
                    }
                    if (!it.quantity.isNullOrEmpty()) {
                        detail += " (" + it.quantity + ")"
                    }
                    if (mapping.request_detail?.activity_detail!!.size - 1 != index) {
                        detail += "<br/>"
                    }
                }
            }
            mapping.request_detail?.user_detail?.detail = detail
        } catch (e: Exception) {
            Timber.d("")
        }
    }

    private fun setOfferDetail(mapping: Mapping) {
        try {
            var detail = ""

            if (mapping.offer_detail?.activity_category == CATEGORY_AMBULANCE) {
                mapping.offer_detail?.activity_detail?.forEachIndexed { index, it ->
                    mapping.offer_detail?.user_detail?.offer_note = mapping.request_detail?.offer_note
                }
            } else if (mapping.offer_detail?.activity_category == CATEGORY_PEOPLE) {

                mapping.offer_detail?.activity_detail?.forEachIndexed { index, it ->
                    if (!it.volunters_detail.isNullOrEmpty()) {
                        detail += it.volunters_detail?.take(30)
                    }
                    if (!it.volunters_quantity.isNullOrEmpty()) {
                        detail += " (" + it.volunters_quantity + ")"
                    }

                    if (!it.technical_personal_detail.isNullOrEmpty()) {
                        if (detail.isNotEmpty()) {
                            detail += "<br/>"
                        }
                        detail += it.technical_personal_detail?.take(30)
                    }
                    if (!it.technical_personal_quantity.isNullOrEmpty()) {
                        detail += " (" + it.technical_personal_quantity + ")"
                    }
                }

            } else {
                mapping.offer_detail?.activity_detail?.forEachIndexed { index, it ->
                    if (!it.detail.isNullOrEmpty()) {
                        detail += it.detail?.take(30)
                    }
                    if (!it.quantity.isNullOrEmpty()) {
                        detail += " (" + it.quantity + ")"
                    }
                    if (mapping.offer_detail?.activity_detail!!.size - 1 != index) {
                        detail += "<br/>"
                    }
                }
            }
            mapping.offer_detail?.user_detail?.detail = detail
        } catch (e: Exception) {
            Timber.d("")
        }
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
                val intent1 = Intent(DATA_REFRESH)
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent1)

                val returnIntent = Intent()
                setResult(Activity.RESULT_OK, returnIntent)
                finish()
            }
        })
    }

    private fun goToRequestDetailScreen() {
        val intent = Intent(baseContext!!, HomeActivity::class.java)
        intent.putExtra(SELECTED_INDEX, helpType)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        overridePendingTransition(R.anim.enter, R.anim.exit)
        finishWithSlideAnimation()
    }

    override fun getLayout(): Int {
        return R.layout.activity_help_map
    }

}
