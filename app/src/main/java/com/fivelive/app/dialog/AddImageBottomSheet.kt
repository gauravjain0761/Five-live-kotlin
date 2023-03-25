package com.fivelive.app.dialog

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fivelive.app.R
import com.fivelive.app.util.AppConstant
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class AddImageBottomSheet : BottomSheetDialogFragment(), View.OnClickListener {
    private var mListener: ItemClickListener? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // return inflater.inflate(R.layout.profile_image_picker_bottom_sheet, container, false);
        return inflater.inflate(R.layout.add_image_bottom_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<View>(R.id.removeImageLinearLayout).setOnClickListener(this)
        view.findViewById<View>(R.id.galleryLinearLayout).setOnClickListener(this)
        view.findViewById<View>(R.id.cameraLinearLayout).setOnClickListener(this)
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

    override fun onClick(view: View) {
        when (view.id) {
            R.id.removeImageLinearLayout -> {
                mListener!!.onItemClick("Remove Photo")
                dismiss()
            }
            R.id.galleryLinearLayout -> {
                mListener!!.onItemClick(AppConstant.GALLERY)
                dismiss()
            }
            R.id.cameraLinearLayout -> {
                mListener!!.onItemClick(AppConstant.CAMERA)
                dismiss()
            }
        }

        // mListener.onItemClick(tvSelected.getText().toString());
        //dismiss();
    }

    interface ItemClickListener {
        fun onItemClick(item: String?)
    }

    companion object {
        const val TAG = "ActionBottomDialog"
        fun newInstance(): AddImageBottomSheet {
            return AddImageBottomSheet()
        }
    }
}