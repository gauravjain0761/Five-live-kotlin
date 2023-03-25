package com.fivelive.app.bottomSheet

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fivelive.app.Model.EditBusinessDetails
import com.fivelive.app.R
import com.fivelive.app.adapter.CuisinesListAdapter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class CategorySheet : BottomSheetDialogFragment(), View.OnClickListener {
    val OPACITY = 0.8f
    private var mListener: ItemClickListener? = null
    var recyclerView: RecyclerView? = null
    var doneButton: TextView? = null
    var closeButton: TextView? = null
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //return super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.category_bottom_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById(R.id.recyclerView)
        doneButton = view.findViewById(R.id.doneButton)
        closeButton = view.findViewById(R.id.closeButton)
        doneButton?.setOnClickListener(this)
        closeButton?.setOnClickListener(this)
        setDataInList()
    }

    fun setDataInList() {
        val adapter = CuisinesListAdapter(requireContext(), mModel!!.cuisines, mModel!!)
        // LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        //  llm.setOrientation(LinearLayoutManager.VERTICAL);
        val manager = GridLayoutManager(
            activity, 2,
            GridLayoutManager.VERTICAL, false
        )
        recyclerView!!.layoutManager = manager
        recyclerView!!.adapter = adapter
    }

    private fun checkSelectedCheckBox() {
        mListener!!.onCategoryItemClick()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.doneButton -> checkSelectedCheckBox()
            R.id.closeButton -> dismiss()
        }
    }

    interface ItemClickListener {
        fun onCategoryItemClick()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mListener = if (context is ItemClickListener) {
            context
        } else {
            throw RuntimeException(
                context.toString()
                        + " must implement ItemClickListener"
            )
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    fun disableAlreadySelectedDays() {}

    companion object {
        const val TAG = "SelectDaySheet"
        var mModel: EditBusinessDetails? = null
        var mContext: Context? = null
        fun newInstance(context: Context?, details: EditBusinessDetails?): CategorySheet {
            mContext = context
            mModel = details
            return CategorySheet()
        }
    }
}