package com.fivelive.app.activity

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.fivelive.app.R
import com.fivelive.app.fragment.EditRegularMenuFragment
import com.fivelive.app.util.AppConstant

class SetRegularMenuDetailsActivity constructor() : AppCompatActivity(), View.OnClickListener {
    var backImageView: ImageView? = null
    var businessId: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_regular_menu_details)
        businessId = getIntent().getStringExtra(AppConstant.BUSINESS_ID)
        initView()
        showBrunchMenuFragment()
    }

    private fun initView() {
        backImageView = findViewById(R.id.backImageView)
        backImageView?.setOnClickListener(this)
    }

    fun showBrunchMenuFragment() {
        val fragment: EditRegularMenuFragment = EditRegularMenuFragment()
        val bundle: Bundle = Bundle()
        bundle.putString(AppConstant.BUSINESS_ID, businessId)
        fragment.setArguments(bundle)
        val fragmentManager: FragmentManager = getSupportFragmentManager()
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(
            R.id.edit_regular_menu_container,
            fragment,
            EditRegularMenuFragment.TAG
        )
        fragmentTransaction.commit()
    }

    public override fun onClick(v: View) {
        when (v.getId()) {
            R.id.backImageView -> finish()
        }
    }
}