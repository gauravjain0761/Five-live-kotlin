package com.fivelive.app.activity


import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.fivelive.app.Model.ProfileDetails
import com.fivelive.app.R
import com.fivelive.app.activity.NumberVerificationActivity
import com.fivelive.app.activity.TakeImage
import com.fivelive.app.dialog.AddImageBottomSheet
import com.fivelive.app.dialog.DatePickerFragment
import com.fivelive.app.dialog.DatePickerFragment.DateCallbackListener
import com.fivelive.app.prefs.SharedPreferenceWriter
import com.fivelive.app.prefs.SharedPrefsKey
import com.fivelive.app.retrofit.ApiClient
import com.fivelive.app.util.AppConstant
import com.fivelive.app.util.AppUtil
import com.fivelive.app.util.PhotoUtil
import com.fivelive.app.util.TextWatcher
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.JsonElement
import de.hdodenhof.circleimageview.CircleImageView
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.MultipartBody.Part.Companion.createFormData
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class EditProfileActivity : AppCompatActivity(), View.OnClickListener,
    AddImageBottomSheet.ItemClickListener, DateCallbackListener {
    var backImageView: ImageView? = null
    var profile_imv: CircleImageView? = null
    var fName_et: EditText? = null
    var lName_et: EditText? = null
    var phoneNumber_et: EditText? = null
    var emailAddress_et: EditText? = null
    var dateOfBirth_et: EditText? = null
    var fnTIL: TextInputLayout? = null
    var lnTIL: TextInputLayout? = null
    var pnTIL: TextInputLayout? = null
    var emailTIL: TextInputLayout? = null
    var dobTIL: TextInputLayout? = null
    var genderTIL: TextInputLayout? = null
    var selectGenderAutoCompTV: AutoCompleteTextView? = null
    var genderList: ArrayList<String>? = null
    var arrayAdapter: ArrayAdapter<String>? = null
    var saveButton: Button? = null
    var changePhoto_tv: TextView? = null
    var profileImageUri: Uri? = null
    var profileImagePath: String? = ""
    var profileImgMultipart: MultipartBody.Part? = null
    var fName: String? = null
    var lName: String? = null
    var phoneNumber: String? = null
    var email: String? = null
    var dob: String? = null
    var gender: String? = null
    var profileDetails: ProfileDetails? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        initView()
        addSpinner()
        dataFromIntent
    }

    private fun initView() {
        profile_imv = findViewById(R.id.profile_imv)
        backImageView = findViewById(R.id.backImageView)
        backImageView?.setOnClickListener(this)
        dobTIL = findViewById(R.id.dobTIL)
        genderTIL = findViewById(R.id.genderTIL)
        emailTIL = findViewById(R.id.emailTIL)
        pnTIL = findViewById(R.id.pnTIL)
        fnTIL = findViewById(R.id.fnTIL)
        lnTIL = findViewById(R.id.lnTIL)
        saveButton = findViewById(R.id.saveButton)
        changePhoto_tv = findViewById(R.id.changePhoto_tv)
        emailAddress_et = findViewById(R.id.emailAddress_et)
        phoneNumber_et = findViewById(R.id.phoneNumber_et)
        lName_et = findViewById(R.id.lName_et)
        fName_et = findViewById(R.id.fName_et)
        dateOfBirth_et = findViewById(R.id.dateOfBirth_et)
        backImageView?.setOnClickListener(this)
        saveButton?.setOnClickListener(this)
        changePhoto_tv?.setOnClickListener(this)
        dateOfBirth_et?.setOnClickListener(this)
        addTextWatcherListener()
    }

    private fun addTextWatcherListener() {
        emailAddress_et!!.addTextChangedListener(TextWatcher(emailTIL!!, emailAddress_et!!))
        lName_et!!.addTextChangedListener(TextWatcher(lnTIL!!, lName_et!!))
        fName_et!!.addTextChangedListener(TextWatcher(fnTIL!!, fName_et!!))
        phoneNumber_et!!.addTextChangedListener(TextWatcher(pnTIL!!, phoneNumber_et!!))
        dateOfBirth_et!!.addTextChangedListener(TextWatcher(dobTIL!!, dateOfBirth_et!!))
        dateOfBirth_et!!.addTextChangedListener(TextWatcher(genderTIL!!, dateOfBirth_et!!))
    }

    private val dataFromIntent: Unit
        private get() {
            val intent = intent
            profileDetails = intent.getParcelableExtra(AppConstant.PROFILE_DETAILS_MODEL)
            phoneNumber = intent.getStringExtra(AppConstant.PHONE_NUMBER)
            email = intent.getStringExtra(AppConstant.EMAIL)
            updateUI()
        }

    private fun updateUI() {
        fName_et!!.setText(profileDetails!!.firstName)
        fName_et!!.setSelection(fName_et!!.text.length)
        lName_et!!.setText(profileDetails!!.lastName)
        //  lName_et.setSelection(lName_et.getText().length());
        phoneNumber_et!!.setText(profileDetails!!.mobile)
        //        phoneNumber_et.setSelection(lName_et.getText().length());
        emailAddress_et!!.setText(profileDetails!!.email)
        // emailAddress_et.setSelection(lName_et.getText().length());
        dateOfBirth_et!!.setText(profileDetails!!.dob)
        // int i = getGender(profileDetails.getGender(), genderList);
        selectGenderAutoCompTV!!.setText(profileDetails!!.gender)
        // selectGenderAutoCompTV.setSelection(getGender(profileDetails.getGender(), genderList));
        profileImagePath = profileDetails!!.image
        if (profileImagePath != null && profileImagePath != "") {
            Glide.with(this)
                .load(profileImagePath)
                .centerCrop()
                .placeholder(R.drawable.profile_img)
                .into(profile_imv!!)
        }
        addSpinner()
    }

    private fun addSpinner() {
        selectGenderAutoCompTV = findViewById(R.id.selectGenderAutoCompTV)
        genderList = ArrayList()
        genderList!!.add("Female")
        genderList!!.add("Male")
        genderList!!.add("Other")
        arrayAdapter =
            ArrayAdapter(applicationContext, R.layout.spinner_drop_down_single_row, genderList!!)
        selectGenderAutoCompTV?.setAdapter(arrayAdapter)
        // selectGenderAutoCompTV.setSelection(getGender(profileDetails.getGender(), genderList));
        selectGenderAutoCompTV?.setOnItemClickListener(OnItemClickListener { parent, view, position, id ->
            genderTIL!!.error = null
            genderTIL!!.isErrorEnabled = false
        })
    }

    fun getGender(selectedItem: String?, strings: List<String>): Int {
        var k = 0
        for (i in strings.indices) {
            if (strings[i].equals(selectedItem, ignoreCase = true)) {
                k = i
                break
            } else {
                k = 0
            }
        }
        return k
    }

    private fun validate(): Boolean {
        fName = fName_et!!.text.toString().trim { it <= ' ' }
        lName = lName_et!!.text.toString().trim { it <= ' ' }
        phoneNumber = phoneNumber_et!!.text.toString().trim { it <= ' ' }
        email = emailAddress_et!!.text.toString().trim { it <= ' ' }
        dob = dateOfBirth_et!!.text.toString().trim { it <= ' ' }
        gender = selectGenderAutoCompTV!!.text.toString()
        if (TextUtils.isEmpty(fName)) {
            fnTIL!!.error = "Enter First Name."
            return false
        }
        if (TextUtils.isEmpty(lName)) {
            lnTIL!!.error = "Enter Last Name."
            return false
        }
        if (TextUtils.isEmpty(phoneNumber)) {
            pnTIL!!.error = "Enter Phone Number."
            return false
        }
        if (!AppUtil.isValidMobile(phoneNumber!!)) {
            pnTIL!!.error = "Please enter Valid Phone Number."
            return false
        }

        /*if(TextUtils.isEmpty(email)){
            emailTIL.setError("Enter Email Address.");
            return false;
        }*/if (email != null && email != "") {
            if (!AppUtil.isValidMail(email)) {
                emailTIL!!.error = "Please enter valid Email ID."
                return false
            }
        }
        if (TextUtils.isEmpty(dob)) {
            dobTIL!!.error = "Select Date of Birth."
            return false
        }
        if (TextUtils.isEmpty(gender)) {
            genderTIL!!.error = "Select Gender."
            return false
        }
        return true
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.backImageView -> finish()
            R.id.saveButton -> dispatchToProfileEditService()
            R.id.changePhoto_tv -> showBottomSheet()
            R.id.dateOfBirth_et -> openDatePicker()
        }
    }

    private fun showBottomSheet() {
        val addImageBottomSheet = AddImageBottomSheet.newInstance()
        addImageBottomSheet.show(supportFragmentManager, AddImageBottomSheet.TAG)
    }

    override fun onItemClick(item: String?) {
        if (item.equals(AppConstant.GALLERY, ignoreCase = true)) {
            openGallery()
        } else if (item.equals(AppConstant.CAMERA, ignoreCase = true)) {
            openCamera()
        }
    }

    fun openDatePicker() {
        val fragment = DatePickerFragment()
        fragment.show(supportFragmentManager, DatePickerFragment.TAG)
    }

    override fun getSelectedDate(date: String?) {
        dateOfBirth_et!!.setText(date)
    }

    fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, EditProfileActivity.Companion.SELECT_GALLERY_IMAGE)
    }

    private fun openCamera() {
        val intent = Intent(this@EditProfileActivity, TakeImage::class.java)
        startActivityForResult(intent, EditProfileActivity.Companion.CAPTURE_IMAGE)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            EditProfileActivity.Companion.SELECT_GALLERY_IMAGE -> if (data != null) {
                profileImageUri = data.data
                profile_imv!!.setImageURI(profileImageUri)
                profileImagePath =
                    PhotoUtil.getImageStringToUri(this@EditProfileActivity, profileImageUri)
            }
            EditProfileActivity.Companion.CAPTURE_IMAGE -> if (data != null) {
                profileImagePath = data.getStringExtra("photoFile")
                val bmOptions = BitmapFactory.Options()
                val bitmap = BitmapFactory.decodeFile(profileImagePath, bmOptions)
                profile_imv!!.setImageBitmap(bitmap)
                profile_imv!!.scaleType = ImageView.ScaleType.CENTER_CROP
            }
        }
    }

    private fun dispatchToProfileEditService() {
        if (AppUtil.isNetworkAvailable(this@EditProfileActivity)) {
            if (validate()) {
                if (phoneNumber != "") {
                    if (!phoneNumber.equals(
                            intent.getStringExtra(AppConstant.PHONE_NUMBER),
                            ignoreCase = true
                        ) || !email.equals(
                            intent.getStringExtra(AppConstant.EMAIL), ignoreCase = true
                        )
                    ) {
                        checkPhoneNumberOrEmailService()
                    } else {
                        editProfileService()
                    }
                } else {
                    editProfileService()
                }
            }
        } else {
            AppUtil.showConnectionError(this)
        }
    }

    fun createMultiPartForProfileImage() {
        println()
        if (profileImagePath != null) {
            if (profileImagePath!!.contains("http")) {
                profileImgMultipart = createFormData("old_image", profileImagePath!!)
            } else {
                if (profileImagePath != "") {
                    val file = File(profileImagePath)
                    val requestBody = file.asRequestBody("*/*".toMediaTypeOrNull())
                    profileImgMultipart =
                        createFormData("image", file.name, requestBody)
                }
            }
        }
    }

    private fun editProfileService() {
        AppUtil.showProgressDialog(this@EditProfileActivity)
        createMultiPartForProfileImage()
        val token = SharedPreferenceWriter.getInstance(this@EditProfileActivity)
            .getString(SharedPrefsKey.TOKEN)
        val sessionToken = token?.toRequestBody("text/plain".toMediaTypeOrNull())
        val firstName = fName!!.toRequestBody("text/plain".toMediaTypeOrNull())
        val lastName = lName!!.toRequestBody("text/plain".toMediaTypeOrNull())
        val dob_body = dob!!.toRequestBody("text/plain".toMediaTypeOrNull())
        val mobile = phoneNumber!!.toRequestBody("text/plain".toMediaTypeOrNull())
        val emailId = email!!.toRequestBody("text/plain".toMediaTypeOrNull())
        val gender_body = gender!!.toRequestBody("text/plain".toMediaTypeOrNull())
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
                                this@EditProfileActivity,
                                "" + `object`.getString("message"),
                                Toast.LENGTH_SHORT
                            ).show()
                            finish()
                        } else {
                            if (`object`.getString("message")
                                    .contains(AppConstant.SESSION_EXPIRED)
                            ) {
                                AppUtil.showLogoutDialog(
                                    this@EditProfileActivity,
                                    resources.getString(R.string.error),
                                    `object`.getString("message")
                                )
                            } else {
                                AppUtil.showErrorDialog(
                                    this@EditProfileActivity,
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
                    this@EditProfileActivity,
                    resources.getString(R.string.error),
                    t.message
                )
            }
        })
    }

    private fun checkPhoneNumberOrEmailService() {
        AppUtil.showProgressDialog(this@EditProfileActivity)
        val token = SharedPreferenceWriter.getInstance(this@EditProfileActivity)
            .getString(SharedPrefsKey.TOKEN)
        val apiServiceInterface = ApiClient.instance.client
        val call = apiServiceInterface.editCheckEmail(token, phoneNumber, email)
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
                            goToOTPScreen()
                        } else {
                            if (`object`.getString("message")
                                    .contains(AppConstant.SESSION_EXPIRED)
                            ) {
                                AppUtil.showLogoutDialog(
                                    this@EditProfileActivity,
                                    resources.getString(R.string.error),
                                    `object`.getString("message")
                                )
                            } else {
                                AppUtil.showErrorDialog(
                                    this@EditProfileActivity,
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
                    this@EditProfileActivity,
                    resources.getString(R.string.error),
                    t.message
                )
            }
        })
    }

    private fun goToOTPScreen() {
        profileDetails!!.firstName = fName!!
        profileDetails!!.lastName = lName!!
        profileDetails!!.mobile = phoneNumber!!
        profileDetails!!.email = email!!
        profileDetails!!.image = profileImagePath!!
        val intent = Intent(this@EditProfileActivity, NumberVerificationActivity::class.java)
        intent.putExtra(AppConstant.PROFILE_DETAILS_MODEL, profileDetails)
        intent.putExtra(AppConstant.PHONE_NUMBER, profileDetails!!.mobile)
        intent.putExtra(AppConstant.FROM, EditProfileActivity.Companion.TAG)
        startActivity(intent)
        finish()
    }

    companion object {
        const val TAG = "EditProfileActivity"
        const val SELECT_GALLERY_IMAGE = 1012
        const val CAPTURE_IMAGE = 1021
    }
}