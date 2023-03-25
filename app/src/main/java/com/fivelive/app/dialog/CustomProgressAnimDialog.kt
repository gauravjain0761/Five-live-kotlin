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
import com.airbnb.lottie.LottieAnimationView
import com.fivelive.app.R

class CustomProgressAnimDialog(context: Context?) : Dialog(
    context!!
) {
    init {
        setCancelable(false)
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.custom_progress_anim_dialog)
        window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        window!!.setGravity(Gravity.CENTER_HORIZONTAL)
        val lottieAnimationView = findViewById<View>(R.id.animation_view) as LottieAnimationView
        lottieAnimationView.playAnimation()
        lottieAnimationView.loop(true)
        // lottieAnimationView.setProgress(3f);
        // lottieAnimationView.setRepeatCount(1);
    }
}