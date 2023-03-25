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
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.fivelive.app.R
import com.fivelive.app.activity.TakeImage
import com.fivelive.app.dialog.AddImageBottomSheet
import com.fivelive.app.dialog.CustomSuccessDialog
import com.fivelive.app.interfaces.DialogButtonListener
import com.fivelive.app.prefs.SharedPreferenceWriter
import com.fivelive.app.prefs.SharedPrefsKey
import com.fivelive.app.retrofit.ApiClient
import com.fivelive.app.util.AppConstant
import com.fivelive.app.util.AppUtil
import com.fivelive.app.util.PhotoUtil
import com.google.android.material.textfield.TextInputLayout
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

class AddHappyHoursImageActivity : AppCompatActivity(), View.OnClickListener,
    AddImageBottomSheet.ItemClickListener {
    var itemTIL: TextInputLayout? = null
    var itemName_et: EditText? = null
    var uploadImageLayout: LinearLayout? = null
    var imageConstraintLayout: ConstraintLayout? = null
    var coverImageView: ImageView? = null
    var clear_imv: ImageView? = null
    var backImageView: ImageView? = null
    var addItemButton: Button? = null
    var coverImageUri: Uri? = null
    var coverImagePath: String? = ""
    var itemName: String? = null
    var businessId: String? = null
    var type: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_happy_hours_image)
        businessId = intent.getStringExtra(AppConstant.BUSINESS_ID)
        type = intent.getStringExtra(AppConstant.TYPE)
        initView()
    }

    private fun initView() {
        addItemButton = findViewById(R.id.addItemButton)
        itemTIL = findViewById(R.id.itemTIL)
        itemName_et = findViewById(R.id.itemName_et)
        coverImageView = findViewById(R.id.coverImageView)
        clear_imv = findViewById(R.id.clear_imv)
        uploadImageLayout = findViewById(R.id.uploadImageLayout)
        imageConstraintLayout = findViewById(R.id.imageConstraintLayout)
        backImageView = findViewById(R.id.backImageView)
        uploadImageLayout?.setOnClickListener(this)
        clear_imv?.setOnClickListener(this)
        backImageView?.setOnClickListener(this)
        addItemButton?.setOnClickListener(this)
    }

    private fun validate(): Boolean {
        itemName = itemName_et!!.text.toString().trim { it <= ' ' }
        if (TextUtils.isEmpty(itemName)) {
            itemTIL!!.error = "Please enter item name."
            return false
        }
        if (TextUtils.isEmpty(coverImagePath)) {
            AppUtil.showErrorDialog(this, getString(R.string.error), "Please select image.")
            return false
        }
        return true
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.uploadImageLayout -> showBottomSheet()
            R.id.clear_imv -> hideOrShowLayout()
            R.id.backImageView -> finish()
            R.id.addItemButton -> goForAddMenuService()
        }
    }

    private fun showBottomSheet() {
        val addImageBottomSheet = AddImageBottomSheet.newInstance()
        addImageBottomSheet.show(supportFragmentManager, AddImageBottomSheet.TAG)
    }

    override fun onItemClick(item: String?) {
        if (item.equals("Gallery", ignoreCase = true)) {
            openGallery()
        } else if (item.equals("Camera", ignoreCase = true)) {
            openCamera()
        }
    }

    fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, SELECT_GALLERY_IMAGE)
    }

    private fun openCamera() {
        val intent = Intent(this@AddHappyHoursImageActivity, TakeImage::class.java)
        startActivityForResult(intent, CAPTURE_IMAGE)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            SELECT_GALLERY_IMAGE -> {
                if (data != null) {
                    coverImageUri = data.data
                    coverImageView!!.setImageURI(coverImageUri)
                    coverImageView!!.scaleType = ImageView.ScaleType.CENTER_CROP
                    coverImagePath = PhotoUtil.getImageStringToUri(
                        this@AddHappyHoursImageActivity,
                        coverImageUri
                    )
                }
                hideOrShowLayout()
            }
            CAPTURE_IMAGE -> {
                if (data != null) {
                    coverImagePath = data.getStringExtra("photoFile")
                    val bmOptions = BitmapFactory.Options()
                    val bitmap = BitmapFactory.decodeFile(coverImagePath, bmOptions)
                    coverImageView!!.setImageBitmap(bitmap)
                    coverImageView!!.scaleType = ImageView.ScaleType.CENTER_CROP
                }
                hideOrShowLayout()
            }
        }
    }

    fun hideOrShowLayout() {
        if (uploadImageLayout!!.visibility == View.VISIBLE) {
            imageConstraintLayout!!.visibility = View.VISIBLE
            uploadImageLayout!!.visibility = View.GONE
        } else {
            imageConstraintLayout!!.visibility = View.GONE
            uploadImageLayout!!.visibility = View.VISIBLE
        }
    }

    fun goForAddMenuService() {
        if (AppUtil.isNetworkAvailable(this@AddHappyHoursImageActivity)) {
            if (validate()) {
                addMenuService()
            }
        } else {
            AppUtil.showConnectionError(this@AddHappyHoursImageActivity)
        }
    }

    fun addMenuService() {
        AppUtil.showProgressDialog(this)
        val file = File(coverImagePath)
        val requestBody = RequestBody.create("*/*".toMediaTypeOrNull(), file)
        val imageMultipart: MultipartBody.Part = createFormData("image", file.name, requestBody)
        val token = SharedPreferenceWriter.getInstance(this)?.getString(SharedPrefsKey.TOKEN)
        val sessionToken = RequestBody.create("text/plain".toMediaTypeOrNull(), token.orEmpty())
        val businessId_body = RequestBody.create("text/plain".toMediaTypeOrNull(), businessId!!)
        val type_body = RequestBody.create("text/plain".toMediaTypeOrNull(), type!!)
        val itemName_body = RequestBody.create("text/plain".toMediaTypeOrNull(), itemName!!)
        val apiServiceInterface = ApiClient.instance.client
        val call = apiServiceInterface.addMenu(
            sessionToken,
            businessId_body,
            type_body,
            itemName_body,
            imageMultipart
        )
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
                        } else {
                            if (`object`.getString("message")
                                    .contains(AppConstant.SESSION_EXPIRED)
                            ) {
                                AppUtil.showLogoutDialog(
                                    this@AddHappyHoursImageActivity,
                                    this@AddHappyHoursImageActivity.resources.getString(R.string.error),
                                    `object`.getString("message")
                                )
                            } else {
                                AppUtil.showErrorDialog(
                                    this@AddHappyHoursImageActivity,
                                    this@AddHappyHoursImageActivity.resources.getString(R.string.error),
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
                    this@AddHappyHoursImageActivity,
                    this@AddHappyHoursImageActivity.resources.getString(R.string.error),
                    t.message
                )
            }
        })
    }

    fun showSuccessDialog(msg: String?) {
        CustomSuccessDialog(this, resources.getString(R.string.success), msg) {
            finish()

        }.show()
    }

    companion object {
        const val SELECT_GALLERY_IMAGE = 1012
        const val CAPTURE_IMAGE = 1021
    }
}