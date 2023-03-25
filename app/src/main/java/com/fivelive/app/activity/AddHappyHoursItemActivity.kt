package com.fivelive.app.activity

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.fivelive.app.Model.CategoryData
import com.fivelive.app.Model.ItemDetailsResponse
import com.fivelive.app.R
import com.fivelive.app.activity.TakeImage
import com.fivelive.app.dialog.AddImageBottomSheet
import com.fivelive.app.dialog.CustomSuccessDialog
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
import java.util.*

class AddHappyHoursItemActivity : AppCompatActivity(), View.OnClickListener,
    AddImageBottomSheet.ItemClickListener {
    var selectCat_actv: AutoCompleteTextView? = null
    var selectSize_actv: AutoCompleteTextView? = null
    var categoryTIL: TextInputLayout? = null
    var sizeTIL: TextInputLayout? = null
    var itemTIL: TextInputLayout? = null
    var rgpTIL: TextInputLayout? = null
    var hhpTIL: TextInputLayout? = null
    var perOfTIL: TextInputLayout? = null
    var item_name_et: EditText? = null
    var regular_price_et: EditText? = null
    var happy_hours_price_et: EditText? = null
    var percentage_et: EditText? = null
    var title_tv: TextView? = null
    var uploadImageLayout: LinearLayout? = null
    var imageConstraintLayout: ConstraintLayout? = null
    var coverImageView: ImageView? = null
    var clear_imv: ImageView? = null
    var backImageView: ImageView? = null
    var addItemBtn: Button? = null
    var coverImageUri: Uri? = null
    var coverImagePath: String? = ""
    var businessId: String? = null
    var itemId: String? = null
    var category: String? = null
    var itemName: String? = null
    var size: String? = null
    var regularPrice: String? = null
    var happyHourPrice: String? = null
    var percentage: String? = null
    var type: String? = null
    var from: String? = null
    var isEdit = false
    var catDataModel: CategoryData? = null
    var imageMultipart: MultipartBody.Part? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_happy_hours_item)
        initView()
        dataFromIntent
        addCategorySpinner()
        addSizeSpinner()
        addTextWatcherListener()
    }

    private val dataFromIntent: Unit
        private get() {
            val intent = intent
            businessId = intent.getStringExtra(AppConstant.BUSINESS_ID)
            itemId = intent.getStringExtra(AppConstant.ITEM_ID)
            from = intent.getStringExtra(AppConstant.FROM)
            isEdit = intent.getBooleanExtra(AppConstant.EDIT, false)
            if (from != null && from.equals(SetHappyHourActivity.TAG, ignoreCase = true)) {
                type = "1"
                title_tv!!.text = getString(R.string.add_hh_item)
            } else if (from != null && from.equals(
                    SetReverseHappyHourActivity.TAG,
                    ignoreCase = true
                )
            ) {
                type = "2"
                title_tv!!.text = getString(R.string.add_reverse_hh_item)
            }
            if (isEdit) {
                if (AppUtil.isNetworkAvailable(this@AddHappyHoursItemActivity)) {
                    dispatchToItemDetailsService()
                } else {
                    AppUtil.showConnectionError(this@AddHappyHoursItemActivity)
                }
            }
        }

    private fun initView() {
        backImageView = findViewById(R.id.backImageView)
        title_tv = findViewById(R.id.title_tv)
        backImageView?.setOnClickListener(this)
        categoryTIL = findViewById(R.id.categoryTIL)
        sizeTIL = findViewById(R.id.sizeTIL)
        itemTIL = findViewById(R.id.itemTIL)
        rgpTIL = findViewById(R.id.rgpTIL)
        hhpTIL = findViewById(R.id.hhpTIL)
        perOfTIL = findViewById(R.id.perOfTIL)
        selectSize_actv = findViewById(R.id.selectSize_actv)
        selectCat_actv = findViewById(R.id.selectCat_actv)
        item_name_et = findViewById(R.id.item_name_et)
        regular_price_et = findViewById(R.id.regular_price_et)
        happy_hours_price_et = findViewById(R.id.happy_hours_price_et)
        percentage_et = findViewById(R.id.percentage_et)
        addItemBtn = findViewById(R.id.addItemBtn)
        uploadImageLayout = findViewById(R.id.uploadImageLayout)
        imageConstraintLayout = findViewById(R.id.imageConstraintLayout)
        clear_imv = findViewById(R.id.clear_imv)
        coverImageView = findViewById(R.id.coverImageView)
        clear_imv?.setOnClickListener(this)
        uploadImageLayout?.setOnClickListener(this)
        addItemBtn?.setOnClickListener(this)
    }

    private fun addTextWatcherListener() {
        item_name_et!!.addTextChangedListener(
            com.fivelive.app.util.TextWatcher(
                itemTIL!!,
                item_name_et!!
            )
        )
        regular_price_et!!.addTextChangedListener(
            com.fivelive.app.util.TextWatcher(
                rgpTIL!!,
                regular_price_et!!
            )
        )
        //  happy_hours_price_et.addTextChangedListener(new TextWatcher(hhpTIL, happy_hours_price_et));
        percentage_et!!.addTextChangedListener(
            com.fivelive.app.util.TextWatcher(
                perOfTIL!!,
                percentage_et!!
            )
        )
        selectCat_actv!!.addTextChangedListener(
            com.fivelive.app.util.TextWatcher(
                categoryTIL!!,
                selectCat_actv!!
            )
        )
        selectSize_actv!!.addTextChangedListener(
            com.fivelive.app.util.TextWatcher(
                sizeTIL!!,
                selectSize_actv!!
            )
        )
        happy_hours_price_et!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                val RPrice = regular_price_et!!.text.toString()
                if (RPrice.length == 0 || RPrice == "") {
                    AppUtil.showErrorDialog(
                        this@AddHappyHoursItemActivity, "Alert!",
                        "First Enter Regular Price."
                    )
                    return
                }
                // Now here we are calculating percentage.
                if (s.toString().length > 0) {
                    val percentage = getPercentage(s.toString().toDouble())
                    percentage_et!!.setText(Math.round(percentage).toString())
                } else {
                    percentage_et!!.setText("")
                }
            }
        })
        percentage_et!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                val RPrice = regular_price_et!!.text.toString()
                if (RPrice.length == 0 || RPrice == "") {
                    AppUtil.showErrorDialog(
                        this@AddHappyHoursItemActivity, "Alert!",
                        "First Enter Regular Price."
                    )
                    return
                }
                // Now here we are calculating percentage.
                if (s.toString().length > 0) {
                    val hhPrice = getHappyHoursPrice(s.toString().toDouble())
                    happy_hours_price_et!!.setText(Math.round(hhPrice).toString())
                } else {
                    happy_hours_price_et!!.setText("")
                }
            }
        })
    }

    fun getPercentage(HHPrice: Double): Double {
        val RPrice = regular_price_et!!.text.toString().toDouble()
        val percentage = HHPrice * 100 / RPrice
        Log.d("getPercentage", "before Round: $percentage")
        Log.d("getPercentage", "after round: " + Math.round(percentage))
        return percentage
    }

    fun getHappyHoursPrice(percentage: Double): Double {
        val RPrice = regular_price_et!!.text.toString().toDouble()
        val HHPrice = percentage * RPrice / 100
        Log.d("getHappyHoursPrice", "before Round: $HHPrice")
        Log.d("getHappyHoursPrice", "after round: " + Math.round(HHPrice))
        return HHPrice
    }

    private fun validate(): Boolean {
        category = selectCat_actv!!.text.toString().trim { it <= ' ' }
        itemName = item_name_et!!.text.toString().trim { it <= ' ' }
        size = selectSize_actv!!.text.toString().trim { it <= ' ' }
        regularPrice = regular_price_et!!.text.toString().trim { it <= ' ' }
        happyHourPrice = happy_hours_price_et!!.text.toString().trim { it <= ' ' }
        percentage = percentage_et!!.text.toString().trim { it <= ' ' }
        if (TextUtils.isEmpty(category)) {
            categoryTIL!!.error = this.getString(R.string.select_category)
            return false
        }
        if (TextUtils.isEmpty(itemName)) {
            itemTIL!!.error = this.getString(R.string.enter_item_name)
            return false
        }
        if (TextUtils.isEmpty(size)) {
            sizeTIL!!.error = this.getString(R.string.select_size)
            return false
        }
        if (TextUtils.isEmpty(regularPrice)) {
            rgpTIL!!.error = this.getString(R.string.enter_regular_price)
            return false
        }
        if (TextUtils.isEmpty(happyHourPrice)) {
            hhpTIL!!.error = this.getString(R.string.enter_happy_hour_price)
            return false
        }
        if (TextUtils.isEmpty(percentage)) {
            perOfTIL!!.error = this.getString(R.string.enter_percentage_off)
            return false
        }
        if (TextUtils.isEmpty(coverImagePath)) {
            AppUtil.showErrorDialog(
                this@AddHappyHoursItemActivity,
                getString(R.string.error),
                this.getString(R.string.please_select_item_image)
            )
            return false
        }
        return true
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.backImageView -> finish()
            R.id.uploadImageLayout -> showBottomSheet()
            R.id.clear_imv -> hideOrShowImageLayout()
            R.id.addItemBtn -> goForAddItemService()
        }
    }

    private fun addCategorySpinner() {
        val cat_array = resources.getStringArray(R.array.category_array)
        val catList: List<String> = ArrayList(Arrays.asList(*cat_array))
        val arrayAdapter =
            ArrayAdapter(applicationContext, R.layout.spinner_drop_down_single_row, catList)
        selectCat_actv!!.setAdapter(arrayAdapter)
    }

    private fun addSizeSpinner() {
        val size_array = resources.getStringArray(R.array.size_array)
        val sizeList: List<String> = ArrayList(Arrays.asList(*size_array))
        val arrayAdapter =
            ArrayAdapter(applicationContext, R.layout.spinner_drop_down_single_row, sizeList)
        selectSize_actv!!.setAdapter(arrayAdapter)
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
        val intent = Intent(this@AddHappyHoursItemActivity, TakeImage::class.java)
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
                    coverImagePath =
                        PhotoUtil.getImageStringToUri(this@AddHappyHoursItemActivity, coverImageUri)
                }
                hideOrShowImageLayout()
            }
            CAPTURE_IMAGE -> {
                if (data != null) {
                    coverImagePath = data.getStringExtra("photoFile")
                    val bmOptions = BitmapFactory.Options()
                    val bitmap = BitmapFactory.decodeFile(coverImagePath, bmOptions)
                    coverImageView!!.setImageBitmap(bitmap)
                    coverImageView!!.scaleType = ImageView.ScaleType.CENTER_CROP
                }
                hideOrShowImageLayout()
            }
        }
    }

    fun hideOrShowImageLayout() {
        if (uploadImageLayout!!.visibility == View.VISIBLE) {
            imageConstraintLayout!!.visibility = View.VISIBLE
            uploadImageLayout!!.visibility = View.GONE
        } else {
            imageConstraintLayout!!.visibility = View.GONE
            uploadImageLayout!!.visibility = View.VISIBLE
        }
    }

    fun goForAddItemService() {
        if (AppUtil.isNetworkAvailable(this@AddHappyHoursItemActivity)) {
            if (validate()) {
                addItemService()
            }
        } else {
            AppUtil.showConnectionError(this@AddHappyHoursItemActivity)
        }
    }

    private fun createMultipart() {
        if (!coverImagePath!!.contains("http")) {
            val file = File(coverImagePath)
            val requestBody = RequestBody.create("*/*".toMediaTypeOrNull(), file)
            imageMultipart = createFormData("image", file.name, requestBody)
        }
    }

    fun addItemService() {
        AppUtil.showProgressDialog(this)
        createMultipart()
        val token = SharedPreferenceWriter.getInstance(this@AddHappyHoursItemActivity)
            ?.getString(SharedPrefsKey.TOKEN)
        val sessionToken = RequestBody.create("text/plain".toMediaTypeOrNull(), token.orEmpty())
        val businessId_body = RequestBody.create("text/plain".toMediaTypeOrNull(), businessId!!)
        val category_body = RequestBody.create("text/plain".toMediaTypeOrNull(), category!!)
        val name_body = RequestBody.create("text/plain".toMediaTypeOrNull(), itemName!!)
        val size_body = RequestBody.create("text/plain".toMediaTypeOrNull(), size!!)
        val regularPrice_body = RequestBody.create("text/plain".toMediaTypeOrNull(), regularPrice!!)
        val offerPrice_body = RequestBody.create("text/plain".toMediaTypeOrNull(), happyHourPrice!!)
        val percentage_body = RequestBody.create("text/plain".toMediaTypeOrNull(), percentage!!)
        val type_body = RequestBody.create("text/plain".toMediaTypeOrNull(), type!!)
        val apiServiceInterface = ApiClient.instance.client
        val call: Call<JsonElement>
        call = if (isEdit) {
            val itemId_body = RequestBody.create("text/plain".toMediaTypeOrNull(), itemId!!)
            apiServiceInterface.updateItems(
                itemId_body, sessionToken, category_body, name_body,
                size_body, regularPrice_body, offerPrice_body, percentage_body, imageMultipart
            )
        } else {
            apiServiceInterface.addItems(
                sessionToken,
                businessId_body,
                category_body,
                name_body,
                size_body,
                regularPrice_body,
                offerPrice_body,
                percentage_body,
                type_body,
                imageMultipart
            )
        }
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
                                    this@AddHappyHoursItemActivity,
                                    this@AddHappyHoursItemActivity.resources.getString(R.string.error),
                                    `object`.getString("message")
                                )
                            } else {
                                AppUtil.showErrorDialog(
                                    this@AddHappyHoursItemActivity,
                                    this@AddHappyHoursItemActivity.resources.getString(R.string.error),
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
                    this@AddHappyHoursItemActivity,
                    this@AddHappyHoursItemActivity.resources.getString(R.string.error),
                    t.message
                )
            }
        })
    }

    fun showSuccessDialog(msg: String?) {
        CustomSuccessDialog(this, this.getString(R.string.success), msg) { finish() }.show()
    }

    private fun dispatchToItemDetailsService() {
        AppUtil.showProgressDialog(this@AddHappyHoursItemActivity)
        val apiServiceInterface = ApiClient.instance.client
        val call = apiServiceInterface.getItemDetails(itemId)
        call.enqueue(object : Callback<ItemDetailsResponse?> {
            override fun onResponse(
                call: Call<ItemDetailsResponse?>,
                response: Response<ItemDetailsResponse?>
            ) {
                AppUtil.dismissProgressDialog()
                try {
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        if (responseBody!!.status.equals(AppConstant.SUCCESS, ignoreCase = true)) {
                            catDataModel = responseBody.iteDetails
                            updateUI()
                        } else {
                            if (responseBody.message.contains(AppConstant.SESSION_EXPIRED)) {
                                AppUtil.showLogoutDialog(
                                    this@AddHappyHoursItemActivity,
                                    this@AddHappyHoursItemActivity.resources.getString(R.string.error),
                                    responseBody.message
                                )
                            } else {
                                AppUtil.showErrorDialog(
                                    this@AddHappyHoursItemActivity,
                                    this@AddHappyHoursItemActivity.resources.getString(R.string.error),
                                    responseBody.message
                                )
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<ItemDetailsResponse?>, t: Throwable) {
                AppUtil.dismissProgressDialog()
                call.cancel()
                AppUtil.showErrorDialog(
                    this@AddHappyHoursItemActivity,
                    getString(R.string.error),
                    t.message
                )
            }
        })
    }

    private fun updateUI() {
        item_name_et!!.setText(catDataModel!!.name)
        regular_price_et!!.setText(catDataModel!!.regularPrice)
        happy_hours_price_et!!.setText(catDataModel!!.offerPrice)
        percentage_et!!.setText(catDataModel!!.percentage)
        selectCat_actv!!.setText(catDataModel!!.itemCategory)
        selectSize_actv!!.setText(catDataModel!!.size)
        coverImagePath = catDataModel!!.image
        if (coverImagePath != null && coverImagePath != "") {
            AppUtil.loadLargeImage(this, catDataModel!!.image, coverImageView)
            hideOrShowImageLayout()
        }
    }

    companion object {
        const val SELECT_GALLERY_IMAGE = 1012
        const val CAPTURE_IMAGE = 1021
    }
}