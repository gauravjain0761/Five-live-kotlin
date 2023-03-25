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
import com.fivelive.app.Model.EditHHDetailsResponse
import com.fivelive.app.Model.ModelTest
import com.fivelive.app.R
import com.fivelive.app.activity.AddLiveMusicDetailsActivity
import com.fivelive.app.adapter.EditLiveMusicDetialAdapter
import com.fivelive.app.dialog.ConfirmationDialog
import com.fivelive.app.dialog.CustomSuccessDialog
import com.fivelive.app.util.AppConstant
import com.fivelive.app.util.AppUtil
import com.fivelive.app.util.CommonAPI

class EditLiveMusicFragment : Fragment(), View.OnClickListener {
    var recyclerView: RecyclerView? = null
    var addMoreMenuLayout: LinearLayout? = null
    var list: MutableList<ModelTest> = ArrayList()
    var adapter: EditLiveMusicDetialAdapter? = null
    var activity: Activity? = null
    var businessId: String? = null
    var type: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity = getActivity()
        if (arguments != null) {
            businessId = requireArguments().getString(AppConstant.BUSINESS_ID)
            type = requireArguments().getString(AppConstant.TYPE)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_edit_live_music, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById(R.id.recyclerView)
        addMoreMenuLayout = view.findViewById(R.id.addMoreMenuLayout)
        addMoreMenuLayout?.setOnClickListener(this)
    }

    override fun onResume() {
        super.onResume()
        goForDetailsListingService()
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.addMoreMenuLayout -> dispatchToAddLiveMusicDetailsActivity(false, "")
        }
    }

    fun goForDetailsListingService() {
        if (AppUtil.isNetworkAvailable(requireContext())) {
            val commonApi = CommonAPI(requireContext())
            commonApi.detailsScheduleListingService(businessId, type) { `object` ->
                if (`object` is EditHHDetailsResponse) {
                    list.clear()
                    list.addAll(`object`.detailList)
                    setDataOnList()
                }
            }
        } else {
            AppUtil.showConnectionError(activity)
        }
    }

    fun setDataOnList() {
        adapter = EditLiveMusicDetialAdapter(requireContext(), list, this@EditLiveMusicFragment)
        val llm = LinearLayoutManager(getActivity())
        llm.orientation = LinearLayoutManager.VERTICAL
        recyclerView!!.layoutManager = llm
        recyclerView!!.adapter = adapter
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

    fun dispatchToAddLiveMusicDetailsActivity(isEdit: Boolean, scheduleId: String?) {
        val intent = Intent(activity, AddLiveMusicDetailsActivity::class.java)
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
}