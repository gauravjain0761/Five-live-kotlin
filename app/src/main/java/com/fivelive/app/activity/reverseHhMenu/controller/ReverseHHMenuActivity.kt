package com.fivelive.app.activity.reverseHhMenu.controller

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.fivelive.app.Model.*
import com.fivelive.app.R
import com.fivelive.app.activity.happyHoursMenu.fragment.BusinessHhMenuFragment
import com.fivelive.app.activity.reverseHhMenu.fragment.BusinessReverseHHDetailsFragment
import com.fivelive.app.activity.reverseHhMenu.fragment.BusinessReverseHhItemFragment
import com.fivelive.app.activity.reverseHhMenu.fragment.ReverseHhMenuBusinessFragment
import com.fivelive.app.prefs.SharedPreferenceWriter
import com.fivelive.app.prefs.SharedPrefsKey
import com.fivelive.app.retrofit.ApiClient
import com.fivelive.app.util.AppConstant
import com.fivelive.app.util.AppUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ReverseHHMenuActivity : AppCompatActivity(), View.OnClickListener {
    var backImageView: ImageView? = null
    var reverseHHMenuTextView: TextView? = null
    var reverseHHDetailsTextView: TextView? = null
    var reverseHHItemsTextView: TextView? = null
    var yelp_rating_bar: RatingBar? = null
    var google_rating_bar: RatingBar? = null
    var businessName_tv: TextView? = null
    var cuisine_tv: TextView? = null
    var address_tv: TextView? = null
    var businessId: String? = null
    var menuDetails: MenuDetails? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reverse_happy_hours__menu)
        businessId = intent.getStringExtra(AppConstant.BUSINESS_ID)
        initView()
        dispatchToReverseHHMenuDetailsService()
    }

    private fun initView() {
        businessName_tv = findViewById(R.id.businessName_tv)
        cuisine_tv = findViewById(R.id.cuisine_tv)
        address_tv = findViewById(R.id.address_tv)
        yelp_rating_bar = findViewById(R.id.yelp_rating_bar)
        google_rating_bar = findViewById(R.id.google_rating_bar)
        backImageView = findViewById(R.id.backImageView)
        reverseHHMenuTextView = findViewById(R.id.reverseHHMenuTextView)
        reverseHHDetailsTextView = findViewById(R.id.reverseHHDetailsTextView)
        reverseHHItemsTextView = findViewById(R.id.reverseHHItemsTextView)
        reverseHHMenuTextView?.setOnClickListener(this)
        reverseHHDetailsTextView?.setOnClickListener(this)
        reverseHHItemsTextView?.setOnClickListener(this)
        backImageView?.setOnClickListener(this)
    }

    fun showReverseHhMenuBusinessFragment() {
        val bundle = Bundle()
        val reverseHHMenuArrayList = ArrayList<HHMenu>()
        reverseHHMenuArrayList.addAll(menuDetails!!.reverseMenu)
        bundle.putParcelableArrayList(AppConstant.MENU_LIST, reverseHHMenuArrayList)
        val fragment = ReverseHhMenuBusinessFragment()
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
            R.id.restaurant_reverse_hh_container,
            fragment,
            BusinessHhMenuFragment.Companion.TAG
        )
        fragmentTransaction.commit()
    }

    fun showRestaurantHhItemFragment() {
        val fragment = BusinessReverseHhItemFragment()
        val bundle = Bundle()
        val categoryArrayList = ArrayList<Category>()
        categoryArrayList.addAll(menuDetails!!.reverseHHItems.category)
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
            R.id.restaurant_reverse_hh_container,
            fragment,
            BusinessHhMenuFragment.Companion.TAG
        )
        fragmentTransaction.commit()
    }

    fun showBusinessReverseHHDetailsFragment() {
        val fragment = BusinessReverseHHDetailsFragment()
        val bundle = Bundle()
        val reverseHHMenuArrayList = ArrayList<HHDetails>()
        reverseHHMenuArrayList.addAll(menuDetails!!.reverseHHDetails)
        bundle.putParcelableArrayList(AppConstant.DETAILS_LIST, reverseHHMenuArrayList)
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
            R.id.restaurant_reverse_hh_container,
            fragment,
            BusinessHhMenuFragment.Companion.TAG
        )
        fragmentTransaction.commit()
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.backImageView -> finish()
            R.id.reverseHHMenuTextView -> {
                selectedHHMenu()
                showReverseHhMenuBusinessFragment()
            }
            R.id.reverseHHDetailsTextView -> {
                selectedHHDetails()
                showBusinessReverseHHDetailsFragment()
            }
            R.id.reverseHHItemsTextView -> {
                selectedHHItems()
                showRestaurantHhItemFragment()
            }
        }
    }

    private fun selectedHHMenu() {
        reverseHHMenuTextView!!.setTextColor(resources.getColor(R.color.white))
        reverseHHDetailsTextView!!.setTextColor(resources.getColor(R.color.light_black))
        reverseHHItemsTextView!!.setTextColor(resources.getColor(R.color.light_black))
        reverseHHMenuTextView!!.background = resources.getDrawable(R.drawable.blue_btn_bg)
        reverseHHDetailsTextView!!.background =
            resources.getDrawable(R.drawable.un_selected_hh_details_bg)
        reverseHHItemsTextView!!.background =
            resources.getDrawable(R.drawable.un_selected_hh_details_bg)
    }

    private fun selectedHHDetails() {
        reverseHHDetailsTextView!!.setTextColor(resources.getColor(R.color.white))
        reverseHHMenuTextView!!.setTextColor(resources.getColor(R.color.light_black))
        reverseHHItemsTextView!!.setTextColor(resources.getColor(R.color.light_black))
        reverseHHDetailsTextView!!.background = resources.getDrawable(R.drawable.blue_btn_bg)
        reverseHHMenuTextView!!.background =
            resources.getDrawable(R.drawable.un_selected_hh_details_bg)
        reverseHHItemsTextView!!.background =
            resources.getDrawable(R.drawable.un_selected_hh_details_bg)
    }

    private fun selectedHHItems() {
        reverseHHItemsTextView!!.setTextColor(resources.getColor(R.color.white))
        reverseHHMenuTextView!!.setTextColor(resources.getColor(R.color.light_black))
        reverseHHDetailsTextView!!.setTextColor(resources.getColor(R.color.light_black))
        reverseHHItemsTextView!!.background = resources.getDrawable(R.drawable.blue_btn_bg)
        reverseHHMenuTextView!!.background =
            resources.getDrawable(R.drawable.un_selected_hh_details_bg)
        reverseHHDetailsTextView!!.background =
            resources.getDrawable(R.drawable.un_selected_hh_details_bg)
    }

    private fun dispatchToReverseHHMenuDetailsService() {
        AppUtil.hideKeyboard(this)
        if (AppUtil.isNetworkAvailable(this@ReverseHHMenuActivity)) {
            reverseHHMenuDetailsService()
        } else {
            AppUtil.showConnectionError(this@ReverseHHMenuActivity)
        }
    }

    private fun reverseHHMenuDetailsService() {
        AppUtil.showProgressDialog(this@ReverseHHMenuActivity)
        val token = SharedPreferenceWriter.getInstance(this@ReverseHHMenuActivity)
            .getString(SharedPrefsKey.TOKEN)
        val apiServiceInterface = ApiClient.instance.client
        val call = apiServiceInterface.reverseHHMenuDetails(token, businessId)
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
                                this@ReverseHHMenuActivity,
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
                    this@ReverseHHMenuActivity,
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
        showReverseHhMenuBusinessFragment()
    }
}