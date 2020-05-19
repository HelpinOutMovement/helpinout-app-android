package org.helpinout.billonlights.view.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.*
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.core.content.ContextCompat
import com.avneesh.crashreporter.CrashReporter
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.ResultCallback
import com.google.android.gms.location.*
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import org.helpinout.billonlights.utils.*

abstract class LocationActivity : BaseActivity(), LocationListener, OnPermissionListener, GoogleApiClient.ConnectionCallbacks, ResultCallback<LocationSettingsResult> {
    var mGoogleApiClient: GoogleApiClient? = null
    private val TAG = "MoreActivity"
    private var mLocationSettingsRequest: LocationSettingsRequest? = null
    var mLocationRequest: LocationRequest? = null
    private val REQUEST_CHECK_SETTINGS = 43
    var showSetting: Boolean = false

    @Synchronized
    fun buildGoogleApiClient() {
        mGoogleApiClient = GoogleApiClient.Builder(this).addConnectionCallbacks(this).addApi(LocationServices.API).build()
        mGoogleApiClient!!.connect()
    }

    override fun onStart() {
        super.onStart()
        registerReceiver(locationServicesChangeReceiver, IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION))
    }

    override fun onConnected(p0: Bundle?) {
        mLocationRequest = LocationRequest()
        mLocationRequest?.interval = UPDATE_INTERVAL_IN_MILLISECONDS
        mLocationRequest?.fastestInterval = FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS
        mLocationRequest?.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            buildLocationSettingsRequest()
            checkLocationSettings()
        }
    }

    fun checkLocationPermission() {
        Dexter.withActivity(this).withPermission(Manifest.permission.ACCESS_FINE_LOCATION).withListener(object : PermissionListener {
            override fun onPermissionGranted(response: PermissionGrantedResponse) {
                onPermissionAllow()
            }

            override fun onPermissionDenied(response: PermissionDeniedResponse) {
                if (showSetting) {
                    showSettingsDialog()
                    showSetting = false
                }
                onPermissionCancel()
            }

            override fun onPermissionRationaleShouldBeShown(permission: PermissionRequest, token: PermissionToken) {
                token.continuePermissionRequest()
                onPermissionCancel()
                showSetting = false
            }
        }).check()
    }


    override fun onConnectionSuspended(p0: Int) {

    }

    abstract fun onLocationOnOff(isEnable: Boolean)

    private fun buildLocationSettingsRequest() {
        val builder = LocationSettingsRequest.Builder()
        builder.setAlwaysShow(true)
        mLocationRequest?.let {
            builder.addLocationRequest(it)
        }
        mLocationSettingsRequest = builder.build()
    }

    private fun checkLocationSettings() {
        val result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, mLocationSettingsRequest)
        result.setResultCallback(this)
    }

    override fun onResult(locationSettingsResult: LocationSettingsResult) {
        val status = locationSettingsResult.status
        when (status.statusCode) {
            LocationSettingsStatusCodes.SUCCESS -> {
                startLocationUpdates()
            }
            REQUEST_APP_SETTINGS -> checkLocationPermission()

            LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                Log.i(TAG, "Location settings are not satisfied. Show the user a dialog to" + "upgrade location settings ")
                try {
                    status.startResolutionForResult(this, REQUEST_CHECK_SETTINGS)
                } catch (e: IntentSender.SendIntentException) {
                    Log.i(TAG, "PendingIntent unable to execute request.")
                }

            }
            LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> Log.i(TAG, "Location settings are inadequate, and cannot be fixed here. Dialog " + "not created.")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_APP_SETTINGS -> checkLocationPermission()
            REQUEST_CHECK_SETTINGS -> if (resultCode == Activity.RESULT_OK) {
                startLocationUpdates()
            } else {
                onLocationOnOff(isLocationEnabled())
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }


    private fun startLocationUpdates() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this)
            }
        } else {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this)
        }
    }

    @SuppressLint("SetTextI18n")
    abstract override fun onLocationChanged(location: Location?)


    override fun onDestroy() {
        super.onDestroy()
        try {
            if (mGoogleApiClient != null && mGoogleApiClient!!.isConnected) {
                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this)
            }
        } catch (e: Exception) {
            CrashReporter.logException(e)
        }
        try {
            unregisterReceiver(locationServicesChangeReceiver)
            if (mGoogleApiClient != null && mGoogleApiClient!!.isConnected) {
                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this)
            }
        } catch (e: Exception) {

        }
    }

    fun stopLocationUpdate() {
        try {
            if (mGoogleApiClient != null && mGoogleApiClient!!.isConnected) {
                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this)
            }
        } catch (e: Exception) {
            CrashReporter.logException(e)
        }
    }


    private fun isLocationEnabled(): Boolean {
        val provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED)
        return provider != ""
    }

    private val locationServicesChangeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            onLocationOnOff(isLocationEnabled())
        }
    }

}