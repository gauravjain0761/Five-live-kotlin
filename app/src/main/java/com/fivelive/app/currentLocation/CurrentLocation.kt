package com.fivelive.app.currentLocation

import android.annotation.SuppressLint
import android.app.Activity
import android.content.IntentSender.SendIntentException
import android.location.Location
import android.os.Looper
import com.fivelive.app.util.AppUtil.showErrorDialog
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.OnSuccessListener

class CurrentLocation internal constructor(val activity: Activity) {
    private val mFusedLocationProviderClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity)
    var mLastLocation: Location? = null
    private var locationRequest: LocationRequest? = null
    private var mLocationCallback: LocationCallback? = null
    var canGetLocation = false
    var latitude // Latitude
            = 0.0
    var longitude // Longitude
            = 0.0

    fun getLatitude_(): Double {
        if (mLastLocation != null) {
            latitude = mLastLocation!!.latitude
        }

        // return latitude
        return latitude
    }

    /**
     * Function to get longitude
     */
    fun getLongitude_(): Double {
        if (mLastLocation != null) {
            longitude = mLastLocation!!.longitude
        }
        // return longitude
        return longitude
    }


    init {
        checkLocationPermission()
    }

    fun checkLocationPermission() {
        locationRequest = LocationRequest.create()
        locationRequest?.interval = 10000
        locationRequest?.fastestInterval = 5000
        locationRequest?.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest!!)
        val settingsClient = LocationServices.getSettingsClient(activity)
        val task = settingsClient.checkLocationSettings(builder.build())
        task.addOnSuccessListener(object : OnSuccessListener<LocationSettingsResponse?> {
            @SuppressLint("MissingPermission")
            override fun onSuccess(locationSettingsResponse: LocationSettingsResponse?) {
                deviceLocation
                //  mFusedLocationProviderClient.requestLocationUpdates(locationRequest, mLocationCallback, Looper.myLooper());
            }
        }).addOnFailureListener(activity) { e ->
            if (e is ResolvableApiException) {
                try {
                    e.startResolutionForResult(activity, REQUEST_CHECK_SETTINGS)
                } catch (sendEx: SendIntentException) {
                    // Ignore the error.
                }
            }
        }
    }

    //Todo some work on last location mLastLocation
    @get:SuppressLint("MissingPermission")
    val deviceLocation: Unit
        get() {
            mFusedLocationProviderClient.lastLocation.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    mLastLocation = task.result
                    if (mLastLocation != null) {
                        //Todo some work on last location mLastLocation
                        canGetLocation = true
                    } else {
                        createNewLocationRequest()
                    }
                } else {
                    showErrorDialog(
                        activity,
                        "Error!",
                        "We are not getting your current location. Try after some time."
                    )
                }
            }
        }

    @SuppressLint("MissingPermission")
    fun createNewLocationRequest() {
        mLocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                if (locationResult == null) {
                    deviceLocation
                } else {
                    mLastLocation = locationResult.lastLocation
                    canGetLocation = true
                    mFusedLocationProviderClient.removeLocationUpdates(mLocationCallback!!)
                    //Todo some work on last location mLastLocation
                }
            }
        }
        mFusedLocationProviderClient.requestLocationUpdates(
            locationRequest!!,
            mLocationCallback!!,
            Looper.myLooper()
        )
    }

    val currentLocation: Location?
        get() {
            if (mLastLocation == null) {
                deviceLocation
            }
            return mLastLocation
        }



    /**
     * Function to check GPS/Wi-Fi enabled
     * @return boolean
     */
    fun canGetLocation(): Boolean {
        return canGetLocation
    }

    fun stopLocationUpdates() {
        // Removing location updates
        mFusedLocationProviderClient.removeLocationUpdates(mLocationCallback!!)
    }

    companion object {
        private const val REQUEST_CHECK_SETTINGS = 3456
        private var mCurrentLocation: CurrentLocation? = null
        fun getInstance(activity: Activity): CurrentLocation? {
            if (null == mCurrentLocation) {
                mCurrentLocation = CurrentLocation(activity)
            }
            return mCurrentLocation
        }
    }
}