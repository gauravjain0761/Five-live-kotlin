package com.fivelive.app.activity

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.fivelive.app.R
import com.fivelive.app.adapter.PreviewImageAdapter
import com.fivelive.app.util.AppConstant

class ShowImagesActivity constructor() : AppCompatActivity(), View.OnClickListener {
    var view_pager: ViewPager2? = null
    var previewImageAdapter: PreviewImageAdapter? = null
    var previewImgList: List<String>? = null
    var selectedMenu: Int = 0
    var backImageView: ImageView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_images)
        view_pager = findViewById(R.id.view_pager)
        backImageView = findViewById(R.id.backImageView)
        backImageView?.setOnClickListener(this)
        previewImgList = getIntent().getSerializableExtra(AppConstant.IMAGES_LIST) as List<String>?
        selectedMenu = getIntent().getIntExtra(AppConstant.IMAGE_INDEX, 0)
        setImagesInList()
    }

    fun setImagesInList() {
        previewImageAdapter = PreviewImageAdapter(this, previewImgList!!)
        view_pager!!.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL)
        view_pager!!.setAdapter(previewImageAdapter)
        view_pager!!.setCurrentItem(selectedMenu)
    }

    public override fun onBackPressed() {
        finish()
        // overridePendingTransition(R.anim.exit_to_right, R.anim.enter_from_left);
    }

    public override fun onClick(v: View) {
        onBackPressed()
        // goBack();
    }
}