package com.fivelive.app.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.content.IntentSender.SendIntentException
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.fivelive.app.Model.AddressList
import com.fivelive.app.R
import com.fivelive.app.prefs.SharedPreferenceWriter
import com.fivelive.app.prefs.SharedPrefsKey
import com.fivelive.app.retrofit.ApiClient
import com.fivelive.app.util.*
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.JsonElement
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.util.*

class AddNewAddressActivity : AppCompatActivity(), OnMapReadyCallback, View.OnClickListener {
    private var mLastLocation: Location? = null
    private var locationRequest: LocationRequest? = null
    private var mLocationCallback: LocationCallback? = null
    private val DEFAULT_ZOOM = 14f
    var isEdit = false
    var addressId: String? = null
    var addressModel: AddressList? = null
    var addressTitle: String? = null
    var address: String? = null
    var backImageView: ImageView? = null
    var addressTitle_et: EditText? = null
    var address_et: EditText? = null
    var addressTitleTIL: TextInputLayout? = null
    var addressTIL: TextInputLayout? = null
    var addAddress_tv: TextView? = null
    var search_et: EditText? = null
    private var mapView: View? = null
    private var mGoogleMap: GoogleMap? = null
    private var mFusedLocationProviderClient: FusedLocationProviderClient? = null
    private var placesClient: PlacesClient? = null
    var autocompleteSessionToken: AutocompleteSessionToken? = null
    private val predictionList: List<AutocompletePrediction>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_new_address)
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        Places.initialize(applicationContext, getString(R.string.google_map_or_places_id))
        placesClient = Places.createClient(this)
        autocompleteSessionToken = AutocompleteSessionToken.newInstance()
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        initView()
        addTextWatcherListener()
        initializeGoogleMap()
        //  InitializingBottomSheet();
        dataFromIntent
    }

    override fun onResume() {
        super.onResume()
        AppUtil.hideKeyboard(this)
    }

    var editFlag = false
    private val dataFromIntent: Unit
        private get() {
            val intent = intent
            isEdit = intent.getBooleanExtra(AppConstant.EDIT, false)
            if (isEdit) {
                editFlag = true
                addressModel = intent.getParcelableExtra(AppConstant.ADDRESS_LIST_MODEL)
                updateUI()
            }
        }

    private fun addTextWatcherListener() {
        address_et!!.addTextChangedListener(TextWatcher(addressTIL!!, address_et!!))
        addressTitle_et!!.addTextChangedListener(TextWatcher(addressTitleTIL!!, addressTitle_et!!))
    }

    private fun updateUI() {
        address_et!!.setText(addressModel!!.address)
        addressTitle_et!!.setText(addressModel!!.title)
    }

    private fun initView() {
        backImageView = findViewById(R.id.backImageView)
        search_et = findViewById(R.id.search_et)
        backImageView?.setOnClickListener(this)
        search_et?.setOnClickListener(View.OnClickListener {
            val fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG)
            // Start the autocomplete intent.
            val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                .build(this@AddNewAddressActivity)
            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
        })
        addAddress_tv = findViewById(R.id.addAddress_tv)
        addressTIL = findViewById(R.id.addressTIL)
        addressTitleTIL = findViewById(R.id.addressTitleTIL)
        address_et = findViewById(R.id.address_et)
        addressTitle_et = findViewById(R.id.addressTitle_et)
        addAddress_tv?.setOnClickListener(this)
    }

    fun initializeGoogleMap() {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)
        mapView = mapFragment.view
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mGoogleMap = googleMap
        enabledCurrentLocationButton()
        locationRequest = LocationRequest.create()
        locationRequest?.setInterval(10000)
        locationRequest?.setFastestInterval(5000)
        locationRequest?.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest!!)
        val settingsClient = LocationServices.getSettingsClient(this@AddNewAddressActivity)
        val task = settingsClient.checkLocationSettings(builder.build())
        task.addOnSuccessListener(object : OnSuccessListener<LocationSettingsResponse?> {
            @SuppressLint("MissingPermission")
            override fun onSuccess(locationSettingsResponse: LocationSettingsResponse?) {
                deviceLocation
                //  mFusedLocationProviderClient.requestLocationUpdates(locationRequest, mLocationCallback, Looper.myLooper());
            }
        }).addOnFailureListener(this) { e ->
            if (e is ResolvableApiException) {
                try {
                    e.startResolutionForResult(this@AddNewAddressActivity, REQUEST_CHECK_SETTINGS)
                } catch (sendEx: SendIntentException) {
                    // Ignore the error.
                }
            }
        }
    }

    var latitude: String? = null
    var longitude: String? = null

    //  Address address = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1).get(0);
    @get:SuppressLint("MissingPermission")
    private val deviceLocation: Unit
        private get() {
            mFusedLocationProviderClient!!.lastLocation.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    mLastLocation = task.result
                    if (mLastLocation != null) {
                        moveCameraOnUpdatedLocation()
                    } else {
                        createNewLocationRequest()
                    }
                } else {
                    Toast.makeText(
                        this@AddNewAddressActivity,
                        "Unable to get last Device Location",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            mGoogleMap!!.setOnCameraIdleListener {
                val latLng = mGoogleMap!!.cameraPosition.target
                Log.d("onCameraIdle", "onCameraIdle: $latLng")
            }
            mGoogleMap!!.setOnCameraChangeListener { cameraPosition ->
                Log.d("onCameraChange", "onCameraChange: ")
                Thread {
                    val latLng = cameraPosition.target
                    latitude = latLng.latitude.toString()
                    longitude = latLng.longitude.toString()
                    val geocoder = Geocoder(this@AddNewAddressActivity, Locale.getDefault())
                    try {
                        val addressList =
                            geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
                        if (addressList!!.size > 0) {
                            //  Address address = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1).get(0);
                            val address = addressList[0]
                            val addStr = address.getAddressLine(0)
                            if (editFlag) {
                                editFlag = false
                            } else {
                                runOnUiThread { address_et!!.setText(addStr) }
                            }
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }.start()
            }
        }

    fun moveCameraOnUpdatedLocation() {
        mGoogleMap!!.clear()
        val latLng = LatLng(mLastLocation!!.latitude, mLastLocation!!.longitude)
        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM)
        //mGoogleMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.blue_pin)));
        mGoogleMap!!.animateCamera(cameraUpdate)
    }

    @SuppressLint("MissingPermission")
    fun createNewLocationRequest() {
        mLocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                if (locationResult == null) {
                    return
                }
                mLastLocation = locationResult.lastLocation
                mLocationCallback?.let { mFusedLocationProviderClient!!.removeLocationUpdates(it) }
                moveCameraOnUpdatedLocation()
            }
        }
        locationRequest?.let {
            mFusedLocationProviderClient!!.requestLocationUpdates(
                it,
                mLocationCallback!!,
                Looper.myLooper()
            )
        }
    }

    @SuppressLint("MissingPermission")
    fun enabledCurrentLocationButton() {
        mGoogleMap!!.isMyLocationEnabled = true
        mGoogleMap!!.uiSettings.setAllGesturesEnabled(true)
        mGoogleMap!!.uiSettings.isMyLocationButtonEnabled = true
        val locationButton =
            (mapView!!.findViewById<View>("1".toInt()).parent as View).findViewById<View>("2".toInt())
        // Change the visibility of my location button
        if (locationButton != null) {
            locationButton.visibility = View.GONE
        }
        findViewById<View>(R.id.gpsImageView).setOnClickListener {
            if (mGoogleMap != null) {
                locationButton?.callOnClick()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            if (resultCode == RESULT_OK) {
                deviceLocation
            } else {
                startActivityForResult(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 0)
            }
        }
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                editFlag = false
                val place = Autocomplete.getPlaceFromIntent(data!!)
                getAddress(place.latLng)
                Log.i(TAG, "Place: " + place.name + ", " + place.id)
                AppUtil.hideKeyboard(this@AddNewAddressActivity)
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                val status = Autocomplete.getStatusFromIntent(
                    data!!
                )
                Log.i(TAG, status.statusMessage!!)
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
            return
        }
    }

    private fun getAddress(latLng: LatLng?) {
        mGoogleMap!!.clear()
        latitude = latLng!!.latitude.toString()
        longitude = latLng.longitude.toString()
        val geocoder = Geocoder(this@AddNewAddressActivity, Locale.getDefault())
        try {
            val addressList = geocoder.getFromLocation(
                latLng.latitude, latLng.longitude, 1
            )
            if (addressList!!.size > 0) {
                val address = addressList[0]
                val addStr = address.getAddressLine(0)
                address_et!!.setText(addStr)
                mGoogleMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM))
                //                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM);
//                mGoogleMap.animateCamera(cameraUpdate);
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun validate(): Boolean {
        addressTitle = addressTitle_et!!.text.toString().trim { it <= ' ' }
        address = address_et!!.text.toString().trim { it <= ' ' }
        if (TextUtils.isEmpty(addressTitle)) {
            addressTitleTIL!!.error = "Enter Address Title."
            return false
        }
        if (TextUtils.isEmpty(address)) {
            addressTIL!!.error = "Enter Address Title."
            return false
        }
        return true
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.backImageView -> finish()
            R.id.addAddress_tv -> dispatchToSavedAddressService()
        }
    }

    private fun dispatchToSavedAddressService() {
        AppUtil.hideKeyboard(this)
        if (AppUtil.isNetworkAvailable(this@AddNewAddressActivity)) {
            if (validate()) {
                saveOrEditAddressService()
            }
        } else {
            AppUtil.showConnectionError(this)
        }
    }

    private fun saveOrEditAddressService() {
        AppUtil.showProgressDialog(this@AddNewAddressActivity)
        val token = SharedPreferenceWriter.getInstance(this@AddNewAddressActivity)
            ?.getString(SharedPrefsKey.TOKEN)
        // String token = "w7ATbuCZiaN3vR5jzE0iKlzjkxcX5Z";
        val apiServiceInterface = ApiClient.instance.client
        val call: Call<JsonElement>
        call = if (isEdit) {
            apiServiceInterface.updateAddress(
                token,
                addressTitle,
                address,
                addressModel!!.id,
                latitude,
                longitude
            )
        } else {
            apiServiceInterface.saveAddress(token, addressTitle, address, latitude, longitude)
        }

        // Call<JsonElement> call = apiServiceInterface.saveAddress(token, addressTitle, address);
        call.enqueue(object : Callback<JsonElement> {
            override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                AppUtil.dismissProgressDialog()
                try {
                    if (response.isSuccessful) {
                        val jsonObject =
                            JSONObject(response.body()!!.asJsonObject.toString().trim { it <= ' ' })
                        if (jsonObject.getString("status")
                                .equals(AppConstant.SUCCESS, ignoreCase = true)
                        ) {
                            finish()
                        } else {
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                AppUtil.dismissProgressDialog()
                call.cancel()
            }
        })
    }

    var bottomSheet: View? = null
    var bottomSheetBehavior: BottomSheetBehavior<*>? = null
    fun InitializingBottomSheet() {
        bottomSheet = findViewById(R.id.addressbottomSheet)
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet!!)
        bottomSheetBehavior?.setState(BottomSheetBehavior.STATE_EXPANDED)
        bottomSheetBehavior?.setHideable(false)
        bottomSheetBehavior?.addBottomSheetCallback(object : BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {}
            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })
        getBottomSheetId(bottomSheet)
    }

    private fun getBottomSheetId(bottomSheet: View?) {
        addAddress_tv = bottomSheet!!.findViewById(R.id.addAddress_tv)
        addressTIL = bottomSheet.findViewById(R.id.addressTIL)
        addressTitleTIL = bottomSheet.findViewById(R.id.addressTitleTIL)
        address_et = bottomSheet.findViewById(R.id.address_et)
        addressTitle_et = bottomSheet.findViewById(R.id.addressTitle_et)
        addAddress_tv?.setOnClickListener(this)
    }

    companion object {
        private const val TAG = "AddNewAddressActivity"
        private const val REQUEST_CHECK_SETTINGS = 3456
        private const val REQUEST_PERMISSIONS_REQUEST_CODE = 34
        private const val AUTOCOMPLETE_REQUEST_CODE = 1
    }
}