package com.fivelive.app.fragment

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.fivelive.app.R
import com.fivelive.app.activity.*
import com.fivelive.app.app.FiveLiveApplication
import com.fivelive.app.dialog.LogoutConfirmationDialog
import com.fivelive.app.retrofit.ApiClient
import com.fivelive.app.util.AppUtil

class MyAccountFragment : Fragment(), View.OnClickListener {
    var activity: Activity? = null
    var backImageView: ImageView? = null
    var myProfile: TextView? = null
    var fiveLivePremium: TextView? = null
    var myAddresses: TextView? = null
    var settings: TextView? = null
    var about_five_live: TextView? = null
    var termsAndCondition: TextView? = null
    var privacyPolicy: TextView? = null
    var contactUsTextView: TextView? = null
    var fAQsTextView: TextView? = null
    var logout: TextView? = null
    var owner_name_tv: TextView? = null
    var business_name_tv: TextView? = null
    var category_tv: TextView? = null
    var yelp_rating_bar: RatingBar? = null
    var google_rating_bar: RatingBar? = null
    var business_imv: ImageView? = null
    var business_owner_cl: ConstraintLayout? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity = getActivity()
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        business_owner_cl = view.findViewById(R.id.business_owner_cl)
        business_imv = view.findViewById(R.id.business_imv)
        owner_name_tv = view.findViewById(R.id.owner_name_tv)
        business_name_tv = view.findViewById(R.id.business_name_tv)
        category_tv = view.findViewById(R.id.category_tv)
        yelp_rating_bar = view.findViewById(R.id.yelp_rating_bar)
        google_rating_bar = view.findViewById(R.id.google_rating_bar)
        backImageView = view.findViewById(R.id.backImageView)
        backImageView?.setVisibility(View.GONE)
        myProfile = view.findViewById(R.id.myProfile)
        fiveLivePremium = view.findViewById(R.id.fiveLivePremium)
        myAddresses = view.findViewById(R.id.myAddresses)
        settings = view.findViewById(R.id.settings)
        about_five_live = view.findViewById(R.id.about_five_live)
        termsAndCondition = view.findViewById(R.id.termsAndCondition)
        privacyPolicy = view.findViewById(R.id.privacyPolicy)
        fAQsTextView = view.findViewById(R.id.fAQsTextView)
        contactUsTextView = view.findViewById(R.id.contactUsTextView)
        logout = view.findViewById(R.id.logout)
        myProfile?.setOnClickListener(this)
        fiveLivePremium?.setOnClickListener(this)
        myAddresses?.setOnClickListener(this)
        settings?.setOnClickListener(this)
        about_five_live?.setOnClickListener(this)
        termsAndCondition?.setOnClickListener(this)
        privacyPolicy?.setOnClickListener(this)
        fAQsTextView?.setOnClickListener(this)
        contactUsTextView?.setOnClickListener(this)
        logout?.setOnClickListener(this)
        updateUI()
    }

    private fun updateUI() {
        val detail = FiveLiveApplication.claimDetail
        if (detail == null || detail.name == null) {
            business_owner_cl!!.visibility = View.GONE
            return
        }
        business_owner_cl!!.visibility = View.VISIBLE
        if (detail.firstName != null && detail.lastName != null) {
            owner_name_tv!!.text = detail.firstName + " " + detail.lastName
        }
        business_name_tv!!.text = detail.name
        category_tv!!.text = AppUtil.getCuisineString(detail.category)
        if (detail.yelpRating != null) {
            yelp_rating_bar!!.rating = detail.yelpRating!!.toFloat()
        }
        if (detail.googleRating != null) {
            google_rating_bar!!.rating = detail.googleRating!!.toFloat()
        }
        AppUtil.loadHorizontalSmallImage(activity, detail.image, business_imv)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.myProfile -> dispatchToMyProfileScreen()
            R.id.fiveLivePremium -> dispatchToSubscriptionScreen()
            R.id.myAddresses -> dispatchToMyAddressesActivity()
            R.id.settings -> dispatchToSettingsActvity()
            R.id.about_five_live -> AppUtil.dispatchToAboutUsActivity(requireContext())
            R.id.termsAndCondition -> AppUtil.dispatchToTermANdConditionActivity(requireContext())
            R.id.privacyPolicy -> AppUtil.dispatchToPrivacyPolicy(requireContext())
            R.id.fAQsTextView -> dispatchToFaqsActivity()
            R.id.contactUsTextView -> dispatchToContactUsActivity()
            R.id.logout -> showLogoutConfirmationDialog()
        }
    }

    fun dispatchToContactUsActivity() {
        startActivity(Intent(activity, ContactUsActivity::class.java))
    }

    fun showLogoutConfirmationDialog() {
        LogoutConfirmationDialog(activity) { AppUtil.logout(requireContext()) }.show()
    }

    private fun dispatchToMyProfileScreen() {
        startActivity(Intent(activity, ProfileActivity::class.java))
    }

    private fun dispatchToSubscriptionScreen() {
        startActivity(Intent(activity, SubscriptionActivity::class.java))
    }

    private fun dispatchToMyAddressesActivity() {
        startActivity(Intent(activity, MySavedAddressActivity::class.java))
    }

    private fun dispatchToSettingsActvity() {
        startActivity(Intent(activity, SettingsActivity::class.java))
    }

    private fun dispatchToAboutUsActivity() {
        // startActivity(new Intent(activity, AboutusActivity.class));
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(ApiClient.Base_URL + "about"))
        startActivity(browserIntent)
    }

    private fun dispatchToTermANdConditionActivity() {
        // startActivity(new Intent(activity, TermAndConditionActivity.class));
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(ApiClient.Base_URL + "terms"))
        startActivity(browserIntent)
    }

    private fun dispatchToPrivacyPolicy() {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(ApiClient.Base_URL + "policy"))
        startActivity(browserIntent)
        // startActivity(new Intent(activity, PrivacyPolicyActivity.class));
    }

    private fun dispatchToFaqsActivity() {
        startActivity(Intent(activity, FaqsActivity::class.java))
    }

    companion object {
        val TAG = MyAccountFragment::class.java.name
    }
}