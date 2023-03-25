package com.fivelive.app.prefs

import android.content.Context
import android.content.SharedPreferences
import com.fivelive.app.Model.Login
import com.fivelive.app.Model.Subscription

class SharedPreferenceWriter {
    fun writeStringValue(key: String?, value: String?) {
        editor!!.putString(key, value)
        editor!!.commit()
    }

    fun writeIntValue(key: String?, value: Int) {
        editor!!.putInt(key, value)
        editor!!.commit()
    }

    fun getIntValue(key: String?): Int {
        return sharedPreferences!!.getInt(key, 0)
    }

    fun getString(key: String?): String? {
        return sharedPreferences!!.getString(key, "")
    }

    fun writeBooleanValue(key: String?, value: Boolean) {
        editor!!.putBoolean(key, value)
        editor!!.commit()
    }

    fun getBooleanValue(key: String?): Boolean {
        return sharedPreferences!!.getBoolean(key, false)
    }

    fun saveUserDetails(register: Login) {
        editor!!.putString(SharedPrefsKey.ID, register.id)
        editor!!.putString(SharedPrefsKey.FIRST_NAME, register.firstName)
        editor!!.putString(SharedPrefsKey.LAST_NAME, register.lastName)
        editor!!.putString(SharedPrefsKey.MOBILE, register.mobile)
        editor!!.putString(SharedPrefsKey.EMAIL, register.email)
        editor!!.putString(SharedPrefsKey.TOKEN, register.token)
    }

    fun saveLoginUserDetails(data: Login) {
        editor!!.putString(SharedPrefsKey.ID, data.id)
        editor!!.putString(SharedPrefsKey.FIRST_NAME, data.firstName)
        editor!!.putString(SharedPrefsKey.LAST_NAME, data.lastName)
        editor!!.putString(SharedPrefsKey.EMAIL, data.email)
        editor!!.putString(SharedPrefsKey.PROFILE_IMAGE, data.image)
        editor!!.putString(SharedPrefsKey.MOBILE, data.mobile)
        editor!!.putString(SharedPrefsKey.TOKEN, data.token)
        editor!!.putString(SharedPrefsKey.NOTIFICATION_STATUS, data.notificationStatus)
        editor!!.putBoolean(SharedPrefsKey.IS_LOGIN, true)
        editor!!.putString(SharedPrefsKey.SIGNUP_TYPE, data.signupType)
        editor!!.putString(SharedPrefsKey.USER_ID, data.id)
        editor!!.putString(SharedPrefsKey.USER_TYPE, data.userType)
        editor!!.commit()
    }

    fun saveSubscriptionDetails(subscription: Subscription) {
        editor!!.putString(SharedPrefsKey.SUBSCRIBED_PURCHASE_TOKEN, subscription.purchaseToken)
        editor!!.putInt(SharedPrefsKey.SUBSCRIBED_STATUS, subscription.status)
        editor!!.commit()
    }

    fun clearAllData() {
        editor!!.clear()
        editor!!.commit()
    }

    companion object {
        private const val PREF_NAME = "com.fivelive.PreferenceFile"
        private var context: Context? = null
        private var prefs: SharedPreferenceWriter? = null
        private var sharedPreferences: SharedPreferences? = null
        private var editor: SharedPreferences.Editor? = null
        @JvmStatic
        fun getInstance(c: Context): SharedPreferenceWriter {
            context = c
            if (null == prefs) {
                prefs = SharedPreferenceWriter()
                sharedPreferences =
                    context!!.getSharedPreferences("PREF_NAME", Context.MODE_PRIVATE)
                editor = sharedPreferences!!.edit()
            }
            return prefs!!
        }
    }
}