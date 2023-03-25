package com.fivelive.app.activity

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fivelive.app.Model.Notification
import com.fivelive.app.Model.NotificationResponse
import com.fivelive.app.R
import com.fivelive.app.adapter.NotificationAdapter
import com.fivelive.app.prefs.SharedPreferenceWriter
import com.fivelive.app.prefs.SharedPrefsKey
import com.fivelive.app.retrofit.ApiClient
import com.fivelive.app.util.AppConstant
import com.fivelive.app.util.AppUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NotificationActivity : AppCompatActivity(), View.OnClickListener {
    var backImageView: ImageView? = null
    var recyclerView: RecyclerView? = null
    var notificationList: List<Notification>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)
        initView()
        dispatchToNotificationListService()
    }

    private fun initView() {
        backImageView = findViewById(R.id.backImageView)
        backImageView?.setOnClickListener(this)
        recyclerView = findViewById(R.id.recyclerView)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.backImageView -> finish()
        }
    }

    private fun dispatchToNotificationListService() {
        if (AppUtil.isNetworkAvailable(this)) {
            notificationListService()
        } else {
            AppUtil.showConnectionError(this)
        }
    }

    fun notificationListService() {
        AppUtil.showProgressDialog(this@NotificationActivity)
        val apiServiceInterface = ApiClient.instance.client
        val token = SharedPreferenceWriter.getInstance(this@NotificationActivity)
            .getString(SharedPrefsKey.TOKEN)
        val call = apiServiceInterface.notificationApi(token)
        call.enqueue(object : Callback<NotificationResponse?> {
            override fun onResponse(
                call: Call<NotificationResponse?>?,
                response: Response<NotificationResponse?>
            ) {
                AppUtil.dismissProgressDialog()
                try {
                    if (response.isSuccessful) {
                        val notificationResponse = response.body()
                        if (notificationResponse!!.status.equals(
                                AppConstant.SUCCESS,
                                ignoreCase = true
                            )
                        ) {
                            notificationList = notificationResponse.notifcationList
                            setDataOnList()
                        } else {
                            if (notificationResponse.message.contains(AppConstant.SESSION_EXPIRED)) {
                                AppUtil.showLogoutDialog(
                                    this@NotificationActivity,
                                    this@NotificationActivity.resources.getString(R.string.error),
                                    notificationResponse.message
                                )
                            } else {
                                AppUtil.showErrorDialog(
                                    this@NotificationActivity,
                                    this@NotificationActivity.resources.getString(R.string.error),
                                    notificationResponse.message
                                )
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<NotificationResponse?>, t: Throwable) {
                AppUtil.dismissProgressDialog()
                call.cancel()
                AppUtil.showErrorDialog(
                    this@NotificationActivity,
                    this@NotificationActivity.resources.getString(R.string.error),
                    t.message
                )
            }
        })
    }

    fun setDataOnList() {
        val adapter = NotificationAdapter(this@NotificationActivity, notificationList!!)
        val llm = LinearLayoutManager(this@NotificationActivity)
        llm.orientation = LinearLayoutManager.VERTICAL
        recyclerView!!.layoutManager = llm
        recyclerView!!.adapter = adapter
    }
}