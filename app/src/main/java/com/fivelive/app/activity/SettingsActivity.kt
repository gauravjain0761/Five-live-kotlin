package com.fivelive.app.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.CompoundButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.fivelive.app.R
import com.fivelive.app.prefs.SharedPreferenceWriter
import com.fivelive.app.prefs.SharedPrefsKey
import com.fivelive.app.retrofit.APIServiceInterface
import com.fivelive.app.retrofit.ApiClient
import com.fivelive.app.util.AppUtil
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.gson.JsonElement
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SettingsActivity constructor() : AppCompatActivity(), View.OnClickListener,
    CompoundButton.OnCheckedChangeListener {
    var backImageView: ImageView? = null
    var changePasswordTextView: TextView? = null
    var notificationSwitchButton: SwitchMaterial? = null
    var notificationStatus: String = "0"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        initView()
        updateUI()
    }

    fun initView() {
        backImageView = findViewById(R.id.backImageView)
        notificationSwitchButton =
            findViewById<View>(R.id.notificationSwitchButton) as SwitchMaterial?
        notificationSwitchButton!!.setOnCheckedChangeListener(this)
        changePasswordTextView = findViewById(R.id.changePasswordTextView)
        changePasswordTextView?.setOnClickListener(this)
        backImageView?.setOnClickListener(this)
    }

    private fun updateUI() {
        val signUpType: String? =
            SharedPreferenceWriter.getInstance(this).getString(SharedPrefsKey.SIGNUP_TYPE)
        if (!(signUpType == "1")) {
            changePasswordTextView!!.setVisibility(View.GONE)
        } else {
            changePasswordTextView!!.setVisibility(View.VISIBLE)
        }
    }

    public override fun onClick(view: View) {
        when (view.getId()) {
            R.id.backImageView -> finish()
            R.id.changePasswordTextView -> dispatchTOChangePasswordScreen()
        }
    }

    private fun dispatchTOChangePasswordScreen() {
        startActivity(Intent(this@SettingsActivity, ChangePasswordActivity::class.java))
    }

    public override fun onCheckedChanged(compoundButton: CompoundButton, b: Boolean) {
        val status: Boolean = compoundButton.isChecked()
        if (compoundButton.isChecked()) {
            notificationStatus = "1"
            SharedPreferenceWriter.getInstance(this)
                .writeStringValue(SharedPrefsKey.NOTIFICATION_STATUS, notificationStatus)
        } else {
            notificationStatus = "0"
            SharedPreferenceWriter.getInstance(this)
                .writeStringValue(SharedPrefsKey.NOTIFICATION_STATUS, notificationStatus)
        }
        if (AppUtil.isNetworkAvailable(this)) {
            updateNotificationStatusService()
        } else {
            AppUtil.showConnectionError(this)
        }
    }

    private fun updateNotificationStatusService() {
        AppUtil.showProgressDialog(this)
        val token: String? = SharedPreferenceWriter.getInstance(this).getString(SharedPrefsKey.TOKEN)
        val apiServiceInterface: APIServiceInterface = ApiClient.instance.client
        val call: Call<JsonElement> =
            apiServiceInterface.updateNotificationStatus(token, notificationStatus)
        call.enqueue(object : Callback<JsonElement> {
            public override fun onResponse(
                call: Call<JsonElement>,
                response: Response<JsonElement>
            ) {
                AppUtil.dismissProgressDialog()
                try {
                    if (response.isSuccessful()) {
                        val `object`: JSONObject = JSONObject(
                            response.body()!!.getAsJsonObject().toString().trim({ it <= ' ' })
                        )
                        if (`object`.getString("status").equals("SUCCESS", ignoreCase = true)) {
                            AppUtil.showSnackBar(
                                this@SettingsActivity,
                                notificationSwitchButton,
                                `object`.getString("message")
                            )
                        } else {
                            if (`object`.getString("message").contains("Session expired")) {
                                AppUtil.showLogoutDialog(
                                    this@SettingsActivity,
                                    getResources().getString(R.string.error),
                                    `object`.getString("message")
                                )
                            } else {
                                AppUtil.showErrorDialog(
                                    this@SettingsActivity,
                                    getResources().getString(R.string.error),
                                    `object`.getString("message")
                                )
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            public override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                AppUtil.dismissProgressDialog()
                call.cancel()
                AppUtil.showErrorDialog(
                    this@SettingsActivity,
                    getResources().getString(R.string.error),
                    t.message
                )
            }
        })
    }
}