package com.fivelive.app.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender.SendIntentException
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnScrollChangedListener
import android.widget.*
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fivelive.app.Model.DayModal
import com.fivelive.app.Model.HomeBusiness
import com.fivelive.app.Model.HomeBusinessResponse
import com.fivelive.app.R
import com.fivelive.app.activity.NotificationActivity
import com.fivelive.app.activity.SearchActivity
import com.fivelive.app.adapter.CategoryAdapter
import com.fivelive.app.adapter.DaysAdapter
import com.fivelive.app.adapter.RestaurantListAdapter
import com.fivelive.app.app.FiveLiveApplication
import com.fivelive.app.prefs.SharedPreferenceWriter
import com.fivelive.app.prefs.SharedPrefsKey
import com.fivelive.app.retrofit.ApiClient
import com.fivelive.app.util.AppConstant
import com.fivelive.app.util.AppUtil
import com.fivelive.app.util.CommonAPI
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.util.*

class HomeFragment : Fragment(), OnMapReadyCallback, View.OnClickListener {
    private var mFusedLocationProviderClient: FusedLocationProviderClient? = null
    private var mLastLocation: Location? = null
    private var locationRequest: LocationRequest? = null
    private var mLocationCallback: LocationCallback? = null
    private var mapView: View? = null
    var bottomSheet: View? = null
    var mGoogleMap: GoogleMap? = null
    private val DEFAULT_ZOOM = 10f
    var bottomNavigation: BottomNavigationView? = null
    var bottomSheetBehavior: BottomSheetBehavior<*>? = null
    var daysRecyclerView: RecyclerView? = null
    var categoryRecyclerView: RecyclerView? = null
    var restaurantRecyclerView: RecyclerView? = null
    var restaurantAdapter: RestaurantListAdapter? = null
    var daysList: MutableList<DayModal> = ArrayList()
    var businessList: MutableList<HomeBusiness> = ArrayList()
    var openListImageView: ImageView? = null
    var gpsImageView: ImageView? = null
    var notificationImageView: ImageView? = null
    var searchBar_et: EditText? = null
    var activity: Activity? = null
    var latitude: String? = null
    var longitude: String? = null
    var from: String? = null
    var scrollView: NestedScrollView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundle = arguments
        latitude = bundle!!.getString(AppConstant.LATITUDE)
        longitude = bundle.getString(AppConstant.LONGITUDE)
        from = bundle.getString(AppConstant.FROM)
        Log.d(TAG, "onCreate: ")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        activity = getActivity()
        mFusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)
        mapView = mapFragment.view
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView(view)
        InitializingBottomSheet(view)
        addListenerToScrollViewToHandleBottomSheetBhaviour(view)
        //   setDataOnRestaurantList();
    }

    fun addListenerToScrollViewToHandleBottomSheetBhaviour(view: View) {
        scrollView = view.findViewById(R.id.scrollView)
        scrollView!!.getViewTreeObserver()
            .addOnScrollChangedListener(object : OnScrollChangedListener {
                var y = 0f
                override fun onScrollChanged() {
                    if (scrollView!!.getScrollY() > 0) {
                        bottomSheetBehavior!!.setState(BottomSheetBehavior.STATE_EXPANDED)
                        Log.d("Message", "Scrolls Down:" + scrollView!!.getScrollY())
                    } else {
                        if (scrollView!!.getScrollY() == 0) {
                            bottomSheetBehavior!!.state = BottomSheetBehavior.STATE_HALF_EXPANDED
                        }
                        Log.d("Message", "Scrolls Up:" + scrollView!!.getScrollY())
                    }
                    y = scrollView!!.getScrollY().toFloat()
                }
            })
    }

    fun dispatchToHomeBusinessService() {
        if (AppUtil.isNetworkAvailable(requireContext())) {
            homeBusinessService()
        } else {
            AppUtil.showConnectionError(activity)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mGoogleMap = googleMap
        enabledCurrentLocationButton()
        if (from != null && from == SearchActivity.TAG) {
            /*this call when we come from search activity location */
            moveCameraOnUpdatedLocation(latitude!!.toDouble(), longitude!!.toDouble())
        } else {
            /* this call when we come from initially*/
            checkLocationPermission()
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
        gpsImageView!!.setOnClickListener {
            if (mGoogleMap != null) {
                locationButton?.callOnClick()
            }
        }
    }

    private fun checkLocationPermission() {
        locationRequest = LocationRequest.create()
        locationRequest!!.setInterval(10000)
        locationRequest!!.setFastestInterval(5000)
        locationRequest!!.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest!!)
        val settingsClient = LocationServices.getSettingsClient(requireActivity())
        val task = settingsClient.checkLocationSettings(builder.build())
        task.addOnSuccessListener(object : OnSuccessListener<LocationSettingsResponse?> {
            @SuppressLint("MissingPermission")
            override fun onSuccess(locationSettingsResponse: LocationSettingsResponse?) {
                deviceLocation
                //  mFusedLocationProviderClient.requestLocationUpdates(locationRequest, mLocationCallback, Looper.myLooper());
            }
        }).addOnFailureListener(requireActivity()) { e ->
            if (e is ResolvableApiException) {
                try {
                    e.startResolutionForResult(requireActivity(), REQUEST_CHECK_SETTINGS)
                } catch (sendEx: SendIntentException) {
                    // Ignore the error.
                }
            }
        }
    }

    @get:SuppressLint("MissingPermission")
    private val deviceLocation: Unit
        private get() {
            mFusedLocationProviderClient!!.lastLocation.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    mLastLocation = task.result
                    if (mLastLocation != null) {
                        moveCameraOnUpdatedLocation(
                            mLastLocation!!.latitude,
                            mLastLocation!!.longitude
                        )
                    } else {
                        createNewLocationRequest()
                    }
                } else {
                    Toast.makeText(
                        activity,
                        "Unable to get last Device Location",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            mGoogleMap!!.setOnCameraChangeListener { cameraPosition ->
                searchBar_et!!.setText("Loading...")
                val latLng = cameraPosition.target
                val geocoder = Geocoder(requireActivity(), Locale.getDefault())
                try {
                    val addressList = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
                    if (addressList!!.size > 0) {
                        val address =
                            geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)!![0]
                        val addStr = address.getAddressLine(0)
                        searchBar_et!!.setText(addStr)
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }

    fun moveCameraOnUpdatedLocation(lat: Double, lon: Double) {
        mGoogleMap!!.clear()
        if (mLastLocation != null) {
            latitude = mLastLocation!!.latitude.toString()
            longitude = mLastLocation!!.longitude.toString()
            drawMarker(mLastLocation!!.latitude, mLastLocation!!.longitude)
        }
        dispatchToHomeBusinessService()
    }

    fun drawMarker(lat: Double, lon: Double) {
        val latLng = LatLng(lat, lon)
        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM)
        // mGoogleMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.pin)));
        // mGoogleMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin)));
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
                mFusedLocationProviderClient!!.removeLocationUpdates(mLocationCallback!!)
                moveCameraOnUpdatedLocation(
                    mLastLocation!!.getLatitude(),
                    mLastLocation!!.getLongitude()
                )
            }
        }
        mFusedLocationProviderClient!!.requestLocationUpdates(
            locationRequest!!,
            mLocationCallback!!,
            Looper.myLooper()
        )
    }

    fun initView(view: View) {
        notificationImageView = view.findViewById(R.id.notificationImageView)
        openListImageView = view.findViewById(R.id.openListImageView)
        searchBar_et = view.findViewById(R.id.searchBar_et)
        gpsImageView = view.findViewById(R.id.gpsImageView)
        openListImageView?.setOnClickListener(this)
        notificationImageView?.setOnClickListener(this)
        searchBar_et?.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.openListImageView -> openBottomSheet()
            R.id.notificationImageView -> dispatchToNotificationScreen()
            R.id.searchBar_et -> dispatchToSearchActivity()
        }
    }

    private fun dispatchToSearchActivity() {
        val intent = Intent(activity, SearchActivity::class.java)
        startActivity(intent)
    }

    private fun dispatchToNotificationScreen() {
        startActivity(Intent(getActivity(), NotificationActivity::class.java))
    }

    fun openBottomSheet() {
        if (bottomSheetBehavior!!.state == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior!!.state = BottomSheetBehavior.STATE_COLLAPSED
            openListImageView!!.setImageResource(R.drawable.list)
        } else if (bottomSheetBehavior!!.state == BottomSheetBehavior.STATE_HALF_EXPANDED) {
            bottomSheetBehavior!!.state = BottomSheetBehavior.STATE_COLLAPSED
            openListImageView!!.setImageResource(R.drawable.list)
        } else {
            bottomSheetBehavior!!.state = BottomSheetBehavior.STATE_EXPANDED
            //  bottomSheetBehavior.setState(BottomSheetBehavior.STATE_SETTLING);
            //   bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HALF_EXPANDED);
            openListImageView!!.setImageResource(R.drawable.map_icn)
        }
    }

    fun InitializingBottomSheet(view: View) {
        //#2 Initializing the BottomSheetBehavior
        bottomSheet = view.findViewById(R.id.bottomSheet)
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet!!)
        bottomSheetBehavior?.setState(BottomSheetBehavior.STATE_HALF_EXPANDED)
        bottomSheetBehavior?.addBottomSheetCallback(object : BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    openListImageView!!.setImageResource(R.drawable.list)
                    //  Toast.makeText(getActivity(), "STATE_COLLAPSED", Toast.LENGTH_SHORT).show();
                } else if (newState == BottomSheetBehavior.STATE_SETTLING) {
                    //  Toast.makeText(getActivity(), "STATE_SETTLING", Toast.LENGTH_SHORT).show();
                } else if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                    // Toast.makeText(getActivity(), "STATE_DRAGGING", Toast.LENGTH_SHORT).show();
                } else if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    // Toast.makeText(getActivity(), "STATE_HIDDEN", Toast.LENGTH_SHORT).show();
                } else if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    //Toast.makeText(getActivity(), "STATE_EXPANDED", Toast.LENGTH_SHORT).show();
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                //Toast.makeText(getActivity(), "onSlide", Toast.LENGTH_SHORT).show();
            }
        })
        daysRecyclerView = bottomSheet?.findViewById(R.id.daysRecyclerView)
        categoryRecyclerView = bottomSheet?.findViewById(R.id.categoryRecyclerView)
        restaurantRecyclerView = bottomSheet?.findViewById(R.id.restaurantRecyclerView)
        setDataOnList()
        setDataOnCategoryList()
    }

    fun setDataOnList() {
        val cuisineArray = resources.getStringArray(R.array.days_array)
        val list: List<String> = ArrayList(Arrays.asList(*cuisineArray))
        for (s in list) {
            val modal = DayModal()
            modal.dayName = s
            daysList.add(modal)
        }
        val adapter = DaysAdapter(requireContext(), daysList, this@HomeFragment)
        val llm = LinearLayoutManager(getActivity())
        llm.orientation = LinearLayoutManager.HORIZONTAL
        daysRecyclerView!!.layoutManager = llm
        daysRecyclerView!!.adapter = adapter
    }

    fun setDataOnCategoryList() {
        val size_array = resources.getStringArray(R.array.home_cat_array)
        val catList: List<String> = ArrayList(Arrays.asList(*size_array))
        val adapter = CategoryAdapter(requireContext(), catList)
        val llm = LinearLayoutManager(getActivity())
        llm.orientation = LinearLayoutManager.HORIZONTAL
        categoryRecyclerView!!.layoutManager = llm
        categoryRecyclerView!!.adapter = adapter
    }

    var page = 1
    private fun homeBusinessService() {
        AppUtil.showProgressDialog(getActivity())
        val token =
            SharedPreferenceWriter.getInstance(requireContext()).getString(SharedPrefsKey.TOKEN)
        val apiServiceInterface = ApiClient.instance.client
        val call = apiServiceInterface.homeBusiness(token, "", "", "", latitude, longitude, page)
        call.enqueue(object : Callback<HomeBusinessResponse?> {
            override fun onResponse(
                call: Call<HomeBusinessResponse?>,
                response: Response<HomeBusinessResponse?>
            ) {
                AppUtil.dismissProgressDialog()
                try {
                    if (response.isSuccessful) {
                        val businessResponse = response.body()
                        if (businessResponse!!.status.equals(
                                AppConstant.SUCCESS,
                                ignoreCase = true
                            )
                        ) {
                            //      Toast.makeText(requireActivity(), "SUCCESS RESPONSE", Toast.LENGTH_SHORT).show();
                            businessList.clear()
                            businessList = businessResponse.homeBusiness
                            FiveLiveApplication.claimDetail = businessResponse.claimDetail
                            setDataOnRestaurantList()
                        } else {
                            AppUtil.showErrorDialog(
                                getActivity(),
                                resources.getString(R.string.error),
                                businessResponse.message
                            )
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<HomeBusinessResponse?>, t: Throwable) {
                AppUtil.dismissProgressDialog()
                call.cancel()
                AppUtil.showErrorDialog(getActivity(), "Error!", t.message)
            }
        })
    }

    fun setDataOnRestaurantList() {
        restaurantAdapter = RestaurantListAdapter(requireContext(), businessList, this@HomeFragment)
        val llm = LinearLayoutManager(getActivity())
        llm.orientation = LinearLayoutManager.VERTICAL
        restaurantRecyclerView!!.layoutManager = llm
        restaurantRecyclerView!!.adapter = restaurantAdapter
        setBusinessMarker()
    }

    fun goForAddFavoriteService(businessId: String) {
        if (AppUtil.isNetworkAvailable(requireContext())) {
            val commonApi = CommonAPI(requireContext(), businessId)
            commonApi.addFavoriteService { status -> updateFavoriteImage(businessId, status) }
        } else {
            AppUtil.showConnectionError(activity)
        }
    }

    private fun updateFavoriteImage(businessId: String, status: Int) {
        for (data in businessList) {
            if (businessId == data.id) {
                data.favStatus = status
                restaurantAdapter!!.notifyDataSetChanged()
                break
            }
        }
    }

    fun dispatchToGoogleMap(latitude: String, longitude: String) {
        val uri = String.format(
            Locale.ENGLISH,
            "http://maps.google.com/maps?q=loc:%f,%f",
            latitude.toDouble(),
            longitude.toDouble()
        )
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
        startActivity(intent)
    }

    private fun setBusinessMarker() {
        //    Toast.makeText(activity, "Called marker set", Toast.LENGTH_SHORT).show();
        mGoogleMap!!.clear()
        var mViewMarker: View
        var markerPosition = 1
        for (business in businessList) {
            val latLng = LatLng(business.latitude.toDouble(), business.longitude.toDouble())
            /* MarkerOptions markerOptions = new MarkerOptions()
                    .position(latLng)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.bluess))
                    .title(business.getName());
            mGoogleMap.addMarker(markerOptions);*/mViewMarker =
                (requireActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(
                    R.layout.custom_place_marker,
                    null
                )
            val number = mViewMarker.findViewById<View>(R.id.markerText) as TextView
            number.text = "" + markerPosition
            val markerOptions = MarkerOptions().position(latLng).title("" + markerPosition).icon(
                BitmapDescriptorFactory
                    .fromBitmap(createDrawableFromView(activity, mViewMarker))
            )
            mGoogleMap!!.addMarker(markerOptions)
            val center = CameraUpdateFactory.newLatLng(latLng)
            val zoom = CameraUpdateFactory.zoomTo(DEFAULT_ZOOM)
            //  mGoogleMap.moveCamera(center);
            mGoogleMap!!.animateCamera(zoom)
            markerPosition++
        }
        // drawMarker(Double.parseDouble(latitude),Double.parseDouble(longitude));
    }

    companion object {
        const val TAG = "HomeFragment"
        private const val REQUEST_CHECK_SETTINGS = 3456

        // Display marker on map using game icon
        fun createDrawableFromView(context: Context?, view: View): Bitmap {
            val displayMetrics = DisplayMetrics()
            (context as Activity?)!!.windowManager.defaultDisplay
                .getMetrics(displayMetrics)
            view.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels)
            view.layout(
                0, 0, displayMetrics.widthPixels,
                displayMetrics.heightPixels
            )
            view.buildDrawingCache()
            val bitmap = Bitmap.createBitmap(
                view.measuredWidth,
                view.measuredHeight, Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            view.draw(canvas)
            return bitmap
        }
    }
}