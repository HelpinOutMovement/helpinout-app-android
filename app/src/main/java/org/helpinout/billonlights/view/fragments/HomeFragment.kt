package org.helpinout.billonlights.view.fragments

import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
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
import javax.inject.Inject


class HomeFragment : LocationFragment(), OnMapReadyCallback, View.OnClickListener {

    private var mapFragment: SupportMapFragment? = null
    private var mMap: GoogleMap? = null
    private var location: Location? = null
    private var radius: Float = 0.0F
    private var zoomLevel = 10.7F

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
        val latLng = LatLng(preferencesService.latitude, preferencesService.longitude)
        mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel))
        showPinOnCurrentLocation(preferencesService.latitude, preferencesService.longitude)
        if (location != null && mMap != null) {
            updateLocation()
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
                mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, zoomLevel))

                detectRadius()
                stopLocationUpdate()
            }
        }
    }

    private fun showPinOnCurrentLocation(latitude: Double, longitude: Double) {
        tv_toolbar_address?.text = activity!!.getAddress(latitude, longitude)
        current_map_pin?.show()
        tv_address?.show()
        tv_address?.text = tv_toolbar_address.text
    }

    private fun detectRadius() {
        mMap?.setOnCameraIdleListener {
            mMap?.let {
                val midLatLng = mMap!!.cameraPosition.target
                preferencesService.latitude = midLatLng.latitude
                preferencesService.longitude = midLatLng.longitude
                showPinOnCurrentLocation(midLatLng!!.latitude, midLatLng.longitude)
                val visibleRegion = it.projection.visibleRegion
                val farRight: LatLng = visibleRegion.farRight
                val farLeft: LatLng = visibleRegion.farLeft
                val nearRight: LatLng = visibleRegion.nearRight
                val nearLeft: LatLng = visibleRegion.nearLeft

                val distanceWidth = FloatArray(2)
                Location.distanceBetween((farRight.latitude + nearRight.latitude) / 2, (farRight.longitude + nearRight.longitude) / 2, (farLeft.latitude + nearLeft.latitude) / 2, (farLeft.longitude + nearLeft.longitude) / 2, distanceWidth)


                val distanceHeight = FloatArray(2)
                Location.distanceBetween((farRight.latitude + nearRight.latitude) / 2, (farRight.longitude + nearRight.longitude) / 2, (farLeft.latitude + nearLeft.latitude) / 2, (farLeft.longitude + nearLeft.longitude) / 2, distanceHeight)


                radius = if (distanceWidth[0] > distanceHeight[0]) {
                    distanceWidth[0]
                } else {
                    distanceHeight[0]
                }
                radius = radius.convertIntoKms().toFloat()
                getRequesterAndHelper()
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
                            mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLing, zoomLevel))
                        }
                        tv_toolbar_address.text = place.address
                        tv_address.text = place.address
                    }
                }
                AutocompleteActivity.RESULT_ERROR -> {
                }
                RESULT_CANCELED -> {
                }
            }
        }
    }

    private fun getRequesterAndHelper() {
        val viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        viewModel.sendUserLocationToServer(radius).observe(this, Observer { it ->
            it.first?.let { res ->
                res.data?.let {


//                    Log.d("======== Total requests: ", it.requests?.size.toString())
//                    Log.d("======== Total offers: ", it.offers?.size.toString())

                    it.offers?.forEach { detail ->
                        try {
                            val loc = detail.geo_location!!.split(",")
                            val lat = loc[0].toDouble()
                            val lon = loc[1].toDouble()
                            val name = detail.user_detail?.first_name + " " + detail.user_detail?.last_name
                            // Log.d("======== Location ", activity!!.getAddress(lat,lon))
                            createMarker(lat, lon, name, name, R.drawable.ic_help_provider)
                        } catch (e: Exception) {

                        }
                    }
                    it.requests?.forEach { detail ->
                        try {
                            val loc = detail.geo_location!!.split(",")
                            val lat = loc[0].toDouble()
                            val lon = loc[1].toDouble()
                            val name = detail.user_detail?.first_name + " " + detail.user_detail?.last_name
                            //Log.d("======== Location ", activity!!.getAddress(lat,lon))
                            createMarker(lat, lon, name, name, R.drawable.ic_help_requester)
                        } catch (e: Exception) {

                        }
                    }
                }
            } ?: kotlin.run {
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

    private fun createMarker(latitude: Double, longitude: Double, title: String?, snippet: String?, iconResID: Int) {
        val marker = MarkerOptions().position(LatLng(latitude, longitude))
        mMap?.addMarker(marker.title(title).icon(BitmapDescriptorFactory.fromResource(iconResID)))
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