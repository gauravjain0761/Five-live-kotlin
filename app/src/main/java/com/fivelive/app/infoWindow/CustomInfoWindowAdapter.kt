package com.fivelive.app.infoWindow

import android.content.Context
import android.graphics.Color
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.TextView
import com.fivelive.app.Model.HomeBusiness
import com.fivelive.app.R
import com.fivelive.app.util.AppUtil
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter
import com.google.android.gms.maps.model.Marker

class CustomInfoWindowAdapter(var context: Context, var business: HomeBusiness) :
    InfoWindowAdapter {
    private val mWindow: View =
        LayoutInflater.from(context).inflate(R.layout.custom_info_window, null)
    private val mContents: View =
        LayoutInflater.from(context).inflate(R.layout.custom_info_contents, null)

    init {
        mContents.layoutParams = LinearLayout.LayoutParams(
            AppUtil.getDeviceWidth(context) - 200,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        mWindow.layoutParams = LinearLayout.LayoutParams(
            AppUtil.getDeviceWidth(context) - 200,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    override fun getInfoWindow(marker: Marker): View? {
        /*render(marker, mWindow);
        return mWindow;*/
        return null
    }

    override fun getInfoContents(marker: Marker): View {
        render(marker, mContents)
        return mContents
    }

    private fun render(marker: Marker, view: View) {
        val badge = R.drawable.img_a
        // Use the equals() method on a Marker to check for equals.  Do not use ==.
        val title = marker.title
        val titleUi = view.findViewById<View>(R.id.title) as TextView
        if (title != null) {
            // Spannable string allows us to edit the formatting of the text.
            val titleText = SpannableString(title)
            titleText.setSpan(ForegroundColorSpan(Color.BLACK), 0, titleText.length, 0)
            titleUi.text = titleText
        } else {
            titleUi.text = ""
        }

        /*Rating here implemented */
        val imageView = view.findViewById<View>(R.id.badge) as ImageView
        val snippetUi = view.findViewById<View>(R.id.snippet) as TextView
        val google_rating_bar = view.findViewById<View>(R.id.google_rating_bar) as RatingBar
        Log.d("Marker Data", "" + marker.snippet)
        val snippt: Array<String?> =
            marker.snippet!!.split("#".toRegex()).dropLastWhile { it.isEmpty() }
                .toTypedArray() //split snippt string using #
        AppUtil.loadHorizontalSmallImage(context, snippt[0], imageView) //load image
        snippetUi.text = snippt[1]
        if (snippt[2] != null && snippt[2] != "") {
            google_rating_bar.rating = snippt[2]!!.toFloat()
        }
    }
}