package com.fivelive.app.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.Intent.*
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
import android.text.TextUtils
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.fivelive.app.Model.*
import com.fivelive.app.R
import com.fivelive.app.dialog.CustomSkipUserDialog
import com.fivelive.app.prefs.SharedPreferenceWriter
import com.fivelive.app.prefs.SharedPrefsKey
import com.fivelive.app.retrofit.ApiClient
import com.fivelive.app.util.*
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task
import com.google.android.material.textfield.TextInputLayout
import org.json.JSONException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class LoginActivity : AppCompatActivity(), View.OnClickListener {
    var daysRecyclerView: RecyclerView? = null
    var signUpTextView: TextView? = null
    var forgotPasswordTextView: TextView? = null
    var editTextView: TextView? = null
    var term_and_cond_tv: TextView? = null
    var privacy_policy_tv: TextView? = null
    var loginButton: Button? = null
    var backImageView: ImageView? = null
    var googleImageView: ImageView? = null
    var facebookImageView: ImageView? = null
    var emailAddress_et: EditText? = null
    var password_et: EditText? = null
    var emailTIL: TextInputLayout? = null
    var passwordTIL: TextInputLayout? = null
    var mGoogleSignInClient: GoogleSignInClient? = null
    var callbackManager: CallbackManager? = null
    var loginManager: LoginManager? = null
    private var mFusedLocationProviderClient: FusedLocationProviderClient? = null
    private var mLastLocation: Location? = null
    var latitude: Double? = null
    var longitude: Double? = null
    var daysList: List<String> = ArrayList()
    var inputValue: String? = null
    var password: String? = null
    var email: String? = null
    var phone: String? = null
    var type: String? = null
    var firebaseDeviceToken: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        firebaseDeviceToken = SharedPreferenceWriter.getInstance(this@LoginActivity)
            .getString(SharedPrefsKey.DEVICE_TOKEN)
        backImageView = findViewById(R.id.backImageView)
        backImageView?.visibility = View.GONE
        initView()
        addTextWatcherListener()
        initializeGoogleLogIn()
        setUpFacebookLogin()
        facebookLogin()
    }

    fun initView() {
        editTextView = findViewById(R.id.editTextView)
        editTextView?.visibility = View.VISIBLE
        val typeface = ResourcesCompat.getFont(this, R.font.whitney_semibold)
        editTextView?.text = "SKIP"
        editTextView?.typeface = typeface
        editTextView?.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
        facebookImageView = findViewById(R.id.facebookImageView)
        googleImageView = findViewById(R.id.googleImageView)
        emailTIL = findViewById(R.id.emailTIL)
        passwordTIL = findViewById(R.id.passwordTIL)
        emailAddress_et = findViewById(R.id.emailAddress_et)
        password_et = findViewById(R.id.password_et)
        loginButton = findViewById(R.id.loginButton)
        signUpTextView = findViewById(R.id.signUpTextView)
        forgotPasswordTextView = findViewById(R.id.forgotPasswordTextView)
        term_and_cond_tv = findViewById(R.id.term_and_cond_tv)
        privacy_policy_tv = findViewById(R.id.privacy_policy_tv)
        signUpTextView?.setOnClickListener(this)
        forgotPasswordTextView?.setOnClickListener(this)
        loginButton?.setOnClickListener(this)
        googleImageView?.setOnClickListener(this)
        editTextView?.setOnClickListener(this)
        term_and_cond_tv?.setOnClickListener(this)
        privacy_policy_tv?.setOnClickListener(this)
    }

    fun setUpFacebookLogin() {
        facebookImageView!!.setOnClickListener {
            loginManager!!.logInWithReadPermissions(this@LoginActivity, Arrays.asList("email"))
            Log.d(TAG, "onClick: ")
        }
    }

    fun facebookLogin() {
        loginManager = LoginManager.getInstance()
        callbackManager = CallbackManager.Factory.create()
        loginManager?.registerCallback(callbackManager, object : FacebookCallback<LoginResult?> {
            override fun onSuccess(loginResult: LoginResult?) {
                val request =
                    GraphRequest.newMeRequest(loginResult?.accessToken) { `object`, response ->
                        if (`object` != null) {
                            try {
                                var email = ""
                                if (`object`.toString().contains("email")) {
                                    email = `object`.getString("email")
                                }
                                val fbUserID = `object`.getString("id")
                                val image_url =
                                    "https://graph.facebook.com/$fbUserID/picture?type=normal"
                                val displayName = `object`.getString("name").trim { it <= ' ' }
                                    .split(" ".toRegex()).dropLastWhile { it.isEmpty() }
                                    .toTypedArray()
                                val firstName = displayName[0]
                                val lastName = displayName[1]
                                disconnectFromFacebook()
                                val deviceType = AppConstant.DEVICE_TYPE
                                val deviceToken = firebaseDeviceToken
                                val signUpType = "fb"
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
                val parameters = Bundle()
                parameters.putString("fields", "id, name, email, gender, birthday")
                request.parameters = parameters
                request.executeAsync()
            }

            override fun onCancel() {
                Log.v("LoginScreen", "---onCancel")
            }

            override fun onError(error: FacebookException) {
                // here write code when get error
                //  disconnectFromFacebook();
                Log.v("LoginScreen", "----onError: " + error.message)
                Toast.makeText(this@LoginActivity, "" + error.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun disconnectFromFacebook() {
        if (AccessToken.getCurrentAccessToken() == null) {
            return  // already logged out
        }
        GraphRequest(
            AccessToken.getCurrentAccessToken(),
            "/me/permissions/",
            null,
            HttpMethod.DELETE,
            callback = { LoginManager.getInstance().logOut() })
            .executeAsync()
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.signUpTextView -> dispatchToSignUpScreen()
            R.id.forgotPasswordTextView -> dispatchToForgotPasswordActivity()
            R.id.loginButton -> dispatchToLoginService()
            R.id.googleImageView -> googleSignIn()
            R.id.term_and_cond_tv -> AppUtil.dispatchToTermANdConditionActivity(this@LoginActivity)
            R.id.privacy_policy_tv -> AppUtil.dispatchToPrivacyPolicy(this@LoginActivity)
            R.id.editTextView -> showCustomSkipUserDialog()
        }
    }

    fun showCustomSkipUserDialog() {
        CustomSkipUserDialog(this) { name ->
            if (name == AppConstant.REGISTER) {
                // Button -- Register  10 Day Free Trial with 100% of Business Shown and No Ads
                dispatchToSignUpScreen()
            } else {
                dispatchToSkipService()
                //  Button --Limited  Free Version with Ads and 60% of Business Shown
            }
        }.show()
    }

    private fun openTermAndConditionScreen() {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(AllAPIs.TERMS_URL))
        startActivity(browserIntent)
    }

    private fun dispatchToSkipService() {
        AppUtil.showProgressDialog(this@LoginActivity)
        val apiServiceInterface = ApiClient.instance.client
        val call = apiServiceInterface.guestLogin()
        call.enqueue(object : Callback<GuestLogin?> {
            override fun onResponse(call: Call<GuestLogin?>?, response: Response<GuestLogin?>) {
                AppUtil.dismissProgressDialog()
                try {
                    if (response.isSuccessful) {
                        val guestLogin = response.body()
                        if (guestLogin!!.status.equals(AppConstant.SUCCESS, ignoreCase = true)) {
                            SharedPreferenceWriter.getInstance(this@LoginActivity)
                                .writeStringValue(SharedPrefsKey.TOKEN, guestLogin.token)
                            SharedPreferenceWriter.getInstance(this@LoginActivity)
                                .writeStringValue(SharedPrefsKey.USER_TYPE, guestLogin.userType)
                            goToHomeActivity()
                        } else {
                            AppUtil.showErrorDialog(
                                this@LoginActivity,
                                this@LoginActivity.resources.getString(R.string.error),
                                guestLogin.message
                            )
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<GuestLogin?>, t: Throwable) {
                AppUtil.dismissProgressDialog()
                call.cancel()
                AppUtil.showErrorDialog(
                    this@LoginActivity,
                    this@LoginActivity.resources.getString(R.string.error),
                    t.message
                )
            }
        })
    }

    fun initializeGoogleLogIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail().build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    private fun googleSignIn() {
        val account = GoogleSignIn.getLastSignedInAccount(this)
        //  Log.d(TAG, "googleSignIn: ");
        if (account != null) {
            // updateUI(account);
            signOut()
        } else {
            val signInIntent = mGoogleSignInClient!!.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }
    }

    private fun signOut() {
        mGoogleSignInClient!!.signOut()
            .addOnCompleteListener(this) { googleSignIn() }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager!!.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CHECK_SETTINGS -> when (resultCode) {
                RESULT_OK -> {}
                RESULT_CANCELED -> startActivityForResult(
                    Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS),
                    REQUEST_MANUALLY_CHANGE_SETTINGS
                )
            }
            REQUEST_MANUALLY_CHANGE_SETTINGS -> {
                val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
                val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                if (!isGpsEnabled) {
                    showLocationDescriptionDialog()
                } else {
                    getDeviceLocation()
                }
            }
            RC_SIGN_IN -> {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                handleSignInResult(task)
            }
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(
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
        val displayName = account.displayName!!.trim { it <= ' ' }
            .split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val firstName = displayName[0]
        val lastName = displayName[1]
        val signUpType = "google"
        val image = account.photoUrl
        var imagePath = ""
        if (image != null) {
            imagePath = image.toString()
        }
        val googleId = account.id
        val email = account.email
        val mobile = ""
        val deviceType = AppConstant.DEVICE_TYPE
        val deviceToken = firebaseDeviceToken
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

    private fun dispatchToHomeScreen() {
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }

    fun dispatchToSignUpScreen() {
        val intent = Intent(this, SignUpActivity::class.java)
        startActivity(intent)
        // overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
    }

    fun dispatchToForgotPasswordActivity() {
        val intent = Intent(this, ForgotPasswordActivity::class.java)
        startActivity(intent)
        // overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private fun dispatchToLoginService() {
        AppUtil.hideKeyboard(this)
        if (AppUtil.isNetworkAvailable(this@LoginActivity)) {
            if (validate()) {
                loginService()
            }
        } else {
            AppUtil.showConnectionError(this@LoginActivity)
        }
    }

    private fun addTextWatcherListener() {
        emailAddress_et!!.addTextChangedListener(TextWatcher(emailTIL, emailAddress_et))
        password_et!!.addTextChangedListener(TextWatcher(passwordTIL, password_et))
    }

    private fun validate(): Boolean {
        inputValue = emailAddress_et!!.text.toString().trim { it <= ' ' }
        password = password_et!!.text.toString().trim { it <= ' ' }
        if (TextUtils.isEmpty(inputValue)) {
            emailTIL!!.error = "Enter Email/Phone Number."
            return false
        }
        if (!inputValue!!.isEmpty()) {
            if (!TextUtils.isDigitsOnly(emailAddress_et!!.text.toString().trim { it <= ' ' })) {
                type = "email"
                email = inputValue
                if (!AppUtil.isValidMail(email)) {
                    emailTIL!!.error = "Please Enter valid Email."
                    return false
                }
            } else {
                type = "phone"
                phone = inputValue
                if (!AppUtil.isValidMobile(phone!!)) {
                    emailTIL!!.error = "Please Enter valid Phone Number."
                    return false
                }
            }
        }
        if (TextUtils.isEmpty(password)) {
            passwordTIL!!.error = "Enter Password."
            return false
        }
        return true
    }

    private fun loginService() {
        if (latitude == null || longitude == null) {
            requestPermissions()
            return
        }
        AppUtil.showProgressDialog(this@LoginActivity)
        val apiServiceInterface = ApiClient.instance.client
        val call = apiServiceInterface.userLogin(
            type,
            password,
            phone,
            email,
            AppConstant.DEVICE_TYPE,
            firebaseDeviceToken,
            latitude!!,
            longitude!!
        )
        call.enqueue(object : Callback<LoginResponse?> {
            override fun onResponse(
                call: Call<LoginResponse?>?,
                response: Response<LoginResponse?>
            ) {
                AppUtil.dismissProgressDialog()
                try {
                    if (response.isSuccessful) {
                        // JSONObject object = new JSONObject(response.body().getAsJsonObject().toString().trim());
                        val detailsResponse = response.body()
                        if (detailsResponse!!.status.equals("SUCCESS", ignoreCase = true)) {
                            saveDataINPreferences(detailsResponse.login)
                            goToHomeActivity()
                        } else {
                            AppUtil.showErrorDialog(
                                this@LoginActivity,
                                resources.getString(R.string.error),
                                detailsResponse.message
                            )
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<LoginResponse?>, t: Throwable) {
                AppUtil.dismissProgressDialog()
                call.cancel()
                AppUtil.showErrorDialog(
                    this@LoginActivity,
                    resources.getString(R.string.error),
                    t.message
                )
            }
        })
    }

    private fun saveDataINPreferences(login: Login) {
        SharedPreferenceWriter.getInstance(this@LoginActivity).saveLoginUserDetails(login)
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
        if (latitude == null || longitude == null) {
            requestPermissions()
            return
        }
        AppUtil.showProgressDialog(this@LoginActivity)
        val apiServiceInterface = ApiClient.instance.client
        val call = apiServiceInterface.socialMediaLogin(
            signUp_type, fName, lName, image, email, mobile,
            device_Type, device_Token, google_Id, latitude!!, longitude!!
        )
        call.enqueue(object : Callback<LoginResponse?> {
            override fun onResponse(
                call: Call<LoginResponse?>?,
                response: Response<LoginResponse?>
            ) {
                AppUtil.dismissProgressDialog()
                try {
                    if (response.isSuccessful) {
                        // JSONObject object = new JSONObject(response.body().getAsJsonObject().toString().trim());
                        val detailsResponse = response.body()
                        if (detailsResponse!!.status.equals("SUCCESS", ignoreCase = true)) {
                            saveDataINPreferences(detailsResponse.login)
                            goToHomeActivity()
                        } else {
                            AppUtil.showErrorDialog(
                                this@LoginActivity,
                                resources.getString(R.string.error),
                                detailsResponse.message
                            )
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<LoginResponse?>, t: Throwable) {
                AppUtil.dismissProgressDialog()
                call.cancel()
                AppUtil.showErrorDialog(this@LoginActivity, "Error!", t.message)
            }
        })
    }

    private fun goToHomeActivity() {
        val intent = Intent(this@LoginActivity, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun facebookLogInService(
        signUp_type: String,
        fName: String,
        lName: String,
        email: String,
        image: String,
        deviceType: String,
        deviceToken: String?,
        facebookId: String
    ) {
        if (latitude == null || longitude == null) {
            requestPermissions()
            return
        }
        AppUtil.showProgressDialog(this@LoginActivity)
        val apiServiceInterface = ApiClient.instance.client
        val call = apiServiceInterface.facebookLogin(
            signUp_type,
            fName,
            lName,
            email,
            image,
            deviceType,
            deviceToken,
            facebookId,
            latitude!!,
            longitude!!
        )
        call.enqueue(object : Callback<LoginResponse?> {
            override fun onResponse(
                call: Call<LoginResponse?>?,
                response: Response<LoginResponse?>
            ) {
                AppUtil.dismissProgressDialog()
                try {
                    if (response.isSuccessful) {
                        val detailsResponse = response.body()
                        if (detailsResponse!!.status.equals(
                                AppConstant.SUCCESS,
                                ignoreCase = true
                            )
                        ) {
                            //SocialMediaLogin mediaLogin = detailsResponse.getSocialMediaLogin();
                            saveDataINPreferences(detailsResponse.login)
                            goToHomeActivity()
                        } else {
                            AppUtil.showErrorDialog(
                                this@LoginActivity,
                                resources.getString(R.string.error),
                                detailsResponse.message
                            )
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<LoginResponse?>, t: Throwable) {
                AppUtil.dismissProgressDialog()
                call.cancel()
                AppUtil.showErrorDialog(this@LoginActivity, "Error!", t.message)
            }
        })
    }

    public override fun onStart() {
        super.onStart()
        if (!checkLocationPermissions(this)) {
            requestPermissions()
        } else {
            getDeviceLocation()
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
                ), REQUEST_PERMISSIONS_REQUEST_CODE
            )
        }
    }

    val builder: AlertDialog.Builder by lazy {
        AlertDialog.Builder(this)
            .setTitle("Permission Required!")
            // builder.setMessage("Turn on your GPS and give access 5Live to your location.");
            .setMessage("Dear user, we require permission to access your location. According your location we provide business.We at 5Live are committed to your privacy and protection of your personal information. Kindly provide the required permission to proceed further.")
            .setNegativeButton("Settings") { dialog, i ->
                val intent = Intent(ACTION_APPLICATION_DETAILS_SETTINGS)
                with(intent) {
                    data = Uri.fromParts("package", packageName, null)
                    addCategory(CATEGORY_DEFAULT)
                    addFlags(FLAG_ACTIVITY_NEW_TASK)
                    addFlags(FLAG_ACTIVITY_NO_HISTORY)
                    addFlags(FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
                }

                startActivity(intent)
            }
            .setPositiveButton("OK") { dialogInterface, i ->
                ActivityCompat.requestPermissions(
                    this@LoginActivity,
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ),
                    REQUEST_PERMISSIONS_REQUEST_CODE
                )

                // startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), REQUEST_MANUALLY_CHANGE_SETTINGS);
            }
            .setCancelable(false)
    }
    private val alertDialog by lazy {
        builder.create()
    }

    private fun showLocationDescriptionDialog() {
        alertDialog.show()
        alertDialog.apply {
            setCanceledOnTouchOutside(false)
            val textView = window!!.findViewById<View>(android.R.id.message) as TextView
            val alertTitle = window!!.findViewById<View>(R.id.alertTitle) as TextView
            val button1 = window!!.findViewById<View>(android.R.id.button1) as Button
            val button2 = window!!.findViewById<View>(android.R.id.button2) as Button
            val typeface = ResourcesCompat.getFont(this@LoginActivity, R.font.whitney_semibold)
            val typeface1 = ResourcesCompat.getFont(this@LoginActivity, R.font.whitneymedium)
            textView.typeface = typeface1
            alertTitle.typeface = typeface
            button1.typeface = typeface
            button2.typeface = typeface
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        println()
        when (requestCode) {
            REQUEST_PERMISSIONS_REQUEST_CODE -> if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted.
                // Toast.makeText(this, "granted", Toast.LENGTH_SHORT).show();
                getDeviceLocation()
            } else {
                // Permission denied.
                showLocationDescriptionDialog()
            }
        }
    }

    // Toast.makeText(LoginActivity.this, "Location found:"+latitude+" "+longitude, Toast.LENGTH_SHORT).show();

    private fun getDeviceLocation() {
        mFusedLocationProviderClient!!.lastLocation.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                mLastLocation = task.result
                if (mLastLocation != null) {
                    latitude = mLastLocation!!.latitude
                    longitude = mLastLocation!!.longitude
                    // Toast.makeText(LoginActivity.this, "Location found:"+latitude+" "+longitude, Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(
                    this@LoginActivity,
                    "Unable to get last Device Location",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    companion object {
        private val TAG = LoginActivity::class.java.name
        private const val RC_SIGN_IN = 1023
        private const val REQUEST_PERMISSIONS_REQUEST_CODE = 34
        private const val REQUEST_MANUALLY_CHANGE_SETTINGS = 3457
        private const val REQUEST_CHECK_SETTINGS = 35
        fun checkLocationPermissions(context: Context?): Boolean {
            return (ContextCompat.checkSelfPermission(
                context!!,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED)
        }


    }
}