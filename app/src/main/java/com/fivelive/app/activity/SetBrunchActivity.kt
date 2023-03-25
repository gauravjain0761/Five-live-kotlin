package com.fivelive.app.activity

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.fivelive.app.R
import com.fivelive.app.activity.happyHoursMenu.fragment.BusinessHhMenuFragment
import com.fivelive.app.fragment.EditBrunchMenuFragment
import com.fivelive.app.fragment.EditCommonDetailsFragment
import com.fivelive.app.util.AppConstant

class SetBrunchActivity constructor() : AppCompatActivity(), View.OnClickListener {
    var brunchMenuTextView: TextView? = null
    var brunchDetailsTextView: TextView? = null
    var backImageView: ImageView? = null
    var businessId: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_brunch)
        businessId = getIntent().getStringExtra(AppConstant.BUSINESS_ID)
        initView()
        showBrunchMenuFragment()
    }

    fun initView() {
        backImageView = findViewById(R.id.backImageView)
        brunchMenuTextView = findViewById(R.id.brunchMenuTextView)
        brunchDetailsTextView = findViewById(R.id.brunchDetailsTextView)
        brunchMenuTextView?.setOnClickListener(this)
        brunchDetailsTextView?.setOnClickListener(this)
        backImageView?.setOnClickListener(this)
    }

    public override fun onClick(view: View) {
        when (view.getId()) {
            R.id.brunchMenuTextView -> {
                selectedBrunchMenu()
                showBrunchMenuFragment()
            }
            R.id.brunchDetailsTextView -> {
                selectedBrunchDetails()
                showBrunchDetailsFragment()
            }
            R.id.backImageView -> finish()
        }
    }

    private fun selectedBrunchMenu() {
        brunchMenuTextView!!.setTextColor(getResources().getColor(R.color.white))
        brunchDetailsTextView!!.setTextColor(getResources().getColor(R.color.light_black))
        brunchMenuTextView!!.setBackground(getResources().getDrawable(R.drawable.blue_btn_bg))
        brunchDetailsTextView!!.setBackground(getResources().getDrawable(R.drawable.un_selected_hh_details_bg))
    }

    private fun selectedBrunchDetails() {
        brunchDetailsTextView!!.setTextColor(getResources().getColor(R.color.white))
        brunchMenuTextView!!.setTextColor(getResources().getColor(R.color.light_black))
        brunchDetailsTextView!!.setBackground(getResources().getDrawable(R.drawable.blue_btn_bg))
        brunchMenuTextView!!.setBackground(getResources().getDrawable(R.drawable.un_selected_hh_details_bg))
    }

    fun showBrunchMenuFragment() {
        val fragment: EditBrunchMenuFragment = EditBrunchMenuFragment()
        val bundle: Bundle = Bundle()
        bundle.putString(AppConstant.BUSINESS_ID, businessId)
        fragment.setArguments(bundle)
        val fragmentManager: FragmentManager = getSupportFragmentManager()
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.setCustomAnimations(
            R.anim.enter_from_left,
            R.anim.exit_to_right,
            R.anim.enter_from_right,
            R.anim.exit_to_left
        )
        fragmentTransaction.replace(R.id.set_brunch_container, fragment, EditBrunchMenuFragment.TAG)
        fragmentTransaction.commit()
    }

    fun showBrunchDetailsFragment() {
        val fragment: EditCommonDetailsFragment = EditCommonDetailsFragment()
        val bundle: Bundle = Bundle()
        bundle.putString(AppConstant.BUSINESS_ID, businessId)
        bundle.putString(AppConstant.TYPE, "3")
        fragment.setArguments(bundle)
        val fragmentManager: FragmentManager = getSupportFragmentManager()
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.setCustomAnimations(
            R.anim.enter_from_right,
            R.anim.exit_to_left,
            R.anim.enter_from_left,
            R.anim.exit_to_right
        )
        fragmentTransaction.replace(R.id.set_brunch_container, fragment, BusinessHhMenuFragment.TAG)
        fragmentTransaction.commit()
    }
}