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
import com.fivelive.app.fragment.EditReverseHHMenuFragment
import com.fivelive.app.fragment.ReverseHHItemsFragment
import com.fivelive.app.interfaces.CallBackListener
import com.fivelive.app.interfaces.DialogButtonListener
import com.fivelive.app.util.AppConstant
import com.fivelive.app.util.AppUtil
import com.fivelive.app.util.CommonAPI

class SetReverseHappyHourActivity constructor() : AppCompatActivity(), View.OnClickListener {
    var reverseHHMenuTextView: TextView? = null
    var reverseHHDetailsTextView: TextView? = null
    var reverseHHItemsTextView: TextView? = null
    var backImageView: ImageView? = null
    var businessId: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reverse_happy_hour)
        businessId = getIntent().getStringExtra(AppConstant.BUSINESS_ID)
        initView()
        showReverseHHMenuFragment()
    }

    private fun initView() {
        backImageView = findViewById(R.id.backImageView)
        reverseHHMenuTextView = findViewById(R.id.reverseHHMenuTextView)
        reverseHHDetailsTextView = findViewById(R.id.reverseHHDetailsTextView)
        reverseHHItemsTextView = findViewById(R.id.reverseHHItemsTextView)
        reverseHHMenuTextView?.setOnClickListener(this)
        reverseHHDetailsTextView?.setOnClickListener(this)
        reverseHHItemsTextView?.setOnClickListener(this)
        backImageView?.setOnClickListener(this)
    }

    fun showReverseHHMenuFragment() {
        val fragment: EditReverseHHMenuFragment = EditReverseHHMenuFragment()
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
        fragmentTransaction.replace(
            R.id.reverse_happy_hour_container,
            fragment,
            EditReverseHHMenuFragment.TAG
        )
        fragmentTransaction.commit()
    }

    fun showReverseHHDetailsFragment() {
        val fragment: EditCommonDetailsFragment = EditCommonDetailsFragment()
        val bundle: Bundle = Bundle()
        bundle.putString(AppConstant.BUSINESS_ID, businessId)
        bundle.putString(AppConstant.TYPE, "2")
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
            R.id.reverse_happy_hour_container,
            fragment,
            EditCommonDetailsFragment.TAG
        )
        fragmentTransaction.commit()
    }

    fun showReverseHHItemsFragment() {
        selectedHHItems()
        val fragment: ReverseHHItemsFragment = ReverseHHItemsFragment()
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
            R.id.reverse_happy_hour_container,
            fragment,
            ReverseHHItemsFragment.TAG
        )
        fragmentTransaction.commit()
    }

    public override fun onClick(view: View) {
        when (view.getId()) {
            R.id.backImageView -> finish()
            R.id.reverseHHMenuTextView -> {
                showReverseHHMenuFragment()
                selectedHHMenu()
            }
            R.id.reverseHHDetailsTextView -> {
                selectedHHDetails()
                showReverseHHDetailsFragment()
            }
            R.id.reverseHHItemsTextView -> showReverseHHItemsFragment()
        }
    }

    private fun selectedHHMenu() {
        reverseHHMenuTextView!!.setTextColor(getResources().getColor(R.color.white))
        reverseHHDetailsTextView!!.setTextColor(getResources().getColor(R.color.light_black))
        reverseHHItemsTextView!!.setTextColor(getResources().getColor(R.color.light_black))
        reverseHHMenuTextView!!.setBackground(getResources().getDrawable(R.drawable.blue_btn_bg))
        reverseHHDetailsTextView!!.setBackground(getResources().getDrawable(R.drawable.un_selected_hh_details_bg))
        reverseHHItemsTextView!!.setBackground(getResources().getDrawable(R.drawable.un_selected_hh_details_bg))
    }

    private fun selectedHHDetails() {
        reverseHHDetailsTextView!!.setTextColor(getResources().getColor(R.color.white))
        reverseHHMenuTextView!!.setTextColor(getResources().getColor(R.color.light_black))
        reverseHHItemsTextView!!.setTextColor(getResources().getColor(R.color.light_black))
        reverseHHDetailsTextView!!.setBackground(getResources().getDrawable(R.drawable.blue_btn_bg))
        reverseHHMenuTextView!!.setBackground(getResources().getDrawable(R.drawable.un_selected_hh_details_bg))
        reverseHHItemsTextView!!.setBackground(getResources().getDrawable(R.drawable.un_selected_hh_details_bg))
    }

    private fun selectedHHItems() {
        reverseHHItemsTextView!!.setTextColor(getResources().getColor(R.color.white))
        reverseHHMenuTextView!!.setTextColor(getResources().getColor(R.color.light_black))
        reverseHHDetailsTextView!!.setTextColor(getResources().getColor(R.color.light_black))
        reverseHHItemsTextView!!.setBackground(getResources().getDrawable(R.drawable.blue_btn_bg))
        reverseHHMenuTextView!!.setBackground(getResources().getDrawable(R.drawable.un_selected_hh_details_bg))
        reverseHHDetailsTextView!!.setBackground(getResources().getDrawable(R.drawable.un_selected_hh_details_bg))
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
        ConfirmationDialog(this@SetReverseHappyHourActivity) {

            goForDeleteItemService(itemID)

        }.show()
    }

    fun goForDeleteItemService(itemID: String?) {
        if (AppUtil.isNetworkAvailable(this@SetReverseHappyHourActivity)) {
            val commonApi: CommonAPI = CommonAPI(this@SetReverseHappyHourActivity)
            commonApi.deleteItemService(itemID, object : CallBackListener {
                public override fun onSuccess(msg: String?) {
                    showSuccessDialog(msg)
                }
            })
        } else {
            AppUtil.showConnectionError(this@SetReverseHappyHourActivity)
        }
    }

    fun showSuccessDialog(msg: String?) {
        CustomSuccessDialog(this, "Success!", msg, {

                showReverseHHItemsFragment()

        }).show()
    }

    companion object {
        @JvmField
        val TAG: String = "SetReverseHappyHourActivity"
    }
}