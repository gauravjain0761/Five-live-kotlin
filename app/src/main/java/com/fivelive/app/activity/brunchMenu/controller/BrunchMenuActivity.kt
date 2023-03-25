package com.fivelive.app.activity.brunchMenu.controller

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.fivelive.app.Model.CommonBusinessResponse
import com.fivelive.app.Model.HHDetails
import com.fivelive.app.Model.HHMenu
import com.fivelive.app.Model.MenuDetails
import com.fivelive.app.R
import com.fivelive.app.activity.brunchMenu.fragment.BusinessBrunchDetailsFragment
import com.fivelive.app.activity.brunchMenu.fragment.RestaurantBrunchMenuFragment
import com.fivelive.app.activity.happyHoursMenu.fragment.BusinessHhMenuFragment
import com.fivelive.app.prefs.SharedPreferenceWriter
import com.fivelive.app.prefs.SharedPrefsKey
import com.fivelive.app.retrofit.ApiClient
import com.fivelive.app.util.AppConstant
import com.fivelive.app.util.AppUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BrunchMenuActivity : AppCompatActivity(), View.OnClickListener {
    var backImageView: ImageView? = null
    var brunchMenuTextView: TextView? = null
    var brunchDetailsTextView: TextView? = null
    var yelp_rating_bar: RatingBar? = null
    var google_rating_bar: RatingBar? = null
    var businessName_tv: TextView? = null
    var cuisine_tv: TextView? = null
    var address_tv: TextView? = null
    var menuDetails: MenuDetails? = null
    var businessId: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant_brunch_menu)
        businessId = intent.getStringExtra(AppConstant.BUSINESS_ID)
        initView()
        dispatchToBrunchMenuDetailsService()
    }

    fun initView() {
        businessName_tv = findViewById(R.id.businessName_tv)
        cuisine_tv = findViewById(R.id.cuisine_tv)
        address_tv = findViewById(R.id.address_tv)
        yelp_rating_bar = findViewById(R.id.yelp_rating_bar)
        google_rating_bar = findViewById(R.id.google_rating_bar)
        backImageView = findViewById(R.id.backImageView)
        brunchMenuTextView = findViewById(R.id.brunchMenuTextView)
        brunchDetailsTextView = findViewById(R.id.brunchDetailsTextView)
        brunchMenuTextView?.setOnClickListener(this)
        brunchDetailsTextView?.setOnClickListener(this)
        backImageView?.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.backImageView -> finish()
            R.id.brunchMenuTextView -> {
                selectedBrunchMenu()
                showBrunchMenuFragment()
            }
            R.id.brunchDetailsTextView -> {
                selectedBrunchDetails()
                showBrunchDetailsFragment()
            }
        }
    }

    private fun selectedBrunchMenu() {
        brunchMenuTextView!!.setTextColor(resources.getColor(R.color.white))
        brunchDetailsTextView!!.setTextColor(resources.getColor(R.color.light_black))
        brunchMenuTextView!!.background = resources.getDrawable(R.drawable.blue_btn_bg)
        brunchDetailsTextView!!.background =
            resources.getDrawable(R.drawable.un_selected_hh_details_bg)
    }

    private fun selectedBrunchDetails() {
        brunchDetailsTextView!!.setTextColor(resources.getColor(R.color.white))
        brunchMenuTextView!!.setTextColor(resources.getColor(R.color.light_black))
        brunchDetailsTextView!!.background = resources.getDrawable(R.drawable.blue_btn_bg)
        brunchMenuTextView!!.background =
            resources.getDrawable(R.drawable.un_selected_hh_details_bg)
    }

    fun showBrunchMenuFragment() {
        val fragment = RestaurantBrunchMenuFragment()
        val bundle = Bundle()
        val hhMenuArrayList = ArrayList<HHMenu>()
        hhMenuArrayList.addAll(menuDetails!!.brunchMenu)
        bundle.putParcelableArrayList(AppConstant.MENU_LIST, hhMenuArrayList)
        fragment.arguments = bundle
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.setCustomAnimations(
            R.anim.enter_from_left,
            R.anim.exit_to_right,
            R.anim.enter_from_right,
            R.anim.exit_to_left
        )
        fragmentTransaction.replace(
            R.id.restaurant_brunch_menu_container,
            fragment,
            BusinessHhMenuFragment.Companion.TAG
        )
        fragmentTransaction.commit()
    }

    fun showBrunchDetailsFragment() {
        val fragment = BusinessBrunchDetailsFragment()
        val bundle = Bundle()
        val hhDetailsArrayList = ArrayList<HHDetails>()
        hhDetailsArrayList.addAll(menuDetails!!.brunchDetails)
        bundle.putParcelableArrayList(AppConstant.DETAILS_LIST, hhDetailsArrayList)
        fragment.arguments = bundle
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.setCustomAnimations(
            R.anim.enter_from_right,
            R.anim.exit_to_left,
            R.anim.enter_from_left,
            R.anim.exit_to_right
        )
        fragmentTransaction.replace(
            R.id.restaurant_brunch_menu_container,
            fragment,
            BusinessHhMenuFragment.Companion.TAG
        )
        fragmentTransaction.commit()
    }

    private fun dispatchToBrunchMenuDetailsService() {
        AppUtil.hideKeyboard(this)
        if (AppUtil.isNetworkAvailable(this@BrunchMenuActivity)) {
            brunchMenuDetailsService()
        } else {
            AppUtil.showConnectionError(this@BrunchMenuActivity)
        }
    }

    private fun brunchMenuDetailsService() {
        AppUtil.showProgressDialog(this@BrunchMenuActivity)
        val token = SharedPreferenceWriter.getInstance(this@BrunchMenuActivity)
            .getString(SharedPrefsKey.TOKEN)
        val apiServiceInterface = ApiClient.instance.client
        val call = apiServiceInterface.brunchMenuDetails(token, businessId)
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
                                this@BrunchMenuActivity,
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
                    this@BrunchMenuActivity,
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
        showBrunchMenuFragment()
    }
}