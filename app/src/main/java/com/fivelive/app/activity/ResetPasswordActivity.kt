package com.fivelive.app.activity

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.fivelive.app.R
import com.fivelive.app.dialog.ResetConfirmedDialog
import com.fivelive.app.retrofit.ApiClient
import com.fivelive.app.util.AppConstant
import com.fivelive.app.util.AppUtil
import com.fivelive.app.util.TextWatcher
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.JsonElement
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ResetPasswordActivity : AppCompatActivity(), View.OnClickListener {
    var updatePassword: Button? = null
    var backImageView: ImageView? = null
    var new_passTIL: TextInputLayout? = null
    var confirm_passTIL: TextInputLayout? = null
    var new_pass_et: EditText? = null
    var confirm_pass_et: EditText? = null
    var mobile: String? = null
    var email: String? = null
    var type: String? = null
    var newPass: String? = null
    var confirmPass: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)
        backImageView = findViewById(R.id.backImageView)
        backImageView?.setOnClickListener(this)
        dataFromIntent
        initView()
    }

    private val dataFromIntent: Unit
        private get() {
            val intent = intent
            mobile = intent.getStringExtra(AppConstant.MOBILE)
            email = intent.getStringExtra(AppConstant.EMAIL)
            if (mobile == null) {
                type = AppConstant.EMAIL
            } else if (email == null) {
                type = AppConstant.MOBILE
            }
        }

    fun initView() {
        confirm_passTIL = findViewById(R.id.confirm_passTIL)
        new_passTIL = findViewById(R.id.new_passTIL)
        confirm_pass_et = findViewById(R.id.confirm_pass_et)
        new_pass_et = findViewById(R.id.new_pass_et)
        updatePassword = findViewById(R.id.updatePassword)
        updatePassword?.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.updatePassword -> dispatchToUpdatePasswordService()
            R.id.backImageView -> finish()
        }
    }

    private fun addTextWatcherListener() {
        new_pass_et!!.addTextChangedListener(TextWatcher(new_passTIL, new_pass_et))
        confirm_pass_et!!.addTextChangedListener(TextWatcher(confirm_passTIL, confirm_pass_et))
    }

    private fun validate(): Boolean {
        addTextWatcherListener()
        newPass = new_pass_et!!.text.toString().trim { it <= ' ' }
        confirmPass = confirm_pass_et!!.text.toString().trim { it <= ' ' }
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

    fun openResetPasswordConfirmationDialog() {
        ResetConfirmedDialog(this@ResetPasswordActivity) { dispatchToLoginScreen() }.show()
    }

    private fun dispatchToUpdatePasswordService() {
        AppUtil.hideKeyboard(this)
        if (AppUtil.isNetworkAvailable(this@ResetPasswordActivity)) {
            if (validate()) {
                updatePasswordService()
            }
        } else {
            AppUtil.showConnectionError(this)
        }
    }

    private fun updatePasswordService() {
        AppUtil.showProgressDialog(this@ResetPasswordActivity)
        val apiServiceInterface = ApiClient.instance.client
        val call = apiServiceInterface.updatePassword(mobile, email, newPass, confirmPass, type)
        call.enqueue(object : Callback<JsonElement> {
            override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                AppUtil.dismissProgressDialog()
                try {
                    if (response.isSuccessful) {
                        val jsonObject =
                            JSONObject(response.body()!!.asJsonObject.toString().trim { it <= ' ' })
                        if (jsonObject.getString("status")
                                .equals(AppConstant.SUCCESS, ignoreCase = true)
                        ) {
                            openResetPasswordConfirmationDialog()
                        } else {
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                AppUtil.dismissProgressDialog()
                call.cancel()
            }
        })
    }

    private fun dispatchToLoginScreen() {
        val intent = Intent(this@ResetPasswordActivity, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
        finish()
    }
}