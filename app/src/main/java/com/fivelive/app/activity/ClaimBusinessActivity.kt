package com.fivelive.app.activity


import android.annotation.SuppressLint

import android.content.Intent

import android.content.IntentSender.SendIntentException

import android.location.Location

import android.net.Uri

import android.os.Bundle

import android.os.CountDownTimer

import android.os.Looper

import android.text.TextUtils

import android.view.View

import android.widget.*

import androidx.appcompat.app.AppCompatActivity

import androidx.constraintlayout.widget.ConstraintLayout

import androidx.recyclerview.widget.LinearLayoutManager

import androidx.recyclerview.widget.RecyclerView

import com.fivelive.app.Model.ClaimBusiness

import com.fivelive.app.Model.ClaimBusinessResponse

import com.fivelive.app.R

import com.fivelive.app.adapter.RecipesImagesAdapter

import com.fivelive.app.dialog.BusinessClaimedDialog

import com.fivelive.app.dialog.CustomSuccessDialog

import com.fivelive.app.prefs.SharedPreferenceWriter

import com.fivelive.app.prefs.SharedPrefsKey

import com.fivelive.app.retrofit.ApiClient

import com.fivelive.app.util.*

import com.google.android.gms.common.api.ResolvableApiException

import com.google.android.gms.location.*

import com.google.android.gms.tasks.OnSuccessListener

import com.google.android.material.textfield.TextInputLayout

import com.google.gson.JsonElement
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class ClaimBusinessActivity : AppCompatActivity(), View.OnClickListener {
    private val DEFAULT_ZOOM = 17.0f
    private var mFusedLocationProviderClient: FusedLocationProviderClient? = null
    private var mLastLocation: Location? = null
    private var locationRequest: LocationRequest? = null
    private var mLocationCallback: LocationCallback? = null
    private var mCountDownTimer: CountDownTimer? = null
    private var mTimerRunning = false
    private var mTimeLeftInMillis = START_TIME_IN_MILLIS
    var timerTextView: TextView? = null
    var resendButton: Button? = null
    var backImageView: ImageView? = null
    var parentLayout: ConstraintLayout? = null
    var claimBusiness: ClaimBusiness? = null
    var verifyButton: Button? = null
    var imagesRecyclerView: RecyclerView? = null
    var saveImageView: ImageView? = null
    var milesTextView: TextView? = null
    var google_rating_bar: RatingBar? = null
    var yelp_rating_bar: RatingBar? = null
    var businessName_tv: TextView? = null
    var cuisine_tv: TextView? = null
    var address_tv: TextView? = null
    var otpTIL: TextInputLayout? = null
    var otp_et: EditText? = null
    var businessId: String? = null
    var otpString: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_claim)
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        businessId = intent.getStringExtra(AppConstant.BUSINESS_ID)
        initView()
        startTimer()
        // dispatchToClaimBusinessService();
        checkLocationPermission()
    }

    fun initView() {
        parentLayout = findViewById(R.id.parentLayout)
        parentLayout?.setVisibility(View.GONE)
        backImageView = findViewById(R.id.backImageView)
        verifyButton = findViewById(R.id.verifyButton)
        resendButton = findViewById(R.id.resendButton)
        verifyButton?.setOnClickListener(this)
        resendButton?.setOnClickListener(this)
        backImageView?.setOnClickListener(this)
        imagesRecyclerView = findViewById<View>(R.id.imagesRecyclerView) as RecyclerView
        saveImageView = findViewById<View>(R.id.saveImageView) as ImageView
        saveImageView!!.setOnClickListener(this)
        milesTextView = findViewById<View>(R.id.milesTextView) as TextView
        businessName_tv = findViewById<View>(R.id.businessName_tv) as TextView
        address_tv = findViewById<View>(R.id.address_tv) as TextView
        cuisine_tv = findViewById<View>(R.id.cuisine_tv) as TextView
        yelp_rating_bar = findViewById<View>(R.id.yelp_rating_bar) as RatingBar
        google_rating_bar = findViewById<View>(R.id.google_rating_bar) as RatingBar
        otp_et = findViewById<View>(R.id.otp_et) as EditText
        otpTIL = findViewById(R.id.otpTIL)
        timerTextView = findViewById(R.id.timerTextView)
        milesTextView!!.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.verifyButton -> dispatchToBusinessVerifyService()
            R.id.resendButton -> dispatchToResendOTPService()
            R.id.backImageView -> finish()
            R.id.saveImageView -> goForAddFavoriteService()
            R.id.milesTextView -> AppUtil.dispatchToGoogleMap(
                this@ClaimBusinessActivity,
                claimBusiness!!.latitude,
                claimBusiness!!.longitude
            )
        }
    }

    override fun onStop() {
        super.onStop()
        mCountDownTimer!!.cancel()
    }

    private fun startTimer() {
        mTimerRunning = true
        if (mCountDownTimer != null) {
            mCountDownTimer!!.cancel()
            mCountDownTimer!!.start()
        } else {
            mCountDownTimer = object : CountDownTimer(mTimeLeftInMillis, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    mTimeLeftInMillis = millisUntilFinished
                    updateCountDownText()
                }

                override fun onFinish() {
                    mTimerRunning = false
                    updateCountDownText()
                }
            }.start()
        }
    }

    private fun updateCountDownText() {
        val minutes = (mTimeLeftInMillis / 1000).toInt() / 60
        val seconds = (mTimeLeftInMillis / 1000).toInt() % 60
        val timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
        timerTextView!!.text = timeLeftFormatted
        if (mTimerRunning) {
            resendButton!!.visibility = View.INVISIBLE
            timerTextView!!.visibility = View.VISIBLE
        } else {
            AppUtil.dismissProgressDialog()
            resendButton!!.visibility = View.VISIBLE
            timerTextView!!.visibility = View.INVISIBLE
        }
    }

    private fun dispatchToClaimBusinessService() {
        if (AppUtil.isNetworkAvailable(this)) {
            claimDetailsBusinessService()
        } else {
            AppUtil.showConnectionError(this)
        }
    }

    fun claimDetailsBusinessService() {
        AppUtil.showProgressDialog(this@ClaimBusinessActivity)
        val token = SharedPreferenceWriter.getInstance(this)?.getString(SharedPrefsKey.TOKEN)
        val apiServiceInterface = ApiClient.instance.client
        val call = apiServiceInterface.claimBusinessDetail(
            token,
            businessId,
            mLastLocation!!.latitude.toString(),
            mLastLocation!!.longitude.toString()
        )
        call.enqueue(object : Callback<ClaimBusinessResponse?> {
            override fun onResponse(
                call: Call<ClaimBusinessResponse?>,
                response: Response<ClaimBusinessResponse?>
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
                            parentLayout!!.visibility = View.VISIBLE
                            claimBusiness = businessResponse.claimBusiness
                            updateUI()
                        } else {
                            if (businessResponse.message.contains(AppConstant.SESSION_EXPIRED)) {
                                AppUtil.showLogoutDialog(
                                    this@ClaimBusinessActivity,
                                    this@ClaimBusinessActivity.resources.getString(R.string.error),
                                    businessResponse.message
                                )
                            } else {
                                AppUtil.showErrorDialog(
                                    this@ClaimBusinessActivity,
                                    this@ClaimBusinessActivity.resources.getString(R.string.error),
                                    businessResponse.message
                                )
                            }
                        }
                    } else {
                        AppUtil.showLogoutDialog(
                            this@ClaimBusinessActivity,
                            this@ClaimBusinessActivity.resources.getString(R.string.error),
                            "Something went wrong."
                        )
                    }
                } catch (e: Exception) {
                    AppUtil.showErrorDialog(
                        this@ClaimBusinessActivity,
                        this@ClaimBusinessActivity.resources.getString(R.string.error),
                        e.message
                    )
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<ClaimBusinessResponse?>, t: Throwable) {
                AppUtil.dismissProgressDialog()
                call.cancel()
                AppUtil.showErrorDialog(
                    this@ClaimBusinessActivity,
                    this@ClaimBusinessActivity.resources.getString(R.string.error),
                    t.message
                )
            }
        })
    }

    private fun updateUI() {
        businessName_tv!!.text = claimBusiness!!.name
        address_tv!!.text = claimBusiness!!.address
        /*here we are making cuisine String */cuisine_tv!!.text = AppUtil.getCuisineString(
            claimBusiness!!.category
        )
        yelp_rating_bar!!.rating = claimBusiness!!.yelpRating.toFloat()
        google_rating_bar!!.rating = claimBusiness!!.googleRating.toFloat()
        milesTextView!!.text = claimBusiness!!.distance + " " + AppConstant.MILES
        if (claimBusiness!!.favStatus == 1) {
            saveImageView!!.setImageDrawable(getDrawable(R.drawable.save))
        } else {
            saveImageView!!.setImageDrawable(getDrawable(R.drawable.unsaved))
        }
        setRecipesImagesList(claimBusiness!!.image)
    }

    fun setRecipesImagesList(imagesList: List<String>) {
        val adapter = RecipesImagesAdapter(this@ClaimBusinessActivity, imagesList.toMutableList())
        val llm = LinearLayoutManager(this@ClaimBusinessActivity)
        llm.orientation = LinearLayoutManager.HORIZONTAL
        imagesRecyclerView!!.layoutManager = llm
        imagesRecyclerView!!.adapter = adapter
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

    private fun dispatchToBusinessVerifyService() {
        if (AppUtil.isNetworkAvailable(this)) {
            if (validate()) {
                businessVerifyService()
            }
        } else {
            AppUtil.showConnectionError(this)
        }
    }

    fun businessVerifyService() {
        AppUtil.showProgressDialog(this@ClaimBusinessActivity)
        val token = SharedPreferenceWriter.getInstance(this)?.getString(SharedPrefsKey.TOKEN)
        val apiServiceInterface = ApiClient.instance.client
        val call = apiServiceInterface.businessVerify(token, businessId, otpString)
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
                            showDialog()
                        } else {
                            if (`object`.getString("message")
                                    .contains(AppConstant.SESSION_EXPIRED)
                            ) {
                                AppUtil.showLogoutDialog(
                                    this@ClaimBusinessActivity,
                                    this@ClaimBusinessActivity.resources.getString(R.string.error),
                                    `object`.getString("message")
                                )
                            } else {
                                AppUtil.showErrorDialog(
                                    this@ClaimBusinessActivity,
                                    this@ClaimBusinessActivity.resources.getString(R.string.error),
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
                    this@ClaimBusinessActivity,
                    this@ClaimBusinessActivity.resources.getString(R.string.error),
                    t.message
                )
            }
        })
    }

    private fun validate(): Boolean {
        otp_et!!.addTextChangedListener(TextWatcher(otpTIL!!, otp_et!!))
        otpString = otp_et!!.text.toString()
        if (TextUtils.isEmpty(otpString)) {
            otpTIL!!.error = "Please enter OTP."
            return false
        }
        if (otpString!!.length != 6) {
            otpTIL!!.error = "Please enter valid OTP."
            return false
        }
        return true
    }

    private fun showDialog() {
        BusinessClaimedDialog(this@ClaimBusinessActivity) { dispatchToDetailsScreen() }.show()
    }

    private fun dispatchToDetailsScreen() {
        val intent = Intent(this@ClaimBusinessActivity, DetailsActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        intent.putExtra(AppConstant.BUSINESS_ID, businessId)
        intent.putExtra(AppConstant.FROM, TAG)
        startActivity(intent)
        finish()
    }

    private fun dispatchToResendOTPService() {
        if (AppUtil.isNetworkAvailable(this)) {
            resendOTPService()
            startTimer()
        } else {
            AppUtil.showConnectionError(this)
        }
    }

    fun resendOTPService() {
        AppUtil.showProgressDialog(this@ClaimBusinessActivity)
        val apiServiceInterface = ApiClient.instance.client
        val call = apiServiceInterface.resendOTP(businessId)
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
                        } else {
                            if (`object`.getString("message")
                                    .contains(AppConstant.SESSION_EXPIRED)
                            ) {
                                AppUtil.showLogoutDialog(
                                    this@ClaimBusinessActivity,
                                    this@ClaimBusinessActivity.resources.getString(R.string.error),
                                    `object`.getString("message")
                                )
                            } else {
                                AppUtil.showErrorDialog(
                                    this@ClaimBusinessActivity,
                                    this@ClaimBusinessActivity.resources.getString(R.string.error),
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
                    this@ClaimBusinessActivity,
                    this@ClaimBusinessActivity.resources.getString(R.string.error),
                    t.message
                )
            }
        })
    }

    fun showSuccessDialog(msg: String?) {
        CustomSuccessDialog(this, "Success!", msg) {
            //
        }.show()
    }

    fun goForAddFavoriteService() {
        if (AppUtil.isNetworkAvailable(this)) {
            val commonApi = CommonAPI(this@ClaimBusinessActivity, businessId)
            commonApi.addFavoriteService { status ->
                claimBusiness!!.favStatus = status
                updateFavoriteImage()
            }
        } else {
            AppUtil.showConnectionError(this)
        }
    }

    fun updateFavoriteImage() {
        if (claimBusiness!!.favStatus == 1) {
            saveImageView!!.setImageDrawable(getDrawable(R.drawable.save))
        } else {
            saveImageView!!.setImageDrawable(getDrawable(R.drawable.unsaved))
        }
    }

    private fun checkLocationPermission() {
        locationRequest = LocationRequest.create()
       locationRequest?.let {locationRequest->
            locationRequest.setInterval(10000)
            locationRequest.setFastestInterval(5000)
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
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
                        e.startResolutionForResult(this@ClaimBusinessActivity, REQUEST_CHECK_SETTINGS)
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
                        dispatchToClaimBusinessService()
                    } else {
                        createNewLocationRequest()
                    }
                } else {
                    Toast.makeText(
                        this@ClaimBusinessActivity,
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
                    dispatchToClaimBusinessService()
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
        const val TAG = "ClaimBusinessActivity"
        private const val REQUEST_CHECK_SETTINGS = 3456
        private const val START_TIME_IN_MILLIS: Long = 120000
    }
}