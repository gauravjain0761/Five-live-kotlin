package com.fivelive.app.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.fivelive.app.R
import com.fivelive.app.dialog.ConfirmationDialog
import com.fivelive.app.dialog.CustomSuccessDialog
import com.fivelive.app.fragment.EditCommonDetailsFragment
import com.fivelive.app.fragment.EditHappyHoursItemsFragment
import com.fivelive.app.fragment.HappyHourMenuFragment
import com.fivelive.app.interfaces.CallBackListener
import com.fivelive.app.interfaces.DialogButtonListener
import com.fivelive.app.util.AppConstant
import com.fivelive.app.util.AppUtil
import com.fivelive.app.util.CommonAPI

class SetHappyHourActivity constructor() : AppCompatActivity(), View.OnClickListener {
    var hhMenuTextView: TextView? = null
    var hhDetailsTextView: TextView? = null
    var hhItemsTextView: TextView? = null
    var backImageView: ImageView? = null
    var businessId: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_happy_hour)
        businessId = getIntent().getStringExtra(AppConstant.BUSINESS_ID)
        initView()
        showHappyHourMenuFragment()
    }

    private fun initView() {
        backImageView = findViewById(R.id.backImageView)
        hhMenuTextView = findViewById(R.id.hhMenuTextView)
        hhDetailsTextView = findViewById(R.id.hhDetailsTextView)
        hhItemsTextView = findViewById(R.id.hhItemsTextView)
        backImageView?.setOnClickListener(this)
        hhMenuTextView?.setOnClickListener(this)
        hhDetailsTextView?.setOnClickListener(this)
        hhItemsTextView?.setOnClickListener(this)
    }

    fun showHappyHourMenuFragment() {
        selectedHHMenu()
        val fragment: HappyHourMenuFragment = HappyHourMenuFragment()
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
        fragmentTransaction.replace(R.id.happy_hour_container, fragment, HappyHourMenuFragment.TAG)
        fragmentTransaction.commit()
    }

    fun showHappyHourDetailsFragment() {
        selectedHHDetails()
        val fragment: EditCommonDetailsFragment = EditCommonDetailsFragment()
        val bundle: Bundle = Bundle()
        bundle.putString(AppConstant.BUSINESS_ID, businessId)
        bundle.putString(AppConstant.TYPE, "1")
        fragment.setArguments(bundle)
        val fragmentManager: FragmentManager = getSupportFragmentManager()
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.setCustomAnimations(
            R.anim.enter_from_right,
            R.anim.exit_to_left,
            R.anim.enter_from_left,
            R.anim.exit_to_right
        )
        fragmentTransaction.replace(
            R.id.happy_hour_container,
            fragment,
            EditCommonDetailsFragment.TAG
        )
        fragmentTransaction.commit()
    }

    fun showHappyHourItemsFragment() {
        selectedHHItems()
        val fragment: EditHappyHoursItemsFragment = EditHappyHoursItemsFragment()
        val bundle: Bundle = Bundle()
        bundle.putString(AppConstant.BUSINESS_ID, businessId)
        fragment.setArguments(bundle)
        val fragmentManager: FragmentManager = getSupportFragmentManager()
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.setCustomAnimations(
            R.anim.enter_from_right,
            R.anim.exit_to_left,
            R.anim.enter_from_left,
            R.anim.exit_to_right
        )
        fragmentTransaction.replace(
            R.id.happy_hour_container,
            fragment,
            EditHappyHoursItemsFragment.TAG
        )
        fragmentTransaction.commit()
    }

    public override fun onClick(view: View) {
        when (view.getId()) {
            R.id.backImageView -> finish()
            R.id.hhMenuTextView -> showHappyHourMenuFragment()
            R.id.hhDetailsTextView -> showHappyHourDetailsFragment()
            R.id.hhItemsTextView -> showHappyHourItemsFragment()
        }
    }

    private fun selectedHHMenu() {
        hhMenuTextView!!.setTextColor(getResources().getColor(R.color.white))
        hhDetailsTextView!!.setTextColor(getResources().getColor(R.color.light_black))
        hhItemsTextView!!.setTextColor(getResources().getColor(R.color.light_black))
        hhMenuTextView!!.setBackground(getResources().getDrawable(R.drawable.blue_btn_bg))
        hhDetailsTextView!!.setBackground(getResources().getDrawable(R.drawable.un_selected_hh_details_bg))
        hhItemsTextView!!.setBackground(getResources().getDrawable(R.drawable.un_selected_hh_details_bg))
    }

    private fun selectedHHDetails() {
        hhDetailsTextView!!.setTextColor(getResources().getColor(R.color.white))
        hhMenuTextView!!.setTextColor(getResources().getColor(R.color.light_black))
        hhItemsTextView!!.setTextColor(getResources().getColor(R.color.light_black))
        hhDetailsTextView!!.setBackground(getResources().getDrawable(R.drawable.blue_btn_bg))
        hhMenuTextView!!.setBackground(getResources().getDrawable(R.drawable.un_selected_hh_details_bg))
        hhItemsTextView!!.setBackground(getResources().getDrawable(R.drawable.un_selected_hh_details_bg))
    }

    private fun selectedHHItems() {
        hhItemsTextView!!.setTextColor(getResources().getColor(R.color.white))
        hhMenuTextView!!.setTextColor(getResources().getColor(R.color.light_black))
        hhDetailsTextView!!.setTextColor(getResources().getColor(R.color.light_black))
        hhItemsTextView!!.setBackground(getResources().getDrawable(R.drawable.blue_btn_bg))
        hhMenuTextView!!.setBackground(getResources().getDrawable(R.drawable.un_selected_hh_details_bg))
        hhDetailsTextView!!.setBackground(getResources().getDrawable(R.drawable.un_selected_hh_details_bg))
    }

    fun dispatchToHHAddItemActivity(itemId: String?) {
        val intent: Intent = Intent(this, AddHappyHoursItemActivity::class.java)
        intent.putExtra(AppConstant.BUSINESS_ID, businessId)
        intent.putExtra(AppConstant.ITEM_ID, itemId)
        intent.putExtra(AppConstant.FROM, TAG)
        intent.putExtra(AppConstant.EDIT, true)
        startActivity(intent)
    }

    fun showDeleteConfirmationDialog(itemID: String?) {
        ConfirmationDialog(this@SetHappyHourActivity) {

            goForDeleteItemService(itemID)

        }.show()
    }

    fun goForDeleteItemService(itemID: String?) {
        if (AppUtil.isNetworkAvailable(this@SetHappyHourActivity)) {
            val commonApi: CommonAPI = CommonAPI(this@SetHappyHourActivity)
            commonApi.deleteItemService(itemID, object : CallBackListener {
                public override fun onSuccess(msg: String?) {
                    showSuccessDialog(msg)
                }
            })
        } else {
            AppUtil.showConnectionError(this@SetHappyHourActivity)
        }
    }

    fun showSuccessDialog(msg: String?) {
        CustomSuccessDialog(this, getString(R.string.success), msg) {

            showHappyHourItemsFragment()

        }.show()
    }

    companion object {
        @JvmField
        val TAG: String = "SetHappyHourActivity"
    }
}