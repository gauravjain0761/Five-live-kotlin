package com.fivelive.app.fragment

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.Group
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fivelive.app.Model.HomeBusiness
import com.fivelive.app.Model.HomeBusinessResponse
import com.fivelive.app.R
import com.fivelive.app.activity.NotificationActivity
import com.fivelive.app.activity.SearchActivity
import com.fivelive.app.adapter.RestaurantListAdapter
import com.fivelive.app.ads.FullScreenAds
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

class HappeningNowFragment : Fragment(), View.OnClickListener {
    var recyclerView: RecyclerView? = null
    var not_found_tv: TextView? = null
    var happeningNow_group: Group? = null
    var happeningNowList: List<HomeBusiness> = ArrayList()
    var restaurantAdapter: RestaurantListAdapter? = null

    // BusinessListOrAdsAdapter restaurantAdapter;
    var activity: Activity? = null
    var notification_imv: ImageView? = null
    var searchBar_et: EditText? = null
    var latitude: String? = null
    var longitude: String? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_happening_now, container, false)
        activity = getActivity()
        FullScreenAds.instance.initializeInterstitialAd(requireActivity())
        val bundle = arguments
        latitude = bundle!!.getString(AppConstant.LATITUDE)
        longitude = bundle.getString(AppConstant.LONGITUDE)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById<View>(R.id.recyclerView) as RecyclerView
        notification_imv = view.findViewById<View>(R.id.notification_imv) as ImageView
        searchBar_et = view.findViewById<View>(R.id.searchBar_et) as EditText
        not_found_tv = view.findViewById<View>(R.id.not_found_tv) as TextView
        // happeningNow_group =  view.findViewById(R.id.happeningNow_group);
        notification_imv!!.setOnClickListener(this)
        searchBar_et!!.setOnClickListener(this)
        dispatchToHappeningNowService()
        makeSearchBarNotEditable()
    }

    fun makeSearchBarNotEditable() {
        searchBar_et!!.isFocusable = false
        searchBar_et!!.isCursorVisible = false
    }

    private fun dispatchToHappeningNowService() {
        if (AppUtil.isNetworkAvailable(requireContext())) {
            happeningNowService()
        } else {
            AppUtil.showErrorDialog(
                activity,
                this.resources.getString(R.string.error),
                this.resources.getString(R.string.internet_connection_message)
            )
        }
    }

    fun setDataOnRestaurantList() {
        restaurantAdapter =
            RestaurantListAdapter(
                requireContext(),
                happeningNowList.toMutableList(),
                this@HappeningNowFragment
            )
        val llm = LinearLayoutManager(getActivity())
        llm.orientation = LinearLayoutManager.VERTICAL
        recyclerView!!.layoutManager = llm
        recyclerView!!.adapter = restaurantAdapter
    }

    /*public void setDataOnRestaurantList() {
        restaurantAdapter = new BusinessListOrAdsAdapter(getActivity(), happeningNowList, HappeningNowFragment.this);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(restaurantAdapter);
    }*/
    fun happeningNowService() {
        val token = SharedPreferenceWriter.getInstance(requireContext()).getString(SharedPrefsKey.TOKEN)
        AppUtil.showProgressDialog(activity)
        val apiServiceInterface = ApiClient.instance.client
        val call = apiServiceInterface.happeningNow(token, latitude, longitude)
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
                            happeningNowList = businessResponse.happeningNow
                            if (happeningNowList.isNotEmpty()) {
                                showList()
                                setDataOnRestaurantList()
                            } else {
                                hideList()
                            }
                        } else {
                            if (businessResponse.message.contains("Session expired")) {
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

    fun goForAddFavoriteService(businessId: String, position: Int) {
        if (AppUtil.isNetworkAvailable(requireContext())) {
            val commonApi = CommonAPI(requireContext(), businessId)
            commonApi.addFavoriteService { status -> updateFavoriteImage(businessId, status) }
        } else {
            AppUtil.showConnectionError(activity)
        }
    }

    fun updateFavoriteImage(businessId: String, status: Int) {
        for (data in happeningNowList) {
            if (businessId == data.id) {
                data.favStatus = status
                restaurantAdapter!!.notifyDataSetChanged()
                break
            }
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.notification_imv -> dispatchToNotificationScreen()
            R.id.searchBar_et -> dispatchToSearchActivity()
        }
    }

    private fun dispatchToSearchActivity() {
        val intent = Intent(activity, SearchActivity::class.java)
        intent.putExtra(AppConstant.FROM, TAG)
        startActivity(intent)
    }

    private fun dispatchToNotificationScreen() {
        startActivity(Intent(getActivity(), NotificationActivity::class.java))
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

    fun showList() {
        //  happeningNow_group.setVisibility(View.VISIBLE);
        recyclerView!!.visibility = View.VISIBLE
        not_found_tv!!.visibility = View.GONE
    }

    fun hideList() {
        //  happeningNow_group.setVisibility(View.GONE);
        recyclerView!!.visibility = View.GONE
        not_found_tv!!.visibility = View.VISIBLE
    }

    companion object {
        const val TAG = "HappeningNowFragment"
    }
}