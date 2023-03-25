package com.fivelive.app.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.ConnectivityManager
import android.net.Uri
import android.util.DisplayMetrics
import android.util.Log
import android.util.Patterns
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityOptionsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.fivelive.app.R
import com.fivelive.app.activity.LoginActivity
import com.fivelive.app.activity.PreviewImageActivity
import com.fivelive.app.dialog.CustomErrorDialog
import com.fivelive.app.dialog.CustomGuestLoginDialog
import com.fivelive.app.dialog.CustomNetErrorDialog
import com.fivelive.app.dialog.CustomProgressAnimDialog
import com.fivelive.app.prefs.SharedPreferenceWriter
import com.fivelive.app.prefs.SharedPrefsKey
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.messaging.FirebaseMessaging
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.MultipartBody.Part.Companion.createFormData
import okhttp3.RequestBody
import java.io.File
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern

object AppUtil {
    private const val TAG = "AppUtil"

    // private static CustomProgressDialog customProgressDialog;
    private var customProgressDialog: CustomProgressAnimDialog? = null

    @JvmStatic
    fun showProgressDialog(context: Context?) {
        if (customProgressDialog == null) // customProgressDialog = new CustomProgressDialog(context);
            customProgressDialog = CustomProgressAnimDialog(context)
        try {
            customProgressDialog!!.setCancelable(false)
            customProgressDialog!!.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @JvmStatic
    fun dismissProgressDialog() {
        try {
            if (null != customProgressDialog && customProgressDialog!!.isShowing) {
                customProgressDialog!!.dismiss()
                customProgressDialog = null
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun isNetworkAvailable(context: Context): Boolean {
        return try {
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val mobile_info = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
            val wifi_info = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
            if (mobile_info != null) {
                if (mobile_info.isConnectedOrConnecting || wifi_info!!.isConnectedOrConnecting) {
                    true
                } else {
                    false
                }
            } else {
                if (wifi_info!!.isConnectedOrConnecting) {
                    true
                } else {
                    false
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            println("" + e)
            false
        }
    }

    fun showSnackBar(context: Context, view: View?, message: String?) {
        Snackbar.make(view!!, message!!, Snackbar.LENGTH_LONG)
            .setBackgroundTint(context.resources.getColor(R.color.blue_color))
            .setActionTextColor(context.resources.getColor(R.color.white))
            .show()
    }

    @JvmStatic
    fun showErrorDialog(context: Context?, alert: String?, msg: String?) {
        if (alert != null) {
            if (msg != null) {
                CustomErrorDialog(context, alert, msg) { }.show()
            }
        }
    }

    fun showGuestLoginDialog(context: Context) {
        CustomGuestLoginDialog(
            context,
            "Alert!",
            "Please do login to use this functionality."
        ) { logout(context) }.show()
    }

    fun checkUserType(context: Context): Boolean {
        val userType =
            SharedPreferenceWriter.getInstance(context).getString(SharedPrefsKey.USER_TYPE)
        return if (userType == AppConstant.USER_TYPE_GUEST) {
            true // user login as Guest from login screen
        } else {
            false // user login with credential
        }
    }

    fun showLogoutDialog(context: Context, alert: String, msg: String) {
        CustomErrorDialog(context, alert, msg) { logout(context) }.show()
    }

    fun showConnectionError(context: Context?) {
        CustomNetErrorDialog(context) { }.show()
    }

    fun goBack(context: Context) {
        val backImageView = (context as Activity).findViewById<ImageView>(R.id.backImageView)
        backImageView.setOnClickListener { context.finish() }
    }

    fun isValidMobile(phone: String): Boolean {
        return if (!Pattern.matches("[a-zA-Z]+", phone)) {
            phone.length == 10
            /*if (phone.length() >= 6 && phone.length() <= 15) {
                return true;
            }*/
        } else false
    }

    fun isValidPassword(password: String?): Boolean {
        val pattern: Pattern
        val matcher: Matcher
        val PASSWORD_PATTERN =
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*()_])(?=\\S+$).{6,15}$"
        pattern = Pattern.compile(PASSWORD_PATTERN)
        matcher = pattern.matcher(password)
        return matcher.matches()
    }

    fun hideKeyboard(activity: Activity) {
        val view = activity.currentFocus
        if (view != null) {
            val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    fun isValidMail(email: String?): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun logout(context: Context) {
        val intent = Intent(context, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        SharedPreferenceWriter.getInstance(context)
            .writeBooleanValue(SharedPrefsKey.IS_LOGIN, false)
        SharedPreferenceWriter.getInstance(context).clearAllData()
        context.startActivity(intent)
        (context as Activity).finish()
    }

    fun getDeviceToken(context: Context) {
        val thread: Thread = object : Thread() {
            override fun run() {
                Log.e(">>>>>>>>>>>>>>", "thred IS running")
                try {
                    FirebaseMessaging.getInstance().token
                        .addOnCompleteListener(OnCompleteListener { task ->
                            if (!task.isSuccessful) {
                                Log.d(TAG, "Fetching FCM registration token failed", task.exception)
                                return@OnCompleteListener
                            }
                            // Get new FCM registration token
                            val token = task.result
                            if (token == null) {
                                getDeviceToken(context)
                            } else {
                                SharedPreferenceWriter.getInstance(context)
                                    .writeStringValue(SharedPrefsKey.DEVICE_TOKEN, token)
                            }
                        })
                } catch (e1: Exception) {
                    e1.printStackTrace()
                }
                super.run()
            }
        }
        thread.start()
    }

    fun loadImage(context: Context?, imagePath: String?, imageView: ImageView?) {
        Glide.with(context!!)
            .load(imagePath)
            .placeholder(R.drawable.img_a)
            .transform(RoundedCorners(6))
            .into(imageView!!)
    }

    fun loadHorizontalSmallImage(context: Context?, imagePath: String?, imageView: ImageView?) {
        Glide.with(context!!)
            .load(imagePath)
            .placeholder(R.drawable.rest_slider)
            .transform(RoundedCorners(6))
            .into(imageView!!)
    }

    fun loadMenuImage(context: Context?, imagePath: String?, imageView: ImageView?) {
        Glide.with(context!!)
            .load(imagePath)
            .placeholder(R.drawable.menu_img)
            .transform(RoundedCorners(12))
            .into(imageView!!)
    }

    fun loadLargeImage(context: Context?, imagePath: String?, imageView: ImageView?) {
        Glide.with(context!!)
            .load(imagePath)
            .placeholder(R.drawable.menu_img)
            .transform(RoundedCorners(10))
            .into(imageView!!)
    }

    fun setHappyHoursTime(happyHoursTime: TextView, startTime: String, endTime: String) {
        if (startTime == "" || endTime == "") {
            happyHoursTime.visibility = View.GONE
        } else {
            happyHoursTime.text = "Happy Hours Today : $startTime - $endTime"
        }
    }

    fun setHappyHoursDayOrTime(
        happyHoursTime: TextView,
        day: String?,
        startTime: String,
        endTime: String,
        staticFilter: String
    ) {
        if (startTime == "" || endTime == "") {
            happyHoursTime.visibility = View.GONE
        } else if (day != null && day != "") {
            if (staticFilter == AppConstant.BRUNCH) {
                happyHoursTime.text = "Brunch $day : $startTime - $endTime"
            } else if (staticFilter == "Reverse") {
                happyHoursTime.text = "Reverse Happy Hours $day : $startTime - $endTime"
            } else {
                happyHoursTime.text = "Happy Hours $day : $startTime - $endTime"
            }
        } else if (day == "") {
            happyHoursTime.text = "Happy Hours Today : $startTime - $endTime"
        }
    }

    fun setMusicLiveTime(musicLiveTime: TextView, startTime: String, endTime: String) {
        if (startTime == "" || endTime == "") {
            musicLiveTime.visibility = View.GONE
        } else {
            musicLiveTime.text = "Live Music Today : $startTime - $endTime"
        }
    }

    fun setMusicLiveDayOrTime(
        musicLiveTime: TextView,
        day: String?,
        startTime: String,
        endTime: String
    ) {
        if (startTime == "" || endTime == "") {
            musicLiveTime.visibility = View.GONE
        } else if (day != null && day != "") {
            musicLiveTime.text = "Live Music $day : $startTime - $endTime"
        } else if (day == "") {
            musicLiveTime.text = "Live Music Today : $startTime - $endTime"
        }
    }

    fun getCuisineString(list: List<String>?): String? {
        var cuisineString = ""
        if (list != null) {
            for (str in list) {
                cuisineString = "$cuisineString$str|"
            }
            if (cuisineString != null && cuisineString != "") {
                return cuisineString.substring(0, cuisineString.length - 1)
            }
        }
        return cuisineString
    }

    fun compareStartOrEndTime(sTime: String?, eTime: String?): Boolean {
        var timeStatus = false
        val sdf = SimpleDateFormat("hh:mm a")
        try {
            val startTime = sdf.parse(sTime)
            val endTime = sdf.parse(eTime)
            timeStatus = startTime.before(endTime)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return timeStatus
    }

    fun createMultiPartForProfileImage(imagePath: String?): MultipartBody.Part {
        var profileImgMultipart: MultipartBody.Part? = null
        println()
        if (imagePath != null) {
            if (!imagePath.contains("http")) {
                val file = File(imagePath)
                val requestBody = RequestBody.create("*/*".toMediaTypeOrNull(), file)
                profileImgMultipart = createFormData("image", file.name, requestBody)
            }
        }
        return profileImgMultipart!!
    }

    fun getDeviceWidth(context: Context): Int {
        val activity = context as Activity
        val displaymetrics = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(displaymetrics)
        return displaymetrics.widthPixels
    }

    // Display marker on map using game icon
    fun createDrawableFromView(context: Context, view: View): Bitmap {
        val displayMetrics = DisplayMetrics()
        (context as Activity).windowManager.defaultDisplay
            .getMetrics(displayMetrics)
        view.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels)
        view.layout(
            0, 0, displayMetrics.widthPixels,
            displayMetrics.heightPixels
        )
        view.buildDrawingCache()
        val bitmap = Bitmap.createBitmap(
            view.measuredWidth,
            view.measuredHeight, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }

    fun showLargeImage(activity: Activity, imageView: ImageView?, imagePath: String?) {
        val intent = Intent(activity, PreviewImageActivity::class.java)
        intent.putExtra(AppConstant.PREVIEW_IMAGE_PATH, imagePath)
        val options = ActivityOptionsCompat
            .makeSceneTransitionAnimation(activity, (imageView as View?)!!, "previewImageView")
        activity.startActivity(intent, options.toBundle())
    }

    fun convert24HoursFormatTo12HoursFormat(value: Int): String {
        var formatTime = ""
        try {
            val _24HourTime = value.toString()
            val _24HourSDF = SimpleDateFormat("HH")
            val _12HourSDF = SimpleDateFormat("hha")
            val _24HourDt = _24HourSDF.parse(_24HourTime)
            println(_24HourDt)
            //  formatTime = _12HourSDF.format(_24HourDt).replace("AM", "am").replace("PM", "pm");
            formatTime = _12HourSDF.format(_24HourDt)
            if (formatTime.startsWith("0")) {
                formatTime = formatTime.substring(1)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return formatTime
    }

    fun dispatchToAboutUsActivity(context: Context) {
        // startActivity(new Intent(activity, AboutusActivity.class));
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(AllAPIs.BASE_URL + "about"))
        context.startActivity(browserIntent)
    }

    fun dispatchToTermANdConditionActivity(context: Context) {
        // startActivity(new Intent(activity, TermAndConditionActivity.class));
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(AllAPIs.BASE_URL + "terms"))
        context.startActivity(browserIntent)
    }

    fun dispatchToPrivacyPolicy(context: Context) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(AllAPIs.BASE_URL + "policy"))
        context.startActivity(browserIntent)
        // startActivity(new Intent(activity, PrivacyPolicyActivity.class));
    }

    fun dispatchToGoogleMap(context: Context, latitude: String, longitude: String) {
        val uri = String.format(
            Locale.ENGLISH,
            "http://maps.google.com/maps?q=loc:%f,%f",
            latitude.toDouble(),
            longitude.toDouble()
        )
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
        context.startActivity(intent)
    }
}