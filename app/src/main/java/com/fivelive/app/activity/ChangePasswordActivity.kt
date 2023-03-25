package com.fivelive.app.activity

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.fivelive.app.R
import com.fivelive.app.dialog.ResetConfirmedDialog
import com.fivelive.app.prefs.SharedPreferenceWriter
import com.fivelive.app.prefs.SharedPrefsKey
import com.fivelive.app.retrofit.ApiClient
import com.fivelive.app.util.AppConstant
import com.fivelive.app.util.AppUtil
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.JsonElement
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChangePasswordActivity : AppCompatActivity(), View.OnClickListener {
    var backImageView: ImageView? = null
    var old_pass_et: EditText? = null
    var new_pass_et: EditText? = null
    var confirm_pass_et: EditText? = null
    var old_passTIL: TextInputLayout? = null
    var new_passTIL: TextInputLayout? = null
    var confirm_passTIL: TextInputLayout? = null
    var updatePasswordBtn: Button? = null
    var oldPass: String? = null
    var newPass: String? = null
    var confirmPass: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)
        initView()
    }

    private fun initView() {
        backImageView = findViewById(R.id.backImageView)
        backImageView?.setOnClickListener(this)
        old_pass_et = findViewById(R.id.old_pass_et)
        new_pass_et = findViewById(R.id.new_pass_et)
        confirm_pass_et = findViewById(R.id.confirm_pass_et)
        old_passTIL = findViewById(R.id.old_passTIL)
        new_passTIL = findViewById(R.id.new_passTIL)
        confirm_passTIL = findViewById(R.id.confirm_passTIL)
        updatePasswordBtn = findViewById(R.id.updatePasswordBtn)
        updatePasswordBtn?.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.backImageView -> finish()
            R.id.updatePasswordBtn -> dispatchToChangePasswordService()
        }
    }

    private fun dispatchToChangePasswordService() {
        AppUtil.hideKeyboard(this)
        if (AppUtil.isNetworkAvailable(this@ChangePasswordActivity)) {
            if (validate()) {
                changePasswordService()
            }
        } else {
            AppUtil.showConnectionError(this)
        }
    }

    private fun validate(): Boolean {
        oldPass = old_pass_et!!.text.toString().trim { it <= ' ' }
        newPass = new_pass_et!!.text.toString().trim { it <= ' ' }
        confirmPass = confirm_pass_et!!.text.toString().trim { it <= ' ' }
        if (TextUtils.isEmpty(oldPass)) {
            old_passTIL!!.error = "Enter Old Password."
            return false
        }
        if (TextUtils.isEmpty(newPass)) {
            new_passTIL!!.error = "Enter New Password."
            return false
        }
        if (!AppUtil.isValidPassword(newPass)) {
            AppUtil.showErrorDialog(
                this,
                resources.getString(R.string.app_name),
                resources.getString(R.string.paasword_validation_msg)
            )
            return false
        }
        if (TextUtils.isEmpty(confirmPass)) {
            confirm_passTIL!!.error = "Enter Confirm Password."
            return false
        }
        if (!newPass.equals(confirmPass, ignoreCase = true)) {
            confirm_passTIL!!.error = resources.getString(R.string.confirm_password_not_matched)
            return false
        }
        return true
    }

    private fun changePasswordService() {
        AppUtil.showProgressDialog(this@ChangePasswordActivity)
        val token = SharedPreferenceWriter.getInstance(this@ChangePasswordActivity)
            ?.getString(SharedPrefsKey.TOKEN)
        val apiServiceInterface = ApiClient.instance.client
        val call = apiServiceInterface.changePassword(token, oldPass, newPass, confirmPass)
        call.enqueue(object : Callback<JsonElement> {
            override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                AppUtil.dismissProgressDialog()
                try {
                    if (response.isSuccessful) {
                        val `object` =
                            JSONObject(response.body()!!.asJsonObject.toString().trim { it <= ' ' })
                        if (`object`.getString("status")
                                .equals(AppConstant.SUCCESS, ignoreCase = true)
                        ) {
                            openResetPasswordConfirmationDialog()
                            AppUtil.showSnackBar(
                                this@ChangePasswordActivity,
                                old_pass_et,
                                `object`.getString("message")
                            )
                        } else {
                            if (`object`.getString("message")
                                    .contains(AppConstant.SESSION_EXPIRED)
                            ) {
                                AppUtil.showLogoutDialog(
                                    this@ChangePasswordActivity,
                                    resources.getString(R.string.error),
                                    `object`.getString("message")
                                )
                            } else {
                                AppUtil.showErrorDialog(
                                    this@ChangePasswordActivity,
                                    resources.getString(R.string.error),
                                    `object`.getString("message")
                                )
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                AppUtil.dismissProgressDialog()
                call.cancel()
                AppUtil.showErrorDialog(
                    this@ChangePasswordActivity,
                    resources.getString(R.string.error),
                    t.message
                )
            }
        })
    }

    fun openResetPasswordConfirmationDialog() {
        ResetConfirmedDialog(this@ChangePasswordActivity) { finish() }.show()
    }
}