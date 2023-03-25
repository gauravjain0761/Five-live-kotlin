package com.fivelive.app.activity


import android.annotation.SuppressLint
import android.content.*
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Group
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.fivelive.app.Model.*
import com.fivelive.app.Model.graph.GraphData
import com.fivelive.app.Model.graph.HourAnalysis
import com.fivelive.app.R
import com.fivelive.app.activity.*
import com.fivelive.app.activity.ClaimBusinessActivity
import com.fivelive.app.activity.DetailsActivity
import com.fivelive.app.activity.EditBusinessActivity
import com.fivelive.app.activity.HomeActivity
import com.fivelive.app.activity.TakeImage
import com.fivelive.app.activity.amenities.controller.AmenitiesActivity
import com.fivelive.app.activity.brunchMenu.controller.BrunchMenuActivity
import com.fivelive.app.activity.happyHoursMenu.controller.HappyHourActivity
import com.fivelive.app.activity.regularMenu.controller.RegularMenuActivity
import com.fivelive.app.activity.reverseHhMenu.controller.ReverseHHMenuActivity
import com.fivelive.app.adapter.*
import com.fivelive.app.ads.FullScreenAds
import com.fivelive.app.customView.WorkaroundMapFragment
import com.fivelive.app.deepLinking.FirebaseUtil
import com.fivelive.app.dialog.AddImageBottomSheet
import com.fivelive.app.dialog.CallDialog
import com.fivelive.app.dialog.CustomErrorDialog
import com.fivelive.app.dialog.CustomSuccessDialog
import com.fivelive.app.imageSlider.SliderAdapter
import com.fivelive.app.polyline.GetPathFromLocation
import com.fivelive.app.prefs.SharedPreferenceWriter
import com.fivelive.app.prefs.SharedPrefsKey
import com.fivelive.app.retrofit.ApiClient
import com.fivelive.app.util.AppConstant
import com.fivelive.app.util.AppUtil
import com.fivelive.app.util.CommonAPI
import com.fivelive.app.util.PhotoUtil
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.tabs.TabLayout
import com.google.gson.JsonElement
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody.Part.Companion.createFormData
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*

class DetailsActivity : AppCompatActivity(), View.OnClickListener,
    AddImageBottomSheet.ItemClickListener, OnMapReadyCallback {
    private val DEFAULT_ZOOM = 15.5f
    var happyHourImageView: ImageView? = null
    var liveMusicImageView: ImageView? = null
    var categoryRecyclerView: RecyclerView? = null
    var happyHourRecyclerView: RecyclerView? = null
    var liveMusicRecyclerVIew: RecyclerView? = null
    var reviewImages_rv: RecyclerView? = null
    var reviewRecyclerView: RecyclerView? = null
    var claim_business_button: Button? = null
    var moreImageView: ImageView? = null
    var shareImageView: ImageView? = null
    var backImageView: ImageView? = null
    var group: Group? = null
    var businessId: String? = null
    var filterSelectedDay: String? = null
    var from: String? = null
    var mGoogleMap: GoogleMap? = null
    var mapView: View? = null
    var latitude = 0.0
    var longitude = 0.0
    var detail: BusinessDetails? = null
    var sliderImagesList: List<String> = ArrayList()
    var detailCategoryList: MutableList<DetailCategory> = ArrayList()
    lateinit var reviewImgMultiPart: Array<MultipartBody.Part?>
    var imagesURIList: MutableList<String> = ArrayList()
    var rating = 0f
    var reviewString: String? = null
    var reviewImagesAdapter: ReviewImagesAdapter? = null
    var dayName: String? = null
    var yelp_rating_bar: RatingBar? = null
    var google_rating_bar: RatingBar? = null
    var large_ratingBar: RatingBar? = null
    var avgRatingBar: RatingBar? = null
    var businessName_tv: TextView? = null
    var cuisine_tv: TextView? = null
    var address_tv: TextView? = null
    var viewPager: ViewPager? = null
    var indicator: TabLayout? = null
    var saveImageView: ImageView? = null
    var facebook_iv: ImageView? = null
    var twitter_iv: ImageView? = null
    var insta_iv: ImageView? = null
    var add_photo_tv: TextView? = null
    var submit_tv: TextView? = null
    var review_et: EditText? = null
    var daySpinner: Spinner? = null
    var dayTextView: TextView? = null
    var start_end_time_tv: TextView? = null
    var drinks_tv: TextView? = null
    var food_tv: TextView? = null
    var drink_heading_tv: TextView? = null
    var food_heading_tv: TextView? = null
    var today_happy_hours_ll: LinearLayout? = null
    var view_direction_tv: TextView? = null
    var chart: BarChart? = null
    var currentDay: String? = null
    var notFound_tv: TextView? = null
    var parentScrollView: ScrollView? = null
    var live_music_cl: ConstraintLayout? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)
        loadsAds()
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        val mapFragment: SupportMapFragment? =
            supportFragmentManager.findFragmentById(R.id.map) as WorkaroundMapFragment?
        mapFragment!!.getMapAsync(this)
        mapView = mapFragment.view
        businessId = intent.getStringExtra(AppConstant.BUSINESS_ID)
        from = intent.getStringExtra(AppConstant.FROM)
        initView()
        initialzeGone()
        getCurrentDay()
        dispatchToBusinessDetailsService()
    }

    fun loadsAds() {
        var adsValue =
            SharedPreferenceWriter.getInstance(this).getIntValue(SharedPrefsKey.ADS_VALUE)
        if (adsValue == 0) {
            adsValue = 1
        } else if (adsValue == 10) {
            adsValue = 0
        }
        if (adsValue % 2 != 0) {
            FullScreenAds.instance.initializeInterstitialAd(this)
        }
        adsValue = adsValue + 1
        SharedPreferenceWriter.getInstance(this).writeIntValue(SharedPrefsKey.ADS_VALUE, adsValue)
    }

    override fun onResume() {
        super.onResume()
        // dispatchToBusinessDetailsService();
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        mGoogleMap = googleMap
        mGoogleMap!!.isMyLocationEnabled = true
        mGoogleMap!!.uiSettings.isMyLocationButtonEnabled = false
        parentScrollView = findViewById<View>(R.id.parentScrollView) as ScrollView
        (supportFragmentManager.findFragmentById(R.id.map) as WorkaroundMapFragment?)
            ?.setListener(object : WorkaroundMapFragment.OnTouchListener {
                override fun onTouch() {
                    parentScrollView!!.requestDisallowInterceptTouchEvent(
                        true
                    )
                }

            })
        latitude =
            SharedPreferenceWriter.getInstance(this).getString(SharedPrefsKey.LATITUDE)?.toDouble() ?: 0.0
        longitude =
            SharedPreferenceWriter.getInstance(this).getString(SharedPrefsKey.LONGITUDE)?.toDouble() ?: 0.0
        val latLng = LatLng(latitude, longitude)
        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM)
        mGoogleMap!!.moveCamera(cameraUpdate)
    }

    fun setUpDaySpinner(value: String?) {
        val guestArray = resources.getStringArray(R.array.full_day_name_array)
        val dayList = Arrays.asList(*guestArray)
        val spinnerAdapter =
            UtilSpinnerAdapter(this, R.layout.spinner_drop_down_single_row, R.id.name, dayList)
        daySpinner!!.adapter = spinnerAdapter
        daySpinner!!.setSelection(DetailsActivity.Companion.setCurrentDay(value, dayList))
        daySpinner!!.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
                /*if (position == 0) {
                    return;
                }*/
                dayName = dayList[position]
                dayTextView!!.text = dayList[position]
                if (graphHashMap.size > 0) {
                    val currentDayEntryArrayList = graphHashMap[dayName]
                    setPopularTimeGraph(currentDayEntryArrayList)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun dispatchToBusinessDetailsService() {
        if (AppUtil.isNetworkAvailable(this@DetailsActivity)) {
            businessDetailsService()
        } else {
            AppUtil.showConnectionError(this)
        }
    }

    fun initView() {
        live_music_cl = findViewById(R.id.live_music_cl)
        parentScrollView = findViewById<View>(R.id.parentScrollView) as ScrollView
        notFound_tv = findViewById<View>(R.id.notFound_tv) as TextView
        parentScrollView!!.visibility = View.GONE
        notFound_tv!!.visibility = View.VISIBLE
        chart = findViewById<View>(R.id.chart) as BarChart
        view_direction_tv = findViewById(R.id.view_direction_tv)
        daySpinner = findViewById(R.id.daySpinner)
        dayTextView = findViewById(R.id.dayTextView)
        saveImageView = findViewById(R.id.saveImageView)
        address_tv = findViewById(R.id.address_tv)
        cuisine_tv = findViewById(R.id.cuisine_tv)
        businessName_tv = findViewById(R.id.businessName_tv)
        yelp_rating_bar = findViewById(R.id.yelp_rating_bar)
        google_rating_bar = findViewById(R.id.google_rating_bar)
        backImageView = findViewById(R.id.backImageView)
        group = findViewById(R.id.group)
        moreImageView = findViewById(R.id.moreImageView)
        shareImageView = findViewById(R.id.shareImageView)
        claim_business_button = findViewById(R.id.claim_business_button)
        reviewImages_rv = findViewById(R.id.reviewImages_rv)
        categoryRecyclerView = findViewById(R.id.categoryRecyclerView)
        happyHourRecyclerView = findViewById(R.id.happyHourRecyclerView)
        liveMusicRecyclerVIew = findViewById(R.id.liveMusicRecyclerVIew)
        reviewRecyclerView = findViewById(R.id.reviewRecyclerView)
        happyHourImageView = findViewById(R.id.happyHourImageView)
        liveMusicImageView = findViewById(R.id.liveMusicImageView)
        add_photo_tv = findViewById(R.id.add_photo_tv)
        submit_tv = findViewById(R.id.submit_tv)
        large_ratingBar = findViewById(R.id.large_ratingBar)
        avgRatingBar = findViewById(R.id.avgRatingBar)
        review_et = findViewById(R.id.review_et)
        start_end_time_tv = findViewById(R.id.start_end_time_tv)
        drink_heading_tv = findViewById(R.id.drink_heading_tv)
        drinks_tv = findViewById(R.id.drinks_tv)
        food_tv = findViewById(R.id.food_tv)
        food_heading_tv = findViewById(R.id.food_heading_tv)
        today_happy_hours_ll = findViewById(R.id.today_happy_hours_ll)
        facebook_iv = findViewById(R.id.facebook_iv)
        twitter_iv = findViewById(R.id.twitter_iv)
        insta_iv = findViewById(R.id.insta_iv)
        backImageView?.setOnClickListener(this)
        shareImageView?.setOnClickListener(this)
        moreImageView?.setOnClickListener(this)
        claim_business_button?.setOnClickListener(this)
        happyHourImageView?.setOnClickListener(this)
        liveMusicImageView?.setOnClickListener(this)
        add_photo_tv?.setOnClickListener(this)
        submit_tv?.setOnClickListener(this)
        dayTextView?.setOnClickListener(this)
        view_direction_tv?.setOnClickListener(this)
        saveImageView?.setOnClickListener(this)
        facebook_iv?.setOnClickListener(this)
        twitter_iv?.setOnClickListener(this)
        insta_iv?.setOnClickListener(this)
    }

    fun setDataOnHappyHoursList(happyHoursList: List<HappyData>) {
        val adapter = DetailsHappyHourAdapter(this@DetailsActivity, happyHoursList.toMutableList())
        val llm = LinearLayoutManager(this@DetailsActivity)
        llm.orientation = LinearLayoutManager.VERTICAL
        happyHourRecyclerView!!.layoutManager = llm
        happyHourRecyclerView!!.adapter = adapter
    }

    fun setDataOnLiveMusicList(liveMusicList: List<MusicData>) {
        if (liveMusicList == null || liveMusicList.size == 0) {
            live_music_cl!!.visibility = View.GONE
            return
        }
        val adapter = DetailsLiveMusicAdapter(this@DetailsActivity, liveMusicList.toMutableList())
        val llm = LinearLayoutManager(this@DetailsActivity)
        llm.orientation = LinearLayoutManager.VERTICAL
        liveMusicRecyclerVIew!!.layoutManager = llm
        liveMusicRecyclerVIew!!.adapter = adapter
    }

    fun setDataOnReviewList(reviewList: List<ReviewArray>) {
        val adapter = ReviewAdapter(this@DetailsActivity, reviewList.toMutableList())
        val llm = LinearLayoutManager(this@DetailsActivity)
        llm.orientation = LinearLayoutManager.VERTICAL
        reviewRecyclerView!!.layoutManager = llm
        reviewRecyclerView!!.adapter = adapter
    }

    fun prepareCategoryList() {
        detailCategoryList = ArrayList()
        for (i in 0..6) {
            val category = DetailCategory()
            when (i) {
                0 -> {
                    category.name = this.getString(R.string.hh_menu)
                    category.image = R.drawable.hh_menu
                    category.isStatus = detail!!.happy_menue_status
                }
                1 -> {
                    category.name = this.getString(R.string.rev_hh_menu_new)
                    category.image = R.drawable.reverse_hh_menu
                    category.isStatus = detail!!.reverse_menue_status
                }
                2 -> {
                    category.name = this.getString(R.string.brunch)
                    category.image = R.drawable.brunch_b
                    category.isStatus = detail!!.brunch_menue_status
                }
                3 -> {
                    category.name = this.getString(R.string.call)
                    category.image = R.drawable.call
                    category.isStatus = 1 //1 means always visiable
                }
                4 -> {
                    category.name = this.getString(R.string.website)
                    category.image = R.drawable.web
                    category.isStatus = 1 //1 means always visiable
                }
                5 -> {
                    category.name = this.getString(R.string.menu)
                    category.image = R.drawable.main_menu
                    category.isStatus = 1 //1 means always visiable
                }
                6 -> {
                    category.name = this.getString(R.string.info)
                    category.image = R.drawable.info
                    category.isStatus = 1 //1 means always visiable
                }
            }
            if (i == 0 && detail!!.happy_menue_status != 1) {
                continue
            }
            if (i == 1 && detail!!.reverse_menue_status != 1) {
                continue
            }
            if (i == 2 && detail!!.brunch_menue_status != 1) {
                continue
            }
            detailCategoryList.add(category)
        }
        setDataCategoryList()
    }

    fun setDataCategoryList() {
        val adapter = DetailsCategoryAdapter(this@DetailsActivity, detailCategoryList)
        val llm = LinearLayoutManager(this@DetailsActivity)
        llm.orientation = LinearLayoutManager.HORIZONTAL
        categoryRecyclerView!!.layoutManager = llm
        categoryRecyclerView!!.adapter = adapter
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.happyHourImageView -> hhViewExpendOrCollapse()
            R.id.liveMusicImageView -> liveMusicViewExpendOrCollapse()
            R.id.claim_business_button -> dispatchToClaimActivity()
            R.id.moreImageView -> dispatchToEditBusinessActivity()
            R.id.shareImageView ->                 // dispatchToEditBusinessActivity();
                shareBusiness()
            R.id.backImageView ->                 // finish();
                onBackPressed()
            R.id.add_photo_tv -> showBottomSheet()
            R.id.submit_tv -> dispatchToAddReviewService()
            R.id.dayTextView -> daySpinner!!.performClick()
            R.id.view_direction_tv -> dispatchToGoogleMap()
            R.id.saveImageView -> goForAddFavoriteService()
            R.id.facebook_iv ->  detail!!.facebook_link?.let { openWebPage(it) }
            R.id.twitter_iv -> detail!!.twitter_link?.let { openWebPage(it) }
            R.id.insta_iv -> detail!!.instagram?.let { openWebPage(it) }
        }
    }

    private fun openWebPage(url: String) {
        val intent = Intent(Intent.ACTION_VIEW).setData(Uri.parse(url))
        startActivity(intent)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        // When we come here from deep linking link because that link directily open details screen when user login
        if (from != null && from == SplashActivity.TAG) {
            val intent = Intent(this, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            finish()
        } else {
            finish()
        }
    }

    private fun dispatchToGoogleMap() {
        val uri = String.format(
            Locale.ENGLISH,
            "http://maps.google.com/maps?q=loc:%f,%f",
            detail!!.latitude.toDouble(),
            detail!!.longitude.toDouble()
        )
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
        startActivity(intent)
    }

    fun initialzeGone() {
        happyHourRecyclerView!!.visibility = View.GONE
        liveMusicRecyclerVIew!!.visibility = View.GONE
    }

    fun hhViewExpendOrCollapse() {
        if (happyHourRecyclerView!!.visibility == View.VISIBLE) {
            happyHourRecyclerView!!.visibility = View.GONE
            happyHourImageView!!.setImageDrawable(resources.getDrawable(R.drawable.ddown))
        } else {
            happyHourRecyclerView!!.visibility = View.VISIBLE
            happyHourImageView!!.setImageDrawable(resources.getDrawable(R.drawable.up_aa))
        }
    }

    fun liveMusicViewExpendOrCollapse() {
        Log.d("", "liveMusicViewExpendOrCollapse: ")
        if (liveMusicRecyclerVIew!!.visibility == View.VISIBLE) {
            liveMusicRecyclerVIew!!.visibility = View.GONE
            liveMusicImageView!!.setImageDrawable(resources.getDrawable(R.drawable.ddown))
        } else {
            liveMusicRecyclerVIew!!.visibility = View.VISIBLE
            liveMusicImageView!!.setImageDrawable(resources.getDrawable(R.drawable.up_aa))
        }
    }

    fun categoryListListener(value: String?) { //
        when (value) {
            AppConstant.HH_MENU -> dispatchToHappyHourActivity()
            AppConstant.REV_HH_MENU -> dispatchToReverseHHMenuActivity()
            AppConstant.BRUNCH -> dispatchToBrunchMenuActivity()
            AppConstant.CALL -> openCallDialog()
            AppConstant.WEBSITE -> openWebSite()
            AppConstant.MENU -> dispatchToRegularMenuActivity()
            AppConstant.INFO -> dispatchToAmenitiesActivity()
        }
    }

    fun openWebSite() {
        if (detail!!.website != null && detail!!.website != "") {
            var url = detail!!.website!!
            url =
                if (url.startsWith("http://") && url.startsWith("https://")) detail!!.website!! else "http://$url"
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            startActivity(i)
        } else {
            Toast.makeText(this, "Web url not Available.", Toast.LENGTH_SHORT).show()
        }
    }

    fun openCallDialog() {
        if (detail!!.call != null && detail!!.call != "") {
            CallDialog(this, detail!!.call!!) { openDialPad() }.show()
        } else {
            Toast.makeText(this, "Phone number not Available.", Toast.LENGTH_SHORT).show()
        }
    }

    fun openDialPad() {
        val u = Uri.parse("tel:" + detail!!.call)
        val i = Intent(Intent.ACTION_DIAL, u)
        startActivity(i)
    }

    private fun dispatchToHappyHourActivity() {
        val intent = Intent(this@DetailsActivity, HappyHourActivity::class.java)
        intent.putExtra(AppConstant.BUSINESS_ID, businessId)
        startActivity(intent)
    }

    private fun dispatchToReverseHHMenuActivity() {
        val intent = Intent(this@DetailsActivity, ReverseHHMenuActivity::class.java)
        intent.putExtra(AppConstant.BUSINESS_ID, businessId)
        startActivity(intent)
    }

    private fun dispatchToBrunchMenuActivity() {
        val intent = Intent(this@DetailsActivity, BrunchMenuActivity::class.java)
        intent.putExtra(AppConstant.BUSINESS_ID, businessId)
        startActivity(intent)
    }

    private fun dispatchToRegularMenuActivity() {
        val intent = Intent(this@DetailsActivity, RegularMenuActivity::class.java)
        intent.putExtra(AppConstant.BUSINESS_ID, businessId)
        startActivity(intent)
    }

    private fun dispatchToAmenitiesActivity() {
        val intent = Intent(this@DetailsActivity, AmenitiesActivity::class.java)
        intent.putExtra(AppConstant.BUSINESS_ID, businessId)
        startActivity(intent)
    }

    private fun dispatchToClaimActivity() {
        if (AppUtil.checkUserType(this@DetailsActivity)) {
            AppUtil.showGuestLoginDialog(this@DetailsActivity)
            return
        }
        val intent = Intent(this@DetailsActivity, ClaimBusinessActivity::class.java)
        intent.putExtra(AppConstant.BUSINESS_ID, businessId)
        startActivity(intent)
    }

    private fun dispatchToEditBusinessActivity() {
        val intent = Intent(this@DetailsActivity, EditBusinessActivity::class.java)
        intent.putExtra(AppConstant.BUSINESS_ID, businessId)
        startActivity(intent)
    }

    fun showImagePreview(position: Int) {
        val intent = Intent(this@DetailsActivity, ShowImagesActivity::class.java)
        intent.putExtra(AppConstant.IMAGES_LIST, sliderImagesList as Serializable)
        intent.putExtra(AppConstant.IMAGE_INDEX, position)
        startActivity(intent)
        // overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
    }

    fun imageSlider() {
        viewPager = findViewById<View>(R.id.imageView24) as ViewPager
        indicator = findViewById<View>(R.id.indicator) as TabLayout
        viewPager!!.adapter = SliderAdapter(this, sliderImagesList)
        indicator!!.setupWithViewPager(viewPager, true)
        val timer = Timer()
        timer.scheduleAtFixedRate(SliderTimer(), 4000, 4000)
    }

    private inner class SliderTimer : TimerTask() {
        override fun run() {
            runOnUiThread {
                if (viewPager!!.currentItem < sliderImagesList.size - 1) {
                    viewPager!!.currentItem = viewPager!!.currentItem + 1
                } else {
                    viewPager!!.currentItem = 0
                }
            }
        }
    }

    private fun businessDetailsService() {
        AppUtil.showProgressDialog(this@DetailsActivity)
        val token =
            SharedPreferenceWriter.getInstance(this@DetailsActivity).getString(SharedPrefsKey.TOKEN)
        val apiServiceInterface = ApiClient.instance.client
        val call = apiServiceInterface.businessDetails(token, businessId)
        call.enqueue(object : Callback<BusinessDetailsResponse?> {
            override fun onResponse(
                call: Call<BusinessDetailsResponse?>,
                response: Response<BusinessDetailsResponse?>
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
                            detail = detailsResponse.businessDetails
                            showParentLayout()
                            updateUI()
                        } else {
                            AppUtil.showErrorDialog(
                                this@DetailsActivity,
                                resources.getString(R.string.error),
                                detailsResponse.message
                            )
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<BusinessDetailsResponse?>, t: Throwable) {
                AppUtil.dismissProgressDialog()
                call.cancel()
                AppUtil.showErrorDialog(this@DetailsActivity, getString(R.string.error), t.message)
            }
        })
    }

    fun showParentLayout() {
        parentScrollView!!.visibility = View.VISIBLE
        notFound_tv!!.visibility = View.GONE
    }

    private fun updateUI() {
        setUrlVisibility()
        drawPath()
        setUpDaySpinner(currentDay)
        prepareCategoryList()
        prepareListForGraph()
        sliderImagesList = detail!!.images
        imageSlider()
        businessName_tv!!.text = detail!!.name
        yelp_rating_bar!!.rating = detail!!.yelpRating.toFloat()
        google_rating_bar!!.rating = detail!!.googleRating.toFloat()
        if (detail!!.avgRating != null && detail!!.avgRating != "") {
            avgRatingBar!!.rating = detail!!.googleRating.toFloat()
        }
        val address = if (detail!!.address == null) "" else detail!!.address
        val city = if (detail!!.city == null) "" else detail!!.city
        val state = if (detail!!.state == null) "" else detail!!.state
        val zipCode = if (detail!!.zipcode == null) "" else detail!!.zipcode
        val country = if (detail!!.country == null) "" else detail!!.country
        address_tv!!.text = "$address,$city,$state,$zipCode,$country"
        start_end_time_tv!!.text = detail!!.hhHoursStartTime + " - " + detail!!.hhHoursEndTime
        if (detail!!.hhDrnk != null && detail!!.hhDrnk != "") {
            drinks_tv!!.text = detail!!.hhDrnk
        } else {
            drink_heading_tv!!.visibility = View.GONE
            drinks_tv!!.visibility = View.GONE
        }
        if (detail!!.hhFood != null && detail!!.hhFood != "") {
            food_tv!!.text = detail!!.hhFood
        } else {
            food_heading_tv!!.visibility = View.GONE
            food_tv!!.visibility = View.GONE
        }

        /*Cuisine String here*/cuisine_tv!!.text = AppUtil.getCuisineString(detail!!.cuisines)
        if (detail!!.favStatus == 1) {
            saveImageView!!.setImageDrawable(getDrawable(R.drawable.save))
        } else {
            //  saveImageView.setImageDrawable(getDrawable(R.drawable.unsaved));
            saveImageView!!.setImageDrawable(getDrawable(R.drawable.un_sav_black))
        }

        //claim status = 1 means user already done claim bussiness
        if (detail!!.claimStatus == 1) {
            claim_business_button!!.visibility = View.GONE
        } else {
            claim_business_button!!.visibility = View.VISIBLE
        }
        if (detail!!.claimStatus == 1) {
            group!!.visibility = View.GONE
            moreImageView!!.visibility = View.VISIBLE
        } else {
            group!!.visibility = View.VISIBLE
            moreImageView!!.visibility = View.GONE
        }

        //happy status = 1 means toady happy hours given in restaurant
        if (detail!!.happyStatus == 1) {
            today_happy_hours_ll!!.visibility = View.VISIBLE
        } else {
            today_happy_hours_ll!!.visibility = View.GONE
        }
        setDataOnHappyHoursList(detail!!.happyData)
        setDataOnLiveMusicList(detail!!.musicData)
        setDataOnReviewList(detail!!.reviewArr)
    }

    private fun setUrlVisibility() {
        if (detail!!.facebook_link != null) {
            facebook_iv!!.visibility = View.VISIBLE
        } else {
            facebook_iv!!.visibility = View.GONE
        }
        if (detail!!.twitter_link != null) {
            twitter_iv!!.visibility = View.VISIBLE
        } else {
            twitter_iv!!.visibility = View.GONE
        }
        if (detail!!.instagram != null) {
            insta_iv!!.visibility = View.VISIBLE
        } else {
            insta_iv!!.visibility = View.GONE
        }
    }

    fun getCurrentDay() {
        val calendar = Calendar.getInstance()
        val date = calendar.time
        currentDay = SimpleDateFormat("EEEE", Locale.ENGLISH).format(date.time)
        // return new SimpleDateFormat("EEEE", Locale.ENGLISH).format(date.getTime());
    }

    fun showBottomSheet() {
        val instance = AddImageBottomSheet.newInstance()
        instance.show(supportFragmentManager, AddImageBottomSheet.TAG)
    }

    override fun onItemClick(item: String?) {
        if (item.equals(AppConstant.GALLERY, ignoreCase = true)) {
            openGallery()
        } else if (item.equals(AppConstant.CAMERA, ignoreCase = true)) {
            openCamera()
        }
    }

    fun openGallery() {
        if (imagesURIList.size == 2) {
            showImageValidation()
            return
        }
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type =
            "image/*" //allows any image file type. Change * to specific extension to limit it
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        startActivityForResult(
            Intent.createChooser(intent, "Select Picture"),
            DetailsActivity.Companion.GALLERY_IMAGE
        )
    }

    private fun openCamera() {
        if (imagesURIList.size == 2) {
            showImageValidation()
            return
        }
        val intent = Intent(this@DetailsActivity, TakeImage::class.java)
        startActivityForResult(intent, DetailsActivity.Companion.CAPTURE_IMAGE)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            DetailsActivity.Companion.GALLERY_IMAGE -> if (resultCode == RESULT_OK) {
                // imagesURIList.clear();
                if (data!!.clipData != null) {
                    imagesURIList.clear()
                    val count = data.clipData!!.itemCount
                    if (count > 2) {
                        showImageValidation()
                        return
                    }
                    var imageUri: Uri? = null
                    var i = 0
                    while (i < count) {
                        imageUri = data.clipData!!.getItemAt(i).uri
                        imagesURIList.add(
                            PhotoUtil.getImageStringToUri(
                                this@DetailsActivity,
                                imageUri
                            )
                        )
                        i++
                    }
                } else if (data.data != null) {
                    val imageUri = data.data
                    imagesURIList.add(PhotoUtil.getImageStringToUri(this@DetailsActivity, imageUri))
                }
                setReviewImages()
            }
            DetailsActivity.Companion.CAPTURE_IMAGE -> {
                if (data != null) {
                    val imagePath = data.getStringExtra("photoFile")
                    imagesURIList.add(imagePath!!)
                }
                setReviewImages()
            }
        }
    }

    private fun showImageValidation() {
        CustomErrorDialog(
            this@DetailsActivity,
            this.getString(R.string.alert),
            "You can add maximum two images."
        ) { }.show()
    }

    private fun setReviewImages() {
        reviewImagesAdapter = ReviewImagesAdapter(this@DetailsActivity, imagesURIList)
        val llm = LinearLayoutManager(this@DetailsActivity)
        llm.orientation = LinearLayoutManager.HORIZONTAL
        reviewImages_rv!!.layoutManager = llm
        reviewImages_rv!!.adapter = reviewImagesAdapter
    }

    private fun dispatchToAddReviewService() {
        AppUtil.hideKeyboard(this)
        if (AppUtil.checkUserType(this@DetailsActivity)) {
            AppUtil.showGuestLoginDialog(this@DetailsActivity)
            return
        }
        if (AppUtil.isNetworkAvailable(this@DetailsActivity)) {
            if (validateSection()) {
                addReviewService()
            }
        } else {
            AppUtil.showConnectionError(this@DetailsActivity)
        }
    }

    private fun validateSection(): Boolean {
        rating = large_ratingBar!!.rating
        reviewString = review_et!!.text.toString().trim { it <= ' ' }
        if (rating.toDouble() == 0.0) {
            AppUtil.showErrorDialog(
                this@DetailsActivity,
                getString(R.string.error),
                "Please give the Rating."
            )
            return false
        }
        if (TextUtils.isEmpty(reviewString)) {
            AppUtil.showErrorDialog(
                this@DetailsActivity,
                getString(R.string.error),
                "Write review here."
            )
            return false
        }
        return true
    }

    private fun showSuccessDialog(msg: String) {
        CustomSuccessDialog(this, "Review Added.", msg) {
            val refresh = Intent(this@DetailsActivity, DetailsActivity::class.java)
            refresh.putExtra(AppConstant.BUSINESS_ID, businessId)
            startActivity(refresh) //Start the same Activity
            finish()
        }.show()
    }

    private fun clearReviewSectionData() {
        large_ratingBar!!.rating = 0f
        review_et!!.setText("")
        imagesURIList.clear()
        reviewImagesAdapter!!.notifyDataSetChanged()
    }

    fun createMultipartForReviewImage() {
        val lengthOfArray = imagesURIList.size
        reviewImgMultiPart = arrayOfNulls<MultipartBody.Part>(lengthOfArray)
        for (index in imagesURIList.indices) {
            val file = File(imagesURIList[index])
            val requestBody = file.asRequestBody("*/*".toMediaTypeOrNull())
            reviewImgMultiPart[index] =
                createFormData("review_image[]", file.name, requestBody)
        }
    }

    private fun addReviewService() {
        AppUtil.showProgressDialog(this@DetailsActivity)
        createMultipartForReviewImage()
        val token =
            SharedPreferenceWriter.getInstance(this@DetailsActivity).getString(SharedPrefsKey.TOKEN)
        val sessionToken = token?.toRequestBody("text/plain".toMediaTypeOrNull())
        val body_rating = rating.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val body_review = reviewString!!.toRequestBody("text/plain".toMediaTypeOrNull())
        val body_businessId = businessId!!.toRequestBody("text/plain".toMediaTypeOrNull())
        val apiServiceInterface = ApiClient.instance.client
        val call = apiServiceInterface
            .addRating(sessionToken, body_rating, body_review, body_businessId, reviewImgMultiPart)
        call.enqueue(object : Callback<JsonElement> {
            override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                AppUtil.dismissProgressDialog()
                try {
                    if (response.isSuccessful) {
                        val `object` =
                            JSONObject(response.body()!!.asJsonObject.toString().trim { it <= ' ' })
                        if (`object`.getString("status")
                                .equals(AppConstant.SUCCESS, ignoreCase = true)
                        ) {
                            showSuccessDialog(`object`.getString("message"))
                            clearReviewSectionData()
                            // AppUtil.showErrorDialog(DetailsActivity.this, "Success!", object.getString("message"));
                        } else {
                            if (`object`.getString("message")
                                    .contains(AppConstant.SESSION_EXPIRED)
                            ) {
                                AppUtil.showLogoutDialog(
                                    this@DetailsActivity,
                                    resources.getString(R.string.error),
                                    `object`.getString("message")
                                )
                            } else {
                                AppUtil.showErrorDialog(
                                    this@DetailsActivity,
                                    resources.getString(R.string.error),
                                    `object`.getString("message")
                                )
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                AppUtil.dismissProgressDialog()
                call.cancel()
                AppUtil.showErrorDialog(
                    this@DetailsActivity,
                    resources.getString(R.string.error),
                    t.message
                )
            }
        })
    }

    fun showErrorDialog(context: Context?, alert: String, msg: String) {
        CustomErrorDialog(context, alert, msg) {
            finish()
            startActivity(intent)
        }.show()
    }

    var busyHoursList: List<Int> = ArrayList()
    var hourAnalysisList: List<HourAnalysis> = ArrayList()
    var graphDayName: String? = null
    var graphHashMap: MutableMap<String?, List<BarEntry>?> = hashMapOf()
    fun prepareListForGraph() {
        val data = detail!!.forecastData
        if (data == null || data.status == "error") {
            return
        }
        Log.d(DetailsActivity.Companion.TAG, "prepareListForGraph: ")
        for (analysis in data.analysis.orEmpty()) {
            val entryArrayList: MutableList<BarEntry> = ArrayList()
            graphDayName = analysis.dayInfo.dayText
            busyHoursList = analysis.busyHours
            hourAnalysisList = analysis.hourAnalysis
            val graphData = GraphData()
            graphData.dayName = analysis.dayInfo.dayText
            for (xValue in 1..24) {
                //for (int xValue : busyHoursList) {
                var yValue = 0f
                for (hourAnalysis in hourAnalysisList) {
                    if (xValue.toFloat() == hourAnalysis.hour.toFloat()) {
                        when (hourAnalysis.intensityTxt) {
                            AppConstant.CLOSED -> yValue = 0f
                            AppConstant.LOW -> yValue = 1f
                            AppConstant.AVERAGE -> yValue = 2f
                            AppConstant.ABOVE_AVERAGE -> yValue = 3f
                            AppConstant.HIGH -> yValue = 4f
                        }
                        Log.d(DetailsActivity.Companion.TAG, "prepareListForGraph: ")
                        entryArrayList.add(BarEntry(xValue.toFloat(), yValue))
                        break
                    }
                    Log.d(DetailsActivity.Companion.TAG, "prepareListForGraph: ")
                }
            }
            //break;
            graphHashMap[graphDayName] = entryArrayList
            Log.d(DetailsActivity.Companion.TAG, "prepareListForGraph: ")
        }
        if (graphHashMap.size > 0) {
            val currentDayEntryArrayList = graphHashMap[currentDay]
            setPopularTimeGraph(currentDayEntryArrayList)
        }
    }

    fun setPopularTimeGraph(currentDayEntryArrayList: List<BarEntry>?) {
        val barDataSet = BarDataSet(currentDayEntryArrayList, "")
        //  BarDataSet barDataSet = new BarDataSet(entryArrayList, "");
        // barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        barDataSet.setColors(Color.BLUE)
        barDataSet.setDrawValues(false)
        val barData = BarData(barDataSet)
        chart!!.setDrawGridBackground(false)
        chart!!.axisLeft.isEnabled = true
        chart!!.axisRight.isEnabled = false
        chart!!.xAxis.setDrawGridLines(false)
        chart!!.axisLeft.setDrawGridLines(false)
        chart!!.axisRight.setDrawGridLines(false)
        chart!!.xAxis.textColor = resources.getColor(R.color.chip_text_color)
        val typeface = ResourcesCompat.getFont(this, R.font.whitney_semibold)
        chart!!.xAxis.typeface = typeface
        chart!!.axisLeft.textColor = resources.getColor(R.color.chip_text_color)
        chart!!.axisLeft.typeface = typeface
        chart!!.isAutoScaleMinMaxEnabled = false
        /*chart.getAxisLeft().setAxisMaximum(5);
        chart.getAxisLeft().setAxisMinimum(0);
        chart.getAxisLeft().setLabelCount(5);*/


        // chart.setAutoScaleMinMaxEnabled(false);
        chart!!.xAxis.position = XAxis.XAxisPosition.BOTTOM
        chart!!.xAxis.labelCount = 8
        chart!!.description.isEnabled = false
        chart!!.setDrawValueAboveBar(false)
        chart!!.isDoubleTapToZoomEnabled = false
        chart!!.setTouchEnabled(false)
        chart!!.setBackgroundColor(resources.getColor(R.color.sky_color_light))
        val legend = chart!!.legend
        legend.isEnabled = false
        chart!!.data = barData
        chart!!.animateY(2000)
        chart!!.invalidate()
        // dynamically set x axis value String type
        val xAxes = ArrayList<String>()
        for (i in 0..24) {
            xAxes.add(i, AppUtil.convert24HoursFormatTo12HoursFormat(i))
        }
        chart!!.xAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                val index = value.toInt()
                return xAxes[index]
            }
        }
        chart!!.axisLeft.axisMinimum = 0f
        chart!!.axisLeft.setLabelCount(5, true)
        chart!!.setVisibleYRangeMaximum(100f, YAxis.AxisDependency.LEFT)

        // dynamically set y axis value String type
        val yAxes = ArrayList<String>()
        for (i in 0..4) {
            if (i == 0) {
                yAxes.add(i, "0%")
            } else if (i == 1) {
                yAxes.add(i, "25%")
            } else if (i == 2) {
                yAxes.add(i, "50%")
            } else if (i == 3) {
                yAxes.add(i, "75%")
            } else if (i == 4) {
                yAxes.add(i, "100%")
            } else if (i == 5) {
                yAxes.add(i, "100%")
            }
        }
        chart!!.axisLeft.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                val index = value.toInt()
                return yAxes[index]
            }
        }


        /* YAxis y = chart.getAxisLeft();
        y.setLabelCount(4);
        y.setAxisMaxValue(100);
        y.setAxisMinValue(0);*/
    }

    fun goForAddFavoriteService() {
        if (AppUtil.checkUserType(this@DetailsActivity)) {
            AppUtil.showGuestLoginDialog(this@DetailsActivity)
            return
        }
        if (AppUtil.isNetworkAvailable(this)) {
            val commonApi = CommonAPI(this@DetailsActivity, businessId)
            commonApi.addFavoriteService { status ->
                detail!!.favStatus = status
                updateFavoriteImage()
            }
        } else {
            AppUtil.showConnectionError(this)
        }
    }

    fun updateFavoriteImage() {
        if (detail!!.favStatus == 1) {
            saveImageView!!.setImageDrawable(getDrawable(R.drawable.save))
        } else {
            // saveImageView.setImageDrawable(getDrawable(R.drawable.unsaved));
            saveImageView!!.setImageDrawable(getDrawable(R.drawable.un_sav_black))
        }
    }

    fun drawPath() {
        val source = LatLng(latitude, longitude)
        //  mGoogleMap.addMarker(new MarkerOptions().position(source).icon(BitmapDescriptorFactory.fromResource(R.drawable.bluess)));
        val destination = LatLng(detail!!.latitude.toDouble(), detail!!.longitude.toDouble())
        // mGoogleMap.addMarker(new MarkerOptions().position(destination).icon(BitmapDescriptorFactory.fromResource(R.drawable.bluess)));
        mGoogleMap!!.addMarker(MarkerOptions().position(destination))
        GetPathFromLocation(this, source, destination) { polyLine ->
            if (polyLine != null) {
                mGoogleMap!!.addPolyline(polyLine)
            }
        }.execute()
    }

    fun shareBusiness() {
        if (AppUtil.checkUserType(this@DetailsActivity)) {
            AppUtil.showGuestLoginDialog(this@DetailsActivity)
            return
        }
        var imageUrl: String? = ""
        if (detail!!.images.size > 0) {
            imageUrl = detail!!.images[0]
        }
        FirebaseUtil.createFirebaseDynamicLink(
            this,
            businessId,
            detail!!.name,
            detail!!.cuisines.toString(),
            imageUrl,
            false
        )
    }

    companion object {
        private val TAG = DetailsActivity::class.java.name
        const val GALLERY_IMAGE = 1012
        const val CAPTURE_IMAGE = 1021
        fun setCurrentDay(day: String?, dayList: List<String>): Int {
            var k = 0
            for (i in dayList.indices) {
                if (dayList[i].equals(day, ignoreCase = true)) {
                    k = i
                    break
                } else {
                    k = 0
                }
            }
            return k
        }
    }
}