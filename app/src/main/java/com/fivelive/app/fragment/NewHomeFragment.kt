package com.fivelive.app.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.IntentSender.SendIntentException
import android.graphics.Rect
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnScrollChangedListener
import android.widget.*
import android.widget.ProgressBar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fivelive.app.Model.*
import com.fivelive.app.R
import com.fivelive.app.activity.*
import com.fivelive.app.adapter.BusinessListOrAdsAdapter
import com.fivelive.app.adapter.CategoryAdapter
import com.fivelive.app.adapter.DaysAdapter
import com.fivelive.app.ads.FullScreenAds
import com.fivelive.app.ads.GoogleAdsHelper
import com.fivelive.app.app.FiveLiveApplication
import com.fivelive.app.customView.PreCachingLayoutManager
import com.fivelive.app.dialog.CustomErrorDialog
import com.fivelive.app.infoWindow.CustomInfoWindowAdapter
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
import com.google.android.gms.maps.GoogleMap.OnCameraMoveStartedListener
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import com.google.android.material.chip.ChipGroup
import com.google.maps.android.heatmaps.HeatmapTileProvider
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.util.*

class NewHomeFragment : Fragment(), OnMapReadyCallback, View.OnClickListener,
    CompoundButton.OnCheckedChangeListener {
    private val DEFAULT_ZOOM = 17.0f
    private var mFusedLocationProviderClient: FusedLocationProviderClient? = null
    private var mLastLocation: Location? = null
    private var locationRequest: LocationRequest? = null
    private var mLocationCallback: LocationCallback? = null
    private var mapView: View? = null
    var bottomSheet: ConstraintLayout? = null
    var mGoogleMap: GoogleMap? = null
    var overlay: TileOverlay? = null

    // Boolean isCameraUpdating=false;
    var bottomNavigation: BottomNavigationView? = null
    var bottomSheetBehavior: BottomSheetBehavior<*>? = null
    var bottomSheetCallback: BottomSheetCallback? = null
    var bottomSheetMaxHeight = 0
    var markerList: MutableList<MarkerOptions>? = null
    var daysRecyclerView: RecyclerView? = null
    var categoryRecyclerView: RecyclerView? = null
    var restaurantRecyclerView: RecyclerView? = null
    var restaurantAdapter: BusinessListOrAdsAdapter? = null
    var daysList: MutableList<DayModal> = ArrayList()
    var businessList: MutableList<HomeBusiness> = ArrayList()
    var mMarkerHashMap: MutableMap<String, String> = HashMap()
    var mRecyclerViewItemsList: MutableList<Any> = ArrayList()
    var staticFilterLists: MutableList<FilterList> = ArrayList()
    var daysAdapter: DaysAdapter? = null
    var openListImageView: ImageView? = null
    var gpsImageView: ImageView? = null
    var notificationImageView: ImageView? = null
    var searchBar_et: EditText? = null
    var not_found_tv: TextView? = null
    var homeActivity: HomeActivity? = null
    var latitude: String? = null
    var longitude: String? = null
    var searchAddress: String? = null
    var from: String? = null
    var dayChipGroup: ChipGroup? = null
    var qfChipGroup: ChipGroup? = null
    var staticChipGroup: ChipGroup? = null
    var chipSelectedDay = ""
    var scrollView: NestedScrollView? = null
    var paging_pgbar: ProgressBar? = null
    var heatMapSwitch_btn: Switch? = null
    var selectedDay = ""
    var quickFilter = ""
    var staticFilter = ""
    var token: String? = null
    var isLoading = false
    var page = 1
    var fromMapSearch = false
    var progressBar: ProgressBar? = null
    var filterResponse: FilterResponse? = null
    var layoutManager: LinearLayoutManager? = null
    var banner_imgView: ImageView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        token = SharedPreferenceWriter.getInstance(requireContext()).getString(SharedPrefsKey.TOKEN)
        val bundle = arguments
        latitude = bundle!!.getString(AppConstant.LATITUDE)
        longitude = bundle.getString(AppConstant.LONGITUDE)
        searchAddress = bundle.getString(AppConstant.SEARCH_ADDRESS)
        from = bundle.getString(AppConstant.FROM)

        /* latitude = "30.2619504";
        longitude = "-97.73400439999999";
        from = "NewHomeFragment";*/Log.d(TAG, "onCreate: ")

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, null)
        homeActivity = getActivity() as HomeActivity?
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
        dispatchToGetQuickFilterList() // here we are getting filter list form API
        dispatchToCheckTrailPeriodService() // to check trial period
        InitializingBottomSheet(view)
        //  scrollingEnableOrDisable();
        // addListenerToScrollViewToHandleBottomSheetBhaviour(view);
        initRecyclerViewScrollListener()
    }

    override fun onResume() {
        super.onResume()
        //  updateBannerVisibility();
    }

    fun dispatchToCheckTrailPeriodService() {
        if (AppUtil.isNetworkAvailable(requireContext())) {
            checkTrailPeriodService()
        } else {
            AppUtil.showConnectionError(requireContext())
        }
    }

    fun dispatchToHomeBusinessService() {
        if (AppUtil.isNetworkAvailable(requireContext())) {
            homeBusinessService()
        } else {
            AppUtil.showConnectionError(requireContext())
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mGoogleMap = googleMap
        enabledCurrentLocationButton()
        if (from != null && from == TAG) {
            if (latitude != "" && longitude != "") {
                /*this call when we come from search activity location */
                moveCameraOnUpdatedLocation(latitude!!.toDouble(), longitude!!.toDouble())
            } else {
                // this call when we tap on Home Button in bottom bar
                checkLocationPermission()
            }
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

        /* mGoogleMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                searchBar_et.setText("Loading...");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        LatLng latLng = cameraPosition.target;
                        Log.d("CameraPosition", "onCameraChange: " + latLng);
                        Geocoder geocoder = new Geocoder(activity, Locale.getDefault());
                        try {
                            List<Address> addressList = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                            if (addressList.size() > 0) {
                                Address address = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1).get(0);
                                String addStr = address.getAddressLine(0);
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        searchBar_et.setText(addStr);
                                    }
                                });
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });*/mGoogleMap!!.setOnCameraMoveStartedListener(object : OnCameraMoveStartedListener {
            override fun onCameraMoveStarted(i: Int) {
                Log.d("CameraPosition", "onCameraMoveStarted: $i")
                if (i == 1) {
                    businessMoveOnMap
                }
            }
        })
    }

    // searchBar_et.setText("Search for Business");
    //setAddressOnSearchBarWhenMapMoveOn(latLng);
    // Toast.makeText(activity, "Called", Toast.LENGTH_SHORT).show();
    val businessMoveOnMap: Unit
        get() {
            if (mGoogleMap == null) {
                return
            }
            mGoogleMap!!.setOnCameraIdleListener {
                val latLng = mGoogleMap!!.cameraPosition.target
                // searchBar_et.setText("Search for Business");
                //setAddressOnSearchBarWhenMapMoveOn(latLng);
                latitude = latLng.latitude.toString()
                longitude = latLng.longitude.toString()
                page = 1
                fromMapSearch = true
                // Toast.makeText(activity, "Called", Toast.LENGTH_SHORT).show();
                dispatchToHomeBusinessService()
                Log.d("CameraPosition", "onCameraIdle: $latLng")
            }
        }

    fun setAddressOnSearchBarWhenMapMoveOn(latLng: LatLng) {
        searchBar_et!!.setText("Loading...")
        Thread { //  LatLng latLng = mGoogleMap.getCameraPosition().target;
            Log.d("CameraPosition", "onCameraChange: $latLng")
            val geocoder = Geocoder(homeActivity!!, Locale.getDefault())
            try {
                val addressList = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
                if (addressList!!.size > 0) {
                    val address =
                        geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)!![0]
                    val addStr = address.getAddressLine(0)
                    homeActivity!!.runOnUiThread { searchBar_et!!.setText(addStr) }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }.start()
    }

    private fun checkLocationPermission() {
        locationRequest = LocationRequest.create()
        locationRequest!!.setInterval(10000)
        locationRequest!!.setFastestInterval(5000)
        locationRequest!!.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest!!)
        val settingsClient = LocationServices.getSettingsClient(homeActivity!!)
        val task = settingsClient.checkLocationSettings(builder.build())
        task.addOnSuccessListener(object : OnSuccessListener<LocationSettingsResponse?> {
            @SuppressLint("MissingPermission")
            override fun onSuccess(locationSettingsResponse: LocationSettingsResponse?) {
                deviceLocation
                //  mFusedLocationProviderClient.requestLocationUpdates(locationRequest, mLocationCallback, Looper.myLooper());
            }
        }).addOnFailureListener(homeActivity!!) { e ->
            if (e is ResolvableApiException) {
                try {
                    e.startResolutionForResult(requireActivity(), REQUEST_CHECK_SETTINGS)
                } catch (sendEx: SendIntentException) {
                    // Ignore the error.
                }
            }
        }
    }

    //Save lat long value in prefs
    @get:SuppressLint("MissingPermission")
    val deviceLocation: Unit
        get() {
            mFusedLocationProviderClient!!.lastLocation.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    mLastLocation = task.result
                    if (mLastLocation != null) {
                        setLatLongInPrefs() //Save lat long value in prefs
                        setLatLongForHappeningNow(mLastLocation)
                        moveCameraOnUpdatedLocation(
                            mLastLocation!!.latitude,
                            mLastLocation!!.longitude
                        )
                    } else {
                        createNewLocationRequest()
                    }
                } else {
                    Toast.makeText(
                        homeActivity,
                        "Unable to get last Device Location",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

    // Save lat long value in prefs
    private fun setLatLongInPrefs() {
        SharedPreferenceWriter.getInstance(requireContext())
            .writeStringValue(SharedPrefsKey.LATITUDE, mLastLocation!!.latitude.toString())
        SharedPreferenceWriter.getInstance(requireContext())
            .writeStringValue(SharedPrefsKey.LONGITUDE, mLastLocation!!.longitude.toString())
    }

    fun setLatLongForHappeningNow(mLastLocation: Location?) {
        homeActivity?.latitude = mLastLocation!!.latitude.toString()
        homeActivity?.longitude = mLastLocation.longitude.toString()
    }

    fun moveCameraOnUpdatedLocation(lat: Double, lon: Double) {
        mGoogleMap!!.clear()
        if (mLastLocation != null) {
            latitude = mLastLocation!!.latitude.toString()
            longitude = mLastLocation!!.longitude.toString()
            val latLng = LatLng(lat, lon)
            zoomMap(latLng)
            // CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM);
            // mGoogleMap.moveCamera(cameraUpdate);
            //  mGoogleMap.animateCamera(cameraUpdate);
            //  drawMarker(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        } else {
            //  searchBar_et.setText(searchAddress);
            val latLng = LatLng(lat, lon)
            zoomMap(latLng)
            //  CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM);
            // mGoogleMap.moveCamera(cameraUpdate);
            //  mGoogleMap.animateCamera(cameraUpdate);
        }
        dispatchToHomeBusinessService()
    }

    fun zoomMap(latLng: LatLng) {
        val cameraPosition = CameraPosition.Builder().target(latLng) //.zoom(DEFAULT_ZOOM)
            .build()
        val cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition)
        mGoogleMap!!.moveCamera(cameraUpdate)
        mGoogleMap!!.animateCamera(CameraUpdateFactory.zoomTo(DEFAULT_ZOOM), 2000, null)
        // mGoogleMap.animateCamera(cameraUpdate);
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
                setLatLongInPrefs() //Save lat long value in prefs
                setLatLongForHappeningNow(mLastLocation)
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
        progressBar = view.findViewById(R.id.progressBar)
        paging_pgbar = view.findViewById(R.id.paging_pgbar)
        notificationImageView = view.findViewById(R.id.notificationImageView)
        openListImageView = view.findViewById(R.id.openListImageView)
        searchBar_et = view.findViewById(R.id.searchBar_et)
        gpsImageView = view.findViewById(R.id.gpsImageView)
        qfChipGroup = view.findViewById(R.id.qfChipGroup)
        staticChipGroup = view.findViewById(R.id.staticChipGroup)
        scrollView = view.findViewById(R.id.scrollView)
        not_found_tv = view.findViewById(R.id.not_found_tv)
        heatMapSwitch_btn = view.findViewById(R.id.heatMapSwitch_btn)
        openListImageView?.setOnClickListener(this)
        notificationImageView?.setOnClickListener(this)
        searchBar_et?.setOnClickListener(this)

        //   heatMapSwitch_btn.setOnCheckedChangeListener(this);
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.openListImageView -> openBottomSheet()
            R.id.notificationImageView -> dispatchToNotificationScreen()
            R.id.searchBar_et -> dispatchToSearchActivity()
            R.id.banner_imgView -> dispatchToSubscriptionPlanActivity()
        }
    }

    private fun dispatchToSubscriptionPlanActivity() {
        if (AppUtil.checkUserType(requireContext())) {
            AppUtil.showGuestLoginDialog(requireContext())
            return
        }
        val intent = Intent(homeActivity, SubscriptionActivity::class.java)
        startActivity(intent)
    }

    private fun dispatchToSearchActivity() {
        val intent = Intent(homeActivity, SearchActivity::class.java)
        intent.putExtra(AppConstant.FROM, TAG)
        startActivity(intent)
    }

    private fun dispatchToNotificationScreen() {
        startActivity(Intent(getActivity(), NotificationActivity::class.java))
    }

    fun removeBottomSheetListener() {
        bottomSheetBehavior!!.removeBottomSheetCallback(bottomSheetCallback!!)
    }

    fun attachBottomSheetListener() {
        bottomSheetBehavior!!.addBottomSheetCallback(object : BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    openListImageView!!.setImageResource(R.drawable.list)
                    //  Toast.makeText(getActivity(), "STATE_COLLAPSED", Toast.LENGTH_SHORT).show();
                } else if (newState == BottomSheetBehavior.STATE_SETTLING) {
                    //   Toast.makeText(getActivity(), "STATE_SETTLING", Toast.LENGTH_SHORT).show();
                } else if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                    //  Toast.makeText(getActivity(), "STATE_DRAGGING", Toast.LENGTH_SHORT).show();
                } else if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    //  Toast.makeText(getActivity(), "STATE_HIDDEN", Toast.LENGTH_SHORT).show();
                } else if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    openListImageView!!.setImageResource(R.drawable.map_icn)
                    //Toast.makeText(getActivity(), "STATE_EXPANDED", Toast.LENGTH_SHORT).show();
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                val upperState = 0.66f
                val lowerState = 0.33f
                if (bottomSheetBehavior!!.state == BottomSheetBehavior.STATE_SETTLING) {
                    if (slideOffset >= upperState) {
                        bottomSheetBehavior!!.state = BottomSheetBehavior.STATE_EXPANDED
                    }
                    if (slideOffset > lowerState && slideOffset < upperState) {
                        bottomSheetBehavior!!.state = BottomSheetBehavior.STATE_HALF_EXPANDED
                    }
                    if (slideOffset <= lowerState) {
                        bottomSheetBehavior!!.state = BottomSheetBehavior.STATE_COLLAPSED
                    }
                }
            }
        }.also { bottomSheetCallback = it })
    }

    fun openBottomSheet() {
        removeBottomSheetListener()
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
        Handler().postDelayed({ attachBottomSheetListener() }, 200)
    }

    var isScrollable = true
    fun scrollingEnableOrDisable() {
        scrollView!!.setOnTouchListener { v, event -> isScrollable }
    }

    fun addListenerToScrollViewToHandleBottomSheetBhaviour(view: View?) {
        scrollView!!.viewTreeObserver.addOnScrollChangedListener(object : OnScrollChangedListener {
            var y = 0f
            override fun onScrollChanged() {
                if (scrollView!!.scrollY > 0) {
                    bottomSheetBehavior!!.state = BottomSheetBehavior.STATE_EXPANDED
                    Log.d("Message", "Scrolls Down:" + scrollView!!.scrollY)
                } else {
                    if (scrollView!!.scrollY == 0) {
                        // bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HALF_EXPANDED);
                    }
                    Log.d("Message", "Scrolls Up:" + scrollView!!.scrollY)
                }
                y = scrollView!!.scrollY.toFloat()
            }
        })
    }

    @SuppressLint("WrongConstant")
    fun InitializingBottomSheet(view: View) {
        //#2 Initializing the BottomSheetBehavior
        bottomSheet = view.findViewById(R.id.bottomSheet)
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet!!)
        bottomSheetBehavior?.setState(BottomSheetBehavior.STATE_HALF_EXPANDED)
        // bottomSheetBehavior.setState(PEEK_HEIGHT_AUTO);
        attachBottomSheetListener()

        //   daysRecyclerView = bottomSheet.findViewById(R.id.daysRecyclerView);
        //  categoryRecyclerView = bottomSheet.findViewById(R.id.categoryRecyclerView);
        banner_imgView = bottomSheet?.findViewById(R.id.banner_imgView)
        banner_imgView?.setOnClickListener(this)
        restaurantRecyclerView = bottomSheet?.findViewById(R.id.restaurantRecyclerView)
        dayChipGroup = bottomSheet?.findViewById(R.id.dayChipGroup)
        attachListenerOnDaysChipGroup()
        // setDataOnList();
        //  setDataOnCategoryList();
    }

    fun removeListenerOnDaysChipGroup() {
        dayChipGroup!!.setOnCheckedChangeListener(null)
    }// Here we need to scroll starting position of recyclerview

    //        Log.d("getFilteredListData", "staticFilter: " + staticFilter);
//        Log.d("getFilteredListData", "quickFilter: " + quickFilter);
//        Log.d("getFilteredListData", "Selected day: " + selectedDay);
    val filteredListData: Unit
        get() {
            FullScreenAds.instance.initializeInterstitialAd(requireActivity())
            //        Log.d("getFilteredListData", "staticFilter: " + staticFilter);
//        Log.d("getFilteredListData", "quickFilter: " + quickFilter);
//        Log.d("getFilteredListData", "Selected day: " + selectedDay);
            page = 1
            paging_pgbar!!.visibility = View.VISIBLE
            hideList()
            if (restaurantAdapter != null) {
                // Here we need to scroll starting position of recyclerview
                restaurantAdapter!!.updateList()
            }
            dispatchToHomeBusinessService()
        }

    fun setDataOnList() {
        val cuisineArray = resources.getStringArray(R.array.days_array)
        val list: List<String> = ArrayList(Arrays.asList(*cuisineArray))
        for (s in list) {
            val modal = DayModal()
            modal.dayName = s
            daysList.add(modal)
        }
        daysAdapter = DaysAdapter(requireContext(), daysList, this@NewHomeFragment)
        val llm = LinearLayoutManager(getActivity())
        llm.orientation = LinearLayoutManager.HORIZONTAL
        daysRecyclerView!!.layoutManager = llm
        daysRecyclerView!!.adapter = daysAdapter
    }

    fun filterListDaysVise(position: Int) {
        val dayModal = daysList[position]
        if (dayModal.isSelected) {
            //Toast.makeText(activity, "day API CALL", Toast.LENGTH_SHORT).show();
            dispatchToHomeBusinessService()
        } else {
            //Toast.makeText(activity, "NOt day API CALL", Toast.LENGTH_SHORT).show();
            dispatchToHomeBusinessService()
        }
        daysAdapter!!.notifyDataSetChanged()
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

    fun showProgressDialog() {
        if (page == 1) {
            if (fromMapSearch) { // this condition run when we move on map to show progress bar bottom of search bar
                fromMapSearch = false
                progressBar!!.visibility = View.VISIBLE
            } else { // this condition run when we click on filters or initially enter the app
                AppUtil.showProgressDialog(getActivity())
            }
        }
    }

    var previousSize = 0
    var businessListMapMarker: List<HomeBusiness> = ArrayList()
    private fun homeBusinessService() {
        showProgressDialog()
        val apiServiceInterface = ApiClient.instance.client
        val call = apiServiceInterface.homeBusiness(
            token,
            selectedDay,
            staticFilter,
            quickFilter,
            latitude,
            longitude,
            page
        )
        call.enqueue(object : Callback<HomeBusinessResponse?> {
            override fun onResponse(
                call: Call<HomeBusinessResponse?>,
                response: Response<HomeBusinessResponse?>
            ) {
                AppUtil.dismissProgressDialog()
                progressBar!!.visibility = View.GONE
                try {
                    if (response.isSuccessful) {
                        val businessResponse = response.body()
                        if (businessResponse!!.status.equals(
                                AppConstant.SUCCESS,
                                ignoreCase = true
                            )
                        ) {
                            //Toast.makeText(requireActivity(), "SUCCESS RESPONSE", Toast.LENGTH_SHORT).show();
                            if (page == 1) {
                                businessList.clear()
                                mRecyclerViewItemsList.clear()
                            }
                            saveSubscriptionInfoInPrefs(businessResponse.subscription!!) //save subscrption info in prefs
//                            val sortedList = businessResponse.homeBusiness.sortedBy { hb ->
//                                val myLocation = Location("").apply {
//                                    latitude =
//                                        this@NewHomeFragment.latitude?.toDoubleOrNull() ?: 0.0
//                                    longitude =
//                                        this@NewHomeFragment.longitude?.toDoubleOrNull() ?: 0.0
//
//                                }
//                                val businessLocation = Location("").apply {
//                                    latitude = hb.latitude.toDoubleOrNull() ?: 0.0
//                                    longitude = hb.longitude.toDoubleOrNull() ?: 0.0
//                                }
//                                val distanceInMeters = myLocation.distanceTo(businessLocation)
//                                distanceInMeters
//                            }
                            var objectList: List<Any> = ArrayList()
                            var businessCopyList: List<HomeBusiness>? = ArrayList()
                            businessCopyList = businessResponse.homeBusiness
                            objectList = businessResponse.homeBusiness as Any as List<Any>
                            previousSize = mRecyclerViewItemsList.size
                            businessList.addAll(businessCopyList)
                            mRecyclerViewItemsList.addAll(objectList)

                            //  businessList = businessResponse.getHomeBusiness();
                            //   mRecyclerViewItemsList = (List<Object>) (Object) businessResponse.getHomeBusiness();
                            FiveLiveApplication.claimDetail = businessResponse.claimDetail
                            if (businessList.size > 0) {
                                showList()
                                setDataOnRestaurantList()
                            } else {
                                mGoogleMap!!.clear()
                                hideList()
                            }
                            isLoading = false
                            //  loadGoogleAds();
                        } else {
                            AppUtil.dismissProgressDialog()
                            if (businessResponse.message.contains(AppConstant.SESSION_EXPIRED)) {
                                AppUtil.showLogoutDialog(
                                    requireContext(),
                                    homeActivity!!.resources.getString(R.string.error),
                                    businessResponse.message
                                )
                            } else {
                                AppUtil.showErrorDialog(
                                    homeActivity,
                                    homeActivity!!.resources.getString(R.string.error),
                                    businessResponse.message
                                )
                            }
                        }
                    }
                } catch (e: Exception) {
                    progressBar!!.visibility = View.GONE
                    AppUtil.dismissProgressDialog()
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<HomeBusinessResponse?>, t: Throwable) {
                AppUtil.dismissProgressDialog()
                progressBar!!.visibility = View.GONE
                call.cancel()
                AppUtil.showErrorDialog(getActivity(), "Error!", t.message.orEmpty())
            }
        })
    }

    fun saveSubscriptionInfoInPrefs(subscription: Subscription) {
        SharedPreferenceWriter.getInstance(requireContext()).saveSubscriptionDetails(subscription)
        updateBannerVisibility()
    }

    fun updateBannerVisibility() {
        /* 0 means unsubscibed
        1 means subscribed*/
        val planStatus = SharedPreferenceWriter.getInstance(requireContext())
            .getIntValue(SharedPrefsKey.SUBSCRIBED_STATUS)
        if (planStatus == 1) {
            banner_imgView!!.visibility = View.GONE
        } else {
            banner_imgView!!.visibility = View.VISIBLE
        }
    }

    fun setDataOnRestaurantList() {
        setBusinessMarker()
        if (page != 0) {
            paging_pgbar!!.visibility = View.GONE
            // restaurantAdapter.diffUtilChangeData(mRecyclerViewItemsList);
            restaurantAdapter!!.notifyItemRangeChanged(previousSize, mRecyclerViewItemsList.size)
            //  restaurantAdapter.notifyDataSetChanged();
            return
        } else {
            if (mRecyclerViewItemsList.size >= 10) paging_pgbar!!.visibility =
                View.VISIBLE else paging_pgbar!!.visibility = View.GONE
        }
        Log.d("MyTag", "setDataOnRestaurantList: before adapter load")
        restaurantRecyclerView!!.recycledViewPool
        restaurantAdapter = BusinessListOrAdsAdapter(
            requireContext(),
            mRecyclerViewItemsList,
            this@NewHomeFragment,
            staticFilter,
            restaurantRecyclerView!!.recycledViewPool
        )
        layoutManager = PreCachingLayoutManager(requireActivity())
        layoutManager?.setOrientation(LinearLayoutManager.VERTICAL)
        restaurantRecyclerView!!.layoutManager = layoutManager
        restaurantRecyclerView!!.adapter = restaurantAdapter
        Log.d("MyTag", "setDataOnRestaurantList: After adapter load")
        deviceHeight
    }

    fun loadGoogleAds() {
        val adsHelper =
            restaurantAdapter?.let { GoogleAdsHelper(requireContext(), it, mRecyclerViewItemsList) }
        adsHelper?.loadNativeAds()
    }

    fun goForAddFavoriteService(businessId: String, buzPosition: Int, business: HomeBusiness?) {
        if (AppUtil.isNetworkAvailable(requireContext())) {
            val commonApi = CommonAPI(requireContext(), businessId)
            commonApi.addFavoriteService { status ->
                updateFavoriteImage(
                    businessId,
                    status,
                    buzPosition,
                    business
                )
            }
        } else {
            AppUtil.showConnectionError(homeActivity)
        }
    }

    fun updateFavoriteImage(
        businessId: String,
        status: Int,
        buzPosition: Int,
        business: HomeBusiness?
    ) {
        for (`object` in mRecyclerViewItemsList) {
            if (`object` is HomeBusiness) {
                val data = `object`
                if (businessId == data.id) {
                    data.favStatus = status
                    restaurantAdapter!!.notifyItemChanged(buzPosition, business)
                    break
                }
            }
        }
    }

    var lastOpenned: Marker? = null
    private fun setBusinessMarker() {
//        isCameraUpdating=true;
        markerList = ArrayList()
        mGoogleMap!!.clear()
        // View mViewMarker;
        var markerPosition = 1
        for (business in businessList) {
            if (business.latitude == null || business.longitude == null) {
                // return;
                continue
            }
            val latLng = LatLng(business.latitude.toDouble(), business.longitude.toDouble())
            val mViewMarker =
                (homeActivity!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(
                    R.layout.custom_place_marker,
                    null
                )
            val number = mViewMarker.findViewById<View>(R.id.markerText) as TextView
            number.text = "" + markerPosition
            val snippet =
                (if (business.images.isEmpty()) "" else business.images[0]) + "#" + AppUtil.getCuisineString(
                    business.cuisines
                ) + "#" + business.googleRating
            val markerOptions = MarkerOptions().position(latLng)
                .title(business.name) //title here
                .snippet(snippet)
                .icon(
                    BitmapDescriptorFactory.fromBitmap(
                        AppUtil.createDrawableFromView(
                            requireContext(),
                            mViewMarker
                        )
                    )
                )
            val marker = mGoogleMap!!.addMarker(markerOptions)
            mMarkerHashMap[marker!!.id] = business.id // set all ids in Hash map
            markerList?.add(markerOptions)
            mGoogleMap!!.setInfoWindowAdapter(CustomInfoWindowAdapter(homeActivity!!, business))
            mGoogleMap!!.setOnInfoWindowClickListener { marker ->
                val businessId = mMarkerHashMap[marker.id]
                homeActivity!!.dispatchToDetailsActivity(businessId.toString())
                //  dispatchToDetailsActivity(businessId.toString());
            }
            mGoogleMap!!.setOnMarkerClickListener(OnMarkerClickListener { marker -> // Check if there is an open info window
                if (lastOpenned != null) {
                    // Close the info window
                    lastOpenned!!.hideInfoWindow()
                    // Is the marker the same marker that was already open
                    if (lastOpenned == marker) {
                        // Nullify the lastOpenned object
                        lastOpenned = null
                        // Return so that the info window isn't openned again
                        return@OnMarkerClickListener true
                    }
                }

                // Open the info window for the marker
                marker.showInfoWindow()
                // Re-assign the last openned such that we can close it later
                lastOpenned = marker

                // Event was handled by our code do not launch default behaviour.
                true
            })


            //  CameraUpdate center = CameraUpdateFactory.newLatLng(latLng);
            // CameraUpdate zoom = CameraUpdateFactory.zoomTo(DEFAULT_ZOOM);
            //  mGoogleMap.moveCamera(center);
            // mGoogleMap.animateCamera(zoom);
            markerPosition++
        }

        /*int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (width * 0.30);

        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        for (MarkerOptions m : markerList) {
            builder.include(m.getPosition());
        }

        LatLngBounds bounds = builder.build();

        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
        mGoogleMap.animateCamera(cu);

        new Handler().postDelayed(() -> {
            isCameraUpdating=false;
        }, 1700);*/

        /*CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude)));
        mGoogleMap.moveCamera(center);*/
    }

    fun dispatchToDetailsActivity(businessId: String?) {
        val intent = Intent(homeActivity, DetailsActivity::class.java)
        intent.putExtra(AppConstant.BUSINESS_ID, businessId)
        // intent.putExtra(AppConstant.FILTER_SELECTED_DAY, selectedDay);
        startActivity(intent)
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

    val deviceHeight: Unit
        get() {
            val displayMetrics = DisplayMetrics()
            requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
            val height = displayMetrics.heightPixels
            val width = displayMetrics.widthPixels
            val qfChipGroupRectf = Rect()
            qfChipGroup!!.getGlobalVisibleRect(qfChipGroupRectf)
            val staticChipGroupRectf = Rect()
            staticChipGroup!!.getGlobalVisibleRect(staticChipGroupRectf)
            bottomSheetMaxHeight = if (qfChipGroupRectf.bottom != 0) {
                val h = qfChipGroupRectf.bottom + 120
                height - h
            } else if (staticChipGroupRectf.bottom != 0) {
                val h = staticChipGroupRectf.bottom + 120
                height - h
            } else {
                500
            }
            bottomSheet!!.maxHeight = bottomSheetMaxHeight
        }

    override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
        if (isChecked) {
            addHeatMap()
        } else {
            if (overlay != null) {
                overlay!!.remove()
            }
            setBusinessMarker()
        }
    }

    private fun addHeatMap() {
        mGoogleMap!!.clear()
        val latLngList: MutableList<LatLng> = ArrayList()
        // Get the data: latitude/longitude positions of buz.
        for (business in businessList) {
            val latLng = LatLng(business.latitude.toDouble(), business.longitude.toDouble())
            latLngList.add(latLng)
        }
        // Create a heat map tile provider, passing it the latlngs of the business.
        val provider = HeatmapTileProvider.Builder().data(latLngList)
            .build()
        // Add a tile overlay to the map, using the heat map tile provider.
        overlay = mGoogleMap!!.addTileOverlay(TileOverlayOptions().tileProvider(provider))
    }

    fun showList() {
        not_found_tv!!.visibility = View.GONE
        scrollView!!.visibility = View.VISIBLE
    }

    fun hideList() {
        not_found_tv!!.visibility = View.VISIBLE
        scrollView!!.visibility = View.GONE
        AppUtil.dismissProgressDialog()
    }

    fun initRecyclerViewScrollListener() {
        scrollView!!.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            if (v.getChildAt(v.childCount - 1) != null) {
                if (scrollY >= v.getChildAt(v.childCount - 1).measuredHeight - v.measuredHeight &&
                    scrollY > oldScrollY
                ) {
                    val visibleItemCount = layoutManager!!.childCount
                    val totalItemCount = layoutManager!!.itemCount
                    val pastVisibleItems = layoutManager!!.findFirstVisibleItemPosition()
                    if (!isLoading) {
                        if (visibleItemCount + pastVisibleItems >= totalItemCount) {
                            page++
                            isLoading = true
                            loadMore()
                        }
                    }
                }
            }
        })
    }

    private fun loadMore() {
        if (mRecyclerViewItemsList.size < 10) {
            return
        }
        paging_pgbar!!.visibility = View.VISIBLE
        dispatchToHomeBusinessService()
    }

    fun dispatchToGetQuickFilterList() {
        for (i in 0..4) {
            val model = FilterList()
            when (i) {
                0 -> model.name = AppConstant.ALL
                1 -> model.name = AppConstant.NOW
                2 -> model.name = AppConstant.BRUNCH
                3 -> model.name = AppConstant.LIVE_MUSIC
                4 -> model.name = AppConstant.REVERS_HAPPY_HOURS
            }
            staticFilterLists.add(model)
        }
        if (AppUtil.isNetworkAvailable(requireContext())) {
            val commonApi = CommonAPI(requireContext())
            commonApi.quickFilterListService { `object` ->
                if (`object` is FilterResponse) {
                    filterResponse = `object`
                    staticFilterLists.addAll(filterResponse!!.filterList)
                    createStaticQuickFilter(staticFilterLists)
                    Log.d(TAG, "onSuccess: ")
                }
            }
        } else {
            AppUtil.showConnectionError(homeActivity)
        }
    }

    fun createStaticQuickFilter(staticFilterLists: List<FilterList>) {
        staticChipGroup!!.removeAllViews()
        for (model in staticFilterLists) {
            if (model.name != null && model.name != "") {
                val tomatoChip = getChip(staticChipGroup, model.name)
                staticChipGroup!!.addView(tomatoChip)
            }
        }
        attachListenerOnStaticChipGroup()
    }

    fun createQuickFilter(quickFilterLists: List<FilterList>, selectedChip: String) {
        qfChipGroup!!.removeAllViews()
        for (model in quickFilterLists) {
            if (model.name != null && model.name != "") {
                if (model.name != selectedChip) {
                    val tomatoChip = getChip(qfChipGroup, model.name)
                    qfChipGroup!!.addView(tomatoChip)
                }
            }
        }
        qfChipGroup!!.setOnCheckedChangeListener(object : ChipGroup.OnCheckedChangeListener {
            override fun onCheckedChanged(group: ChipGroup, checkedId: Int) {
                if (checkedId != -1) {
                    val ids = group.checkedChipIds
                    for (id in ids) {
                        val chip = group.findViewById<Chip>(id!!)
                        quickFilter = chip.text.toString()
                        break
                    }
                } else {
                    // when user unchecked chips we need set again quick Filter blank
                    quickFilter = ""
                }
                filteredListData
            }
        })
        qfChipGroup!!.post(object : Runnable {
            override fun run() {
                deviceHeight
            }
        })
    }

    private fun getChip(chipGroup: ChipGroup?, text: String): Chip {
        val chip = Chip(homeActivity)
        chip.setChipDrawable(
            ChipDrawable.createFromResource(
                requireActivity(),
                R.xml.quick_filter_chip
            )
        )
        chip.elevation = 10f
        chip.isCloseIconVisible = false
        chip.setTextColor(requireActivity().resources.getColorStateList(R.color.text_color_chip_state_list))
        chip.setTextAppearance(R.style.ChipTextStyleAppearance)
        val paddingDp = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            10f,
            requireActivity().resources.displayMetrics
        ).toInt()
        chip.setPadding(paddingDp, paddingDp, paddingDp, paddingDp)
        chip.text = text
        return chip
    }

    fun removeListenerOnStaticChipGroup() {
        staticChipGroup!!.setOnCheckedChangeListener(null)
    }

    fun attachListenerOnStaticChipGroup() {
        staticChipGroup!!.setOnCheckedChangeListener(object : ChipGroup.OnCheckedChangeListener {
            override fun onCheckedChanged(group: ChipGroup, checkedId: Int) {
                qfChipGroup!!.removeAllViews()
                if (checkedId != -1) {
                    val ids = group.checkedChipIds
                    for (id in ids) {
                        val chip = group.findViewById<Chip>(id!!)
                        staticFilter = chip.text.toString()
                        break
                    }
                    if (staticFilter == AppConstant.BRUNCH) {
                        staticFilter = "Brunch"
                        createQuickFilter(filterResponse!!.brunchFilterList, "")
                    } else if (staticFilter == AppConstant.ALL) {
                        staticFilter = "All"
                        quickFilter = ""
                        selectedDay = ""
                        removeListenerOnDaysChipGroup()
                        dayChipGroup!!.clearCheck()
                        attachListenerOnDaysChipGroup() // Reset Day filter when user select all option in Quick filter
                        //  qfChipGroup.removeAllViews();
                    } else if (staticFilter == AppConstant.NOW) {
                        staticFilter = "Now"
                        quickFilter = ""
                        //  qfChipGroup.removeAllViews();
                    } else if (staticFilter == AppConstant.LIVE_MUSIC) {
                        staticFilter = "Live"
                        quickFilter = ""
                        //  qfChipGroup.removeAllViews();
                    } else if (staticFilter == AppConstant.REVERS_HAPPY_HOURS) {
                        staticFilter = "Reverse"
                        quickFilter = ""
                        //   qfChipGroup.removeAllViews();
                    } else {
                        // create quick filter for Happy hours Quick filter
                        createQuickFilter(filterResponse!!.filterList, staticFilter)
                    }
                } else {
                    // when user unchecked chips we need set again static Filter blank
                    if (staticFilter == "All") {
                        removeListenerOnDaysChipGroup()
                        dayChipGroup!!.clearCheck()
                        attachListenerOnDaysChipGroup()
                        selectedDay = ""
                    }
                    quickFilter = ""
                    staticFilter = ""
                }

                // this is post method using For getting height of chip group after adding or removing the chips
                qfChipGroup!!.post(object : Runnable {
                    override fun run() {
                        deviceHeight
                    }
                })
                filteredListData
            }
        })
    }

    fun attachListenerOnDaysChipGroup() {
        dayChipGroup!!.setOnCheckedChangeListener(object : ChipGroup.OnCheckedChangeListener {
            override fun onCheckedChanged(group: ChipGroup, checkedId: Int) {
                selectedDay = when (checkedId) {
                    R.id.mon_chip -> AppConstant.MONDAY
                    R.id.tue_chip -> AppConstant.TUESDAY
                    R.id.wed_chip -> AppConstant.WEDNESDAY
                    R.id.thu_chip -> AppConstant.THURSDAY
                    R.id.fri_chip -> AppConstant.FRIDAY
                    R.id.sat_chip -> AppConstant.SATURDAY
                    R.id.sun_chip -> AppConstant.SUNDAY
                    else -> ""
                }
                if (staticFilter == "All") {
                    // because in all filter we claer days so that this listener call
                    staticFilter = ""
                    removeListenerOnStaticChipGroup()
                    staticChipGroup!!.clearCheck()
                    attachListenerOnStaticChipGroup()
                }
                filteredListData
            }
        })
    }

    private fun checkTrailPeriodService() {
        //  showProgressDialog();
        val apiServiceInterface = ApiClient.instance.client
        val call = apiServiceInterface.checkTrailPeriod(token)
        call.enqueue(object : Callback<TrialDetailsResponse?> {
            override fun onResponse(
                call: Call<TrialDetailsResponse?>,
                response: Response<TrialDetailsResponse?>
            ) {
                AppUtil.dismissProgressDialog()
                try {
                    if (response.isSuccessful) {
                        val detailsResponse = response.body()
                        if (detailsResponse!!.status.equals(
                                AppConstant.SUCCESS,
                                ignoreCase = true
                            )
                        ) {
                            val trialDetails = detailsResponse.details
                            if (trialDetails.trailStatus == 1) {
                                showTrialDialog("Hope you're enjoying your Free Trial. Only " + trialDetails.leftDays + " Days Left.")
                            }
                        } else {
                            if (detailsResponse.message.contains(AppConstant.SESSION_EXPIRED)) {
                                AppUtil.showLogoutDialog(
                                    requireContext(),
                                    homeActivity!!.resources.getString(R.string.error),
                                    detailsResponse.message
                                )
                            } else {
                                AppUtil.showErrorDialog(
                                    homeActivity,
                                    homeActivity!!.resources.getString(R.string.error),
                                    detailsResponse.message
                                )
                            }
                        }
                    }
                } catch (e: Exception) {
                    progressBar!!.visibility = View.GONE
                    AppUtil.dismissProgressDialog()
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<TrialDetailsResponse?>, t: Throwable) {
                AppUtil.dismissProgressDialog()
                call.cancel()
                AppUtil.showErrorDialog(getActivity(), "Error!", t.message.toString())
            }
        })
    }

    fun showTrialDialog(msg: String) {
        CustomErrorDialog(homeActivity, resources.getString(R.string.app_name), msg) { }.show()
    }

    companion object {
        const val TAG = "NewHomeFragment"
        private const val REQUEST_CHECK_SETTINGS = 3456
    }
}