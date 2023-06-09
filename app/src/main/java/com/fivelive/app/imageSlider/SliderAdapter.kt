package com.fivelive.app.imageSlider

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.fivelive.app.R
import com.fivelive.app.activity.DetailsActivity

class SliderAdapter(private val context: Context, private var imagesList: List<String>) : PagerAdapter() {
    override fun getCount(): Int {
        return imagesList.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.item_slider, null)
        val imageUrl = imagesList[position]
        val im_slider = view.findViewById<View>(R.id.im_slider) as ImageView
        // im_slider.setImageResource(R.drawable.header_img);
        Glide.with(context)
            .load(imageUrl)
            .placeholder(R.drawable.main_img)
            .into(im_slider)
        im_slider.setOnClickListener { (context as DetailsActivity).showImagePreview(position) }
        val viewPager = container as ViewPager
        viewPager.addView(view, 0)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        val viewPager = container as ViewPager
        val view = `object` as View
        viewPager.removeView(view)
    }
}