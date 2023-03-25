package com.fivelive.app.activity.amenities.controller

import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fivelive.app.Model.AmenitiesResponse
import com.fivelive.app.Model.BusinessAmenities
import com.fivelive.app.R
import com.fivelive.app.activity.amenities.adapter.BizAmenitiesAdapter
import com.fivelive.app.prefs.SharedPreferenceWriter
import com.fivelive.app.prefs.SharedPrefsKey
import com.fivelive.app.readMoreFunctionality.ExpandableTextView
import com.fivelive.app.retrofit.ApiClient
import com.fivelive.app.util.AppConstant
import com.fivelive.app.util.AppUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AmenitiesActivity : AppCompatActivity(), View.OnClickListener {
    var backImageView: ImageView? = null
    var yelp_rating_bar: RatingBar? = null
    var google_rating_bar: RatingBar? = null
    var businessName_tv: TextView? = null
    var cuisine_tv: TextView? = null
    var address_tv: TextView? = null
    var des_tv: TextView? = null
    var recyclerView: RecyclerView? = null
    var businessAmenities: BusinessAmenities? = null
    var businessId: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant_amenities)
        businessId = intent.getStringExtra(AppConstant.BUSINESS_ID)
        initView()
        dispatchToBusinessAmenitiesService()
    }

    private fun initView() {
        businessName_tv = findViewById(R.id.businessName_tv)
        cuisine_tv = findViewById(R.id.cuisine_tv)
        address_tv = findViewById(R.id.address_tv)
        des_tv = findViewById(R.id.des_tv)
        yelp_rating_bar = findViewById(R.id.yelp_rating_bar)
        google_rating_bar = findViewById(R.id.google_rating_bar)
        backImageView = findViewById(R.id.backImageView)
        recyclerView = findViewById(R.id.recyclerView)
        backImageView?.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.backImageView -> finish()
        }
    }

    private fun dispatchToBusinessAmenitiesService() {
        AppUtil.hideKeyboard(this)
        if (AppUtil.isNetworkAvailable(this@AmenitiesActivity)) {
            businessAmenitiesService()
        } else {
            AppUtil.showConnectionError(this@AmenitiesActivity)
        }
    }

    private fun businessAmenitiesService() {
        AppUtil.showProgressDialog(this@AmenitiesActivity)
        val token = SharedPreferenceWriter.getInstance(this@AmenitiesActivity)
            .getString(SharedPrefsKey.TOKEN)
        val apiServiceInterface = ApiClient.instance.client
        val call = apiServiceInterface.businessInfo(token, businessId)
        call.enqueue(object : Callback<AmenitiesResponse?> {
            override fun onResponse(
                call: Call<AmenitiesResponse?>,
                response: Response<AmenitiesResponse?>
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
                            businessAmenities = businessResponse.businessAmenities
                            updateUI()
                        } else {
                            AppUtil.showErrorDialog(
                                this@AmenitiesActivity,
                                resources.getString(R.string.error),
                                businessResponse.message
                            )
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<AmenitiesResponse?>, t: Throwable) {
                AppUtil.dismissProgressDialog()
                call.cancel()
                AppUtil.showErrorDialog(
                    this@AmenitiesActivity,
                    resources.getString(R.string.error),
                    t.message
                )
            }
        })
    }

    private fun updateUI() {
        businessName_tv!!.text = businessAmenities!!.businessName
        yelp_rating_bar!!.rating = businessAmenities!!.yelpRating.toFloat()
        google_rating_bar!!.rating = businessAmenities!!.googleRating.toFloat()
        val address = if (businessAmenities!!.address == null) "" else businessAmenities!!.address
        val city = if (businessAmenities!!.city == null) "" else businessAmenities!!.city
        val state = if (businessAmenities!!.state == null) "" else businessAmenities!!.state
        val zipCode = if (businessAmenities!!.zipcode == null) "" else businessAmenities!!.zipcode
        val country = if (businessAmenities!!.country == null) "" else businessAmenities!!.country
        address_tv!!.text = "$address,$city,$state,$zipCode,$country"
        /*Cuisine String here*/cuisine_tv!!.text = AppUtil.getCuisineString(
            businessAmenities!!.cuisines
        )
        if (businessAmenities!!.description != null && businessAmenities!!.description != "") {
            des_tv!!.text = Html.fromHtml(businessAmenities!!.description)
            if (businessAmenities!!.description.length > AppConstant.EXPENDABLE_TEXT_LENGTH) {
                ExpandableTextView.makeTextViewResizable(des_tv!!, 3, AppConstant.SEE_MORE, true)
            }
        }
        setDataInList()
    }

    fun setDataInList() {
        val adapter = BizAmenitiesAdapter(this@AmenitiesActivity, businessAmenities!!.amenities)
        val manager =
            GridLayoutManager(this@AmenitiesActivity, 3, GridLayoutManager.VERTICAL, false)
        recyclerView!!.layoutManager = manager
        recyclerView!!.adapter = adapter
    }
}