package com.fivelive.app.activity

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.fivelive.app.R
import com.fivelive.app.fragment.EditCommonDetailsFragment
import com.fivelive.app.fragment.EditLiveMusicFragment
import com.fivelive.app.util.AppConstant

class SetLiveMusicActivity constructor() : AppCompatActivity(), View.OnClickListener {
    var backImageView: ImageView? = null
    var businessId: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_live_music)
        businessId = getIntent().getStringExtra(AppConstant.BUSINESS_ID)
        initView()
        showEditLiveMusicFragment()
    }

    private fun initView() {
        backImageView = findViewById(R.id.backImageView)
        backImageView?.setOnClickListener(this)
    }

    fun showEditLiveMusicFragment() {
        val fragment: EditLiveMusicFragment = EditLiveMusicFragment()
        val bundle: Bundle = Bundle()
        bundle.putString(AppConstant.BUSINESS_ID, businessId)
        bundle.putString(AppConstant.TYPE, "4")
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
            R.id.set_liveMusic_container,
            fragment,
            EditCommonDetailsFragment.TAG
        )
        fragmentTransaction.commit()
    }

    public override fun onClick(v: View) {
        when (v.getId()) {
            R.id.backImageView -> finish()
        }
    }
}