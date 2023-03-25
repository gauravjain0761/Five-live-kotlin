package com.fivelive.app.activity

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fivelive.app.Model.AddressList
import com.fivelive.app.Model.SavedAddressResponse
import com.fivelive.app.R
import com.fivelive.app.adapter.SavesAddressAdapter
import com.fivelive.app.prefs.SharedPreferenceWriter
import com.fivelive.app.prefs.SharedPrefsKey
import com.fivelive.app.retrofit.ApiClient
import com.fivelive.app.util.AppConstant
import com.fivelive.app.util.AppUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SavedAddressActivity : AppCompatActivity(), View.OnClickListener {
    var recyclerView: RecyclerView? = null
    var backImageView: ImageView? = null
    var addressList: List<AddressList>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_saved_address)
        initView()
        setDataOnList()
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

    private fun addressListService() {
        AppUtil.showProgressDialog(this@SavedAddressActivity)
        val token = SharedPreferenceWriter.getInstance(this@SavedAddressActivity)
            .getString(SharedPrefsKey.TOKEN)
        val apiServiceInterface = ApiClient.instance.client
        val call = apiServiceInterface.addressList(token)
        call.enqueue(object : Callback<SavedAddressResponse?> {
            override fun onResponse(
                call: Call<SavedAddressResponse?>,
                response: Response<SavedAddressResponse?>
            ) {
                AppUtil.dismissProgressDialog()
                try {
                    if (response.isSuccessful) {
                        val detailsResponse = response.body()
                        if (detailsResponse!!.status.equals(
                                AppConstant.SUCCESS,
                                ignoreCase = true
                            )
                        ) {
                            addressList = detailsResponse.addressList
                            setDataOnList()
                        } else {
                            AppUtil.showErrorDialog(
                                this@SavedAddressActivity,
                                resources.getString(R.string.error),
                                detailsResponse.message
                            )
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<SavedAddressResponse?>, t: Throwable) {
                AppUtil.dismissProgressDialog()
                call.cancel()
                AppUtil.showErrorDialog(
                    this@SavedAddressActivity,
                    resources.getString(R.string.error),
                    t.message
                )
            }
        })
    }

    fun setDataOnList() {
        val list: MutableList<String> = ArrayList()
        for (i in 0..2) {
            list.add("Sanju")
        }
        val adapter = SavesAddressAdapter(this, list)
        val llm = LinearLayoutManager(this)
        llm.orientation = LinearLayoutManager.VERTICAL
        recyclerView!!.layoutManager = llm
        recyclerView!!.adapter = adapter
    }
}