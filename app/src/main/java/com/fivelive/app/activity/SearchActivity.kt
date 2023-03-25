package com.fivelive.app.activity

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.TypedValue
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fivelive.app.Model.*
import com.fivelive.app.R
import com.fivelive.app.adapter.SearchAdapter
import com.fivelive.app.prefs.SharedPreferenceWriter
import com.fivelive.app.prefs.SharedPrefsKey
import com.fivelive.app.retrofit.ApiClient
import com.fivelive.app.util.AppConstant
import com.fivelive.app.util.AppUtil
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FetchPlaceResponse
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import com.google.android.material.chip.ChipGroup
import com.mancj.materialsearchbar.MaterialSearchBar
import com.mancj.materialsearchbar.MaterialSearchBar.OnSearchActionListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class SearchActivity() : AppCompatActivity() {
    var searchBar: MaterialSearchBar? = null
    private var placesClient: PlacesClient? = null
    var autocompleteSessionToken: AutocompleteSessionToken? = null
    private var predictionList: List<AutocompletePrediction>? = null
    var gpsSuggestionList: MutableList<SuggestionModel> = ArrayList()
    var apiSuggestionList: MutableList<SuggestionModel> = ArrayList()
    var suggestionList: MutableList<SuggestionModel> = ArrayList()
    var recyclerView: RecyclerView? = null
    var searchAdapter: SearchAdapter? = null
    var from: String? = null
    var chipGroup: ChipGroup? = null
    var addressList: List<AddressList>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        from = intent.getStringExtra(AppConstant.FROM)
        Places.initialize(applicationContext, getString(R.string.google_map_or_places_id))
        // Create a new PlacesClient instance
        placesClient = Places.createClient(this)
        autocompleteSessionToken = AutocompleteSessionToken.newInstance()
        initView()
        savedAddressList
    }

    private fun initView() {
        recyclerView = findViewById(R.id.recyclerView)
        searchBar = findViewById(R.id.searchBar)
        chipGroup = findViewById(R.id.chipGroup)
        if (searchBar?.isSearchOpened != true) {
            searchBar?.openSearch()
        }
        addPlacesAPIOnSearchBar()
    }

    fun addPlacesAPIOnSearchBar() {
        searchBar!!.setOnSearchActionListener(object : OnSearchActionListener {
            override fun onSearchStateChanged(enabled: Boolean) {
                if (!enabled) {
                    AppUtil.hideKeyboard(this@SearchActivity)
                    finish()
                }
            }

            override fun onSearchConfirmed(text: CharSequence) {
                Toast.makeText(this@SearchActivity, "onSearchConfirmed", Toast.LENGTH_SHORT).show()
                //startSearch(text.toString(), true, null, true);
            }

            override fun onButtonClicked(buttonCode: Int) {
                if (buttonCode == MaterialSearchBar.BUTTON_BACK) {
                    Toast.makeText(this@SearchActivity, "onButtonClicked", Toast.LENGTH_SHORT)
                        .show()
                    searchBar!!.closeSearch()
                }
            }
        })
        searchBar!!.addTextChangeListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                val predictionsRequest = FindAutocompletePredictionsRequest.builder()
                    .setCountry("US")
                    .setTypeFilter(TypeFilter.ADDRESS)
                    .setSessionToken(autocompleteSessionToken)
                    .setQuery(charSequence.toString())
                    .build()
                placesClient!!.findAutocompletePredictions(predictionsRequest)
                    .addOnCompleteListener(
                        OnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val predictionsResponse = task.result
                                if (predictionsResponse != null) {
                                    predictionList = predictionsResponse.autocompletePredictions
                                    suggestionList.clear()
                                    gpsSuggestionList.clear()
                                    for (prediction: AutocompletePrediction in predictionList!!) {
                                        val model = SuggestionModel()
                                        model.name = prediction.getFullText(null).toString()
                                        model.businessId = prediction.placeId
                                        model.isFromServer = false
                                        gpsSuggestionList.add(model)
                                    }
                                    setDataOnList(gpsSuggestionList)
                                }
                            } else {
                                Toast.makeText(
                                    this@SearchActivity,
                                    "predication fetching task unsuccessful.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        })
            }

            override fun afterTextChanged(editable: Editable) {
                val keyword = editable.toString().trim { it <= ' ' }
                if (keyword.length >= 2) {
                    callAutoSuggestionBusinessService(editable.toString())
                }
            }
        })
    }

    fun callAutoSuggestionBusinessService(keyword: String?) {
        if (AppUtil.isNetworkAvailable(this)) {
            autoSuggestBusinessService(keyword)
        } else {
            AppUtil.showConnectionError(this)
        }
    }

    fun autoSuggestBusinessService(keyword: String?) {
        //AppUtil.showProgressDialog(SearchActivity.this);
        val apiServiceInterface = ApiClient.instance.client
        val call = apiServiceInterface.autoSuggestBusiness(keyword)
        call.enqueue(object : Callback<CommonBusinessResponse?> {
            override fun onResponse(
                call: Call<CommonBusinessResponse?>,
                response: Response<CommonBusinessResponse?>
            ) {
                // AppUtil.dismissProgressDialog();
                try {
                    if (response.isSuccessful) {
                        val listingResponse = response.body()
                        if (listingResponse!!.status.equals(
                                AppConstant.SUCCESS,
                                ignoreCase = true
                            )
                        ) {
                            prepareSuggestionList(listingResponse.businessList)
                        } else {
                            if (listingResponse.message.contains(AppConstant.SESSION_EXPIRED)) {
                                AppUtil.showLogoutDialog(
                                    this@SearchActivity,
                                    this@SearchActivity.resources.getString(R.string.error),
                                    listingResponse.message
                                )
                            } else {
                                AppUtil.showErrorDialog(
                                    this@SearchActivity,
                                    this@SearchActivity.resources.getString(R.string.error),
                                    listingResponse.message
                                )
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<CommonBusinessResponse?>, t: Throwable) {
                //  AppUtil.dismissProgressDialog();
                call.cancel()
                AppUtil.showErrorDialog(
                    this@SearchActivity,
                    this@SearchActivity.resources.getString(R.string.error),
                    t.message
                )
            }
        })
    }

    fun prepareSuggestionList(businessLists: List<BusinessList>) {
        apiSuggestionList.clear()
        for (model: BusinessList in businessLists) {
            val suggestionModel = SuggestionModel()
            suggestionModel.name = model.name
            suggestionModel.isFromServer = true
            suggestionModel.businessId = model.id
            apiSuggestionList.add(suggestionModel)
        }
        suggestionList.clear()
        suggestionList.addAll(apiSuggestionList)
        suggestionList.addAll(gpsSuggestionList)
        setDataOnList(suggestionList)
    }

    fun setDataOnList(stringList: List<SuggestionModel>?) {
        searchAdapter = SearchAdapter(this, stringList!!)
        val llm = LinearLayoutManager(this)
        llm.orientation = LinearLayoutManager.VERTICAL
        recyclerView!!.layoutManager = llm
        recyclerView!!.adapter = searchAdapter
    }

    fun dispatchToDetailsScreen(businessId: String?) {
        val intent = Intent(this@SearchActivity, DetailsActivity::class.java)
        intent.putExtra(AppConstant.BUSINESS_ID, businessId)
        startActivity(intent)
        finish()
    }

    fun fetchPlacesRequest(placeId: String?, placeName: String?) {
        val placeFields = Arrays.asList(Place.Field.LAT_LNG)
        val fetchPlaceRequest = FetchPlaceRequest.builder((placeId)!!, placeFields).build()
        placesClient!!.fetchPlace(fetchPlaceRequest)
            .addOnSuccessListener(object : OnSuccessListener<FetchPlaceResponse> {
                override fun onSuccess(fetchPlaceResponse: FetchPlaceResponse) {
                    val place = fetchPlaceResponse.place
                    val latLng = place.latLng
                    dispatchToHomeScreen(latLng!!.latitude, latLng.longitude, placeName)
                }
            }).addOnFailureListener(object : OnFailureListener {
            override fun onFailure(e: Exception) {
                if (e is ApiException) {
                    val apiException = e
                    apiException.printStackTrace()
                    val statusCode = apiException.statusCode
                    Log.d("MyTag", "onFailure: Place Not found" + e.message)
                }
            }
        })
    }

    fun dispatchToHomeScreen(latitude: Double, longitude: Double, placeName: String?) {
        val intent = Intent(this@SearchActivity, HomeActivity::class.java)
        intent.putExtra(AppConstant.LATITUDE, latitude.toString())
        intent.putExtra(AppConstant.LONGITUDE, longitude.toString())
        intent.putExtra(AppConstant.SEARCH_ADDRESS, placeName)
        intent.putExtra(AppConstant.FROM, from)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        finish()
    }

    val savedAddressList: Unit
        get() {
            if (AppUtil.isNetworkAvailable(this)) {
                addressListService()
            } else {
                AppUtil.showConnectionError(this)
            }
        }

    private fun addressListService() {
        AppUtil.showProgressDialog(this@SearchActivity)
        val token =
            SharedPreferenceWriter.getInstance(this@SearchActivity).getString(SharedPrefsKey.TOKEN)
        val apiServiceInterface = ApiClient.instance.client
        val call = apiServiceInterface.addressList(token)
        call.enqueue(object : Callback<SavedAddressResponse?> {
            override fun onResponse(
                call: Call<SavedAddressResponse?>,
                response: Response<SavedAddressResponse?>
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
                            addressList = detailsResponse.addressList
                            createDynamically()
                        } else {
                            AppUtil.showErrorDialog(
                                this@SearchActivity,
                                resources.getString(R.string.error),
                                detailsResponse.message
                            )
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<SavedAddressResponse?>, t: Throwable) {
                AppUtil.dismissProgressDialog()
                call.cancel()
                AppUtil.showErrorDialog(
                    this@SearchActivity,
                    resources.getString(R.string.error),
                    t.message
                )
            }
        })
    }

    fun createDynamically() {
        for (data: AddressList in addressList!!) {
            val chip = getChip(data.title)
            chipGroup!!.addView(chip)
        }
        chipGroup!!.setOnCheckedChangeListener(object : ChipGroup.OnCheckedChangeListener {
            override fun onCheckedChanged(group: ChipGroup, checkedId: Int) {
                val chip = group.findViewById<Chip>(checkedId)
                val title = chip.text.toString()
                getAddressTitle(title)
            }
        })
    }

    fun getAddressTitle(title: String) {
        for (data: AddressList in addressList!!) {
            if ((title == data.title)) {
                dispatchToHomeScreen(
                    data.latitude.toDouble(),
                    data.longitude.toDouble(),
                    data.title
                )
                break
            }
        }
    }

    private fun getChip(text: String): Chip {
        val chip = Chip(this)
        chip.setChipDrawable(ChipDrawable.createFromResource(this, R.xml.quick_filter_chip))
        chip.elevation = 10f
        chip.isCloseIconVisible = false
        chip.setTextColor(this.resources.getColorStateList(R.color.text_color_chip_state_list))
        chip.setTextAppearance(R.style.ChipTextStyleAppearance)
        val paddingDp = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            10f,
            this.resources.displayMetrics
        ).toInt()
        chip.setPadding(paddingDp, paddingDp, paddingDp, paddingDp)
        chip.text = text
        return chip
    }

    companion object {
        @JvmField
        val TAG = SearchActivity::class.java.name
    }
}