package com.fivelive.app.fragment

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fivelive.app.Model.HomeBusiness
import com.fivelive.app.Model.HomeBusinessResponse
import com.fivelive.app.R
import com.fivelive.app.activity.NotificationActivity
import com.fivelive.app.adapter.RestaurantListAdapter
import com.fivelive.app.ads.FullScreenAds
import com.fivelive.app.currentLocation.CurrentLocation
import com.fivelive.app.prefs.SharedPreferenceWriter
import com.fivelive.app.prefs.SharedPrefsKey
import com.fivelive.app.retrofit.ApiClient
import com.fivelive.app.util.AppConstant
import com.fivelive.app.util.AppUtil
import com.fivelive.app.util.CommonAPI
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class SavedFragment : Fragment(), View.OnClickListener {
    var activity: Activity? = null
    var recyclerView: RecyclerView? = null
    var favList: List<HomeBusiness> = ArrayList()
    var restaurantAdapter: RestaurantListAdapter? = null
    var parentLayout: ConstraintLayout? = null
    var notification_imv: ImageView? = null
    var searchBar_et: EditText? = null
    var mCurrentLocation: CurrentLocation? = null
    var latitude: String? = null
    var longitude: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity = getActivity()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        FullScreenAds.instance.initializeInterstitialAd(requireActivity())
        val view = inflater.inflate(R.layout.fragment_saved, container, false)
        val bundle = arguments
        latitude = bundle!!.getString(AppConstant.LATITUDE)
        longitude = bundle.getString(AppConstant.LONGITUDE)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        parentLayout = view.findViewById(R.id.parentLayout)
        notification_imv = view.findViewById(R.id.notification_imv)
        searchBar_et = view.findViewById(R.id.searchBar_et)
        searchBar_et?.setHint("Search Saved Restaurant")
        notification_imv?.setOnClickListener(this)
        recyclerView = view.findViewById(R.id.recyclerView)
        addTextWatcherListener()
        // setDataOnRestaurantList();
    }

    override fun onResume() {
        super.onResume()
        dispatchToMyFavoriteService()
    }

    fun dispatchToMyFavoriteService() {
        if (AppUtil.isNetworkAvailable(requireContext())) {
            myFavoriteService()
        } else {
            AppUtil.showErrorDialog(
                activity,
                this.resources.getString(R.string.error),
                this.resources.getString(R.string.internet_connection_message)
            )
        }
    }

    private fun addTextWatcherListener() {
        searchBar_et!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                setUpFilter(s.toString().trim { it <= ' ' })
            }

            override fun afterTextChanged(s: Editable) {}
        })
    }

    fun setUpFilter(s: String) {
        restaurantAdapter!!.filter(s)
    }

    private fun dispatchToNotificationScreen() {
        startActivity(Intent(getActivity(), NotificationActivity::class.java))
    }

    fun myFavoriteService() {
        val token = SharedPreferenceWriter.getInstance(requireContext()).getString(SharedPrefsKey.TOKEN)
        AppUtil.showProgressDialog(activity)
        val apiServiceInterface = ApiClient.instance.client
        val call = apiServiceInterface.myFavoriteBusiness(token, latitude, longitude)
        call.enqueue(object : Callback<HomeBusinessResponse?> {
            override fun onResponse(
                call: Call<HomeBusinessResponse?>,
                response: Response<HomeBusinessResponse?>
            ) {
                AppUtil.dismissProgressDialog()
                try {
                    if (response.isSuccessful) {
                        val businessResponse = response.body()
                        if (businessResponse!!.status.equals(
                                AppConstant.SUCCESS,
                                ignoreCase = true
                            )
                        ) {
                            favList = businessResponse.myFavorite
                            setDataOnRestaurantList()
                        } else {
                            if (businessResponse.message.contains(AppConstant.SESSION_EXPIRED)) {
                                AppUtil.showLogoutDialog(
                                    requireContext(),
                                    activity!!.resources.getString(R.string.error),
                                    businessResponse.message
                                )
                            } else {
                                AppUtil.showErrorDialog(
                                    activity,
                                    activity!!.resources.getString(R.string.error),
                                    businessResponse.message
                                )
                            }
                        }
                    } else {
                        AppUtil.showErrorDialog(
                            activity,
                            activity!!.resources.getString(R.string.error),
                            response.message()
                        )
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<HomeBusinessResponse?>, t: Throwable) {
                AppUtil.dismissProgressDialog()
                call.cancel()
                AppUtil.showErrorDialog(
                    activity,
                    activity!!.resources.getString(R.string.error),
                    t.message
                )
            }
        })
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.notification_imv -> dispatchToNotificationScreen()
        }
    }

    fun goForAddFavoriteService(businessId: String, position: Int) {
        if (AppUtil.isNetworkAvailable(requireContext())) {
            val commonApi = CommonAPI(requireContext(), businessId)
            commonApi.addFavoriteService {
                restaurantAdapter!!.removeFromList(businessId)
                // updateFavoriteImage(businessId, status);
            }
        } else {
            AppUtil.showConnectionError(activity)
        }
    }

    fun setDataOnRestaurantList() {
        restaurantAdapter = RestaurantListAdapter(requireContext(), favList.toMutableList(), this@SavedFragment)
        val llm = LinearLayoutManager(getActivity())
        llm.orientation = LinearLayoutManager.VERTICAL
        recyclerView!!.layoutManager = llm
        recyclerView!!.adapter = restaurantAdapter
    }

    /*public void setDataOnRestaurantList() {
        AdsAdapterDemo restaurantAdapter = new AdsAdapterDemo(getActivity(), favList, SavedFragment.this);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(restaurantAdapter);
    }*/
    fun updateFavoriteImage(businessId: String, status: Int) {
        for (data in favList) {
            if (businessId == data.id) {
                data.favStatus = status
                restaurantAdapter!!.notifyDataSetChanged()
                break
            }
        }
    }

    fun dispatchToGoogleMap(latitude: String, longitude: String) {
        val uri = String.format(
            Locale.ENGLISH,
            "http://maps.google.com/maps?q=loc:%f,%f",
            latitude.toDouble(),
            longitude.toDouble()
        )
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
        startActivity(intent)
    }

    companion object {
        val TAG = MyAccountFragment::class.java.name
    }
}