package com.fivelive.app.activity

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.fivelive.app.R
import com.fivelive.app.activity.PreviewImageActivity
import com.fivelive.app.customView.TouchImageView
import com.fivelive.app.util.AppConstant

class PreviewImageActivity : AppCompatActivity(), View.OnClickListener {
    var previewImageView: TouchImageView? = null
    var imagePath: String? = null
    var backImageView: ImageView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preview_image)
        // getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.preview_status_color));
        backImageView = findViewById(R.id.backImageView)
        backImageView?.setOnClickListener(this)
        imagePath = intent.getStringExtra(AppConstant.PREVIEW_IMAGE_PATH)
        previewImageView = findViewById(R.id.previewImageView)
        previewImageView?.let {
            Glide.with(this)
                .load(imagePath)
                .placeholder(R.drawable.main_img)
                .into(it)
        }

    }

    /*@Override
    public void onBackPressed() {
        finish();
       // overridePendingTransition(R.anim.exit_to_right, R.anim.enter_from_left);
    }*/
    override fun onClick(v: View) {
        onBackPressed()
    }

    companion object {
        val TAG = PreviewImageActivity::class.java.name
    }
}