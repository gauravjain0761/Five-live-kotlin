package com.fivelive.app.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.view.MenuItem
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.fivelive.app.R
import com.fivelive.app.fragment.*
import com.fivelive.app.prefs.SharedPreferenceWriter
import com.fivelive.app.prefs.SharedPrefsKey
import com.fivelive.app.util.AppConstant
import com.fivelive.app.util.AppUtil
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {
    public var latitude: String? = null
    public var longitude: String? = null
    var searchAddress: String? = null
    var from: String? = null
    var bottomNavigation: BottomNavigationView? = null
    var userType: String? = null
    var doubleBackToExitPressedOnce = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        userType = SharedPreferenceWriter.getInstance(this)?.getString(SharedPrefsKey.USER_TYPE)
        dataFromIntent
        initView()
        checkFromStatus()
    }

    fun checkFromStatus() {
        if (from != null) {
            if (from.equals(NewHomeFragment.TAG, ignoreCase = true)) {
                showHomeFragment()
            } else if (from.equals(HappeningNowFragment.TAG, ignoreCase = true)) {
                showHappeningNowFragment()
            }
        } else {
            showHomeFragment()
        }
    }

    val dataFromIntent: Unit
        get() {
            val intent = intent
            latitude = intent.getStringExtra(AppConstant.LATITUDE)
            longitude = intent.getStringExtra(AppConstant.LONGITUDE)
            searchAddress = intent.getStringExtra(AppConstant.SEARCH_ADDRESS)
            from = intent.getStringExtra(AppConstant.FROM)
        }

    fun initView() {
        bottomNavigation = findViewById(R.id.bottom_navigation)
        // bottomNavigation.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_LABELED);
        bottomNavigation?.setOnNavigationItemSelectedListener(this)
        bottomNavigation?.setItemIconTintList(null)
    }

    /*Bottom navigation item selected listener */
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> {
                latitude = ""
                longitude = ""
                showHomeFragment()
            }
            R.id.nav_happeningNow ->                 //  mapFragmentHide();
                showHappeningNowFragment()
            R.id.nav_saved ->                 //  mapFragmentHide();
                showSavedFragment()
            R.id.nav_myAccount ->                 //  mapFragmentHide();
                showMyAccountFragment()
        }
        return true
    }

    var homeFragment: NewHomeFragment? = null
    fun showHomeFragment() {
        // HomeFragment fragment = new HomeFragment();
        homeFragment = NewHomeFragment()
        val bundle = Bundle()
        bundle.putString(AppConstant.LATITUDE, latitude)
        bundle.putString(AppConstant.LONGITUDE, longitude)
        bundle.putString(AppConstant.SEARCH_ADDRESS, searchAddress)
        bundle.putString(AppConstant.FROM, from)
        homeFragment!!.arguments = bundle
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout_container, homeFragment!!, HomeFragment.TAG)
        fragmentTransaction.commit()
    }

    fun showHappeningNowFragment() {
        // mapLayout.setVisibility(View.GONE);
        val fragment = HappeningNowFragment()
        val bundle = Bundle()
        bundle.putString(AppConstant.LATITUDE, latitude)
        bundle.putString(AppConstant.LONGITUDE, longitude)
        fragment.arguments = bundle
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout_container, fragment, "RandomRecipeFragment")
        fragmentTransaction.commit()
    }

    fun showSavedFragment() {
        if (AppUtil.checkUserType(this@HomeActivity)) {
            AppUtil.showGuestLoginDialog(this@HomeActivity)
            return
        }
        val fragment = SavedFragment()
        val bundle = Bundle()
        bundle.putString(AppConstant.LATITUDE, latitude)
        bundle.putString(AppConstant.LONGITUDE, longitude)
        fragment.arguments = bundle
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout_container, fragment, SavedFragment.TAG)
        fragmentTransaction.commit()
    }

    fun showMyAccountFragment() {
        if (AppUtil.checkUserType(this@HomeActivity)) {
            AppUtil.showGuestLoginDialog(this@HomeActivity)
            return
        }
        val fragment = MyAccountFragment()
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout_container, fragment, MyAccountFragment.TAG)
        fragmentTransaction.commit()
    }

    fun dispatchToDetailsActivity(businessId: String?) {
        val intent = Intent(this@HomeActivity, DetailsActivity::class.java)
        intent.putExtra(AppConstant.BUSINESS_ID, businessId)
        startActivity(intent)
    }

    public override fun onStart() {
        super.onStart()
        if (!HomeActivity.Companion.checkPermissions(this@HomeActivity)) {
            requestPermissions()
        }
    }

    private fun requestPermissions() {
        val shouldProvideRationale = ActivityCompat.shouldShowRequestPermissionRationale(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        if (shouldProvideRationale) {
            showLocationDescriptionDialog()
        } else {
            ActivityCompat.requestPermissions(
                this, arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ), HomeActivity.Companion.REQUEST_PERMISSIONS_REQUEST_CODE
            )
        }
    }

    fun showLocationDescriptionDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Turn on your GPS and give 5Live access to your location.")
        builder.setPositiveButton("OK") { dialogInterface, i ->
            ActivityCompat.requestPermissions(
                this@HomeActivity,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                HomeActivity.Companion.REQUEST_PERMISSIONS_REQUEST_CODE
            )
        }
        val alertDialog = builder.create()
        alertDialog.setCanceledOnTouchOutside(false)
        alertDialog.show()
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            HomeActivity.Companion.REQUEST_CHECK_SETTINGS -> when (resultCode) {
                RESULT_OK -> homeFragment!!.deviceLocation
                RESULT_CANCELED -> startActivityForResult(
                    Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS),
                    HomeActivity.Companion.REQUEST_MANUALLY_CHANGE_SETTINGS
                )
            }
            HomeActivity.Companion.REQUEST_MANUALLY_CHANGE_SETTINGS -> {
                val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
                val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                if (!isGpsEnabled) {
                    openGpsEnableSetting()
                } else {
                    homeFragment!!.deviceLocation
                }
            }
        }
    }

    fun openGpsEnableSetting() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Turn on your GPS and give access 5Live to your location.")
        builder.setPositiveButton("OK") { dialogInterface, i ->
            startActivityForResult(
                Intent(
                    Settings.ACTION_LOCATION_SOURCE_SETTINGS
                ), HomeActivity.Companion.REQUEST_MANUALLY_CHANGE_SETTINGS
            )
        }
        val alertDialog = builder.create()
        alertDialog.setCanceledOnTouchOutside(false)
        alertDialog.show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        println()
        when (requestCode) {
            HomeActivity.Companion.REQUEST_PERMISSIONS_REQUEST_CODE -> if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted.
                Toast.makeText(this, "granted", Toast.LENGTH_SHORT).show()
            } else {
                // Permission denied.
                showLocationDescriptionDialog()
            }
        }
    }

    override fun onBackPressed() {
        //super.onBackPressed();
        if (doubleBackToExitPressedOnce) {
            finish()
        }
        Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
        doubleBackToExitPressedOnce = true
        Toast.makeText(this, "Press Back Again To Exit.", Toast.LENGTH_SHORT).show()
    }

    companion object {
        private val TAG = HomeActivity::class.java.name
        private const val REQUEST_PERMISSIONS_REQUEST_CODE = 34
        private const val REQUEST_CHECK_SETTINGS = 3456
        private const val REQUEST_MANUALLY_CHANGE_SETTINGS = 3457
        fun checkPermissions(context: Context?): Boolean {
            return if (ContextCompat.checkSelfPermission(
                    context!!,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                true
            } else false
        }
    }
}