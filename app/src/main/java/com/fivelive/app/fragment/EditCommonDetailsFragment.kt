package com.fivelive.app.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ScrollView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fivelive.app.Model.EditHHDetailsResponse
import com.fivelive.app.Model.ModelTest
import com.fivelive.app.R
import com.fivelive.app.activity.AddHappyHourDetailsActivity
import com.fivelive.app.adapter.EditCommonDetailsAdapter
import com.fivelive.app.dialog.ConfirmationDialog
import com.fivelive.app.dialog.CustomSuccessDialog
import com.fivelive.app.util.AppConstant
import com.fivelive.app.util.AppUtil
import com.fivelive.app.util.CommonAPI

class EditCommonDetailsFragment : Fragment(), View.OnClickListener {
    var neverRepeatRecyclerView: RecyclerView? = null
    var scrollView: ScrollView? = null
    var addMoreMenuLayout: LinearLayout? = null
    var list: MutableList<ModelTest> = ArrayList()
    var adapter: EditCommonDetailsAdapter? = null
    var activity: Activity? = null
    var businessId: String? = null
    var type: String? = null

    // Type = 1 for HappyHours Details
    // Type = 2 for Reverse HappyHours details
    // Type = 3 for Brunch Details
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity = getActivity()
        businessId = requireArguments().getString(AppConstant.BUSINESS_ID)
        type = requireArguments().getString(AppConstant.TYPE)
        return inflater.inflate(R.layout.fragment_happy_hour_details, container, false)
    }

    // @Override
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        neverRepeatRecyclerView = view.findViewById(R.id.neverRepeatRecyclerView)
        addMoreMenuLayout = view.findViewById(R.id.addMoreMenuLayout)
        addMoreMenuLayout?.setOnClickListener(this)
        scrollView = view.findViewById(R.id.scrollView)
    }

    override fun onResume() {
        super.onResume()
        goForDetailsListingService()
    }

    fun scrollScreen() {
        scrollView!!.post { scrollView!!.smoothScrollTo(0, scrollView!!.bottom) }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.addMoreMenuLayout -> dispatchToAddHHDetailsActivity(false, "")
        }
    }

    fun dispatchToAddHHDetailsActivity(isEdit: Boolean, scheduleId: String?) {
        val intent = Intent(activity, AddHappyHourDetailsActivity::class.java)
        intent.putExtra(AppConstant.BUSINESS_ID, businessId)
        intent.putExtra(AppConstant.ID, scheduleId)
        intent.putExtra(AppConstant.TYPE, type)
        intent.putExtra(AppConstant.EDIT, isEdit)
        /*convert list to ArrayList */
        val modelTestArrayList = ArrayList<ModelTest>(list.size)
        modelTestArrayList.addAll(list)
        intent.putParcelableArrayListExtra(AppConstant.MODEL_TEST_LIST, modelTestArrayList)
        startActivity(intent)
    }

    fun showSuccessDialog(context: Context?, msg: String?) {
        CustomSuccessDialog(activity, requireActivity().getString(R.string.success), msg) { }.show()
    }

    fun goForDetailsListingService() {
        if (AppUtil.isNetworkAvailable(requireContext())) {
            val commonApi = CommonAPI(requireContext())
            commonApi.detailsScheduleListingService(businessId, type) { `object` ->
                list.clear()
                list.addAll(`object`.detailList)
                setDataOnList()
            }
        } else {
            AppUtil.showConnectionError(activity)
        }
    }

    fun setDataOnList() {
        adapter = EditCommonDetailsAdapter(requireContext(), list, this, type!!)
        val llm = LinearLayoutManager(getActivity())
        llm.orientation = LinearLayoutManager.VERTICAL
        neverRepeatRecyclerView!!.layoutManager = llm
        neverRepeatRecyclerView!!.adapter = adapter
    }

    fun showDeleteConfirmationDialog(itemID: String) {
        ConfirmationDialog(activity) { goForDeleteMenuScheduleService(itemID) }.show()
    }

    fun goForDeleteMenuScheduleService(id: String) {
        if (AppUtil.isNetworkAvailable(requireContext())) {
            val commonApi = CommonAPI(requireContext())
            commonApi.deleteMenuScheduleService(id, type) { msg ->
                adapter!!.removeDataFromList(id)
                showSuccessDialog(msg)
            }
        } else {
            AppUtil.showConnectionError(activity)
        }
    }

    fun showSuccessDialog(msg: String?) {
        CustomSuccessDialog(activity, requireActivity().getString(R.string.success), msg) { }.show()
    }

    companion object {
        const val TAG = "EditCommonDetailsFragment"
    }
}