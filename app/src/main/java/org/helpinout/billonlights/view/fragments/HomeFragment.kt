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
import org.helpinout.billonlights.model.database.entity.PlaceData
import org.helpinout.billonlights.utils.*
import org.helpinout.billonlights.view.activity.AskForHelpActivity
import org.helpinout.billonlights.view.activity.HomeActivity
import org.helpinout.billonlights.view.activity.OfferHelpActivity
import org.helpinout.billonlights.viewmodel.HomeViewModel
import org.jetbrains.anko.startActivity
import org.json.JSONObject
import java.util.*
import javax.inject.Inject


class HomeFragment : LocationFragment(), OnMapReadyCallback, View.OnClickListener {

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
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
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
//                            mMap?.addMarker(MarkerOptions().position(latLing).title(place.address))
                            mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLing, 12.0f))
                            findNearestHelpProviders(it.latitude, it.longitude)
                            findNearestHelpRequester(it.latitude, it.longitude)
                        }
                        tv_current_address.text = place.address
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
                    tv_current_address.text = activity!!.getAddress(midLatLng.latitude, midLatLng.longitude)
                }
                tv_current_address.text = activity!!.getAddress(loc.latitude, loc.longitude)
                stopLocationUpdate()
                findNearestHelpProviders(loc.latitude, loc.longitude)
                findNearestHelpRequester(loc.latitude, loc.longitude)
            }
        }
    }

    private fun findNearestHelpProviders(latitude: Double, longitude: Double) {
        val url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?key=$PLACE_SEARCH_KEY&location=$latitude,$longitude&sensor=true&radius=1000&types=gas_station"

        val nearestPumpViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        nearestPumpViewModel.getNearestDestination(url)!!.observe(this, Observer {
            try {
                val json = JSONObject(it!!)
                finsHelperRequester(json, R.drawable.ic_help_provider)
            } catch (e: Exception) {
                Log.d("", "")
            }
        })
    }

    private fun findNearestHelpRequester(latitude: Double, longitude: Double) {
        val url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?key=$PLACE_SEARCH_KEY&location=$latitude,$longitude&sensor=true&radius=1000&types=atm"

        val viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        viewModel.getNearestDestination(url)!!.observe(this, Observer {
            try {
                val json = JSONObject(it!!)
                finsHelperRequester(json, R.drawable.ic_help_requester)
            } catch (e: Exception) {

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