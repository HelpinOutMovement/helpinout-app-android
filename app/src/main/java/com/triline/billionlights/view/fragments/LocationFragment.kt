package com.triline.billionlights.view.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
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
import com.triline.billionlights.utils.FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS
import com.triline.billionlights.utils.UPDATE_INTERVAL_IN_MILLISECONDS

abstract class LocationFragment : Fragment(), LocationListener,
    GoogleApiClient.ConnectionCallbacks,
    ResultCallback<LocationSettingsResult> {

    var mGoogleApiClient: GoogleApiClient? = null

    private val TAG = "MoreActivity"
    private var mLocationSettingsRequest: LocationSettingsRequest? = null
    var mLocationRequest: LocationRequest? = null
    private val REQUEST_CHECK_SETTINGS = 43

    @Synchronized
    fun buildGoogleApiClient() {
        mGoogleApiClient = GoogleApiClient.Builder(activity!!)
            .addConnectionCallbacks(this)
            .addApi(LocationServices.API)
            .build()
        mGoogleApiClient!!.connect()
    }

    override fun onConnected(p0: Bundle?) {
        mLocationRequest = LocationRequest()
        mLocationRequest?.interval = UPDATE_INTERVAL_IN_MILLISECONDS
        mLocationRequest?.fastestInterval = FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS
        mLocationRequest?.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        activity?.let {
            if (ContextCompat.checkSelfPermission(
                    activity!!,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                buildLocationSettingsRequest()
                checkLocationSettings()
            }
        }

    }

    fun checkPermissionAndAccessLocation() {
        Dexter.withActivity(activity!!)
            .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse) {
                    onPermissionAllow()
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse) {
                }

                override fun onPermissionRationaleShouldBeShown(
                    permission: PermissionRequest,
                    token: PermissionToken
                ) {
                    token.continuePermissionRequest()
                }
            }).check()
    }

    abstract fun onPermissionAllow()

    override fun onConnectionSuspended(p0: Int) {

    }


    private fun buildLocationSettingsRequest() {
        val builder = LocationSettingsRequest.Builder()
        builder.setAlwaysShow(true)
        mLocationRequest?.let {
            builder.addLocationRequest(it)
        }
        mLocationSettingsRequest = builder.build()
    }

    private fun checkLocationSettings() {
        val result = LocationServices.SettingsApi
            .checkLocationSettings(
                mGoogleApiClient,
                mLocationSettingsRequest
            )
        result.setResultCallback(this)
    }

    override fun onResult(locationSettingsResult: LocationSettingsResult) {
        val status = locationSettingsResult.status
        when (status.statusCode) {
            LocationSettingsStatusCodes.SUCCESS -> {
                startLocationUpdates()
            }
            LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                Log.i(
                    TAG,
                    "Location settings are not satisfied. Show the user a dialog to" + "upgrade location settings "
                )
                try {
                    status.startResolutionForResult(activity!!, REQUEST_CHECK_SETTINGS)
                } catch (e: IntentSender.SendIntentException) {
                    Log.i(TAG, "PendingIntent unable to execute request.")
                }

            }
            LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> Log.i(
                TAG,
                "Location settings are inadequate, and cannot be fixed here. Dialog " + "not created."
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        when (requestCode) {
            REQUEST_CHECK_SETTINGS -> if (resultCode == Activity.RESULT_OK) {
                startLocationUpdates()
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }


    private fun startLocationUpdates() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    activity!!,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
                == PackageManager.PERMISSION_GRANTED
            ) {
                LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient,
                    mLocationRequest,
                    this
                )
            }
        } else {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient,
                mLocationRequest,
                this
            )
        }
    }

    @SuppressLint("SetTextI18n")
    abstract override fun onLocationChanged(location: Location?)


    override fun onDestroy() {
        super.onDestroy()
        try {
            if (mGoogleApiClient != null) {
                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this)
            }
        } catch (e: Exception) {
            CrashReporter.logException(e)
        }
    }

    fun stopLocationUpdate() {
        try {
            if (mGoogleApiClient != null) {
                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this)
            }
        } catch (e: Exception) {
            CrashReporter.logException(e)
        }
    }

}