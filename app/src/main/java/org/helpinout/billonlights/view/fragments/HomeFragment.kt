package org.helpinout.billonlights.view.fragments

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Location
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.avneesh.crashreporter.CrashReporter
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import kotlinx.android.synthetic.main.fragment_map.*
import kotlinx.android.synthetic.main.layout_ask_for_help.view.*
import kotlinx.android.synthetic.main.layout_map_toolbar.*
import org.helpinout.billonlights.R
import org.helpinout.billonlights.model.BillionLightsApplication
import org.helpinout.billonlights.model.dagger.PreferencesService
import org.helpinout.billonlights.utils.*
import org.helpinout.billonlights.view.activity.AskForHelpActivity
import org.helpinout.billonlights.view.activity.HomeActivity
import org.helpinout.billonlights.view.activity.OfferHelpActivity
import org.helpinout.billonlights.viewmodel.HomeViewModel
import org.jetbrains.anko.startActivity
import javax.inject.Inject


class HomeFragment : LocationFragment(), OnMapReadyCallback, View.OnClickListener {

    private var mapFragment: SupportMapFragment? = null
    private var mMap: GoogleMap? = null
    private var location: Location? = null
    private var toggleAddress = true
    private var toggleRequest = false
    private var toggleOffer = false
    private var requestNearMe: String = ""
    private var offerNearMe: String = ""


    @Inject
    lateinit var preferencesService: PreferencesService


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity!!.application as BillionLightsApplication).getAppComponent().inject(this)
        if (!Places.isInitialized()) {
            Places.initialize(activity!!, getString(R.string.google_maps_key))
        }
        checkPermissionAndAccessLocation()
        mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment?.getMapAsync(this)
        my_self.setOnClickListener(this)
        someone_else.setOnClickListener(this)
        iv_menu.setOnClickListener(this)
        tv_change.setOnClickListener(this)
        tv_current_location.setOnClickListener(this)
        tv_toolbar_address.setOnClickListener(this)
        tv_offer.setOnClickListener(this)
        tv_request.setOnClickListener(this)
    }


    private fun startLocationPicker() {
        val fields: List<Place.Field> = listOf(Place.Field.ID, Place.Field.ADDRESS, Place.Field.NAME, Place.Field.LAT_LNG)
        val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields).build(activity!!)
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        try {
            mMap!!.setOnMarkerClickListener { _ ->
                true
            }
            if (preferencesService.latitude !== 0.0) {
                val latLng = LatLng(preferencesService.latitude, preferencesService.longitude)
                mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, preferencesService.zoomLevel))
                showPinOnCurrentLocation(preferencesService.latitude, preferencesService.longitude)
            }
            if (location != null && mMap != null) {
                updateLocation()
            }
        } catch (e: Exception) {
            CrashReporter.logException(e)
        }
    }

    override fun onLocationChanged(location: Location?) {
        this.location = location
        if (location != null && mMap != null) {
            updateLocation()
            stopLocationUpdate()
        }
    }

    private fun updateLocation() {
        location?.let { loc ->
            mMap?.let {
                mMap!!.clear()
                mMap!!.isMyLocationEnabled = true
                changeMyLocationButton()
                val latlng = LatLng(loc.latitude, loc.longitude)
                mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 12.05F))
                detectRadius()
                try {
                    getRequesterAndHelper()
                }catch (e:Exception){

                }
                stopLocationUpdate()
            }
        }
    }

    private fun showPinOnCurrentLocation(latitude: Double, longitude: Double) {
        tv_toolbar_address?.text = activity!!.getAddress(latitude, longitude)
        current_map_pin?.show()
    }

    private fun detectRadius() {
        mMap?.setOnCameraIdleListener {
            mMap?.let {
                val midLatLng = mMap!!.cameraPosition.target
                preferencesService.latitude = midLatLng.latitude
                preferencesService.longitude = midLatLng.longitude
                showPinOnCurrentLocation(midLatLng!!.latitude, midLatLng.longitude)
            }
        }
    }

    override fun onPermissionAllow() {
        buildGoogleApiClient()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            when (resultCode) {
                RESULT_OK -> {
                    data?.let {
                        val place = Autocomplete.getPlaceFromIntent(data)
                        val latLing = place.latLng
                        latLing?.let {
                            preferencesService.latitude = it.latitude
                            preferencesService.longitude = it.longitude
                            mMap?.clear()
                            mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLing, preferencesService.zoomLevel))
                            tv_toolbar_address.text = activity!!.getAddress(it.latitude, it.longitude) //(place.name + "<br/>" + place.address).fromHtml()
                        }

                    }
                }
            }
        }
    }

    private fun getRequesterAndHelper() {
        progressBar?.show()
        val viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)//activity as HomeActivity).radius
        viewModel.sendUserLocationToServer(6349.542f).observe(this, Observer { it ->
            it.first?.let { res ->
                res.data?.let {
                    progressBar?.hide()
                    requestNearMe = getString(R.string.request_near_me_home, it.my_offers_match)
                    offerNearMe = getString(R.string.offer_near_me_home, it.my_requests_match)

                    tv_offer.visibleIf(it.my_offers_match != 0)
                    tv_request.visibleIf(it.my_requests_match != 0)
                }
            } ?: kotlin.run {
                progressBar?.hide()
                if (!isNetworkAvailable()) {
                    toastError(R.string.toast_error_internet_issue)
                } else toastError(it.second)
            }
        })
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

    private fun toggleAddress() {
        toggleAddress = !toggleAddress
        if (toggleAddress) {
            tv_toolbar_address.ellipsize = TextUtils.TruncateAt.END
            tv_toolbar_address.isSingleLine = true
            tv_toolbar_address.maxLines = 1
        } else {
            tv_toolbar_address.ellipsize = null
            tv_toolbar_address.isSingleLine = false
            tv_toolbar_address.maxLines = Integer.MAX_VALUE
        }
    }


    override fun onClick(v: View) {
        when (v) {
            my_self -> {
                showConfirmationDialog()
            }
            someone_else -> {
                activity?.startActivity<OfferHelpActivity>(HELP_TYPE to HELP_TYPE_OFFER, RADIUS to (activity as HomeActivity).radius)
                activity?.overridePendingTransition(R.anim.enter, R.anim.exit)
            }
            iv_menu -> {
                (activity as HomeActivity).menuClick()
            }
            tv_change -> {
                startLocationPicker()
            }
            tv_toolbar_address, tv_current_location -> {
                toggleAddress()
            }
            tv_offer -> {
                toggleOffer = !toggleOffer
                if (toggleOffer) {
                    tv_offer.text = requestNearMe
                } else {
                    tv_offer.text = "<b>!</b>"
                }
            }
            tv_request -> {
                toggleRequest = !toggleRequest
                if (toggleRequest) {
                    tv_request.text = offerNearMe
                } else {
                    tv_request.text = "<b>!</b>"
                }
            }
        }
    }

    private fun showConfirmationDialog() {
        val alertLayout: View = layoutInflater.inflate(R.layout.layout_ask_for_help, null)
        val alert: AlertDialog.Builder = AlertDialog.Builder(activity!!)
        alert.setView(alertLayout)
        val dialog: AlertDialog = alert.create()

        alertLayout.my_self.setOnClickListener {
            dialog.dismiss()
            activity?.startActivity<AskForHelpActivity>(HELP_TYPE to HELP_TYPE_REQUEST,SELF_ELSE to 1)
            activity?.overridePendingTransition(R.anim.enter, R.anim.exit)
        }
        alertLayout.someone_else.setOnClickListener {
            dialog.dismiss()
            activity?.startActivity<AskForHelpActivity>(HELP_TYPE to HELP_TYPE_REQUEST,SELF_ELSE to 2)
            activity?.overridePendingTransition(R.anim.enter, R.anim.exit)
        }

        dialog.show()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }
}