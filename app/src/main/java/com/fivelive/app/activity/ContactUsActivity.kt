package com.fivelive.app.activity


import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.fivelive.app.R
import com.fivelive.app.activity.TakeImage
import com.fivelive.app.dialog.AddImageBottomSheet
import com.fivelive.app.dialog.CustomErrorDialog
import com.fivelive.app.prefs.SharedPreferenceWriter
import com.fivelive.app.prefs.SharedPrefsKey
import com.fivelive.app.retrofit.ApiClient
import com.fivelive.app.util.AppConstant
import com.fivelive.app.util.AppUtil
import com.fivelive.app.util.PhotoUtil
import com.google.gson.JsonElement
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.MultipartBody.Part.Companion.createFormData
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class ContactUsActivity : AppCompatActivity(), View.OnClickListener,
    AddImageBottomSheet.ItemClickListener {
    var fAQsTextView: TextView? = null
    var first_img_cl: ConstraintLayout? = null
    var second_img_cl: ConstraintLayout? = null
    var third_img_cl: ConstraintLayout? = null
    var first_img: ImageView? = null
    var second_img: ImageView? = null
    var third_img: ImageView? = null
    var cl_first_img_cross: ConstraintLayout? = null
    var cl_second_img_cross: ConstraintLayout? = null
    var cl_third_img_cross: ConstraintLayout? = null
    var cross_first_imv: ImageView? = null
    var cross_second_imv: ImageView? = null
    var cross_third_imv: ImageView? = null
    var emailAddress_et: EditText? = null
    var descriptionEditText: EditText? = null
    var submit_btn: Button? = null
    var backImageView: ImageView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_us)
        initView()
    }

    fun initView() {
        backImageView = findViewById(R.id.backImageView)
        emailAddress_et = findViewById(R.id.emailAddress_et)
        descriptionEditText = findViewById(R.id.descriptionEditText)
        submit_btn = findViewById(R.id.submit_btn)
        cl_first_img_cross = findViewById(R.id.cl_first_img_cross)
        cl_second_img_cross = findViewById(R.id.cl_second_img_cross)
        cl_third_img_cross = findViewById(R.id.cl_third_img_cross)
        cross_first_imv = findViewById(R.id.cross_first_imv)
        cross_second_imv = findViewById(R.id.cross_second_imv)
        cross_third_imv = findViewById(R.id.cross_third_imv)
        cross_first_imv?.setOnClickListener(this)
        cross_second_imv?.setOnClickListener(this)
        cross_third_imv?.setOnClickListener(this)
        first_img = findViewById(R.id.first_img)
        second_img = findViewById(R.id.second_img)
        third_img = findViewById(R.id.third_img)
        first_img_cl = findViewById(R.id.first_img_cl)
        second_img_cl = findViewById(R.id.second_img_cl)
        third_img_cl = findViewById(R.id.third_img_cl)
        fAQsTextView = findViewById(R.id.fAQsTextView)
        fAQsTextView?.setOnClickListener(this)
        first_img_cl?.setOnClickListener(this)
        second_img_cl?.setOnClickListener(this)
        third_img_cl?.setOnClickListener(this)
        submit_btn?.setOnClickListener(this)
        backImageView?.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.fAQsTextView -> dispatchToFaqsActivity()
            R.id.first_img_cl -> {
                imageSelected = 1
                showBottomSheet()
            }
            R.id.second_img_cl -> {
                imageSelected = 2
                showBottomSheet()
            }
            R.id.third_img_cl -> {
                imageSelected = 3
                showBottomSheet()
            }
            R.id.submit_btn -> dispatchTOSendEnquiryService()
            R.id.cross_first_imv -> removeFirstImage()
            R.id.cross_second_imv -> removeSecondImage()
            R.id.cross_third_imv -> removeThirdImage()
            R.id.backImageView -> finish()
        }
    }

    fun removeFirstImage() {
        imagePath1 = ""
        cl_first_img_cross!!.visibility = View.GONE
        first_img_cl!!.visibility = View.VISIBLE
    }

    fun removeSecondImage() {
        imagePath2 = ""
        cl_second_img_cross!!.visibility = View.GONE
        second_img_cl!!.visibility = View.VISIBLE
    }

    fun removeThirdImage() {
        imagePath3 = ""
        cl_third_img_cross!!.visibility = View.GONE
        third_img_cl!!.visibility = View.VISIBLE
    }

    var imageSelected = 0
    private fun dispatchToFaqsActivity() {
        startActivity(Intent(this@ContactUsActivity, FaqsActivity::class.java))
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

    fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, SELECT_GALLERY_IMAGE)
    }

    private fun openCamera() {
        val intent = Intent(this@ContactUsActivity, TakeImage::class.java)
        startActivityForResult(intent, CAPTURE_IMAGE)
    }

    var profileImageUri: Uri? = null
    var profileImagePath: String? = ""
    var imageList: MutableList<String?> = ArrayList()
    var imagePath1: String? = ""
    var imagePath2: String? = ""
    var imagePath3: String? = ""
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            SELECT_GALLERY_IMAGE -> if (data != null) {
                profileImageUri = data.data
                profileImagePath =
                    PhotoUtil.getImageStringToUri(this@ContactUsActivity, profileImageUri)
                if (imageSelected == 1) {
                    first_img!!.setImageURI(profileImageUri)
                    cl_first_img_cross!!.visibility = View.VISIBLE
                    first_img_cl!!.visibility = View.GONE
                    imagePath1 = profileImagePath
                } else if (imageSelected == 2) {
                    second_img!!.setImageURI(profileImageUri)
                    cl_second_img_cross!!.visibility = View.VISIBLE
                    second_img_cl!!.visibility = View.GONE
                    imagePath2 = profileImagePath
                } else if (imageSelected == 3) {
                    third_img!!.setImageURI(profileImageUri)
                    cl_third_img_cross!!.visibility = View.VISIBLE
                    third_img_cl!!.visibility = View.GONE
                    imagePath3 = profileImagePath
                }

                // imageList.add(profileImagePath);
            }
            CAPTURE_IMAGE -> if (data != null) {
                profileImagePath = data.getStringExtra("photoFile")
                //  imageList.add(profileImagePath);
                val bmOptions = BitmapFactory.Options()
                val bitmap = BitmapFactory.decodeFile(profileImagePath, bmOptions)
                if (imageSelected == 1) {
                    first_img!!.setImageBitmap(bitmap)
                    first_img!!.scaleType = ImageView.ScaleType.CENTER_CROP
                    cl_first_img_cross!!.visibility = View.VISIBLE
                    first_img_cl!!.visibility = View.GONE
                    imagePath1 = profileImagePath
                } else if (imageSelected == 2) {
                    second_img!!.setImageBitmap(bitmap)
                    second_img!!.scaleType = ImageView.ScaleType.CENTER_CROP
                    cl_second_img_cross!!.visibility = View.VISIBLE
                    second_img_cl!!.visibility = View.GONE
                    imagePath2 = profileImagePath
                } else if (imageSelected == 3) {
                    third_img!!.setImageBitmap(bitmap)
                    third_img!!.scaleType = ImageView.ScaleType.CENTER_CROP
                    cl_third_img_cross!!.visibility = View.VISIBLE
                    third_img_cl!!.visibility = View.GONE
                    imagePath3 = profileImagePath
                }
            }
        }
    }

    var email: String? = null
    var description: String? = null
    fun validate(): Boolean {
        email = emailAddress_et!!.text.toString().trim { it <= ' ' }
        description = descriptionEditText!!.text.toString().trim { it <= ' ' }
        if (TextUtils.isEmpty(email)) {
            AppUtil.showErrorDialog(this@ContactUsActivity, "Error!", "Please enter email address.")
            return false
        }
        if (email != null && email != "") {
            if (!AppUtil.isValidMail(email)) {
                AppUtil.showErrorDialog(
                    this@ContactUsActivity,
                    "Error!",
                    "Please enter valid email address."
                )
                return false
            }
        }
        if (TextUtils.isEmpty(description)) {
            AppUtil.showErrorDialog(this@ContactUsActivity, "Error!", "Please add description.")
            return false
        }
        addimagesINList()
        return true
    }

    fun addimagesINList() {
        if (imagePath1 != "") {
            imageList.add(imagePath1)
        }
        if (imagePath2 != "") {
            imageList.add(imagePath2)
        }
        if (imagePath3 != "") {
            imageList.add(imagePath2)
        }
    }

    lateinit var reviewImgMultiPart: Array<MultipartBody.Part?>
    fun createMultipartForReviewImage() {
        val lengthOfArray = imageList.size
        reviewImgMultiPart = arrayOfNulls<MultipartBody.Part>(lengthOfArray)
        for (index in imageList.indices) {
            val file = File(imageList[index])
            val requestBody = RequestBody.create("*/*".toMediaTypeOrNull(), file)
            reviewImgMultiPart[index] =
                createFormData("files[]", file.name, requestBody)
        }
    }

    private fun sendEnquiryService() {
        AppUtil.showProgressDialog(this@ContactUsActivity)
        createMultipartForReviewImage()
        val token = SharedPreferenceWriter.getInstance(this@ContactUsActivity)
            ?.getString(SharedPrefsKey.TOKEN)
        val sessionToken = RequestBody.create("text/plain".toMediaTypeOrNull(), token.orEmpty())
        val body_email = RequestBody.create("text/plain".toMediaTypeOrNull(), email!!)
        val body_description = RequestBody.create("text/plain".toMediaTypeOrNull(), description!!)
        val apiServiceInterface = ApiClient.instance.client
        val call = apiServiceInterface
            .sendEnquiry(sessionToken, body_description, body_email, reviewImgMultiPart)
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
                            showSuccessDialog(`object`.getString("message"))
                            // AppUtil.showErrorDialog(ContactUsActivity.this, "Success!", object.getString("message"));
                        } else {
                            if (`object`.getString("message")
                                    .contains(AppConstant.SESSION_EXPIRED)
                            ) {
                                AppUtil.showLogoutDialog(
                                    this@ContactUsActivity,
                                    resources.getString(R.string.error),
                                    `object`.getString("message")
                                )
                            } else {
                                AppUtil.showErrorDialog(
                                    this@ContactUsActivity,
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
                    this@ContactUsActivity,
                    resources.getString(R.string.error),
                    t.message
                )
            }
        })
    }

    private fun showSuccessDialog(msg: String) {
        CustomErrorDialog(
            this,
            "Query Added",
            "Thank you for contacting us! We will be in touch with you shortly. Please check your email for the update."
        ) { finish() }.show()
    }

    private fun dispatchTOSendEnquiryService() {
        AppUtil.hideKeyboard(this)
        if (AppUtil.isNetworkAvailable(this@ContactUsActivity)) {
            if (validate()) {
                sendEnquiryService()
            }
        } else {
            AppUtil.showConnectionError(this@ContactUsActivity)
        }
    }

    companion object {
        const val SELECT_GALLERY_IMAGE = 1012
        const val CAPTURE_IMAGE = 1021
    }
}