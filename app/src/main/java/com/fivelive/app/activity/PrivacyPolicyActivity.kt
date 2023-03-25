package com.fivelive.app.activity

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.fivelive.app.R

class PrivacyPolicyActivity : AppCompatActivity() {
    var backImageView: ImageView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_privacy_policy)
        backImageView = findViewById(R.id.backImageView)
        backImageView?.setOnClickListener(View.OnClickListener { finish() })
    }
}