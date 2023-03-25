package com.fivelive.app.bottomSheet

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fivelive.app.Model.FilterList
import com.fivelive.app.Model.ModelTest
import com.fivelive.app.R
import com.fivelive.app.adapter.QuickFilterChkboxAdapter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class QuickFilterBottomSheet : BottomSheetDialogFragment(), View.OnClickListener {
    val OPACITY = 0.8f
    private var mListener: FilterClickListener? = null
    var doneButton: TextView? = null
    var closeButton: TextView? = null
    var qf_recyclerview: RecyclerView? = null
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //return super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.quick_filter_bottom_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        qf_recyclerview = view.findViewById(R.id.qf_recyclerview)
        doneButton = view.findViewById(R.id.doneButton)
        closeButton = view.findViewById(R.id.closeButton)
        doneButton?.setOnClickListener(this)
        closeButton?.setOnClickListener(this)
        setDataInQuickFilterList()
        disableAlreadySelectedDays()
    }

    fun setDataInQuickFilterList() {
        val adapter = QuickFilterChkboxAdapter(requireContext(), mFilterLists!!, mModel!!)
        // LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        // llm.setOrientation(LinearLayoutManager.VERTICAL);
        val manager = GridLayoutManager(
            activity, 2,
            GridLayoutManager.VERTICAL, false
        )
        qf_recyclerview!!.layoutManager = manager
        qf_recyclerview!!.adapter = adapter
    }

    private fun checkSelectedCheckBox() {
        mModel!!.hhFilterList.clear()
        for (data in mFilterLists!!) {
            if (data.isSelected) {
                mModel!!.hhFilterList.add(data.name)
                data.isSelected = false
            }
        }
        mListener!!.onFilterClickListener("")
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.doneButton -> checkSelectedCheckBox()
            R.id.closeButton -> dismiss()
        }
    }

    interface FilterClickListener {
        fun onFilterClickListener(item: String?)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mListener = if (mFragment != null) {
            if (mFragment is FilterClickListener) {
                mFragment as FilterClickListener?
            } else {
                throw RuntimeException(
                    context.toString()
                            + " must implement ItemClickListener"
                )
            }
        } else {
            if (context is FilterClickListener) {
                context
            } else {
                throw RuntimeException(
                    context.toString()
                            + " must implement ItemClickListener"
                )
            }
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    fun disableAlreadySelectedDays() {}

    companion object {
        val TAG = QuickFilterBottomSheet::class.java.name
        var mFragment: Fragment? = null
        var mModel: ModelTest? = null
        var mContext: Context? = null
        var mFilterLists: List<FilterList>? = null
        fun newInstance(
            context: Context?,
            fragment: Fragment?,
            model: ModelTest?,
            filterLists: List<FilterList>?
        ): QuickFilterBottomSheet {
            mFragment = fragment
            mContext = context
            mModel = model
            mFilterLists = filterLists
            return QuickFilterBottomSheet()
        }
    }
}