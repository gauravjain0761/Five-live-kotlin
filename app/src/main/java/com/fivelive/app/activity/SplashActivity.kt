package com.fivelive.app.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.VideoView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.fivelive.app.R
import com.fivelive.app.activity.LoginActivity.Companion.checkLocationPermissions
import com.fivelive.app.prefs.SharedPreferenceWriter
import com.fivelive.app.prefs.SharedPrefsKey
import com.fivelive.app.util.AppConstant
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    var businessId: String? = null

    @RequiresApi(api = Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        checkNotificationPermissions(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        /*new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                AppUtil.getDeviceToken(SplashActivity.this);
                dispatchToHomeActivity();
            }
        }, 3000);*/
        val videoView: VideoView = findViewById<View>(R.id.videoView) as VideoView
        videoView.setAudioFocusRequest(AudioManager.AUDIOFOCUS_NONE)
        val video: Uri =
            Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.splash_white)
        videoView.setVideoURI(video)
        videoView.setOnCompletionListener { //  dispatchToHomeActivity();
            deepLinkingData
        }
        videoView.start()
    }

    // Get deep link from result (may be null if no link is found)
    private val deepLinkingData: Unit
        get() {
            FirebaseDynamicLinks.getInstance()
                .getDynamicLink(intent)
                .addOnSuccessListener(
                    this
                ) { pendingDynamicLinkData -> // Get deep link from result (may be null if no link is found)
                    if (pendingDynamicLinkData != null) {
                        businessId =
                            pendingDynamicLinkData.link!!.getQueryParameter("businessId")
                        disPatchToBizDetailsActivity()
                    } else {
                        dispatchToHomeActivity()
                    }
                }
                .addOnFailureListener(this) { e ->
                    Log.d("getDynamicLink", "getDynamicLink:onFailure", e)
                    dispatchToHomeActivity()
                }
        }

    private fun checkIsLoginAndLocation(): Boolean {
        return SharedPreferenceWriter.getInstance(this@SplashActivity)
            .getBooleanValue(SharedPrefsKey.IS_LOGIN) && checkLocationPermissions(this)
    }

    private fun dispatchToHomeActivity() {
        if (checkIsLoginAndLocation()) {
            goToHomeScreen()
        } else {
            goToLoginScreen()
        }
    }

    private fun goToHomeScreen() {
        val intent: Intent = Intent(this@SplashActivity, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun goToLoginScreen() {
        val intent: Intent = Intent(this@SplashActivity, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun disPatchToBizDetailsActivity() {
        if (checkIsLoginAndLocation()) {
            goToBizDetailsScreen()
        } else {
            goToLoginScreen()
        }
    }

    private fun goToBizDetailsScreen() {
        val intent = Intent(this@SplashActivity, DetailsActivity::class.java)
        intent.putExtra(AppConstant.BUSINESS_ID, businessId)
        intent.putExtra(AppConstant.FROM, TAG)
        startActivity(intent)
        finish()
    }


    companion object {

        @JvmField
        val TAG: String = SplashActivity::class.java.name

        fun checkNotificationPermissions(activity: Activity) {
            if (ContextCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    activity, arrayOf(
                        Manifest.permission.POST_NOTIFICATIONS,
                    ), 12
                )
            }
        }
    }
}