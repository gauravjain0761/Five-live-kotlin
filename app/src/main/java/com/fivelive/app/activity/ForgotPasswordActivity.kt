package com.fivelive.app.activity


import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.fivelive.app.R
import com.fivelive.app.activity.ForgotPasswordActivity
import com.fivelive.app.activity.NumberVerificationActivity
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

class ForgotPasswordActivity : AppCompatActivity(), View.OnClickListener {
    var backImageView: ImageView? = null
    var resetPasswordButton: Button? = null
    var inputTIL: TextInputLayout? = null
    var inputEditText: EditText? = null
    var submitButton: Button? = null
    var backToLogin_btn: Button? = null
    var type: String? = null
    var mobile: String? = null
    var email: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)
        backImageView = findViewById(R.id.backImageView)
        backImageView?.setOnClickListener(this)
        initView()
    }

    private fun initView() {
        backToLogin_btn = findViewById(R.id.backToLogin_btn)
        submitButton = findViewById(R.id.submitButton)
        inputEditText = findViewById(R.id.inputEditText)
        inputTIL = findViewById(R.id.inputTIL)
        resetPasswordButton = findViewById(R.id.resetPasswordButton)
        resetPasswordButton?.setOnClickListener(View.OnClickListener { view: View -> onClick(view) })
        submitButton?.setOnClickListener(View.OnClickListener { view: View -> onClick(view) })
        backToLogin_btn?.setOnClickListener(View.OnClickListener { view: View -> onClick(view) })
    }

    private fun dispatchToNumberVerificationScreen() {
        val intent = Intent(this@ForgotPasswordActivity, NumberVerificationActivity::class.java)
        intent.putExtra(AppConstant.EMAIL, email)
        intent.putExtra(AppConstant.PHONE_NUMBER, mobile)
        intent.putExtra(AppConstant.FROM, ForgotPasswordActivity.Companion.TAG)
        startActivity(intent)
        finish()
    }

    private fun dispatchToForgotPasswordService() {
        if (AppUtil.isNetworkAvailable(this)) {
            if (validate()) {
                forgotPasswordService()
            }
        } else {
            AppUtil.showConnectionError(this)
        }
    }

    fun dispatchToResetPasswordActivity() {
        val intent = Intent(this@ForgotPasswordActivity, ResetPasswordActivity::class.java)
        intent.putExtra(AppConstant.EMAIL, email)
        intent.putExtra(AppConstant.MOBILE, mobile)
        startActivity(intent)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.backImageView -> finish()
            R.id.resetPasswordButton -> dispatchToResetPasswordActivity()
            R.id.submitButton -> dispatchToForgotPasswordService()
            R.id.backToLogin_btn -> finish()
        }
    }

    private fun validate(): Boolean {
        val inputValue = inputEditText!!.text.toString().trim { it <= ' ' }
        inputEditText!!.addTextChangedListener(TextWatcher(inputTIL!!, inputEditText!!))
        if (TextUtils.isEmpty(inputValue)) {
            inputTIL!!.error = "Enter Email/Phone Number."
            return false
        }
        if (!inputValue.isEmpty()) {
            if (!TextUtils.isDigitsOnly(inputEditText!!.text)) {
                type = "email"
                email = inputValue
                if (!AppUtil.isValidMail(email)) {
                    inputTIL!!.error = "Please Enter valid Email."
                    return false
                }
            } else {
                type = "mobile"
                mobile = inputValue
                if (!AppUtil.isValidMobile(mobile!!)) {
                    inputTIL!!.error = "Please Enter valid Phone Number."
                    return false
                }
            }
        }
        return true
    }

    private fun forgotPasswordService() {
        AppUtil.showProgressDialog(this@ForgotPasswordActivity)
        val apiServiceInterface = ApiClient.instance.client
        val call = apiServiceInterface.forgotPassword(type, mobile, email)
        call.enqueue(object : Callback<JsonElement?> {
            override fun onResponse(call: Call<JsonElement?>, response: Response<JsonElement?>) {
                AppUtil.dismissProgressDialog()
                try {
                    if (response.isSuccessful) {
                        val jsonObject =
                            JSONObject(response.body()!!.asJsonObject.toString().trim { it <= ' ' })
                        if (jsonObject.getString("status")
                                .equals(AppConstant.SUCCESS, ignoreCase = true)
                        ) {
                            dispatchToNumberVerificationScreen()
                        } else {
                            Toast.makeText(
                                this@ForgotPasswordActivity,
                                "" + jsonObject.getString("message"),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        AppUtil.showErrorDialog(
                            this@ForgotPasswordActivity,
                            "Error!",
                            "Something went wrong."
                        )
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<JsonElement?>, t: Throwable?) {
                AppUtil.dismissProgressDialog()
                call.cancel()
            }
        })
    }

    companion object {
        val TAG = ForgotPasswordActivity::class.java.name
    }
}