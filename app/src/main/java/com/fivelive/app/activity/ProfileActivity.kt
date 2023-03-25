package com.fivelive.app.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.content.IntentSender.SendIntentException
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fivelive.app.Model.MyClaimBusiness
import com.fivelive.app.Model.ProfileDetails
import com.fivelive.app.Model.ProfileResponse
import com.fivelive.app.R
import com.fivelive.app.adapter.ProfileClaimedBusinessAdapter
import com.fivelive.app.ads.FullScreenAds
import com.fivelive.app.prefs.SharedPreferenceWriter
import com.fivelive.app.prefs.SharedPrefsKey
import com.fivelive.app.retrofit.ApiClient
import com.fivelive.app.util.AppConstant
import com.fivelive.app.util.AppUtil
import com.fivelive.app.util.CommonAPI
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.OnSuccessListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileActivity : AppCompatActivity(), View.OnClickListener {
    private val DEFAULT_ZOOM = 17.0f
    private var mFusedLocationProviderClient: FusedLocationProviderClient? = null
    private var mLastLocation: Location? = null
    private var locationRequest: LocationRequest? = null
    private var mLocationCallback: LocationCallback? = null
    var claimedBusiness_rv: RecyclerView? = null
    var backImageView: ImageView? = null
    var editTextView: TextView? = null
    var profile_imv: ImageView? = null
    var user_name_tv: TextView? = null
    var gender_tv: TextView? = null
    var email_tv: TextView? = null
    var profileDetails: ProfileDetails? = null
    var myClaimBusinessList: List<MyClaimBusiness>? = null
    var businessAdapter: ProfileClaimedBusinessAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        FullScreenAds.instance.initializeInterstitialAd(this@ProfileActivity)
        initView()
    }

    override fun onResume() {
        super.onResume()
        checkLocationPermission()
        //  dispatchToProfileDetailsService();
    }

    private fun initView() {
        editTextView = findViewById(R.id.editTextView)
        editTextView?.setVisibility(View.VISIBLE)
        backImageView = findViewById(R.id.backImageView)
        profile_imv = findViewById(R.id.profile_imv)
        email_tv = findViewById(R.id.email_tv)
        gender_tv = findViewById(R.id.gender_tv)
        user_name_tv = findViewById(R.id.user_name_tv)
        claimedBusiness_rv = findViewById(R.id.claimedBusiness_rv)
        editTextView?.setOnClickListener(this)
        backImageView?.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.backImageView -> finish()
            R.id.editTextView -> dispatchToEditScreen()
        }
    }

    private fun dispatchToEditScreen() {
        val intent = Intent(this@ProfileActivity, EditProfileActivity::class.java)
        intent.putExtra(AppConstant.PROFILE_DETAILS_MODEL, profileDetails)
        intent.putExtra(AppConstant.PHONE_NUMBER, profileDetails!!.mobile)
        intent.putExtra(AppConstant.EMAIL, profileDetails!!.email)
        startActivity(intent)
    }

    private fun dispatchToProfileDetailsService() {
        if (AppUtil.isNetworkAvailable(this)) {
            profileDetailsService()
        } else {
            AppUtil.showConnectionError(this)
        }
    }

    fun profileDetailsService() {
        AppUtil.showProgressDialog(this@ProfileActivity)
        val userId =
            SharedPreferenceWriter.getInstance(this@ProfileActivity).getString(SharedPrefsKey.ID)
        val apiServiceInterface = ApiClient.instance.client
        val call = apiServiceInterface.getProfileDetails(
            userId,
            mLastLocation!!.latitude.toString(),
            mLastLocation!!.longitude.toString()
        )
        call.enqueue(object : Callback<ProfileResponse?> {
            override fun onResponse(
                call: Call<ProfileResponse?>?,
                response: Response<ProfileResponse?>
            ) {
                AppUtil.dismissProgressDialog()
                try {
                    if (response.isSuccessful) {
                        val profileResponse = response.body()
                        if (profileResponse!!.status.equals(
                                AppConstant.SUCCESS,
                                ignoreCase = true
                            )
                        ) {
                            profileDetails = profileResponse.profileDetails
                            myClaimBusinessList = profileResponse.myClaimBusiness
                            setDataOnList()
                            updateUI()
                        } else {
                            if (profileResponse.message.contains(AppConstant.SESSION_EXPIRED)) {
                                AppUtil.showLogoutDialog(
                                    this@ProfileActivity,
                                    this@ProfileActivity.resources.getString(R.string.error),
                                    profileResponse.message
                                )
                            } else {
                                AppUtil.showErrorDialog(
                                    this@ProfileActivity,
                                    this@ProfileActivity.resources.getString(R.string.error),
                                    profileResponse.message
                                )
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<ProfileResponse?>, t: Throwable) {
                AppUtil.dismissProgressDialog()
                call.cancel()
                AppUtil.showErrorDialog(
                    this@ProfileActivity,
                    this@ProfileActivity.resources.getString(R.string.error),
                    t.message
                )
            }
        })
    }

    private fun updateUI() {
        user_name_tv!!.text = profileDetails!!.firstName + " " + profileDetails!!.lastName
        if (profileDetails!!.gender != null && profileDetails!!.gender != "") {
            gender_tv!!.text = profileDetails!!.gender
        } else {
            gender_tv!!.visibility = View.GONE
        }
        email_tv!!.text = profileDetails!!.email
        updateProfileImage(profileDetails!!.image)
    }

    private fun updateProfileImage(imagePath: String?) {
        //String imagePath = SharedPreferenceWriter.getInstance(ProfileActivity.this).getString(SharedPrefsKey.PROFILE_IMAGE);
        if (imagePath != null && imagePath != "") {
            Glide.with(this)
                .load(imagePath)
                .centerCrop()
                .skipMemoryCache(false)
                .placeholder(R.drawable.profile_other)
                .into(profile_imv!!)
        }
    }

    fun setDataOnList() {
        businessAdapter = ProfileClaimedBusinessAdapter(this@ProfileActivity, myClaimBusinessList!!)
        val llm = LinearLayoutManager(this@ProfileActivity)
        llm.orientation = LinearLayoutManager.VERTICAL
        claimedBusiness_rv!!.layoutManager = llm
        claimedBusiness_rv!!.adapter = businessAdapter
    }

    fun goForAddFavoriteService(businessId: String) {
        if (AppUtil.isNetworkAvailable(this@ProfileActivity)) {
            val commonApi = CommonAPI(this@ProfileActivity, businessId)
            commonApi.addFavoriteService { status -> updateFavoriteImage(businessId, status) }
        } else {
            AppUtil.showConnectionError(this@ProfileActivity)
        }
    }

    fun updateFavoriteImage(businessId: String, status: Int) {
        for (data in myClaimBusinessList!!) {
            if (businessId == data.businessId) {
                //   data.setFavStatus(status);
                businessAdapter!!.notifyDataSetChanged()
                break
            }
        }
    }

    fun dispatchToDetailsActivity(businessId: String?) {
        val intent = Intent(this@ProfileActivity, DetailsActivity::class.java)
        intent.putExtra(AppConstant.BUSINESS_ID, businessId)
        startActivity(intent)
    }

    private fun checkLocationPermission() {
        locationRequest = LocationRequest.create()
        locationRequest?.setInterval(10000)
        locationRequest?.setFastestInterval(5000)
        locationRequest?.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        locationRequest?.let {
            val builder = LocationSettingsRequest.Builder().addLocationRequest(it)
            val settingsClient = LocationServices.getSettingsClient(this)
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
                        e.startResolutionForResult(
                            this@ProfileActivity,
                            ProfileActivity.Companion.REQUEST_CHECK_SETTINGS
                        )
                    } catch (sendEx: SendIntentException) {
                        // Ignore the error.
                    }
                }
            }
        }

    }

    @get:SuppressLint("MissingPermission")
    val deviceLocation: Unit
        get() {
            mFusedLocationProviderClient!!.lastLocation.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    mLastLocation = task.result
                    if (mLastLocation != null) {
                        dispatchToProfileDetailsService()
                    } else {
                        createNewLocationRequest()
                    }
                } else {
                    Toast.makeText(
                        this@ProfileActivity,
                        "Unable to get last Device Location",
                        Toast.LENGTH_SHORT
                    ).show()
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
                    dispatchToProfileDetailsService()
                    mFusedLocationProviderClient!!.removeLocationUpdates(mLocationCallback!!)
                }
            }
        }
        mFusedLocationProviderClient!!.requestLocationUpdates(
            locationRequest!!,
            mLocationCallback!!,
            Looper.myLooper()
        )
    }

    companion object {
        const val TAG = "ProfileActivity"
        private const val REQUEST_CHECK_SETTINGS = 3456
    }
}