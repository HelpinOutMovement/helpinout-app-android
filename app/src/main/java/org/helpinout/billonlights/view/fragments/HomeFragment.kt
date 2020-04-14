package org.helpinout.billonlights.view.fragments

import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.common.api.Status
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import kotlinx.android.synthetic.main.fragment_map.*
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
import timber.log.Timber
import javax.inject.Inject


class HomeFragment : LocationFragment(), OnMapReadyCallback, View.OnClickListener {

    private var mapFragment: SupportMapFragment? = null
    private var mMap: GoogleMap? = null
    private var location: Location? = null

    @Inject
    lateinit var preferencesService: PreferencesService

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity!!.application as BillionLightsApplication).getAppComponent().inject(this)
        if (!Places.isInitialized()) {
            Places.initialize(activity!!, getString(R.string.google_api_key))
        }
        checkPermissionAndAccessLocation()
        mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment?.getMapAsync(this)
        ask_for_help.setOnClickListener(this)
        offer_help.setOnClickListener(this)
        iv_menu.setOnClickListener {
            (activity as HomeActivity).menuClick()
        }
        tv_change.setOnClickListener {
            startLocationPicker()
        }
        Handler().postDelayed({
            tvInstruction?.hide()
        }, 5000)
    }


    private fun startLocationPicker() {
        val fields: List<Place.Field> = listOf(Place.Field.ID, Place.Field.ADDRESS, Place.Field.NAME, Place.Field.LAT_LNG)
        val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields).build(activity!!)
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        if (location != null && mMap != null) {
            updateLocation()
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
                            mMap?.clear()
                            mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLing, 12.0f))
                            getRequesterAndhelper()
                        }
                        tv_current_address.text = place.address
                        tv_address.text = place.address
                    }
                }
                AutocompleteActivity.RESULT_ERROR -> {
                    val status: Status = Autocomplete.getStatusFromIntent(data!!)
                    Log.i("TAG", status.statusMessage)
                }
                RESULT_CANCELED -> {
                    // The user canceled the operation.
                }
            }
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
                mMap!!.isMyLocationEnabled = true

                changeMyLocationButton()

                mMap!!.clear()
                val currentLocation = LatLng(loc.latitude, loc.longitude)
                it.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 14.0f))
                mMap?.setOnCameraIdleListener {
                    val midLatLng = mMap!!.cameraPosition.target
                    current_map_pin.show()
                    tv_address?.show()
                    tv_current_address?.text = activity!!.getAddress(midLatLng.latitude, midLatLng.longitude)
                    tv_address?.text = tv_current_address.text
                }
                tv_current_address?.text = activity!!.getAddress(loc.latitude, loc.longitude)
                tv_address?.text = tv_current_address.text
                stopLocationUpdate()
                try {
                    getRequesterAndhelper()
                } catch (e: Exception) {

                }
            }
        }
    }

    private fun getRequesterAndhelper() {
        val viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        viewModel.sendUserLocationToServer().observe(this, Observer { it ->
            it.first?.let { res ->
                res.data?.let {
                    it.offers?.forEach { detail ->
                        try {
                            val loc = detail.geo_location!!.split(",")
                            val lat = loc[0].toDouble()
                            val lon = loc[1].toDouble()
                            val name = detail.app_user_detail?.first_name + " " + detail.app_user_detail?.last_name
                            createMarker(lat, lon, name, name, R.drawable.ic_help_provider)
                        } catch (e: Exception) {

                        }
                    }
                    it.requests?.forEach { detail ->
                        try {
                            val loc = detail.geo_location!!.split(",")
                            val lat = loc[0].toDouble()
                            val lon = loc[1].toDouble()
                            val name = detail.app_user_detail?.first_name + " " + detail.app_user_detail?.last_name
                            createMarker(lat, lon, name, name, R.drawable.ic_help_requester)
                        } catch (e: Exception) {

                        }
                    }
                }
            } ?: kotlin.run {
                Timber.d(it.second)
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

    private fun createMarker(latitude: Double, longitude: Double, title: String?, snippet: String?, iconResID: Int) {
        mMap?.addMarker(MarkerOptions().position(LatLng(latitude, longitude)).anchor(0.5f, 0.5f).title(title).snippet(snippet).icon(BitmapDescriptorFactory.fromResource(iconResID)))
    }


    override fun onClick(v: View) {
        when (v) {
            ask_for_help -> {
                activity?.startActivity<AskForHelpActivity>(HELP_TYPE to HELP_TYPE_REQUEST)
                activity?.overridePendingTransition(R.anim.enter, R.anim.exit)
            }
            offer_help -> {
                activity?.startActivity<OfferHelpActivity>(HELP_TYPE to HELP_TYPE_OFFER)
                activity?.overridePendingTransition(R.anim.enter, R.anim.exit)
            }
        }
    }
}