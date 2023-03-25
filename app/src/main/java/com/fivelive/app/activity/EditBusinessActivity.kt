package com.fivelive.app.activity


import android.content.Intent
import android.graphics.BitmapFactory
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fivelive.app.Model.EditBusinessDetails
import com.fivelive.app.Model.EditBusinessResponse
import com.fivelive.app.Model.Image
import com.fivelive.app.R
import com.fivelive.app.activity.SetHappyHourActivity
import com.fivelive.app.activity.SetReverseHappyHourActivity
import com.fivelive.app.activity.TakeImage
import com.fivelive.app.adapter.EditBusinessRecipesImagesAdapter
import com.fivelive.app.bottomSheet.CategorySheet
import com.fivelive.app.bottomSheet.SelectDaySheet
import com.fivelive.app.dialog.AddImageBottomSheet
import com.fivelive.app.dialog.ConfirmationDialog
import com.fivelive.app.dialog.CustomSuccessDialog
import com.fivelive.app.prefs.SharedPreferenceWriter
import com.fivelive.app.prefs.SharedPrefsKey
import com.fivelive.app.retrofit.ApiClient
import com.fivelive.app.util.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.JsonElement
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.MultipartBody.Part.Companion.createFormData
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.IOException
import java.util.*

class EditBusinessActivity : AppCompatActivity(), View.OnClickListener,
    CategorySheet.ItemClickListener, AddImageBottomSheet.ItemClickListener {
    var backImageView: ImageView? = null
    var saveTextView: TextView? = null
    var businessNameTIL: TextInputLayout? = null
    var streetAddressTIL: TextInputLayout? = null
    var zipCodeTIL: TextInputLayout? = null
    var cityTIL: TextInputLayout? = null
    var stateTIL: TextInputLayout? = null
    var countryTIL: TextInputLayout? = null
    var webLinkTIL: TextInputLayout? = null
    var phoneNumberTIL: TextInputLayout? = null
    var emailAddressTIL: TextInputLayout? = null
    var businessName_et: EditText? = null
    var streetAddress_et: EditText? = null
    var zipCode_et: EditText? = null
    var city_et: EditText? = null
    var state_et: EditText? = null
    var country_et: EditText? = null
    var webLink_et: EditText? = null
    var phoneNumber_et: EditText? = null
    var emailAddress_et: EditText? = null
    var recipeImageRecyclerView: RecyclerView? = null
    var happy_hour_menu_constraint_layout: ConstraintLayout? = null
    var reverse_happy_hour_menu_layout: ConstraintLayout? = null
    var add_img_cl: ConstraintLayout? = null
    var brunch_menu_constraint_layout: ConstraintLayout? = null
    var live_music_constraint_layout: ConstraintLayout? = null
    var regular_menu_constraint_layout: ConstraintLayout? = null
    var selectCategory_tv: TextView? = null
    var chipGroup: ChipGroup? = null
    var search_et: EditText? = null
    var details: EditBusinessDetails? = null
    var businessId: String? = null
    var myBottomSheet: CategorySheet? = null
    var editBusinessRecipesImagesAdapter: EditBusinessRecipesImagesAdapter? = null
    var imagePath: String? = null
    var imageUri: Uri? = null
    lateinit var imgMultipart: Array<MultipartBody.Part?>
    lateinit var categoryRequestBody: Array<RequestBody?>
    var businessName: String? = null
    var streetAddress: String? = null
    var zipCode: String? = null
    var city: String? = null
    var state: String? = null
    var country: String? = null
    var website: String? = null
    var phoneNumber: String? = null
    var email: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_business)
        businessId = intent.getStringExtra(AppConstant.BUSINESS_ID)
        initView()
        addTextWatcherListener()
        if (AppUtil.isNetworkAvailable(this@EditBusinessActivity)) {
            editBusinessDetailsService()
        } else {
            AppUtil.showConnectionError(this)
        }
    }

    fun initView() {
        backImageView = findViewById(R.id.backImageView)
        saveTextView = findViewById(R.id.editTextView)
        saveTextView?.setVisibility(View.VISIBLE)
        saveTextView?.setText("Save")
        saveTextView?.setOnClickListener(this)
        recipeImageRecyclerView = findViewById<View>(R.id.recipeImageRecyclerView) as RecyclerView
        happy_hour_menu_constraint_layout =
            findViewById<View>(R.id.happy_hour_menu_constraint_layout) as ConstraintLayout
        reverse_happy_hour_menu_layout =
            findViewById<View>(R.id.reverse_happy_hour_menu_layout) as ConstraintLayout
        brunch_menu_constraint_layout =
            findViewById<View>(R.id.brunch_menu_constraint_layout) as ConstraintLayout
        live_music_constraint_layout =
            findViewById<View>(R.id.live_music_constraint_layout) as ConstraintLayout
        regular_menu_constraint_layout =
            findViewById<View>(R.id.regular_menu_constraint_layout) as ConstraintLayout
        add_img_cl = findViewById<View>(R.id.add_img_cl) as ConstraintLayout
        chipGroup = findViewById(R.id.chipGroup)
        selectCategory_tv = findViewById(R.id.selectCategory_tv)
        businessName_et = findViewById(R.id.businessName_et)
        streetAddress_et = findViewById(R.id.streetAddress_et)
        zipCode_et = findViewById(R.id.zipCode_et)
        city_et = findViewById(R.id.city_et)
        state_et = findViewById(R.id.state_et)
        country_et = findViewById(R.id.country_et)
        webLink_et = findViewById(R.id.webLink_et)
        phoneNumber_et = findViewById(R.id.phoneNumber_et)
        emailAddress_et = findViewById(R.id.emailAddress_et)
        businessNameTIL = findViewById(R.id.businessNameTIL)
        streetAddressTIL = findViewById(R.id.streetAddressTIL)
        zipCodeTIL = findViewById(R.id.zipCodeTIL)
        cityTIL = findViewById(R.id.cityTIL)
        stateTIL = findViewById(R.id.stateTIL)
        countryTIL = findViewById(R.id.countryTIL)
        webLinkTIL = findViewById(R.id.webLinkTIL)
        phoneNumberTIL = findViewById(R.id.phoneNumberTIL)
        emailAddressTIL = findViewById(R.id.emailAddressTIL)
        search_et = findViewById(R.id.search_et)
        happy_hour_menu_constraint_layout!!.setOnClickListener(this)
        reverse_happy_hour_menu_layout!!.setOnClickListener(this)
        brunch_menu_constraint_layout!!.setOnClickListener(this)
        live_music_constraint_layout!!.setOnClickListener(this)
        regular_menu_constraint_layout!!.setOnClickListener(this)
        backImageView?.setOnClickListener(this)
        selectCategory_tv?.setOnClickListener(this)
        add_img_cl!!.setOnClickListener(this)
        addSearchScreen()
    }

    private var placesClient: PlacesClient? = null
    var autocompleteSessionToken: AutocompleteSessionToken? = null
    private val predictionList: List<AutocompletePrediction>? = null
    fun addSearchScreen() {
        Places.initialize(applicationContext, getString(R.string.google_map_or_places_id))
        placesClient = Places.createClient(this)
        autocompleteSessionToken = AutocompleteSessionToken.newInstance()
        search_et!!.setOnClickListener {
            val fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG)
            // Start the autocomplete intent.
            val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                .build(this@EditBusinessActivity)
            startActivityForResult(intent, EditBusinessActivity.Companion.AUTOCOMPLETE_REQUEST_CODE)
        }
    }

    var latitude = 0.0
    var longitude = 0.0
    private fun getAddress(latLng: LatLng?) {
        val geocoder = Geocoder(this@EditBusinessActivity, Locale.getDefault())
        latitude = latLng!!.latitude
        longitude = latLng.longitude
        try {
            val addressList = geocoder.getFromLocation(latitude, longitude, 1)
            if (addressList!!.size > 0) {
                // Address address = geocoder.getFromLocation(latitude, longitude, 1).get(0);
                val address = addressList[0]
                val addStr = address.getAddressLine(0)
                search_et!!.setText(addStr)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun setRecipesImagesList(recipesImagesList: List<Image>) {
        editBusinessRecipesImagesAdapter =
            EditBusinessRecipesImagesAdapter(
                this@EditBusinessActivity,
                recipesImagesList.toMutableList()
            )
        val llm = LinearLayoutManager(this@EditBusinessActivity)
        llm.orientation = LinearLayoutManager.HORIZONTAL
        recipeImageRecyclerView!!.layoutManager = llm
        recipeImageRecyclerView!!.adapter = editBusinessRecipesImagesAdapter
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.happy_hour_menu_constraint_layout -> dispatchToSetHHDetailsActivity()
            R.id.brunch_menu_constraint_layout -> dispatchToSetBrunchActivity()
            R.id.live_music_constraint_layout -> dispatchToSetLiveMusicActivity()
            R.id.reverse_happy_hour_menu_layout -> dispatchToSetReverseHHActivity()
            R.id.regular_menu_constraint_layout -> dispatchToSetRegularMenuDetailsActivity()
            R.id.backImageView -> finish()
            R.id.selectCategory_tv -> openBottomSheet()
            R.id.add_img_cl -> chooseOptionSheet()
            R.id.editTextView -> dispatchToUpdateBusinessService()
        }
    }

    private fun addTextWatcherListener() {
        businessName_et!!.addTextChangedListener(TextWatcher(businessNameTIL!!, businessName_et!!))
        streetAddress_et!!.addTextChangedListener(
            TextWatcher(
                streetAddressTIL!!,
                streetAddress_et!!
            )
        )
        zipCode_et!!.addTextChangedListener(TextWatcher(zipCodeTIL!!, zipCode_et!!))
        city_et!!.addTextChangedListener(TextWatcher(cityTIL!!, city_et!!))
        state_et!!.addTextChangedListener(TextWatcher(stateTIL!!, state_et!!))
        country_et!!.addTextChangedListener(TextWatcher(countryTIL!!, country_et!!))
        webLink_et!!.addTextChangedListener(TextWatcher(webLinkTIL!!, webLink_et!!))
        phoneNumber_et!!.addTextChangedListener(TextWatcher(phoneNumberTIL!!, phoneNumber_et!!))
        emailAddress_et!!.addTextChangedListener(TextWatcher(emailAddressTIL!!, emailAddress_et!!))
    }

    private fun validate(): Boolean {
        businessName = businessName_et!!.text.toString().trim { it <= ' ' }
        streetAddress = streetAddress_et!!.text.toString().trim { it <= ' ' }
        zipCode = zipCode_et!!.text.toString().trim { it <= ' ' }
        city = city_et!!.text.toString().trim { it <= ' ' }
        state = state_et!!.text.toString().trim { it <= ' ' }
        country = country_et!!.text.toString().trim { it <= ' ' }
        website = webLink_et!!.text.toString().trim { it <= ' ' }
        phoneNumber = phoneNumber_et!!.text.toString().trim { it <= ' ' }
        email = emailAddress_et!!.text.toString().trim { it <= ' ' }
        val searchStr = search_et!!.text.toString().trim { it <= ' ' }
        if (TextUtils.isEmpty(businessName)) {
            businessNameTIL!!.error = "Enter Business Name."
            return false
        }
        if (TextUtils.isEmpty(streetAddress)) {
            streetAddressTIL!!.error = this.getString(R.string.enter_business_name)
            return false
        }
        if (TextUtils.isEmpty(zipCode)) {
            zipCodeTIL!!.error = this.getString(R.string.enter_zip_code)
            return false
        }
        if (TextUtils.isEmpty(city)) {
            cityTIL!!.error = this.getString(R.string.enter_city_name)
            return false
        }
        if (TextUtils.isEmpty(state)) {
            stateTIL!!.error = this.getString(R.string.enter_state_name)
            return false
        }
        if (TextUtils.isEmpty(country)) {
            countryTIL!!.error = this.getString(R.string.enter_country_name)
            return false
        }
        if (TextUtils.isEmpty(searchStr)) {
            AppUtil.showErrorDialog(this, "Alert!", "Search Location.")
            return false
        }
        if (TextUtils.isEmpty(website)) {
            webLinkTIL!!.error = this.getString(R.string.enter_website_link)
            return false
        }
        if (TextUtils.isEmpty(phoneNumber)) {
            phoneNumberTIL!!.error = this.getString(R.string.enter_phone_number)
            return false
        }
        if (TextUtils.isEmpty(email)) {
            emailAddressTIL!!.error = this.getString(R.string.enter_email_address)
            return false
        }
        return true
    }

    fun openBottomSheet() {
        myBottomSheet = CategorySheet.newInstance(this@EditBusinessActivity, details)
        myBottomSheet?.show(supportFragmentManager, SelectDaySheet.TAG)
    }

    override fun onCategoryItemClick() {
        myBottomSheet!!.dismiss()
        chipGroup!!.removeAllViews()
        details!!.category.clear()
        for (cuisine in details!!.cuisines) {
            if (cuisine.isSelected) {
                cuisine.isSelected = false
                details!!.category.add(cuisine.name)
            }
        }
        for (str in details!!.category) {
            createDynamically(str)
        }
    }

    fun createDynamically(chips: String?) {
        val tomatoChip = getChip(chips)
        chipGroup!!.addView(tomatoChip)
    }

    private fun getChip(text: String?): Chip {
        val chip = Chip(this)
        chip.setChipDrawable(ChipDrawable.createFromResource(this, R.xml.chip))
        val paddingDp =
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10f, resources.displayMetrics)
                .toInt()
        chip.setPadding(paddingDp, paddingDp, paddingDp, paddingDp)
        chip.text = text
        chip.elevation = 5.0f
        chip.setOnCloseIconClickListener {
            chipGroup!!.removeView(chip)
            for (chipName in details!!.category) {
                if (chipName == chip.text.toString()) {
                    details!!.category.remove(chipName)
                    break
                }
            }
        }
        return chip
    }

    private fun dispatchToSetHHDetailsActivity() {
        val intent = Intent(this@EditBusinessActivity, SetHappyHourActivity::class.java)
        intent.putExtra(AppConstant.BUSINESS_ID, businessId)
        startActivity(intent)
    }

    private fun dispatchToSetBrunchActivity() {
        val intent = Intent(this@EditBusinessActivity, SetBrunchActivity::class.java)
        intent.putExtra(AppConstant.BUSINESS_ID, businessId)
        startActivity(intent)
    }

    private fun dispatchToSetLiveMusicActivity() {
        val intent = Intent(this@EditBusinessActivity, SetLiveMusicActivity::class.java)
        intent.putExtra(AppConstant.BUSINESS_ID, businessId)
        startActivity(intent)
    }

    private fun dispatchToSetReverseHHActivity() {
        val intent = Intent(this@EditBusinessActivity, SetReverseHappyHourActivity::class.java)
        intent.putExtra(AppConstant.BUSINESS_ID, businessId)
        startActivity(intent)
    }

    private fun dispatchToSetRegularMenuDetailsActivity() {
        val intent = Intent(this@EditBusinessActivity, SetRegularMenuDetailsActivity::class.java)
        intent.putExtra(AppConstant.BUSINESS_ID, businessId)
        startActivity(intent)
    }

    private fun editBusinessDetailsService() {
        AppUtil.showProgressDialog(this@EditBusinessActivity)
        val token = SharedPreferenceWriter.getInstance(this@EditBusinessActivity)
            .getString(SharedPrefsKey.TOKEN)
        val apiServiceInterface = ApiClient.instance.client
        val call = apiServiceInterface.editBusinessDetails(token, businessId)
        call.enqueue(object : Callback<EditBusinessResponse?> {
            override fun onResponse(
                call: Call<EditBusinessResponse?>?,
                response: Response<EditBusinessResponse?>
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
                            details = detailsResponse.editBusinessDetails
                            updateUI()
                        } else {
                            AppUtil.showErrorDialog(
                                this@EditBusinessActivity,
                                resources.getString(R.string.error),
                                detailsResponse.message
                            )
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<EditBusinessResponse?>, t: Throwable) {
                AppUtil.dismissProgressDialog()
                call.cancel()
                AppUtil.showErrorDialog(
                    this@EditBusinessActivity,
                    getString(R.string.error),
                    t.message
                )
            }
        })
    }

    fun updateUI() {
        businessName_et!!.setText(details!!.name)
        streetAddress_et!!.setText(details!!.address)
        zipCode_et!!.setText(details!!.zipcode)
        city_et!!.setText(details!!.city)
        state_et!!.setText(details!!.state)
        country_et!!.setText(details!!.country)
        webLink_et!!.setText(details!!.website)
        phoneNumber_et!!.setText(details!!.phone)
        emailAddress_et!!.setText(details!!.email)
        if (details!!.category.size > 0) {
            for (str in details!!.category) {
                createDynamically(str)
            }
        }
        setRecipesImagesList(details!!.images)
    }

    fun showDeleteConfirmationDialog(itemID: String?, position: Int) {
        ConfirmationDialog(this) {
            if (itemID != null) {
                goForDeleteImagesService(itemID, position)
            } else {
                editBusinessRecipesImagesAdapter!!.removeMenuItemFromList(itemID, position)
            }
        }.show()
    }

    fun goForDeleteImagesService(itemId: String?, position: Int) {
        if (AppUtil.isNetworkAvailable(this)) {
            val commonApi = CommonAPI(this)
            commonApi.deleteEditBusinessImageService(itemId) { msg ->
                editBusinessRecipesImagesAdapter!!.removeMenuItemFromList(itemId, position)
                showSuccessDialog(msg, false)
            }
        } else {
            AppUtil.showConnectionError(this)
        }
    }

    fun showSuccessDialog(msg: String?, finish: Boolean) {
        CustomSuccessDialog(this, getString(R.string.success), msg) {
            if (finish) {
                finish()
            }
        }.show()
    }

    private fun chooseOptionSheet() {
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
        startActivityForResult(intent, EditBusinessActivity.Companion.SELECT_GALLERY_IMAGE)
    }

    private fun openCamera() {
        val intent = Intent(this@EditBusinessActivity, TakeImage::class.java)
        startActivityForResult(intent, EditBusinessActivity.Companion.CAPTURE_IMAGE)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            EditBusinessActivity.Companion.SELECT_GALLERY_IMAGE -> if (data != null) {
                imageUri = data.data
                imagePath = PhotoUtil.getImageStringToUri(this@EditBusinessActivity, imageUri)
                addImageInList(imagePath.orEmpty())
            }
            EditBusinessActivity.Companion.CAPTURE_IMAGE -> if (data != null) {
                imagePath = data.getStringExtra("photoFile")
                val bmOptions = BitmapFactory.Options()
                val bitmap = BitmapFactory.decodeFile(imagePath, bmOptions)
                addImageInList(imagePath.orEmpty())
            }
            EditBusinessActivity.Companion.AUTOCOMPLETE_REQUEST_CODE -> if (resultCode == RESULT_OK) {
                val place = Autocomplete.getPlaceFromIntent(data!!)
                getAddress(place.latLng)
                Log.d(EditBusinessActivity.Companion.TAG, "Place: " + place.name + ", " + place.id)
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                val status = Autocomplete.getStatusFromIntent(
                    data!!
                )
                Log.d(EditBusinessActivity.Companion.TAG, status.statusMessage!!)
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    fun addImageInList(imagePath: String) {
        val image = Image()
        image.image = imagePath
        details!!.images.add(0, image)
        setRecipesImagesList(details!!.images)
    }

    fun createMultipartForImage() {
        var lengthOfArray = 0
        for (image in details!!.images) {
            if (!image.image.contains("http")) {
                lengthOfArray = lengthOfArray + 1
            }
        }
        imgMultipart = arrayOfNulls<MultipartBody.Part>(lengthOfArray)
        for (index in details!!.images.indices) {
            val image = details!!.images[index]
            if (!image.image.contains("http")) {
                val file = File(image.image)
                val requestBody = RequestBody.create("*/*".toMediaTypeOrNull(), file)
                imgMultipart[index] =
                    createFormData("images[]", file.name, requestBody)
            }
        }
        categoryRequestBody = arrayOfNulls(details!!.category.size)
        for (i in details!!.category.indices) {
            categoryRequestBody[i] =
                RequestBody.create("text/plain".toMediaTypeOrNull(), details!!.category[i])
        }
    }

    private fun dispatchToUpdateBusinessService() {
        if (AppUtil.isNetworkAvailable(this)) {
            if (validate()) {
                updateBusinessService()
            }
        } else {
            AppUtil.showConnectionError(this)
        }
    }

    private fun updateBusinessService() {
        AppUtil.showProgressDialog(this@EditBusinessActivity)
        createMultipartForImage()
        val token = SharedPreferenceWriter.getInstance(this@EditBusinessActivity)
            .getString(SharedPrefsKey.TOKEN)
        val sessionToken = token?.toRequestBody("text/plain".toMediaTypeOrNull())
        val body_businessName = businessName!!.toRequestBody("text/plain".toMediaTypeOrNull())
        val body_businessId = businessId!!.toRequestBody("text/plain".toMediaTypeOrNull())
        val body_address = streetAddress!!.toRequestBody("text/plain".toMediaTypeOrNull())
        val body_state = state!!.toRequestBody("text/plain".toMediaTypeOrNull())
        val body_city = city!!.toRequestBody("text/plain".toMediaTypeOrNull())
        val body_zipCode = zipCode!!.toRequestBody("text/plain".toMediaTypeOrNull())
        val body_country = country!!.toRequestBody("text/plain".toMediaTypeOrNull())
        val body_website = website!!.toRequestBody("text/plain".toMediaTypeOrNull())
        val body_phone = phoneNumber!!.toRequestBody("text/plain".toMediaTypeOrNull())
        val body_email = email!!.toRequestBody("text/plain".toMediaTypeOrNull())
        val body_latitude = latitude.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val body_longitude = longitude.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val apiServiceInterface = ApiClient.instance.client
        val call = apiServiceInterface
            .updateBusiness(
                sessionToken,
                body_businessName,
                body_businessId,
                categoryRequestBody,
                body_address,
                body_state,
                body_city,
                body_zipCode,
                body_country,
                body_website,
                body_phone,
                body_email,
                body_latitude,
                body_longitude,
                imgMultipart
            )
        call.enqueue(object : Callback<JsonElement?> {
            override fun onResponse(call: Call<JsonElement?>, response: Response<JsonElement?>) {
                AppUtil.dismissProgressDialog()
                try {
                    if (response.isSuccessful) {
                        val `object` =
                            JSONObject(response.body()!!.asJsonObject.toString().trim { it <= ' ' })
                        if (`object`.getString("status").equals("SUCCESS", ignoreCase = true)) {
                            showSuccessDialog(`object`.getString("message"), true)
                        } else {
                            if (`object`.getString("message").contains("Session expired")) {
                                AppUtil.showLogoutDialog(
                                    this@EditBusinessActivity,
                                    resources.getString(R.string.error),
                                    `object`.getString("message")
                                )
                            } else {
                                AppUtil.showErrorDialog(
                                    this@EditBusinessActivity,
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
                    this@EditBusinessActivity,
                    resources.getString(R.string.error),
                    t.message
                )
            }
        })
    }

    companion object {
        const val SELECT_GALLERY_IMAGE = 1012
        const val CAPTURE_IMAGE = 1021
        private const val AUTOCOMPLETE_REQUEST_CODE = 1
        private const val TAG = "EditBusinessActivity"
    }
}