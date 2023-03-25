package com.fivelive.app.util

import android.content.Context
import android.widget.Toast
import com.fivelive.app.Model.EditHHDetailsResponse
import com.fivelive.app.Model.FilterResponse
import com.fivelive.app.Model.MenuListingResponse
import com.fivelive.app.Model.ModelTest
import com.fivelive.app.R
import com.fivelive.app.interfaces.CallBackListener
import com.fivelive.app.interfaces.FavoriteCallback
import com.fivelive.app.interfaces.ObjectCallBackListener
import com.fivelive.app.prefs.SharedPreferenceWriter
import com.fivelive.app.prefs.SharedPrefsKey
import com.fivelive.app.retrofit.ApiClient
import com.google.gson.JsonElement
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.MultipartBody.Part.Companion.createFormData
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class CommonAPI {
    var context: Context
    var businessId: String? = null
    var status: String? = null

    constructor(context: Context) {
        this.context = context
    }

    constructor(context: Context, businessId: String?, status: String?) {
        this.context = context
        this.businessId = businessId
        this.status = status
    }

    constructor(context: Context, businessId: String?) {
        this.context = context
        this.businessId = businessId
    }

    fun addFavoriteService(onSuccess: (Int)-> Unit) {
        val token = SharedPreferenceWriter.getInstance(context)?.getString(SharedPrefsKey.TOKEN)
        //  AppUtil.showProgressDialog(context);
        val apiServiceInterface = ApiClient.instance.client
        val call = apiServiceInterface.addFavorite(token, businessId)
        call.enqueue(object : Callback<JsonElement> {
            override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                //  AppUtil.dismissProgressDialog();
                try {
                    if (response.isSuccessful) {
                        //{"status":"SUCCESS","message":"Recipe added to Favorite.","fav_status":1}
                        val `object` =
                            JSONObject(response.body()!!.asJsonObject.toString().trim { it <= ' ' })
                        if (`object`.getString("status")
                                .equals(AppConstant.SUCCESS, ignoreCase = true)
                        ) {
                            onSuccess(`object`.getInt("fav_status"))
                            if (`object`.getInt("fav_status") == 1) {
                                Toast.makeText(
                                    context,
                                    context.resources.getString(R.string.added_into_fav_msg),
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Toast.makeText(
                                    context,
                                    context.resources.getString(R.string.remove_to_fav_msg),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            if (`object`.getString("message")
                                    .contains(AppConstant.SESSION_EXPIRED)
                            ) {
                                AppUtil.showLogoutDialog(
                                    context,
                                    context.resources.getString(R.string.error),
                                    `object`.getString("message")
                                )
                            } else {
                                AppUtil.showErrorDialog(
                                    context,
                                    context.resources.getString(R.string.error),
                                    `object`.getString("message")
                                )
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                // AppUtil.dismissProgressDialog();
                call.cancel()
                AppUtil.showErrorDialog(
                    context,
                    context.resources.getString(R.string.error),
                    t.message
                )
            }
        })
    }

    fun addItemService(
        businessId: String?, category: String?, name: String?, size: String?, regularPrice: String?,
        offerPrice: String?, percentage: String?, type: String?, imagePath: String?,
        callBackListener: CallBackListener
    ) {
        AppUtil.showProgressDialog(context)
        val file = File(imagePath)
        val requestBody = file.asRequestBody("*/*".toMediaTypeOrNull())
        val imageMultipart: MultipartBody.Part = createFormData("image", file.name, requestBody)
        val token = SharedPreferenceWriter.getInstance(context)?.getString(SharedPrefsKey.TOKEN)
        val sessionToken = token.orEmpty().toRequestBody("text/plain".toMediaTypeOrNull())
        val businessId_body = businessId!!.toRequestBody("text/plain".toMediaTypeOrNull())
        val category_body = category!!.toRequestBody("text/plain".toMediaTypeOrNull())
        val name_body = name!!.toRequestBody("text/plain".toMediaTypeOrNull())
        val size_body = size!!.toRequestBody("text/plain".toMediaTypeOrNull())
        val regularPrice_body = regularPrice!!.toRequestBody("text/plain".toMediaTypeOrNull())
        val offerPrice_body = offerPrice!!.toRequestBody("text/plain".toMediaTypeOrNull())
        val percentage_body = percentage!!.toRequestBody("text/plain".toMediaTypeOrNull())
        val type_body = type!!.toRequestBody("text/plain".toMediaTypeOrNull())
        val apiServiceInterface = ApiClient.instance.client
        val call = apiServiceInterface.addItems(
            sessionToken,
            businessId_body,
            category_body,
            name_body,
            size_body,
            regularPrice_body,
            offerPrice_body,
            percentage_body,
            type_body,
            imageMultipart
        )
        call.enqueue(object : Callback<JsonElement> {
            override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                AppUtil.dismissProgressDialog()
                try {
                    if (response.isSuccessful) {
                        val `object` =
                            JSONObject(response.body()!!.asJsonObject.toString().trim { it <= ' ' })
                        if (`object`.getString("status")
                                .equals(AppConstant.SUCCESS, ignoreCase = true)
                        ) {
                            // Toast.makeText(context, ""+object.getString("message"), Toast.LENGTH_SHORT).show();
                            callBackListener.onSuccess(`object`.getString("message"))
                        } else {
                            if (`object`.getString("message")
                                    .contains(AppConstant.SESSION_EXPIRED)
                            ) {
                                AppUtil.showLogoutDialog(
                                    context,
                                    context.resources.getString(R.string.error),
                                    `object`.getString("message")
                                )
                            } else {
                                AppUtil.showErrorDialog(
                                    context,
                                    context.resources.getString(R.string.error),
                                    `object`.getString("message")
                                )
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                AppUtil.dismissProgressDialog()
                call.cancel()
                AppUtil.showErrorDialog(
                    context,
                    context.resources.getString(R.string.error),
                    t.message
                )
            }
        })
    }

    fun deleteItemService(itemID: String?, listener: CallBackListener) {
        AppUtil.showProgressDialog(context)
        val apiServiceInterface = ApiClient.instance.client
        val call = apiServiceInterface.itemDelete(itemID)
        call.enqueue(object : Callback<JsonElement> {
            override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                AppUtil.dismissProgressDialog()
                try {
                    if (response.isSuccessful) {
                        val `object` =
                            JSONObject(response.body()!!.asJsonObject.toString().trim { it <= ' ' })
                        if (`object`.getString("status")
                                .equals(AppConstant.SUCCESS, ignoreCase = true)
                        ) {
                            listener.onSuccess(`object`.getString("message"))
                        } else {
                            if (`object`.getString("message")
                                    .contains(AppConstant.SESSION_EXPIRED)
                            ) {
                                AppUtil.showLogoutDialog(
                                    context,
                                    context.resources.getString(R.string.error),
                                    `object`.getString("message")
                                )
                            } else {
                                AppUtil.showErrorDialog(
                                    context,
                                    context.resources.getString(R.string.error),
                                    `object`.getString("message")
                                )
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                AppUtil.dismissProgressDialog()
                call.cancel()
                AppUtil.showErrorDialog(
                    context,
                    context.resources.getString(R.string.error),
                    t.message
                )
            }
        })
    }

    fun menuItemListingService(
        businessId: String?,
        type: String?,
        onSuccess: (MenuListingResponse?)-> Unit
    ) {
        AppUtil.showProgressDialog(context)
        val token = SharedPreferenceWriter.getInstance(context)?.getString(SharedPrefsKey.TOKEN)
        val apiServiceInterface = ApiClient.instance.client
        val call = apiServiceInterface.menuListingByType(token, businessId, type)
        call.enqueue(object : Callback<MenuListingResponse?> {
            override fun onResponse(
                call: Call<MenuListingResponse?>,
                response: Response<MenuListingResponse?>
            ) {
                AppUtil.dismissProgressDialog()
                try {
                    if (response.isSuccessful) {
                        val listingResponse = response.body()
                        if (listingResponse!!.status.equals(
                                AppConstant.SUCCESS,
                                ignoreCase = true
                            )
                        ) {
                            onSuccess(listingResponse)
                        } else {
                            if (listingResponse.message.contains(AppConstant.SESSION_EXPIRED)) {
                                AppUtil.showLogoutDialog(
                                    context,
                                    context.resources.getString(R.string.error),
                                    listingResponse.message
                                )
                            } else {
                                AppUtil.showErrorDialog(
                                    context,
                                    context.resources.getString(R.string.error),
                                    listingResponse.message
                                )
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<MenuListingResponse?>, t: Throwable) {
                AppUtil.dismissProgressDialog()
                call.cancel()
                AppUtil.showErrorDialog(
                    context,
                    context.resources.getString(R.string.error),
                    t.message
                )
            }
        })
    }

    fun deleteMenuItemService(itemId: String?, onSuccess: (String) -> Unit) {
        AppUtil.showProgressDialog(context)
        val apiServiceInterface = ApiClient.instance.client
        val call = apiServiceInterface.deleteMenuItem(itemId)
        call.enqueue(object : Callback<JsonElement> {
            override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                AppUtil.dismissProgressDialog()
                try {
                    if (response.isSuccessful) {
                        val `object` =
                            JSONObject(response.body()!!.asJsonObject.toString().trim { it <= ' ' })
                        if (`object`.getString("status")
                                .equals(AppConstant.SUCCESS, ignoreCase = true)
                        ) {
                            onSuccess(`object`.getString("message"))
                        } else {
                            if (`object`.getString("message")
                                    .contains(AppConstant.SESSION_EXPIRED)
                            ) {
                                AppUtil.showLogoutDialog(
                                    context,
                                    context.resources.getString(R.string.error),
                                    `object`.getString("message")
                                )
                            } else {
                                AppUtil.showErrorDialog(
                                    context,
                                    context.resources.getString(R.string.error),
                                    `object`.getString("message")
                                )
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                AppUtil.dismissProgressDialog()
                call.cancel()
                AppUtil.showErrorDialog(
                    context,
                    context.resources.getString(R.string.error),
                    t.message
                )
            }
        })
    }

    fun addMenuScheduleService(model: ModelTest, businessId: String?, onSuccess: (String) -> Unit) {
        AppUtil.showProgressDialog(context)
        val apiServiceInterface = ApiClient.instance.client
        val token = SharedPreferenceWriter.getInstance(context)?.getString(SharedPrefsKey.TOKEN)
        val call = apiServiceInterface.addMenuSchedule(
            token,
            businessId,
            model.days,
            model.hhFilterList,
            model.startTime,
            model.endTime,
            model.drink,
            model.food,
            model.description,
            model.type
        )
        call.enqueue(object : Callback<JsonElement> {
            override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                AppUtil.dismissProgressDialog()
                try {
                    if (response.isSuccessful) {
                        val `object` =
                            JSONObject(response.body()!!.asJsonObject.toString().trim { it <= ' ' })
                        if (`object`.getString("status")
                                .equals(AppConstant.SUCCESS, ignoreCase = true)
                        ) {
                            onSuccess(`object`.getString("message"))
                        } else {
                            if (`object`.getString("message")
                                    .contains(AppConstant.SESSION_EXPIRED)
                            ) {
                                AppUtil.showLogoutDialog(
                                    context,
                                    context.resources.getString(R.string.error),
                                    `object`.getString("message")
                                )
                            } else {
                                AppUtil.showErrorDialog(
                                    context,
                                    context.resources.getString(R.string.error),
                                    `object`.getString("message")
                                )
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                AppUtil.dismissProgressDialog()
                call.cancel()
                AppUtil.showErrorDialog(
                    context,
                    context.resources.getString(R.string.error),
                    t.message
                )
            }
        })
    }

    fun detailsScheduleListingService(
        businessId: String?,
        type: String?,
        onSuccess: (EditHHDetailsResponse) -> Unit
    ) {
        AppUtil.showProgressDialog(context)
        val token = SharedPreferenceWriter.getInstance(context)?.getString(SharedPrefsKey.TOKEN)
        val apiServiceInterface = ApiClient.instance.client
        val call = apiServiceInterface.scheduleListing(token, businessId, type)
        call.enqueue(object : Callback<EditHHDetailsResponse?> {
            override fun onResponse(
                call: Call<EditHHDetailsResponse?>,
                response: Response<EditHHDetailsResponse?>
            ) {
                AppUtil.dismissProgressDialog()
                try {
                    if (response.isSuccessful) {
                        val listingResponse = response.body()
                        if (listingResponse!!.status.equals(
                                AppConstant.SUCCESS,
                                ignoreCase = true
                            )
                        ) {
                            onSuccess(listingResponse)
                        } else {
                            if (listingResponse.message.contains(AppConstant.SESSION_EXPIRED)) {
                                AppUtil.showLogoutDialog(
                                    context,
                                    context.resources.getString(R.string.error),
                                    listingResponse.message
                                )
                            } else {
                                AppUtil.showErrorDialog(
                                    context,
                                    context.resources.getString(R.string.error),
                                    listingResponse.message
                                )
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<EditHHDetailsResponse?>, t: Throwable) {
                AppUtil.dismissProgressDialog()
                call.cancel()
                AppUtil.showErrorDialog(
                    context,
                    context.resources.getString(R.string.error),
                    t.message
                )
            }
        })
    }

    fun deleteMenuScheduleService(id: String?, type: String?, onSuccess: (String) -> Unit) {
        AppUtil.showProgressDialog(context)
        val apiServiceInterface = ApiClient.instance.client
        val token = SharedPreferenceWriter.getInstance(context)?.getString(SharedPrefsKey.TOKEN)
        val call = apiServiceInterface.deleteMenuSchedule(token, id, type)
        call.enqueue(object : Callback<JsonElement> {
            override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                AppUtil.dismissProgressDialog()
                try {
                    if (response.isSuccessful) {
                        val `object` =
                            JSONObject(response.body()!!.asJsonObject.toString().trim { it <= ' ' })
                        if (`object`.getString("status")
                                .equals(AppConstant.SUCCESS, ignoreCase = true)
                        ) {
                            onSuccess(`object`.getString("message"))
                        } else {
                            if (`object`.getString("message")
                                    .contains(AppConstant.SESSION_EXPIRED)
                            ) {
                                AppUtil.showLogoutDialog(
                                    context,
                                    context.resources.getString(R.string.error),
                                    `object`.getString("message")
                                )
                            } else {
                                AppUtil.showErrorDialog(
                                    context,
                                    context.resources.getString(R.string.error),
                                    `object`.getString("message")
                                )
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                AppUtil.dismissProgressDialog()
                call.cancel()
                AppUtil.showErrorDialog(
                    context,
                    context.resources.getString(R.string.error),
                    t.message
                )
            }
        })
    }

    fun updateScheduleDetailsService(model: ModelTest, type: String?, onSuccess: (String)-> Unit) {
        AppUtil.showProgressDialog(context)
        val apiServiceInterface = ApiClient.instance.client
        val token = SharedPreferenceWriter.getInstance(context)?.getString(SharedPrefsKey.TOKEN)
        val call = apiServiceInterface.updateScheduleDetails(
            token,
            model.id,
            type,
            model.days,
            model.hhFilterList,
            model.startTime,
            model.endTime,
            model.drink,
            model.food,
            model.description
        )
        call.enqueue(object : Callback<JsonElement> {
            override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                AppUtil.dismissProgressDialog()
                try {
                    if (response.isSuccessful) {
                        val `object` =
                            JSONObject(response.body()!!.asJsonObject.toString().trim { it <= ' ' })
                        if (`object`.getString("status")
                                .equals(AppConstant.SUCCESS, ignoreCase = true)
                        ) {
                            onSuccess(`object`.getString("message"))
                        } else {
                            if (`object`.getString("message")
                                    .contains(AppConstant.SESSION_EXPIRED)
                            ) {
                                AppUtil.showLogoutDialog(
                                    context,
                                    context.resources.getString(R.string.error),
                                    `object`.getString("message")
                                )
                            } else {
                                AppUtil.showErrorDialog(
                                    context,
                                    context.resources.getString(R.string.error),
                                    `object`.getString("message")
                                )
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                AppUtil.dismissProgressDialog()
                call.cancel()
                AppUtil.showErrorDialog(
                    context,
                    context.resources.getString(R.string.error),
                    t.message
                )
            }
        })
    }

    fun deleteEditBusinessImageService(itemId: String?, onSuccess: (String) -> Unit) {
        AppUtil.showProgressDialog(context)
        val apiServiceInterface = ApiClient.instance.client
        val call = apiServiceInterface.deleteImages(itemId)
        call.enqueue(object : Callback<JsonElement> {
            override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                AppUtil.dismissProgressDialog()
                try {
                    if (response.isSuccessful) {
                        val `object` =
                            JSONObject(response.body()!!.asJsonObject.toString().trim { it <= ' ' })
                        if (`object`.getString("status")
                                .equals(AppConstant.SUCCESS, ignoreCase = true)
                        ) {
                            onSuccess(`object`.getString("message"))
                        } else {
                            if (`object`.getString("message")
                                    .contains(AppConstant.SESSION_EXPIRED)
                            ) {
                                AppUtil.showLogoutDialog(
                                    context,
                                    context.resources.getString(R.string.error),
                                    `object`.getString("message")
                                )
                            } else {
                                AppUtil.showErrorDialog(
                                    context,
                                    context.resources.getString(R.string.error),
                                    `object`.getString("message")
                                )
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                AppUtil.dismissProgressDialog()
                call.cancel()
                AppUtil.showErrorDialog(
                    context,
                    context.resources.getString(R.string.error),
                    t.message
                )
            }
        })
    }

    fun quickFilterListService(onSuccess: (FilterResponse) -> Unit) {
        AppUtil.showProgressDialog(context)
        val apiServiceInterface = ApiClient.instance.client
        val call = apiServiceInterface.quickFilterList()
        call.enqueue(object : Callback<FilterResponse?> {
            override fun onResponse(
                call: Call<FilterResponse?>,
                response: Response<FilterResponse?>
            ) {
                //  AppUtil.dismissProgressDialog();
                try {
                    if (response.isSuccessful) {
                        val listingResponse = response.body()
                        if (listingResponse!!.status.equals(
                                AppConstant.SUCCESS,
                                ignoreCase = true
                            )
                        ) {
                            onSuccess(listingResponse)
                        } else {
                            if (listingResponse.message.contains(AppConstant.SESSION_EXPIRED)) {
                                AppUtil.showLogoutDialog(
                                    context,
                                    context.resources.getString(R.string.error),
                                    listingResponse.message
                                )
                            } else {
                                AppUtil.showErrorDialog(
                                    context,
                                    context.resources.getString(R.string.error),
                                    listingResponse.message
                                )
                            }
                        }
                    }
                } catch (e: Exception) {
                    AppUtil.dismissProgressDialog()
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<FilterResponse?>, t: Throwable) {
                AppUtil.dismissProgressDialog()
                call.cancel()
                AppUtil.showErrorDialog(
                    context,
                    context.resources.getString(R.string.error),
                    t.message
                )
            }
        })
    }
}