package org.helpinout.billonlights.view.activity

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Location
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.appcompat.app.AlertDialog
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
import kotlinx.android.synthetic.main.layout_detail.view.*
import kotlinx.android.synthetic.main.layout_enable_location.*
import kotlinx.android.synthetic.main.layout_map_toolbar.*
import kotlinx.android.synthetic.main.layout_permission.*
import org.helpinout.billonlights.R
import org.helpinout.billonlights.model.database.entity.ActivityAddDetail
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
        bottomAdapter = BottomSheetHelpAdapter(bottomItemList) { item -> onViewDetailClick(item) }
        val itemDecorator = ItemOffsetDecoration(this, R.dimen.item_offset)
        recycler_view.addItemDecoration(itemDecorator)
        recycler_view.adapter = bottomAdapter
    }

    private fun onViewDetailClick(item: ActivityAddDetail) {
        val view: View = layoutInflater.inflate(R.layout.layout_detail, null)

        view.tvName.text = item.user_detail?.profile_name
        view.tv_notes.text = ("<b>" + getString(R.string.note) + "</b> " + item.offer_note).fromHtml()
        if (item.activity_type == HELP_TYPE_REQUEST) {
            if (item.activity_category == CATEGORY_MEDICAL_PAID_WORK) {
                view.tv_free_paid.text = getString(R.string.must_get_paid)
            } else view.tv_free_paid.text = getString(if (item.pay == 1) R.string.can_pay else R.string.can_not_pay)
            view.tv_message.text = (getString(R.string.need_help_with) + "<br/>" + item.user_detail?.detail).fromHtml()
        } else {
            view.tv_message.text = (getString(R.string.can_help_with) + "<br/>" + item.user_detail?.detail).fromHtml()
            if (item.activity_category == CATEGORY_MEDICAL_PAID_WORK) {
                view.tv_free_paid.text = getString(R.string.we_will_pay)
            } else view.tv_free_paid.text = getString(if (item.pay == 1) R.string.not_free else R.string.free)
        }
        val alert: AlertDialog.Builder = AlertDialog.Builder(this)
        alert.setView(view)
        val dialog: AlertDialog = alert.create()

        dialog.show()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
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
                val topLeftCorner: LatLng = visibleRegion.farLeft
                val topRightCorner: LatLng = visibleRegion.nearLeft

                radius = (SphericalUtil.computeDistanceBetween(topLeftCorner, topRightCorner) / 2).toFloat()

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
                    if (!chk_all.isChecked && bottomAdapter!!.getCheckedItemsList().isEmpty()) {
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
        val isSendToAll = if (chk_all.isChecked) 1 else 0 // this is for check all
        viewModel.sendOfferRequesterToServer(radius, suggestionData!!.latitude, suggestionData!!.longitude, isSendToAll, suggestionData!!.activity_type, suggestionData!!.activity_uuid, list).observe(this, Observer {
            dialog?.let {pb->
                if (pb.isShowing) pb.dismiss()
            }
            if (it.first != null) {
                if (it.first!!.data != null) {
                    if (it.first!!.data!!.mapping.isNullOrEmpty()) {
                        toastError(R.string.toast_error_some_error)
                        finish()
                    } else {
                        showNextActivity()
                        toastSuccess(if (helpType == HELP_TYPE_REQUEST) R.string.request_send_success else R.string.offer_send_success)
                    }
                }
            } else {
                if (!isNetworkAvailable()) {
                    toastError(R.string.toast_error_internet_issue)
                } else toastError(it.second)
                CrashReporter.logCustomLogs(it.second)
            }
        })
    }

    private fun showNextActivity() {
        startActivity<RequestDetailActivity>(OFFER_TYPE to helpType, INITIATOR to helpType, HELP_TYPE to helpType, ACTIVITY_UUID to (suggestionData?.activity_uuid ?: ""))
        overridePendingTransition(R.anim.enter, R.anim.exit)
        val intent1 = Intent(DATA_REFRESH)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent1)

        val returnIntent = Intent()
        setResult(Activity.RESULT_OK, returnIntent)
        finish()

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
