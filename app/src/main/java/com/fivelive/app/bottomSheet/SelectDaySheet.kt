package com.fivelive.app.bottomSheet

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.fivelive.app.Model.ModelTest
import com.fivelive.app.R
import com.fivelive.app.util.AppConstant
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class SelectDaySheet : BottomSheetDialogFragment(), View.OnClickListener {
    // public final float OPACITY = 0.8f ;
    val OPACITY = 0.3f
    private var mListener: ItemClickListener? = null
    var monCkb: CheckBox? = null
    var tueCkb: CheckBox? = null
    var wedCkb: CheckBox? = null
    var thuCkb: CheckBox? = null
    var friCkb: CheckBox? = null
    var satCkb: CheckBox? = null
    var sunCkb: CheckBox? = null
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
        return inflater.inflate(R.layout.select_day_bottom_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        monCkb = view.findViewById(R.id.monCkb)
        tueCkb = view.findViewById(R.id.tueCkb)
        monCkb = view.findViewById(R.id.monCkb)
        wedCkb = view.findViewById(R.id.wedCkb)
        thuCkb = view.findViewById(R.id.thuCkb)
        friCkb = view.findViewById(R.id.friCkb)
        satCkb = view.findViewById(R.id.satCkb)
        sunCkb = view.findViewById(R.id.sunCkb)
        doneButton = view.findViewById(R.id.doneButton)
        closeButton = view.findViewById(R.id.closeButton)
        doneButton?.setOnClickListener(this)
        closeButton?.setOnClickListener(this)
        disableAlreadySelectedDays()
    }

    private fun checkSelectedCheckBox() {
        if (monCkb!!.isChecked) {
            mModel!!.days.add(AppConstant.MONDAY)
        }
        if (tueCkb!!.isChecked) {
            mModel!!.days.add(AppConstant.TUESDAY)
        }
        if (wedCkb!!.isChecked) {
            mModel!!.days.add(AppConstant.WEDNESDAY)
        }
        if (thuCkb!!.isChecked) {
            mModel!!.days.add(AppConstant.THURSDAY)
        }
        if (friCkb!!.isChecked) {
            mModel!!.days.add(AppConstant.FRIDAY)
        }
        if (satCkb!!.isChecked) {
            mModel!!.days.add(AppConstant.SATURDAY)
        }
        if (sunCkb!!.isChecked) {
            mModel!!.days.add(AppConstant.SUNDAY)
        }
        /*String dayName = monCkb.getText().toString();
        mModel.daysList.add(dayName);*/mListener!!.onItemClick("")
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.doneButton -> checkSelectedCheckBox()
            R.id.closeButton -> dismiss()
        }
    }

    interface ItemClickListener {
        fun onItemClick(item: String?)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mListener = if (mFragment != null) {
            if (mFragment is ItemClickListener) {
                mFragment as ItemClickListener?
            } else {
                throw RuntimeException(
                    context.toString()
                            + " must implement ItemClickListener"
                )
            }
        } else {
            if (context is ItemClickListener) {
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

    fun disableAlreadySelectedDays() {
        for (modelTest in mList!!) {
            for (day in modelTest.days) {
                when (day) {
                    AppConstant.MONDAY -> {
                        monCkb!!.alpha = OPACITY
                        monCkb!!.isClickable = false
                    }
                    AppConstant.TUESDAY -> {
                        tueCkb!!.alpha = OPACITY
                        tueCkb!!.isClickable = false
                    }
                    AppConstant.WEDNESDAY -> {
                        wedCkb!!.alpha = OPACITY
                        wedCkb!!.isClickable = false
                    }
                    AppConstant.THURSDAY -> {
                        thuCkb!!.alpha = OPACITY
                        thuCkb!!.isClickable = false
                    }
                    AppConstant.FRIDAY -> {
                        friCkb!!.alpha = OPACITY
                        friCkb!!.isClickable = false
                    }
                    AppConstant.SATURDAY -> {
                        satCkb!!.alpha = OPACITY
                        satCkb!!.isClickable = false
                    }
                    AppConstant.SUNDAY -> {
                        sunCkb!!.alpha = OPACITY
                        sunCkb!!.isClickable = false
                    }
                }
            }
        }
    }

    companion object {
        const val TAG = "SelectDaySheet"
        var mFragment: Fragment? = null
        var mModel: ModelTest? = null
        var mContext: Context? = null
        var mList: List<ModelTest>? = null
        @JvmStatic
        fun newInstance(
            context: Context?,
            fragment: Fragment?,
            model: ModelTest?,
            list: List<ModelTest>?
        ): SelectDaySheet {
            mFragment = fragment
            mContext = context
            mModel = model
            mList = list
            return SelectDaySheet()
        }
    }
}