package com.fivelive.app.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fivelive.app.Model.Category
import com.fivelive.app.Model.EditHHItemsResponse
import com.fivelive.app.R
import com.fivelive.app.activity.AddHappyHoursItemActivity
import com.fivelive.app.activity.SetHappyHourActivity
import com.fivelive.app.adapter.ParentCategoryAdapter
import com.fivelive.app.prefs.SharedPreferenceWriter
import com.fivelive.app.prefs.SharedPrefsKey
import com.fivelive.app.retrofit.ApiClient
import com.fivelive.app.util.AppConstant
import com.fivelive.app.util.AppUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditHappyHoursItemsFragment : Fragment(), View.OnClickListener {
    var recyclerView: RecyclerView? = null
    var addItemLinearLayout: LinearLayout? = null
    var activity: Activity? = null
    var categoryList: List<Category>? = null
    var businessId: String? = null
    var type = "1"
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity = getActivity()
        val bundle = arguments
        businessId = bundle!!.getString(AppConstant.BUSINESS_ID)
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_happy_hours_items, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById<View>(R.id.recyclerView) as RecyclerView
        addItemLinearLayout = view.findViewById<View>(R.id.addItemLinearLayout) as LinearLayout
        addItemLinearLayout!!.setOnClickListener(this)
    }

    override fun onResume() {
        super.onResume()
        if (AppUtil.isNetworkAvailable(requireContext())) {
            dispatchToItemListService()
        } else {
            AppUtil.showConnectionError(activity)
        }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.addItemLinearLayout -> dispatchToHHAddItemActivity()
        }
    }

    private fun dispatchToHHAddItemActivity() {
        val intent = Intent(activity, AddHappyHoursItemActivity::class.java)
        intent.putExtra(AppConstant.BUSINESS_ID, businessId)
        intent.putExtra(AppConstant.FROM, SetHappyHourActivity.TAG)
        startActivity(intent)
    }

    private fun dispatchToItemListService() {
        AppUtil.showProgressDialog(activity)
        val token = SharedPreferenceWriter.getInstance(requireContext()).getString(SharedPrefsKey.TOKEN)
        val apiServiceInterface = ApiClient.instance.client
        val call = apiServiceInterface.itemsList(type, businessId, token)
        call.enqueue(object : Callback<EditHHItemsResponse?> {
            override fun onResponse(
                call: Call<EditHHItemsResponse?>,
                response: Response<EditHHItemsResponse?>
            ) {
                AppUtil.dismissProgressDialog()
                try {
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        if (responseBody!!.status.equals(AppConstant.SUCCESS, ignoreCase = true)) {
                            categoryList = responseBody.itemList.category
                            setDataOnList()
                        } else {
                            if (responseBody.message.contains(AppConstant.SESSION_EXPIRED)) {
                                AppUtil.showLogoutDialog(
                                    requireContext(),
                                    activity!!.resources.getString(R.string.error),
                                    responseBody.message
                                )
                            } else {
                                AppUtil.showErrorDialog(
                                    activity,
                                    activity!!.resources.getString(R.string.error),
                                    responseBody.message
                                )
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<EditHHItemsResponse?>, t: Throwable) {
                AppUtil.dismissProgressDialog()
                call.cancel()
                AppUtil.showErrorDialog(activity, getString(R.string.error), t.message)
            }
        })
    }

    fun setDataOnList() {
        val stringList: MutableList<String> = ArrayList()
        stringList.add("Happy Hour Drink Menu")
        stringList.add("Happy Hour Food Menu")
        val adapter = ParentCategoryAdapter(requireContext(), categoryList!!)
        val llm = LinearLayoutManager(getActivity())
        llm.orientation = LinearLayoutManager.VERTICAL
        recyclerView!!.layoutManager = llm
        recyclerView!!.adapter = adapter
    }

    companion object {
        const val TAG = "HappyHoursItemsFragment"
    }
}