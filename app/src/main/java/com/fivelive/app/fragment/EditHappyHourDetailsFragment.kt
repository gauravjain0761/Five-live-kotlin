package com.fivelive.app.fragment

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fivelive.app.Model.ModelTest
import com.fivelive.app.R
import com.fivelive.app.adapter.NeverRepeatMenuAdapter
import com.fivelive.app.bottomSheet.SelectDaySheet
import com.fivelive.app.bottomSheet.SelectDaySheet.Companion.newInstance
import com.fivelive.app.dialog.CustomSuccessDialog
import com.fivelive.app.dialog.TimePickerFragment
import com.fivelive.app.dialog.TimePickerFragment.TimeCallbackListener
import com.fivelive.app.util.AppConstant
import com.fivelive.app.util.AppUtil
import com.fivelive.app.util.CommonAPI

class EditHappyHourDetailsFragment : Fragment(), View.OnClickListener,
    SelectDaySheet.ItemClickListener, TimeCallbackListener {
    val TYPE = "1"
    var allDayEditImageView: ImageView? = null
    var neverRepeatRecyclerView: RecyclerView? = null
    var scrollView: ScrollView? = null
    var addMoreMenuLayout: LinearLayout? = null
    var list: MutableList<ModelTest> = ArrayList()
    var adapter: NeverRepeatMenuAdapter? = null
    var activity: Activity? = null
    var businessId: String? = null
    var myBottomSheet: SelectDaySheet? = null
    var model: ModelTest? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity = getActivity()
        businessId = requireArguments().getString(AppConstant.BUSINESS_ID)
        return inflater.inflate(R.layout.fragment_happy_hour_details, container, false)
        // return inflater.inflate(R.layout.never_repeat_layout, container, false);
    }

    // @Override
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        neverRepeatRecyclerView = view.findViewById(R.id.neverRepaetRecyclerView)
        addMoreMenuLayout = view.findViewById(R.id.addMoreMenuLayout)
        addMoreMenuLayout?.setOnClickListener(this)
        scrollView = view.findViewById(R.id.scrollView)
        goForDetailsListingService()
    }

    fun scrollScreen() {
        scrollView!!.post { scrollView!!.smoothScrollTo(0, scrollView!!.bottom) }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.addMoreMenuLayout -> {
                adapter!!.addMenuInList()
                scrollScreen()
            }
        }
    }

    fun deleteMenuCard(position: Int) {
        list.removeAt(position)
        adapter!!.notifyDataSetChanged()
    }

    fun openBottomSheet(position: Int) {
        val model = list[position]
        myBottomSheet = newInstance(null, this@EditHappyHourDetailsFragment, model, list)
        myBottomSheet!!.show(childFragmentManager, "MyTag")
    }

    override fun onItemClick(item: String?) {
        adapter!!.notifyDataSetChanged()
        myBottomSheet!!.dismiss()
    }

    fun openTimePicker(position: Int, fromTime: String?) {
        model = list[position]
        model!!.from = fromTime
        val fragment = TimePickerFragment.newInstance(null, this@EditHappyHourDetailsFragment)
        fragment.show(childFragmentManager, TimePickerFragment.TAG)
    }

    override fun getSelectedTime(item: String?) {
        if (model!!.from == AppConstant.START_TIME) {
            model!!.startTime = item
        } else if (model!!.from == AppConstant.END_TIME) {
            model!!.endTime = item
        }
        if (model!!.startTime != null && model!!.endTime != null) {
            if (AppUtil.compareStartOrEndTime(model!!.startTime, model!!.endTime)) {
                adapter!!.notifyDataSetChanged()
            } else {
                AppUtil.showErrorDialog(
                    getActivity(),
                    requireActivity().getString(R.string.error),
                    "End time should be grater than Start Time."
                )
            }
        } else {
            adapter!!.notifyDataSetChanged()
        }
    }

    fun goForAddMenuScheduleService(model: ModelTest) {
        if (AppUtil.isNetworkAvailable(requireContext())) {
            model.type = "1"
            val commonApi = CommonAPI(requireContext())
            commonApi.addMenuScheduleService(model, businessId) { msg ->
                showSuccessDialog(
                    activity,
                    msg
                )
            }
        } else {
            AppUtil.showConnectionError(activity)
        }
    }

    fun showSuccessDialog(context: Context?, msg: String?) {
        CustomSuccessDialog(activity, requireActivity().getString(R.string.success), msg,{

        }) .show()
    }

    fun goForDetailsListingService() {
        if (AppUtil.isNetworkAvailable(requireContext())) {
            val commonApi = CommonAPI(requireContext())
            commonApi.detailsScheduleListingService(businessId, TYPE) { `object` ->
                list.addAll(`object`.detailList)
                setDataOnList()
            }
        } else {
            AppUtil.showConnectionError(activity)
        }
    }

    fun setDataOnList() {
        adapter = NeverRepeatMenuAdapter(requireContext(), list, this)
        val llm = LinearLayoutManager(getActivity())
        llm.orientation = LinearLayoutManager.VERTICAL
        neverRepeatRecyclerView!!.layoutManager = llm
        neverRepeatRecyclerView!!.adapter = adapter
    }

    companion object {
        const val TAG = "EditHappyHourDetailsFra"
    }
}