package com.fivelive.app.activity


import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.os.CountDownTimer
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.fivelive.app.Model.*
import com.fivelive.app.R
import com.fivelive.app.activity.*
import com.fivelive.app.dialog.CustomErrorDialog
import com.fivelive.app.dialog.RegistrationConfirmedDialog
import com.fivelive.app.prefs.SharedPreferenceWriter
import com.fivelive.app.prefs.SharedPrefsKey
import com.fivelive.app.retrofit.APIServiceInterface
import com.fivelive.app.retrofit.ApiClient
import com.fivelive.app.util.*
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken
import com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks
import com.google.gson.JsonElement
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import java.util.concurrent.TimeUnit

class NumberVerificationActivity : AppCompatActivity(), View.OnClickListener {
    private var mCountDownTimer: CountDownTimer? = null
    private var mTimerRunning = false
    private var mTimeLeftInMillis: Long = NumberVerificationActivity.Companion.START_TIME_IN_MILLIS
    var timerTextView: TextView? = null
    var resendButton: Button? = null
    var verifyButton: Button? = null
    var otp_et: EditText? = null
    var otpTIL: TextInputLayout? = null
    var backImageView: ImageView? = null
    var heading_tv: TextView? = null
    var message_tv: TextView? = null
    var mVerificationId: String? = null
    var mAuth: FirebaseAuth? = null
    private var mFusedLocationProviderClient: FusedLocationProviderClient? = null
    private var mLastLocation: Location? = null
    var latitude = 0.0
    var longitude = 0.0
    var otpNumber: String? = null
    var fName: String? = null
    var lName: String? = null
    var phoneNumber: String? = null
    var email: String? = null
    var dob: String? = null
    var gender: String? = null
    var password: String? = null
    var otpCode: String? = null
    var firebaseDeviceToken: String? = null
    var mobile: String? = null
    var form: String? = null
    var profileDetails: ProfileDetails? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_number_verification)
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        mAuth = FirebaseAuth.getInstance()
        firebaseDeviceToken =
            SharedPreferenceWriter.getInstance(this).getString(SharedPrefsKey.DEVICE_TOKEN)
        if (firebaseDeviceToken == "") {
            AppUtil.getDeviceToken(this)
        }
        dataFromIntent
        initView()
        updateUI()
        startTimer()
        if (phoneNumber != null) {
            otpNumber = "+1$phoneNumber"
            sendVerificationCode(otpNumber)
        }
    }

    private val dataFromIntent: Unit
        private get() {
            val intent = intent
            fName = intent.getStringExtra(AppConstant.FIRST_NAME)
            lName = intent.getStringExtra(AppConstant.LAST_NAME)
            phoneNumber = intent.getStringExtra(AppConstant.PHONE_NUMBER)
            email = intent.getStringExtra(AppConstant.EMAIL)
            dob = intent.getStringExtra(AppConstant.DATE_OF_BIRTH)
            gender = intent.getStringExtra(AppConstant.GENDER)
            password = intent.getStringExtra(AppConstant.PASSWORD)
            form = intent.getStringExtra(AppConstant.FROM)
            latitude = intent.getDoubleExtra(AppConstant.LATITUDE, 0.0)
            longitude = intent.getDoubleExtra(AppConstant.LONGITUDE, 0.0)
            profileDetails = intent.getParcelableExtra(AppConstant.PROFILE_DETAILS_MODEL)
            Log.d(NumberVerificationActivity.Companion.TAG, "getDataFromIntent: ")
        }

    fun updateUI() {
        if (form == ForgotPasswordActivity.Companion.TAG) {
            if (phoneNumber != null) {
                heading_tv!!.text = resources.getString(R.string.number_verification)
                message_tv!!.text =
                    resources.getString(R.string.enter_6_digit_one_time_password_verification_code_on_received_on_your_mobile_number)
            } else {
                heading_tv!!.text = resources.getString(R.string.email_verification)
                message_tv!!.text =
                    resources.getString(R.string.enter_6_digit_one_time_password_verification_code_on_received_on_your_email_id)
            }
        } else {
            heading_tv!!.text = resources.getString(R.string.number_verification)
            message_tv!!.text =
                resources.getString(R.string.enter_6_digit_one_time_password_verification_code_on_received_on_your_mobile_number)
        }
    }

    private fun initView() {
        message_tv = findViewById(R.id.message_tv)
        heading_tv = findViewById(R.id.heading_tv)
        otpTIL = findViewById(R.id.otpTIL)
        resendButton = findViewById(R.id.resendButton)
        timerTextView = findViewById(R.id.timerTextView)
        backImageView = findViewById(R.id.backImageView)
        verifyButton = findViewById(R.id.verifyButton)
        otp_et = findViewById(R.id.otp_et)
        verifyButton?.setOnClickListener(this)
        backImageView?.setOnClickListener(this)
        resendButton?.setOnClickListener(this)
    }

    @get:SuppressLint("MissingPermission")
    private val deviceLocation: Unit
        private get() {
            mFusedLocationProviderClient!!.lastLocation.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    mLastLocation = task.result
                    if (mLastLocation != null) {
                        latitude = mLastLocation!!.latitude
                        longitude = mLastLocation!!.longitude
                    }
                } else {
                    Toast.makeText(
                        this@NumberVerificationActivity,
                        "Unable to get last Device Location",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.backImageView -> finish()
            R.id.resendButton -> resendOTP()
            R.id.verifyButton -> performAction()
        }
    }

    fun resendOTP() {
        otp_et!!.setText("")
        startTimer()
        if (form == ForgotPasswordActivity.Companion.TAG) {
            if (phoneNumber != null) {
                firebaseSignUp()
            } else {
                // when user come here trough Email Address
                forgotPasswordService("email") //
            }
        } else {
            sendVerificationCode(otpNumber)
        }
    }

    private fun validate(): Boolean {
        otp_et!!.addTextChangedListener(TextWatcher(otpTIL, otp_et))
        otpCode = otp_et!!.text.toString().trim { it <= ' ' }
        if (TextUtils.isEmpty(otpCode) && otpCode!!.length != 6) {
            otpTIL!!.error = "Enter valid One Time Password."
            return false
        }
        return true
    }

    override fun onStop() {
        super.onStop()
        mCountDownTimer!!.cancel()
    }

    private fun startTimer() {
        mTimerRunning = true
        if (mCountDownTimer != null) {
            mCountDownTimer!!.cancel()
            mCountDownTimer!!.start()
        } else {
            mCountDownTimer = object : CountDownTimer(mTimeLeftInMillis, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    mTimeLeftInMillis = millisUntilFinished
                    updateCountDownText()
                }

                override fun onFinish() {
                    mTimerRunning = false
                    updateCountDownText()
                }
            }.start()
        }
    }

    private fun updateCountDownText() {
        val minutes = (mTimeLeftInMillis / 1000).toInt() / 60
        val seconds = (mTimeLeftInMillis / 1000).toInt() % 60
        val timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
        timerTextView!!.text = timeLeftFormatted
        if (mTimerRunning) {
            resendButton!!.visibility = View.GONE
            timerTextView!!.visibility = View.VISIBLE
        } else {
            AppUtil.dismissProgressDialog()
            resendButton!!.visibility = View.VISIBLE
            timerTextView!!.visibility = View.GONE
        }
    }

    private fun openSuccessRegistrationDialog() {
        RegistrationConfirmedDialog(this@NumberVerificationActivity) {
            val intent = Intent(this@NumberVerificationActivity, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            finish()
        }.show()
    }

    fun sendVerificationCode(phoneNumber: String?) {
        AppUtil.showProgressDialog(this)
        val options = PhoneAuthOptions.newBuilder()
            .setPhoneNumber(phoneNumber!!) // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this) // Activity (for callback binding)
            .setCallbacks(mCallbacks) // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    var mCallbacks: OnVerificationStateChangedCallbacks =
        object : OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                AppUtil.dismissProgressDialog()
                Log.d(
                    NumberVerificationActivity.Companion.TAG,
                    "onVerificationCompleted: " + credential.smsCode
                )
                Toast.makeText(
                    this@NumberVerificationActivity,
                    "onVerificationCompleted:",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onVerificationFailed(e: FirebaseException) {
                AppUtil.dismissProgressDialog()
                Log.d(NumberVerificationActivity.Companion.TAG, "onVerificationFailed", e)
                e.message?.let { showErrorDialog(this@NumberVerificationActivity, "Error!", it) }
            }

            override fun onCodeSent(verificationId: String, token: ForceResendingToken) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                mVerificationId = verificationId
                AppUtil.dismissProgressDialog()
                Toast.makeText(
                    this@NumberVerificationActivity,
                    "OTP Send Successfully.",
                    Toast.LENGTH_SHORT
                ).show()
                Log.d(NumberVerificationActivity.Companion.TAG, "onCodeSent:$verificationId")
            }

            override fun onCodeAutoRetrievalTimeOut(s: String) {
                super.onCodeAutoRetrievalTimeOut(s)
                // Toast.makeText(NumberVerificationActivity.this, "TimeOut:" + s, Toast.LENGTH_SHORT).show();
            }
        }

    private fun verifyCode() {
        if (validate()) {
            AppUtil.showProgressDialog(this)
            val credential = PhoneAuthProvider.getCredential(mVerificationId!!, otpCode!!)
            signInWithPhoneAuthCredential(credential)
        }
    }

    fun firebaseSignUp() {
        if (validate()) {
            AppUtil.showProgressDialog(this)
            val credential = PhoneAuthProvider.getCredential(mVerificationId!!, otpCode!!)
            signInWithPhoneAuthCredential(credential)
        }
    }

    fun performAction() {
        if (form.equals(SignUpActivity.TAG, ignoreCase = true)) {
            firebaseSignUp()
        } else if (form.equals(EditProfileActivity.Companion.TAG, ignoreCase = true)) {
            firebaseSignUp()
        } else if (form.equals(ForgotPasswordActivity.Companion.TAG, ignoreCase = true)) {
            if (phoneNumber != null) {
                firebaseSignUp()
            } else {
                if (validate()) {
                    emailOtpVerifyService()
                }
            }
        } else {
            /*in this case come from PhoneNumberFragment*/
            firebaseSignUp()
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        mAuth!!.signInWithCredential(credential)
            .addOnCompleteListener(this, object : OnCompleteListener<AuthResult?> {
                override fun onComplete(task: Task<AuthResult?>) {
                    AppUtil.dismissProgressDialog()
                    if (task.isSuccessful) {
                        otp_et!!.setText("")
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(
                            NumberVerificationActivity.Companion.TAG,
                            "signInWithCredential:success"
                        )
                        val user = task.result?.user
                        if (form == SignUpActivity.TAG) {
                            registerService()
                        } else if (form == ForgotPasswordActivity.Companion.TAG) {
                            dispatchToResetPasswordActivity()
                        } else if (form.equals(
                                EditProfileActivity.Companion.TAG,
                                ignoreCase = true
                            )
                        ) {
                            updateProfileService()
                        }
                    } else {
                        AppUtil.showErrorDialog(
                            this@NumberVerificationActivity,
                            getString(R.string.error),
                            "Please enter valid OTP."
                        )

                        // Sign in failed, display a message and update the UI
                        Log.d(
                            NumberVerificationActivity.Companion.TAG,
                            "signInWithCredential:failure",
                            task.exception
                        )
                        if (task.exception is FirebaseAuthInvalidCredentialsException) {
                            // The verification code entered was invalid
                        }
                    }
                }
            })
    }

    private fun registerService() {
        AppUtil.showProgressDialog(this@NumberVerificationActivity)
        firebaseDeviceToken =
            SharedPreferenceWriter.getInstance(this).getString(SharedPrefsKey.DEVICE_TOKEN)
        val apiServiceInterface = ApiClient.instance.client
        val call = apiServiceInterface.userSignUp(
            fName, lName, email, phoneNumber, gender,
            dob, AppConstant.DEVICE_TYPE, firebaseDeviceToken, password, latitude, longitude
        )
        call.enqueue(object : Callback<SignUpResponse?> {
            override fun onResponse(
                call: Call<SignUpResponse?>?,
                response: Response<SignUpResponse?>
            ) {
                AppUtil.dismissProgressDialog()
                try {
                    if (response.isSuccessful) {
                        val signUpResponse = response.body()
                        if (signUpResponse!!.status.equals(
                                AppConstant.SUCCESS,
                                ignoreCase = true
                            )
                        ) {
                            signUpResponse.mLogin?.let {
                                saveDataInPrefs(it)
                                openSuccessRegistrationDialog()
                            }

                        } else {
                            AppUtil.showErrorDialog(
                                this@NumberVerificationActivity,
                                "Error!",
                                signUpResponse.message
                            )
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<SignUpResponse?>, t: Throwable?) {
                AppUtil.dismissProgressDialog()
                call.cancel()
            }
        })
    }

    private fun saveDataInPrefs(register: Login) {
        // SharedPreferenceWriter.getInstance(NumberVerificationActivity.this).saveUserDetails(register);
        SharedPreferenceWriter.getInstance(this@NumberVerificationActivity)
            .saveLoginUserDetails(register)
    }

    fun dispatchToResetPasswordActivity() {
        val intent = Intent(this@NumberVerificationActivity, ResetPasswordActivity::class.java)
        intent.putExtra(AppConstant.EMAIL, email)
        intent.putExtra(AppConstant.MOBILE, phoneNumber)
        startActivity(intent)
        finish()
    }

    private fun updateProfileService() {
        AppUtil.showProgressDialog(this@NumberVerificationActivity)
        val profileImgMultipart: MultipartBody.Part =
            AppUtil.createMultiPartForProfileImage(profileDetails!!.image)
        val token = SharedPreferenceWriter.getInstance(this@NumberVerificationActivity)
            .getString(SharedPrefsKey.TOKEN)
        val sessionToken = token.orEmpty().toRequestBody("text/plain".toMediaTypeOrNull())
        val firstName = profileDetails!!.firstName.toRequestBody("text/plain".toMediaTypeOrNull())
        val lastName = profileDetails!!.lastName.toRequestBody("text/plain".toMediaTypeOrNull())
        val dob_body = profileDetails!!.dob?.toRequestBody("text/plain".toMediaTypeOrNull())
        val mobile = profileDetails!!.mobile?.toRequestBody("text/plain".toMediaTypeOrNull())
        val emailId = profileDetails!!.email.toRequestBody("text/plain".toMediaTypeOrNull())
        val gender_body = profileDetails!!.gender?.toRequestBody("text/plain".toMediaTypeOrNull())
        val apiServiceInterface = ApiClient.instance.client
        val call = apiServiceInterface.updateProfile(
            sessionToken,
            firstName,
            lastName,
            mobile,
            emailId,
            dob_body,
            gender_body,
            profileImgMultipart
        )
        call.enqueue(object : Callback<JsonElement?> {
            override fun onResponse(call: Call<JsonElement?>, response: Response<JsonElement?>) {
                AppUtil.dismissProgressDialog()
                try {
                    if (response.isSuccessful) {
                        val `object` =
                            JSONObject(response.body()!!.asJsonObject.toString().trim { it <= ' ' })
                        if (`object`.getString("status")
                                .equals(AppConstant.SUCCESS, ignoreCase = true)
                        ) {
                            // showSuccessDialog(object.getString("message"));
                            Toast.makeText(
                                this@NumberVerificationActivity,
                                "" + `object`.getString("message"),
                                Toast.LENGTH_SHORT
                            ).show()
                            finish()
                        } else {
                            if (`object`.getString("message")
                                    .contains(AppConstant.SESSION_EXPIRED)
                            ) {
                                AppUtil.showLogoutDialog(
                                    this@NumberVerificationActivity,
                                    resources.getString(R.string.error),
                                    `object`.getString("message")
                                )
                            } else {
                                AppUtil.showErrorDialog(
                                    this@NumberVerificationActivity,
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

            override fun onFailure(call: Call<JsonElement?>, t: Throwable) {
                AppUtil.dismissProgressDialog()
                call.cancel()
                AppUtil.showErrorDialog(
                    this@NumberVerificationActivity,
                    resources.getString(R.string.error),
                    t.message
                )
            }
        })
    }

    fun showErrorDialog(context: Context?, alert: String, msg: String) {
        CustomErrorDialog(context, alert, msg) { finish() }.show()
    }

    private fun emailOtpVerifyService() {
        AppUtil.showProgressDialog(this@NumberVerificationActivity)
        val apiServiceInterface: APIServiceInterface = ApiClient.instance.client
        val call = apiServiceInterface.emailOtpVerify(otpCode)
        call.enqueue(object : Callback<JsonElement?> {
            override fun onResponse(call: Call<JsonElement?>, response: Response<JsonElement?>) {
                AppUtil.dismissProgressDialog()
                try {
                    if (response.isSuccessful) {
                        val `object` =
                            JSONObject(response.body()!!.asJsonObject.toString().trim { it <= ' ' })
                        if (`object`.getString("status").equals("SUCCESS", ignoreCase = true)) {
                            dispatchToResetPasswordActivity()
                        } else {
                            AppUtil.showErrorDialog(
                                this@NumberVerificationActivity,
                                "Error!",
                                `object`.getString("message").trim { it <= ' ' })
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<JsonElement?>, t: Throwable) {
                AppUtil.dismissProgressDialog()
                call.cancel()
                AppUtil.showErrorDialog(this@NumberVerificationActivity, "Error!", t.message)
            }
        })
    }

    private fun forgotPasswordService(type: String) {
        AppUtil.showProgressDialog(this)
        val apiServiceInterface = ApiClient.instance.client
        val call = apiServiceInterface.forgotPassword(type, phoneNumber, email)
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
                            Toast.makeText(
                                this@NumberVerificationActivity,
                                resources.getString(R.string.please_check_your_emial_address_to_get_the_otp),
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                this@NumberVerificationActivity,
                                "" + jsonObject.getString("message"),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
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
        private const val TAG = "NumberVerificationActiv"
        private const val START_TIME_IN_MILLIS: Long = 60000
    }
}