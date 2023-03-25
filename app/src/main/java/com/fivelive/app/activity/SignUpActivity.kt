package com.fivelive.app.activity

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Intent
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import androidx.appcompat.app.AppCompatActivity
import com.facebook.*
import com.facebook.GraphRequest.GraphJSONObjectCallback
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.fivelive.app.Model.*
import com.fivelive.app.R
import com.fivelive.app.prefs.SharedPreferenceWriter
import com.fivelive.app.prefs.SharedPrefsKey
import com.fivelive.app.retrofit.APIServiceInterface
import com.fivelive.app.retrofit.ApiClient
import com.fivelive.app.util.*
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.JsonElement
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class SignUpActivity constructor() : AppCompatActivity(), View.OnClickListener {
    private var mFusedLocationProviderClient: FusedLocationProviderClient? = null
    private var mLastLocation: Location? = null
    var backImageView: ImageView? = null
    var signUpButton: Button? = null
    var picker: DatePickerDialog? = null
    var fName_et: EditText? = null
    var lName_et: EditText? = null
    var phoneNumber_et: EditText? = null
    var emailAddress_et: EditText? = null
    var password_et: EditText? = null
    var confirmPassword_et: EditText? = null
    var dateOfBirth_et: EditText? = null
    var fnTIL: TextInputLayout? = null
    var lnTIL: TextInputLayout? = null
    var pnTIL: TextInputLayout? = null
    var eaTIL: TextInputLayout? = null
    var dobTIL: TextInputLayout? = null
    var genderTIL: TextInputLayout? = null
    var pTIL: TextInputLayout? = null
    var cpTIL: TextInputLayout? = null
    var latitude: Double = 0.0
    var longitude: Double = 0.0
    var fName: String? = null
    var lName: String? = null
    var phoneNumber: String? = null
    var email: String? = null
    var dob: String? = null
    var gender: String? = null
    var password: String? = null
    var confirmPassword: String? = null
    var term_and_cond_tv: TextView? = null
    var privacy_policy_tv: TextView? = null
    var facebook_btn: ImageView? = null
    var google_btn: ImageView? = null
    var firebaseDeviceToken: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        firebaseDeviceToken =
            SharedPreferenceWriter.getInstance(this).getString(SharedPrefsKey.DEVICE_TOKEN)
        if ((firebaseDeviceToken == "")) {
            AppUtil.getDeviceToken(this)
        }
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        backImageView = findViewById(R.id.backImageView)
        backImageView?.setOnClickListener(object : View.OnClickListener {
            public override fun onClick(view: View) {
                finish()
            }
        })
        initView()
        initializeGoogleLogIn()
        setUpFacebookLogin()
        facebookLogin()
        addTextWatcherListener()
        openCalender()
        addSpinner()
        deviceLocation
    }

    private fun initView() {
        facebook_btn = findViewById(R.id.facebook_btn)
        google_btn = findViewById(R.id.google_btn)
        privacy_policy_tv = findViewById(R.id.privacy_policy_tv)
        term_and_cond_tv = findViewById(R.id.term_and_cond_tv)
        cpTIL = findViewById(R.id.cpTIL)
        pTIL = findViewById(R.id.pTIL)
        genderTIL = findViewById(R.id.genderTIL)
        dobTIL = findViewById(R.id.dobTIL)
        eaTIL = findViewById(R.id.eaTIL)
        pnTIL = findViewById(R.id.pnTIL)
        fnTIL = findViewById(R.id.fnTIL)
        lnTIL = findViewById(R.id.lnTIL)
        confirmPassword_et = findViewById(R.id.confirmPassword_et)
        password_et = findViewById(R.id.password_et)
        emailAddress_et = findViewById(R.id.emailAddress_et)
        phoneNumber_et = findViewById(R.id.phoneNumber_et)
        lName_et = findViewById(R.id.lName_et)
        fName_et = findViewById(R.id.fName_et)
        dateOfBirth_et = findViewById(R.id.dateOfBirth_et)
        signUpButton = findViewById(R.id.signUpButton)
        signUpButton?.setOnClickListener(this)
        privacy_policy_tv?.setOnClickListener(this)
        term_and_cond_tv?.setOnClickListener(this)
        google_btn?.setOnClickListener(this)
    }

    public override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

    var selectGenderAutoCompTV: AutoCompleteTextView? = null
    var genderList: ArrayList<String>? = null
    var arrayAdapter: ArrayAdapter<String>? = null
    private fun addSpinner() {
        selectGenderAutoCompTV = findViewById(R.id.selectGenderAutoCompTV)
        genderList = ArrayList()
        genderList!!.add("Female")
        genderList!!.add("Male")
        genderList!!.add("Other")
        arrayAdapter = ArrayAdapter(
            getApplicationContext(),
            R.layout.spinner_drop_down_single_row,
            genderList!!
        )
        selectGenderAutoCompTV?.setAdapter(arrayAdapter)
        selectGenderAutoCompTV?.setThreshold(1)
        selectGenderAutoCompTV?.setOnItemClickListener(object : OnItemClickListener {
            public override fun onItemClick(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
                genderTIL!!.setError(null)
                genderTIL!!.setErrorEnabled(false)
            }
        })
    }

    public override fun onClick(view: View) {
        when (view.getId()) {
            R.id.signUpButton ->                 // dispatchToNumberVerificationActivity();
                goToSinUpCall()
            R.id.term_and_cond_tv -> AppUtil.dispatchToTermANdConditionActivity(this@SignUpActivity)
            R.id.privacy_policy_tv -> AppUtil.dispatchToPrivacyPolicy(this@SignUpActivity)
            R.id.google_btn -> googleSignIn()
        }
    }

    private fun openPrivacyPolicyScreen() {
        val browserIntent: Intent =
            Intent(Intent.ACTION_VIEW, Uri.parse(AllAPIs.PRIVACY_POLICY_URL))
        startActivity(browserIntent)
    }

    private fun openTermAndConditionScreen() {
        val browserIntent: Intent = Intent(Intent.ACTION_VIEW, Uri.parse(AllAPIs.TERMS_URL))
        startActivity(browserIntent)
    }

    private fun goToSinUpCall() {
        if (AppUtil.isNetworkAvailable(this@SignUpActivity)) {
            if (validate()) {
                checkEmailService()
                // dispatchToNumberVerificationActivity();
            }
        } else {
            AppUtil.showConnectionError(this)
        }
    }

    fun openCalender() {
        dateOfBirth_et!!.setOnClickListener(object : View.OnClickListener {
            public override fun onClick(view: View) {
                val cldr: Calendar = Calendar.getInstance()
                cldr.add(Calendar.YEAR, -5)
                //myCalendar.add(Calendar.YEAR,-18)
                val day: Int = cldr.get(Calendar.DAY_OF_MONTH)
                val month: Int = cldr.get(Calendar.MONTH)
                val year: Int = cldr.get(Calendar.YEAR)
                // date picker dialog
                picker = DatePickerDialog(this@SignUpActivity, object : OnDateSetListener {
                    public override fun onDateSet(
                        view: DatePicker,
                        year: Int,
                        monthOfYear: Int,
                        dayOfMonth: Int
                    ) {
                        dateOfBirth_et!!.setText(dayOfMonth.toString() + "/" + (monthOfYear + 1) + "/" + year)
                    }
                }, year, month, day)
                //  picker.getDatePicker().setMaxDate(System.currentTimeMillis());
                // picker.getDatePicker().setMaxDate(System.currentTimeMillis());
                val sdf: SimpleDateFormat = SimpleDateFormat("dd/MM/yyyy")
                var d: Date? = null
                try {
                    d = sdf.parse("31/12/2015")
                } catch (e: ParseException) {
                    e.printStackTrace()
                }
                picker!!.getDatePicker().setMaxDate(d!!.getTime())
                picker!!.show()
            }
        })
    }

    fun addTextWatcherListener() {
        fName_et!!.addTextChangedListener(TextWatcher(fnTIL, fName_et))
        lName_et!!.addTextChangedListener(TextWatcher(lnTIL, lName_et))
        phoneNumber_et!!.addTextChangedListener(TextWatcher(pnTIL, phoneNumber_et))
        emailAddress_et!!.addTextChangedListener(TextWatcher(eaTIL, emailAddress_et))
        dateOfBirth_et!!.addTextChangedListener(TextWatcher(dobTIL, dateOfBirth_et))
        password_et!!.addTextChangedListener(TextWatcher(pTIL, password_et))
        confirmPassword_et!!.addTextChangedListener(TextWatcher(cpTIL, confirmPassword_et))
    }

    private fun validate(): Boolean {
        fName = fName_et!!.getText().toString().trim({ it <= ' ' })
        lName = lName_et!!.getText().toString().trim({ it <= ' ' })
        phoneNumber = phoneNumber_et!!.getText().toString().trim({ it <= ' ' })
        email = emailAddress_et!!.getText().toString().trim({ it <= ' ' })
        dob = dateOfBirth_et!!.getText().toString().trim({ it <= ' ' })
        gender = selectGenderAutoCompTV!!.getText().toString()
        password = password_et!!.getText().toString().trim({ it <= ' ' })
        confirmPassword = confirmPassword_et!!.getText().toString().trim({ it <= ' ' })
        if (TextUtils.isEmpty(fName)) {
            fnTIL!!.setError("Enter First Name.")
            return false
        }
        if (TextUtils.isEmpty(lName)) {
            lnTIL!!.setError("Enter Last Name.")
            return false
        }
        if (TextUtils.isEmpty(phoneNumber)) {
            pnTIL!!.setError("Enter Phone Number.")
            return false
        }
        if (!AppUtil.isValidMobile(phoneNumber!!)) {
            pnTIL!!.setError("Please enter Valid Phone Number.")
            return false
        }

        /*if(TextUtils.isEmpty(email)){
            eaTIL.setError("Enter Email Address.");
            return false;
        }*/if (email != null && !(email == "")) {
            if (!AppUtil.isValidMail(email)) {
                eaTIL!!.setError("Please enter valid Email ID.")
                return false
            }
        }
        if (TextUtils.isEmpty(dob)) {
            dobTIL!!.setError("Select Date of Birth.")
            return false
        }
        if (TextUtils.isEmpty(gender)) {
            genderTIL!!.setError("Select Gender.")
            return false
        }
        if (TextUtils.isEmpty(password)) {
            pTIL!!.setError("Enter password.")
            return false
        }
        if (!AppUtil.isValidPassword(password)) {
            AppUtil.showErrorDialog(
                this,
                getResources().getString(R.string.app_name),
                getResources().getString(R.string.paasword_validation_msg)
            )
            return false
        }
        if (TextUtils.isEmpty(confirmPassword)) {
            cpTIL!!.setError("Enter confirm password.")
            return false
        }
        if (!password.equals(confirmPassword, ignoreCase = true)) {
            cpTIL!!.setError(getResources().getString(R.string.confirm_password_not_matched))
            return false
        }
        return true
    }

    private fun checkEmailService() {
        AppUtil.showProgressDialog(this@SignUpActivity)
        val apiServiceInterface: APIServiceInterface = ApiClient.instance.client
        val call: Call<JsonElement> = apiServiceInterface.checkEmail(phoneNumber, email)
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
                        if (`object`.getString("status")
                                .equals(AppConstant.SUCCESS, ignoreCase = true)
                        ) {
                            dispatchToNumberVerificationActivity()
                        } else {
                            AppUtil.showErrorDialog(
                                this@SignUpActivity,
                                "Error!",
                                `object`.getString("message")
                            )
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            public override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                AppUtil.dismissProgressDialog()
                call.cancel()
                AppUtil.showErrorDialog(this@SignUpActivity, "Error!", t.message)
            }
        })
    }

    fun dispatchToNumberVerificationActivity() {
        val intent: Intent = Intent(this@SignUpActivity, NumberVerificationActivity::class.java)
        intent.putExtra(AppConstant.FIRST_NAME, fName)
        intent.putExtra(AppConstant.LAST_NAME, lName)
        intent.putExtra(AppConstant.PHONE_NUMBER, phoneNumber)
        intent.putExtra(AppConstant.EMAIL, email)
        intent.putExtra(AppConstant.DATE_OF_BIRTH, dob)
        intent.putExtra(AppConstant.GENDER, gender)
        intent.putExtra(AppConstant.PASSWORD, password)
        intent.putExtra(AppConstant.FROM, TAG)
        intent.putExtra(AppConstant.LATITUDE, latitude)
        intent.putExtra(AppConstant.LONGITUDE, longitude)
        startActivity(intent)
        // finish();
    }

    @get:SuppressLint("MissingPermission")
    private val deviceLocation: Unit
        private get() {
            mFusedLocationProviderClient!!.getLastLocation()
                .addOnCompleteListener(object : OnCompleteListener<Location?> {
                    public override fun onComplete(task: Task<Location?>) {
                        if (task.isSuccessful()) {
                            mLastLocation = task.getResult()
                            if (mLastLocation != null) {
                                latitude = mLastLocation!!.getLatitude()
                                longitude = mLastLocation!!.getLongitude()
                                Log.d(TAG, "onComplete: ")
                            }
                        } else {
                            Toast.makeText(
                                this@SignUpActivity,
                                "Unable to get last Device Location",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                })
        }
    var callbackManager: CallbackManager? = null
    var loginManager: LoginManager? = null
    fun setUpFacebookLogin() {
        facebook_btn!!.setOnClickListener(object : View.OnClickListener {
            public override fun onClick(v: View) {
                loginManager!!.logInWithReadPermissions(this@SignUpActivity, Arrays.asList("email"))
                Log.d(TAG, "onClick: ")
            }
        })
    }

    fun facebookLogin() {
        loginManager = LoginManager.getInstance()
        callbackManager = CallbackManager.Factory.create()
        loginManager?.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            public override fun onSuccess(loginResult: LoginResult) {
                val request: GraphRequest = GraphRequest.newMeRequest(
                    loginResult.getAccessToken(),
                    object : GraphJSONObjectCallback {
                        public override fun onCompleted(
                            `object`: JSONObject?,
                            response: GraphResponse?
                        ) {
                            if (`object` != null) {
                                try {
                                    var email: String = ""
                                    if (`object`.toString().contains("email")) {
                                        email = `object`.getString("email")
                                    }
                                    val fbUserID: String = `object`.getString("id")
                                    val image_url: String =
                                        "https://graph.facebook.com/" + fbUserID + "/picture?type=normal"
                                    val displayName: Array<String> =
                                        `object`.getString("name").trim({ it <= ' ' })
                                            .split(" ".toRegex()).dropLastWhile({ it.isEmpty() })
                                            .toTypedArray()
                                    val firstName: String = displayName.get(0)
                                    val lastName: String = displayName.get(1)
                                    disconnectFromFacebook()
                                    val deviceType: String = AppConstant.DEVICE_TYPE
                                    val deviceToken: String? = firebaseDeviceToken
                                    val signUpType: String = "fb"
                                    facebookLogInService(
                                        signUpType,
                                        firstName,
                                        lastName,
                                        email,
                                        image_url,
                                        deviceType,
                                        deviceToken,
                                        fbUserID
                                    )
                                } catch (e: JSONException) {
                                    e.printStackTrace()
                                } catch (e: NullPointerException) {
                                    e.printStackTrace()
                                }
                            }
                        }
                    })
                val parameters: Bundle = Bundle()
                parameters.putString("fields", "id, name, email, gender, birthday")
                request.parameters = parameters
                request.executeAsync()
            }

            public override fun onCancel() {
                Log.v("LoginScreen", "---onCancel")
            }

            public override fun onError(error: FacebookException) {
                // here write code when get error
                //  disconnectFromFacebook();
                Log.v("LoginScreen", "----onError: " + error.message)
                Toast.makeText(this@SignUpActivity, "" + error.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun disconnectFromFacebook() {
        if (AccessToken.getCurrentAccessToken() == null) {
            return  // already logged out
        }
        GraphRequest(AccessToken.getCurrentAccessToken(),
            "/me/permissions/",
            null,
            HttpMethod.DELETE,
            object : GraphRequest.Callback {
                public override fun onCompleted(graphResponse: GraphResponse) {
                    LoginManager.getInstance().logOut()
                }
            })
            .executeAsync()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager!!.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                RC_SIGN_IN -> {
                    val task: Task<GoogleSignInAccount> =
                        GoogleSignIn.getSignedInAccountFromIntent(data)
                    handleSignInResult(task)
                }
            }
        }
    }

    private fun facebookLogInService(
        signUp_type: String, fName: String, lName: String, email: String, image: String,
        deviceType: String, deviceToken: String?, facebookId: String
    ) {
        AppUtil.showProgressDialog(this@SignUpActivity)
        val apiServiceInterface: APIServiceInterface = ApiClient.instance.client
        val call: Call<LoginResponse> = apiServiceInterface.facebookLogin(
            signUp_type,
            fName,
            lName,
            email,
            image,
            deviceType,
            deviceToken,
            facebookId,
            latitude,
            longitude
        )
        call.enqueue(object : Callback<LoginResponse?> {
            public override fun onResponse(
                call: Call<LoginResponse?>,
                response: Response<LoginResponse?>
            ) {
                AppUtil.dismissProgressDialog()
                try {
                    if (response.isSuccessful()) {
                        val detailsResponse: LoginResponse? = response.body()
                        if (detailsResponse!!.status
                                .equals(AppConstant.SUCCESS, ignoreCase = true)
                        ) {
                            //SocialMediaLogin mediaLogin = detailsResponse.getSocialMediaLogin();
                            saveDataINPreferences(detailsResponse.login)
                            goToHomeActivity()
                        } else {
                            AppUtil.showErrorDialog(
                                this@SignUpActivity,
                                getResources().getString(R.string.error),
                                detailsResponse.message
                            )
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            public override fun onFailure(call: Call<LoginResponse?>, t: Throwable) {
                AppUtil.dismissProgressDialog()
                call.cancel()
                AppUtil.showErrorDialog(this@SignUpActivity, "Error!", t.message)
            }
        })
    }

    private fun saveDataINPreferences(login: Login) {
        SharedPreferenceWriter.getInstance(this@SignUpActivity).saveLoginUserDetails(login)
    }

    private fun goToHomeActivity() {
        val intent: Intent = Intent(this@SignUpActivity, HomeActivity::class.java)
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        finish()
    }

    var mGoogleSignInClient: GoogleSignInClient? = null
    fun initializeGoogleLogIn() {
        val gso: GoogleSignInOptions =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail().build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    private fun googleSignIn() {
        val account: GoogleSignInAccount? = GoogleSignIn.getLastSignedInAccount(this)
        //  Log.d(TAG, "googleSignIn: ");
        if (account != null) {
            // updateUI(account);
            signOut()
        } else {
            val signInIntent: Intent = mGoogleSignInClient!!.getSignInIntent()
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }
    }

    private fun signOut() {
        mGoogleSignInClient!!.signOut()
            .addOnCompleteListener(this, object : OnCompleteListener<Void?> {
                public override fun onComplete(task: Task<Void?>) {
                    googleSignIn()
                }
            })
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account: GoogleSignInAccount = completedTask.getResult(
                ApiException::class.java
            )
            // Signed in successfully, show authenticated UI.
            updateUI(account)
        } catch (e: ApiException) {
            Log.d(TAG, "handleSignInResult: ")
            Toast.makeText(this, "" + e.message, Toast.LENGTH_SHORT).show()
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
        }
    }

    private fun updateUI(account: GoogleSignInAccount) {
        val displayName: Array<String> =
            account.getDisplayName()!!.trim({ it <= ' ' }).split(" ".toRegex())
                .dropLastWhile({ it.isEmpty() }).toTypedArray()
        val firstName: String = displayName.get(0)
        val lastName: String = displayName.get(1)
        val signUpType: String = "google"
        val image: Uri? = account.getPhotoUrl()
        var imagePath: String = ""
        if (image != null) {
            imagePath = image.toString()
        }
        val googleId: String? = account.getId()
        val email: String? = account.getEmail()
        val mobile: String = ""
        val deviceType: String = AppConstant.DEVICE_TYPE
        val deviceToken: String? = firebaseDeviceToken
        socialMediaGoogleLogInService(
            signUpType,
            firstName,
            lastName,
            imagePath,
            email,
            mobile,
            deviceType,
            deviceToken,
            googleId
        )
    }

    private fun socialMediaGoogleLogInService(
        signUp_type: String,
        fName: String,
        lName: String,
        image: String,
        email: String?,
        mobile: String,
        device_Type: String,
        device_Token: String?,
        google_Id: String?
    ) {
        AppUtil.showProgressDialog(this@SignUpActivity)
        val apiServiceInterface: APIServiceInterface = ApiClient.instance.client
        val call: Call<LoginResponse> = apiServiceInterface.socialMediaLogin(
            signUp_type, fName, lName, image, email, mobile,
            device_Type, device_Token, google_Id, latitude, longitude
        )
        call.enqueue(object : Callback<LoginResponse?> {
            public override fun onResponse(
                call: Call<LoginResponse?>,
                response: Response<LoginResponse?>
            ) {
                AppUtil.dismissProgressDialog()
                try {
                    if (response.isSuccessful()) {
                        // JSONObject object = new JSONObject(response.body().getAsJsonObject().toString().trim());
                        val detailsResponse: LoginResponse? = response.body()
                        if (detailsResponse!!.status.equals("SUCCESS", ignoreCase = true)) {
                            saveDataINPreferences(detailsResponse.login)
                            goToHomeActivity()
                        } else {
                            AppUtil.showErrorDialog(
                                this@SignUpActivity,
                                getResources().getString(R.string.error),
                                detailsResponse.message
                            )
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            public override fun onFailure(call: Call<LoginResponse?>, t: Throwable) {
                AppUtil.dismissProgressDialog()
                call.cancel()
                AppUtil.showErrorDialog(this@SignUpActivity, "Error!", t.message)
            }
        })
    }

    companion object {
        @JvmField
        val TAG: String = SignUpActivity::class.java.getName()
        private val RC_SIGN_IN: Int = 1023
    }
}