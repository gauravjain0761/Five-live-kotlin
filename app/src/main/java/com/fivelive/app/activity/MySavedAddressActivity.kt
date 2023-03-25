package com.fivelive.app.activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fivelive.app.Model.AddressList
import com.fivelive.app.Model.SavedAddressResponse
import com.fivelive.app.R
import com.fivelive.app.activity.AddNewAddressActivity
import com.fivelive.app.adapter.MySavesAddressAdapter
import com.fivelive.app.interfaces.ItemClickListener
import com.fivelive.app.prefs.SharedPreferenceWriter
import com.fivelive.app.prefs.SharedPrefsKey
import com.fivelive.app.retrofit.ApiClient
import com.fivelive.app.util.AppConstant
import com.fivelive.app.util.AppUtil
import com.google.gson.JsonElement
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MySavedAddressActivity : AppCompatActivity(), View.OnClickListener, ItemClickListener {
    var recyclerView: RecyclerView? = null
    var addNewAddressLayout: LinearLayout? = null
    var backImageView: ImageView? = null
    var addressList: MutableList<AddressList>? = null
    var adapter: MySavesAddressAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_saved_address)
        initView()
    }

    private fun initView() {
        backImageView = findViewById(R.id.backImageView)
        backImageView?.setOnClickListener(this)
        addNewAddressLayout = findViewById(R.id.addNewAddressLayout)
        addNewAddressLayout?.setOnClickListener(this)
        recyclerView = findViewById(R.id.recyclerView)
    }

    override fun onResume() {
        super.onResume()
        if (AppUtil.isNetworkAvailable(this)) {
            addressListService()
        } else {
            AppUtil.showConnectionError(this)
        }
    }

    fun setDataOnList() {
        adapter = MySavesAddressAdapter(this, addressList!!, this)
        val llm = LinearLayoutManager(this)
        llm.orientation = LinearLayoutManager.VERTICAL
        recyclerView!!.layoutManager = llm
        recyclerView!!.adapter = adapter
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.backImageView -> finish()
            R.id.addNewAddressLayout -> dispatchToNewAddressActivity()
        }
    }

    private fun dispatchToNewAddressActivity() {
        val intent = Intent(this@MySavedAddressActivity, AddNewAddressActivity::class.java)
        intent.putExtra(AppConstant.EDIT, false)
        startActivity(intent)
    }

    private fun addressListService() {
        AppUtil.showProgressDialog(this@MySavedAddressActivity)
        val token = SharedPreferenceWriter.getInstance(this@MySavedAddressActivity)
            .getString(SharedPrefsKey.TOKEN)
        val apiServiceInterface = ApiClient.instance.client
        val call = apiServiceInterface.addressList(token)
        call.enqueue(object : Callback<SavedAddressResponse?> {
            override fun onResponse(
                call: Call<SavedAddressResponse?>?,
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
                                this@MySavedAddressActivity,
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
                    this@MySavedAddressActivity,
                    resources.getString(R.string.error),
                    t.message
                )
            }
        })
    }

    fun deleteAddressService(addressId: Int) {
        AppUtil.showProgressDialog(this@MySavedAddressActivity)
        // String token = "w7ATbuCZiaN3vR5jzE0iKlzjkxcX5Z";
        val token = SharedPreferenceWriter.getInstance(this@MySavedAddressActivity)
            .getString(SharedPrefsKey.TOKEN)
        val apiServiceInterface = ApiClient.instance.client
        val call = apiServiceInterface.deleteAddress(token, addressId)
        call.enqueue(object : Callback<JsonElement?> {
            override fun onResponse(call: Call<JsonElement?>, response: Response<JsonElement?>) {
                AppUtil.dismissProgressDialog()
                try {
                    if (response.isSuccessful) {
                        val `object` =
                            JSONObject(response.body()!!.asJsonObject.toString().trim { it <= ' ' })
                        if (`object`.getString("status")
                                .equals(AppConstant.SUCCESS, ignoreCase = true)
                        ) {
                            removeItemFromList(addressId)
                        } else {
                            AppUtil.showErrorDialog(
                                this@MySavedAddressActivity,
                                "Error!",
                                `object`.getString("message")
                            )
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<JsonElement?>, t: Throwable) {
                AppUtil.dismissProgressDialog()
                call.cancel()
                AppUtil.showErrorDialog(this@MySavedAddressActivity, "Error!", t.message)
            }


        })
    }

    private fun removeItemFromList(id: Int) {
        for (model in addressList!!) {
            if (model.id == id) {
                addressList!!.remove(model)
                break
            }
        }
        adapter!!.notifyDataSetChanged()
    }

    override fun itemClick() {}
    fun editSavedAddress(model: AddressList?) {
        val intent = Intent(this@MySavedAddressActivity, AddNewAddressActivity::class.java)
        intent.putExtra(AppConstant.ADDRESS_LIST_MODEL, model)
        intent.putExtra(AppConstant.EDIT, true)
        startActivity(intent)
    }
}