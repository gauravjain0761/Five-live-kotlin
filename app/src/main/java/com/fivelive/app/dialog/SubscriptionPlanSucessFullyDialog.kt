package com.fivelive.app.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.TextView
import com.fivelive.app.Model.PlanDetails
import com.fivelive.app.R
import com.fivelive.app.interfaces.DialogButtonListener

class SubscriptionPlanSucessFullyDialog(
    context: Context?,
    planDetails: PlanDetails?,
    var listener: DialogButtonListener?
) : Dialog(
    context!!
) {
//    var message: String
//    var alert: String
    var messageText: TextView? = null
    var alertText: TextView? = null
    var planName_tv: TextView? = null
    var expireDate_tv: TextView? = null
    var okayBtn: Button? = null
    var planDetails: PlanDetails?

    init {
//        message = message
//        alert = alert
        this.planDetails = planDetails
        setCancelable(false)
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.subscription_paln_successfully_dialog)
        window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        window!!.setGravity(Gravity.CENTER_HORIZONTAL)
        okayBtn = findViewById<View>(R.id.okayBtn) as Button
        messageText = findViewById<View>(R.id.messageText) as TextView
        alertText = findViewById<View>(R.id.alertText) as TextView
        planName_tv = findViewById<View>(R.id.planName_tv) as TextView
        expireDate_tv = findViewById<View>(R.id.expireDate_tv) as TextView
        if (planDetails != null) {
            planName_tv!!.text = "Plan Name : " + planDetails!!.packageName
            expireDate_tv!!.text = "Expiring On : " + planDetails!!.endDate
        }
        okayBtn!!.setOnClickListener {
            dismiss()
            if (listener != null) listener!!.onButtonClicked()
        }
    }
}