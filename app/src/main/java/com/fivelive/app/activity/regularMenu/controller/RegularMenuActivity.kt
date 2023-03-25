package com.fivelive.app.activity.regularMenu.controller

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.fivelive.app.Model.CommonBusinessResponse
import com.fivelive.app.Model.HHMenu
import com.fivelive.app.Model.MenuDetails
import com.fivelive.app.R
import com.fivelive.app.activity.happyHoursMenu.fragment.BusinessHhMenuFragment
import com.fivelive.app.activity.regularMenu.fragment.RestaurantRegularMenuFragment
import com.fivelive.app.prefs.SharedPreferenceWriter
import com.fivelive.app.prefs.SharedPrefsKey
import com.fivelive.app.retrofit.ApiClient
import com.fivelive.app.util.AppConstant
import com.fivelive.app.util.AppUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegularMenuActivity : AppCompatActivity(), View.OnClickListener {
    var backImageView: ImageView? = null
    var yelp_rating_bar: RatingBar? = null
    var google_rating_bar: RatingBar? = null
    var businessName_tv: TextView? = null
    var cuisine_tv: TextView? = null
    var address_tv: TextView? = null
    var businessId: String? = null
    var menuDetails: MenuDetails? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant_regular_menn)
        businessId = intent.getStringExtra(AppConstant.BUSINESS_ID)
        initView()
        dispatchToRegularMenuDetailsService()
    }

    private fun initView() {
        backImageView = findViewById(R.id.backImageView)
        backImageView?.setOnClickListener(this)
        businessName_tv = findViewById(R.id.businessName_tv)
        cuisine_tv = findViewById(R.id.cuisine_tv)
        address_tv = findViewById(R.id.address_tv)
        yelp_rating_bar = findViewById(R.id.yelp_rating_bar)
        google_rating_bar = findViewById(R.id.google_rating_bar)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.backImageView -> finish()
        }
    }

    private fun dispatchToRegularMenuDetailsService() {
        AppUtil.hideKeyboard(this)
        if (AppUtil.isNetworkAvailable(this@RegularMenuActivity)) {
            regularMenuDetailsService()
        } else {
            AppUtil.showConnectionError(this@RegularMenuActivity)
        }
    }

    private fun regularMenuDetailsService() {
        AppUtil.showProgressDialog(this@RegularMenuActivity)
        val token = SharedPreferenceWriter.getInstance(this@RegularMenuActivity)
            .getString(SharedPrefsKey.TOKEN)
        val apiServiceInterface = ApiClient.instance.client
        val call = apiServiceInterface.regularMenuDetails(token, businessId)
        call.enqueue(object : Callback<CommonBusinessResponse?> {
            override fun onResponse(
                call: Call<CommonBusinessResponse?>,
                response: Response<CommonBusinessResponse?>
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
                            menuDetails = businessResponse.menueDetails
                            updateUI()
                        } else {
                            AppUtil.showErrorDialog(
                                this@RegularMenuActivity,
                                resources.getString(R.string.error),
                                businessResponse.message
                            )
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<CommonBusinessResponse?>, t: Throwable) {
                AppUtil.dismissProgressDialog()
                call.cancel()
                AppUtil.showErrorDialog(
                    this@RegularMenuActivity,
                    resources.getString(R.string.error),
                    t.message
                )
            }
        })
    }

    private fun updateUI() {
        businessName_tv!!.text = menuDetails!!.businessName
        yelp_rating_bar!!.rating = menuDetails!!.yelpRating.toFloat()
        google_rating_bar!!.rating = menuDetails!!.googleRating.toFloat()
        val address = if (menuDetails!!.address == null) "" else menuDetails!!.address
        val city = if (menuDetails!!.city == null) "" else menuDetails!!.city
        val state = if (menuDetails!!.state == null) "" else menuDetails!!.state
        val zipCode = if (menuDetails!!.zipcode == null) "" else menuDetails!!.zipcode
        val country = if (menuDetails!!.country == null) "" else menuDetails!!.country
        address_tv!!.text = "$address,$city,$state,$zipCode,$country"
        /*Cuisine String here*/cuisine_tv!!.text = AppUtil.getCuisineString(menuDetails!!.cuisines)
        showRestaurantRegularMenuFragment()
    }

    fun showRestaurantRegularMenuFragment() {
        val fragment = RestaurantRegularMenuFragment()
        val bundle = Bundle()
        val hhMenuArrayList = ArrayList<HHMenu>()
        hhMenuArrayList.addAll(menuDetails!!.regularMenu)
        bundle.putParcelableArrayList(AppConstant.MENU_LIST, hhMenuArrayList)
        fragment.arguments = bundle
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(
            R.id.restaurant_regular_menu_container,
            fragment,
            BusinessHhMenuFragment.Companion.TAG
        )
        fragmentTransaction.commit()
    }
}