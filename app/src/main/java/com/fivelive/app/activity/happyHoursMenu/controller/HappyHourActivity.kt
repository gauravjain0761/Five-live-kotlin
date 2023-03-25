package com.fivelive.app.activity.happyHoursMenu.controller

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.fivelive.app.Model.*
import com.fivelive.app.R
import com.fivelive.app.activity.happyHoursMenu.fragment.BusinessHHDetailsFragment
import com.fivelive.app.activity.happyHoursMenu.fragment.BusinessHHItemFragment
import com.fivelive.app.activity.happyHoursMenu.fragment.BusinessHhMenuFragment
import com.fivelive.app.prefs.SharedPreferenceWriter
import com.fivelive.app.prefs.SharedPrefsKey
import com.fivelive.app.retrofit.ApiClient
import com.fivelive.app.util.AppConstant
import com.fivelive.app.util.AppUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HappyHourActivity : AppCompatActivity(), View.OnClickListener {
    var backImageView: ImageView? = null
    var hhMenuTextView: TextView? = null
    var hhDetailsTextView: TextView? = null
    var hhItemsTextView: TextView? = null
    var yelp_rating_bar: RatingBar? = null
    var google_rating_bar: RatingBar? = null
    var businessName_tv: TextView? = null
    var cuisine_tv: TextView? = null
    var address_tv: TextView? = null
    var menuDetails: MenuDetails? = null
    var businessId: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_happy_hour_business)
        businessId = intent.getStringExtra(AppConstant.BUSINESS_ID)
        initView()
        dispatchToHHMenuDetailsService()
    }

    private fun initView() {
        businessName_tv = findViewById(R.id.businessName_tv)
        cuisine_tv = findViewById(R.id.cuisine_tv)
        address_tv = findViewById(R.id.address_tv)
        yelp_rating_bar = findViewById(R.id.yelp_rating_bar)
        google_rating_bar = findViewById(R.id.google_rating_bar)
        backImageView = findViewById(R.id.backImageView)
        hhMenuTextView = findViewById(R.id.hhMenuTextView)
        hhDetailsTextView = findViewById(R.id.hhDetailsTextView)
        hhItemsTextView = findViewById(R.id.hhItemsTextView)
        backImageView?.setOnClickListener(this)
        hhMenuTextView?.setOnClickListener(this)
        hhDetailsTextView?.setOnClickListener(this)
        hhItemsTextView?.setOnClickListener(this)
    }

    fun showBusinessHhMenuFragment() {
        val fragment = BusinessHhMenuFragment()
        val bundle = Bundle()
        val hhMenuArrayList = ArrayList<HHMenu>()
        hhMenuArrayList.addAll(menuDetails!!.hhMenue)
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
            R.id.restaurant_happy_hour_container,
            fragment,
            BusinessHhMenuFragment.Companion.TAG
        )
        fragmentTransaction.commit()
    }

    fun showBusinessHHItemFragment() {
        val fragment = BusinessHHItemFragment()
        val bundle = Bundle()
        val categoryArrayList = ArrayList<Category>()
        categoryArrayList.addAll(menuDetails!!.hhItems.category)
        bundle.putParcelableArrayList(AppConstant.CATEGORY_LIST, categoryArrayList)
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
            R.id.restaurant_happy_hour_container,
            fragment,
            BusinessHhMenuFragment.Companion.TAG
        )
        fragmentTransaction.commit()
    }

    fun showRestaurantHhDetailsFragment() {
        val fragment = BusinessHHDetailsFragment()
        val bundle = Bundle()
        val hhDetailsArrayList = ArrayList<HHDetails>()
        hhDetailsArrayList.addAll(menuDetails!!.hhDetails)
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
            R.id.restaurant_happy_hour_container,
            fragment,
            BusinessHhMenuFragment.Companion.TAG
        )
        fragmentTransaction.commit()
    }

    fun replaceFragment(fragment: Fragment?) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.setCustomAnimations(
            R.anim.enter_from_right,
            R.anim.exit_to_left,
            R.anim.enter_from_left,
            R.anim.exit_to_right
        )
        fragmentTransaction.replace(
            R.id.restaurant_happy_hour_container,
            fragment!!,
            BusinessHhMenuFragment.Companion.TAG
        )
        fragmentTransaction.commit()
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.backImageView -> finish()
            R.id.hhMenuTextView -> {
                selectedHHMenu()
                showBusinessHhMenuFragment()
            }
            R.id.hhDetailsTextView -> {
                selectedHHDetails()
                showRestaurantHhDetailsFragment()
            }
            R.id.hhItemsTextView -> {
                selectedHHItems()
                showBusinessHHItemFragment()
            }
        }
    }

    private fun selectedHHMenu() {
        hhMenuTextView!!.setTextColor(resources.getColor(R.color.white))
        hhDetailsTextView!!.setTextColor(resources.getColor(R.color.light_black))
        hhItemsTextView!!.setTextColor(resources.getColor(R.color.light_black))
        hhMenuTextView!!.background = resources.getDrawable(R.drawable.blue_btn_bg)
        hhDetailsTextView!!.background = resources.getDrawable(R.drawable.un_selected_hh_details_bg)
        hhItemsTextView!!.background = resources.getDrawable(R.drawable.un_selected_hh_details_bg)
    }

    private fun selectedHHDetails() {
        hhDetailsTextView!!.setTextColor(resources.getColor(R.color.white))
        hhMenuTextView!!.setTextColor(resources.getColor(R.color.light_black))
        hhItemsTextView!!.setTextColor(resources.getColor(R.color.light_black))
        hhDetailsTextView!!.background = resources.getDrawable(R.drawable.blue_btn_bg)
        hhMenuTextView!!.background = resources.getDrawable(R.drawable.un_selected_hh_details_bg)
        hhItemsTextView!!.background = resources.getDrawable(R.drawable.un_selected_hh_details_bg)
    }

    private fun selectedHHItems() {
        hhItemsTextView!!.setTextColor(resources.getColor(R.color.white))
        hhMenuTextView!!.setTextColor(resources.getColor(R.color.light_black))
        hhDetailsTextView!!.setTextColor(resources.getColor(R.color.light_black))
        hhItemsTextView!!.background = resources.getDrawable(R.drawable.blue_btn_bg)
        hhMenuTextView!!.background = resources.getDrawable(R.drawable.un_selected_hh_details_bg)
        hhDetailsTextView!!.background = resources.getDrawable(R.drawable.un_selected_hh_details_bg)
    }

    private fun dispatchToHHMenuDetailsService() {
        AppUtil.hideKeyboard(this)
        if (AppUtil.isNetworkAvailable(this@HappyHourActivity)) {
            hhMenuDetailsService()
        } else {
            AppUtil.showConnectionError(this@HappyHourActivity)
        }
    }

    private fun hhMenuDetailsService() {
        AppUtil.showProgressDialog(this@HappyHourActivity)
        val token = SharedPreferenceWriter.getInstance(this@HappyHourActivity)
            .getString(SharedPrefsKey.TOKEN)
        val apiServiceInterface = ApiClient.instance.client
        val call = apiServiceInterface.hhMenuDetails(token, businessId)
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
                                this@HappyHourActivity,
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
                    this@HappyHourActivity,
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
        showBusinessHhMenuFragment()
    }
}