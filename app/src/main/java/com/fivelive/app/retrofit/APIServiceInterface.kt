package com.fivelive.app.retrofit

import com.fivelive.app.Model.*
import com.google.gson.JsonElement
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface APIServiceInterface {
    @FormUrlEncoded
    @POST("checkEmail")
    fun checkEmail(
        @Field("mobile") mobile: String?,
        @Field("email") email: String?
    ): Call<JsonElement>

    @FormUrlEncoded
    @POST("register")
    fun userSignUp(
        @Field("first_name") first_name: String?,
        @Field("last_name") last_name: String?,
        @Field("email") email: String?,
        @Field("mobile") mobile: String?,
        @Field("gender") gender: String?,
        @Field("dob") dob: String?,
        @Field("device_type") device_type: String?,
        @Field("device_token") device_token: String?,
        @Field("password") password: String?,
        @Field("latitude") latitude: Double,
        @Field("longitude") longitude: Double
    ): Call<SignUpResponse?>

    @FormUrlEncoded
    @POST("login")
    fun userLogin(
        @Field("type") type: String?,
        @Field("password") password: String?,
        @Field("phone_no") phone_no: String?,
        @Field("email") email: String?,
        @Field("device_type") device_type: String?,
        @Field("device_token") device_token: String?,
        @Field("latitude") latitude: Double,
        @Field("longitude") longitude: Double
    ): Call<LoginResponse?>

    @FormUrlEncoded
    @POST("forgotpassword")
    fun forgotPassword(
        @Field("type") type: String?,
        @Field("mobile") mobile: String?,
        @Field("email") email: String?
    ): Call<JsonElement>

    @FormUrlEncoded
    @POST("updatepassword")
    fun updatePassword(
        @Field("mobile") mobile: String?,
        @Field("email") email: String?,
        @Field("new_pass") new_pass: String?,
        @Field("confirm_pass") confirm_pass: String?,
        @Field("type") type: String?
    ): Call<JsonElement>

    @FormUrlEncoded
    @POST("changepassword")
    fun changePassword(
        @Field("token") token: String?,
        @Field("old_pass") old_pass: String?,
        @Field("new_pass") new_pass: String?,
        @Field("confirm_pass") confirm_pass: String?
    ): Call<JsonElement>

    @FormUrlEncoded
    @POST("updateNotificationstatus")
    fun updateNotificationStatus(
        @Field("token") token: String?,
        @Field("status") status: String?
    ): Call<JsonElement>

    @FormUrlEncoded
    @POST("socialMediaLogin")
    fun socialMediaLogin(
        @Field("signup_by") signup_by: String?,
        @Field("first_name") first_name: String?,
        @Field("last_name") last_name: String?,
        @Field("image") image: String?,
        @Field("email") email: String?,
        @Field("mobile") mobile: String?,
        @Field("device_type") device_type: String?,
        @Field("device_token") device_token: String?,
        @Field("google_id") google_id: String?,
        @Field("latitude ") latitude: Double,
        @Field("longitude ") longitude: Double
    ): Call<LoginResponse>

    @FormUrlEncoded
    @POST("socialMediaLogin")
    fun facebookLogin(
        @Field("signup_by") signup_by: String?,
        @Field("first_name") first_name: String?,
        @Field("last_name") last_name: String?,
        @Field("email") email: String?,
        @Field("image") image: String?,
        @Field("device_type") device_type: String?,
        @Field("device_token") device_token: String?,
        @Field("facebook_id") facebook_id: String?,
        @Field("latitude ") latitude: Double,
        @Field("longitude ") longitude: Double
    ): Call<LoginResponse>

    @FormUrlEncoded
    @POST("save_address")
    fun saveAddress(
        @Field("token") token: String?,
        @Field("title") title: String?,
        @Field("address") address: String?,
        @Field("latitude") latitude: String?,
        @Field("longitude") longitude: String?
    ): Call<JsonElement>

    @FormUrlEncoded
    @POST("update_address")
    fun updateAddress(
        @Field("token") token: String?,
        @Field("title") title: String?,
        @Field("address") address: String?,
        @Field("address_id") address_id: Int,
        @Field("latitude") latitude: String?,
        @Field("longitude") longitude: String?
    ): Call<JsonElement>

    @FormUrlEncoded
    @POST("address_list")
    fun addressList(@Field("token") token: String?): Call<SavedAddressResponse?>

    @FormUrlEncoded
    @POST("address_delete")
    fun deleteAddress(
        @Field("token") token: String?,
        @Field("address_id") address_id: Int
    ): Call<JsonElement>

    @FormUrlEncoded
    @POST("home_businessv2")
    fun homeBusiness(
        @Field("token") token: String?,
        @Field("day") day: String?,
        @Field("static_filter") static_filter: String?,
        @Field("quick_filter") quick_filter: String?,
        @Field("latitude") latitude: String?,
        @Field("longitude") longitude: String?,
        @Field("page") page: Int
    ): Call<HomeBusinessResponse?>

    @FormUrlEncoded
    @POST("business_details")
    fun businessDetails(
        @Field("token") token: String?,
        @Field("business_id") business_id: String?
    ): Call<BusinessDetailsResponse?>

    @FormUrlEncoded
    @POST("add_favorite")
    fun addFavorite(
        @Field("token") token: String?,
        @Field("business_id") business_id: String?
    ): Call<JsonElement>

    @FormUrlEncoded
    @POST("my_favorite")
    fun myFavoriteBusiness(
        @Field("token") token: String?,
        @Field("latitude") latitude: String?,
        @Field("longitude") longitude: String?
    ): Call<HomeBusinessResponse?>

    @FormUrlEncoded
    @POST("happening_now")
    fun happeningNow(
        @Field("token") token: String?,
        @Field("latitude") latitude: String?,
        @Field("longitude") longitude: String?
    ): Call<HomeBusinessResponse?>

    @Multipart
    @POST("add_rating")
    fun addRating(
        @Part("token") token: RequestBody?,
        @Part("rating") rating: RequestBody?,
        @Part("review") review: RequestBody?,
        @Part("business_id") business_id: RequestBody?,
        @Part review_image: Array<MultipartBody.Part?>?
    ): Call<JsonElement>

    @FormUrlEncoded
    @POST("hh_menue_details")
    fun hhMenuDetails(
        @Field("token") token: String?,
        @Field("business_id") business_id: String?
    ): Call<CommonBusinessResponse?>

    @FormUrlEncoded
    @POST("reverse_menue_details")
    fun reverseHHMenuDetails(
        @Field("token") token: String?,
        @Field("business_id") business_id: String?
    ): Call<CommonBusinessResponse?>

    @FormUrlEncoded
    @POST("brunch_menue_details")
    fun brunchMenuDetails(
        @Field("token") token: String?,
        @Field("business_id") business_id: String?
    ): Call<CommonBusinessResponse?>

    @FormUrlEncoded
    @POST("basic_menue_details")
    fun regularMenuDetails(
        @Field("token") token: String?,
        @Field("business_id") business_id: String?
    ): Call<CommonBusinessResponse?>

    @FormUrlEncoded
    @POST("business_info")
    fun businessInfo(
        @Field("token") token: String?,
        @Field("business_id") business_id: String?
    ): Call<AmenitiesResponse?>

    @GET("profileDetail/{id}")
    fun getProfileDetailsOld(
        @Path("id") id: String?,
        @Path("latitude") latitude: String?,
        @Path("longitude") longitude: String?
    ): Call<ProfileResponse?>

    @FormUrlEncoded
    @POST("profileDetail")
    fun getProfileDetails(
        @Field("id") id: String?,
        @Field("latitude") latitude: String?,
        @Field("longitude") longitude: String?
    ): Call<ProfileResponse?>

    @Multipart
    @POST("updateProfile")
    fun updateProfile(
        @Part("token") token: RequestBody?,
        @Part("first_name") first_name: RequestBody?,
        @Part("last_name") last_name: RequestBody?,
        @Part("mobile") bio: RequestBody?,
        @Part("email") email: RequestBody?,
        @Part("dob") dob: RequestBody?,
        @Part("gender") gender: RequestBody?,
        @Part file: MultipartBody.Part?
    ): Call<JsonElement>

    @Multipart
    @POST("add_items")
    fun addItems(
        @Part("token") token: RequestBody?,
        @Part("business_id") business_id: RequestBody?,
        @Part("category") category: RequestBody?,
        @Part("name") name: RequestBody?,
        @Part("size") size: RequestBody?,
        @Part("regular_price") regular_price: RequestBody?,
        @Part("offer_price") offer_price: RequestBody?,
        @Part("percentage") percentage: RequestBody?,
        @Part("type") type: RequestBody?,
        @Part file: MultipartBody.Part?
    ): Call<JsonElement>

    @Multipart
    @POST("update_items")
    fun updateItems(
        @Part("item_id") item_id: RequestBody?,
        @Part("token") token: RequestBody?,
        @Part("category") category: RequestBody?,
        @Part("name") name: RequestBody?,
        @Part("size") size: RequestBody?,
        @Part("regular_price") regular_price: RequestBody?,
        @Part("offer_price") offer_price: RequestBody?,
        @Part("percentage") percentage: RequestBody?,
        @Part file: MultipartBody.Part?
    ): Call<JsonElement>

    @FormUrlEncoded
    @POST("items_list")
    fun itemsList(
        @Field("type") type: String?,
        @Field("business_id") business_id: String?,
        @Field("token") token: String?
    ): Call<EditHHItemsResponse?>

    @GET("item_details/{id}")
    fun getItemDetails(@Path("id") id: String?): Call<ItemDetailsResponse?>

    @DELETE("delete_item/{id}")
    fun itemDelete(@Path("id") id: String?): Call<JsonElement>

    @Multipart
    @POST("add_menues")
    fun addMenu(
        @Part("token") token: RequestBody?,
        @Part("business_id") business_id: RequestBody?,
        @Part("type") type: RequestBody?,
        @Part("title") title: RequestBody?,
        @Part file: MultipartBody.Part?
    ): Call<JsonElement>

    @FormUrlEncoded
    @POST("menue_list")
    fun menuListingByType(
        @Field("token") token: String?,
        @Field("business_id") business_id: String?,
        @Field("type") type: String?
    ): Call<MenuListingResponse?>

    @DELETE("delete_menue/{id}")
    fun deleteMenuItem(@Path("id") id: String?): Call<JsonElement>

    @FormUrlEncoded
    @POST("claim_business_detail")
    fun claimBusinessDetail(
        @Field("token") token: String?,
        @Field("business_id") business_id: String?,
        @Field("latitude") latitude: String?,
        @Field("longitude") longitude: String?
    ): Call<ClaimBusinessResponse?>

    @FormUrlEncoded
    @POST("business_verify")
    fun businessVerify(
        @Field("token") token: String?,
        @Field("business_id") business_id: String?,
        @Field("otp") otp: String?
    ): Call<JsonElement>

    @GET("resend_otp/{businessId}")
    fun resendOTP(@Path("businessId") businessId: String?): Call<JsonElement>

    @FormUrlEncoded
    @POST("add_menue_schedule")
    fun addMenuSchedule(
        @Field("token") token: String?,
        @Field("business_id") business_id: String?,
        @Field("days[]") dayList: List<String?>?,
        @Field("quick_filter_arr[]") qfList: List<String?>?,
        @Field("start_time") start_time: String?,
        @Field("end_time") end_time: String?,
        @Field("drink") drink: String?,
        @Field("food") food: String?,
        @Field("description") description: String?,
        @Field("type") type: String?
    ): Call<JsonElement>

    @FormUrlEncoded
    @POST("update_schedule_details")
    fun updateScheduleDetails(
        @Field("token") token: String?,
        @Field("id") id: String?,
        @Field("type") type: String?,
        @Field("days[]") dayList: List<String?>?,
        @Field("quick_filter_arr[]") qfList: List<String?>?,
        @Field("start_time") start_time: String?,
        @Field("end_time") end_time: String?,
        @Field("drink") drink: String?,
        @Field("food") food: String?,
        @Field("description") description: String?
    ): Call<JsonElement>

    @FormUrlEncoded
    @POST("schedule_listing")
    fun scheduleListing(
        @Field("token") token: String?,
        @Field("business_id") business_id: String?,
        @Field("type") type: String?
    ): Call<EditHHDetailsResponse?>

    @FormUrlEncoded
    @POST("delete_menue_schedule")
    fun deleteMenuSchedule(
        @Field("token") token: String?,
        @Field("id") id: String?,
        @Field("type") type: String?
    ): Call<JsonElement>

    @FormUrlEncoded
    @POST("edit_business_details")
    fun editBusinessDetails(
        @Field("token") token: String?,
        @Field("business_id") business_id: String?
    ): Call<EditBusinessResponse?>

    @DELETE("delete_images/{imageId}")
    fun deleteImages(@Path("imageId") imageId: String?): Call<JsonElement>

    @Multipart
    @POST("update_business")
    fun updateBusiness(
        @Part("token") token: RequestBody?,
        @Part("name") name: RequestBody?,
        @Part("business_id") business_id: RequestBody?,
        @Part("category[]") category: Array<RequestBody?>?,
        @Part("address") address: RequestBody?,
        @Part("state") state: RequestBody?,
        @Part("city") city: RequestBody?,
        @Part("zipcode") zipcode: RequestBody?,
        @Part("country") country: RequestBody?,
        @Part("website") website: RequestBody?,
        @Part("phone") phone: RequestBody?,
        @Part("email") email: RequestBody?,
        @Part("latitude") latitude: RequestBody?,
        @Part("longitude") longitude: RequestBody?,
        @Part file: Array<MultipartBody.Part?>?
    ): Call<JsonElement>

    @get:GET("faqs")
    val faqList: Call<FaqResponse?>

    @FormUrlEncoded
    @POST("notification")
    fun notificationApi(@Field("token") token: String?): Call<NotificationResponse?>

    @FormUrlEncoded
    @POST("EditCheckEmail")
    fun editCheckEmail(
        @Field("token") token: String?,
        @Field("mobile") mobile: String?,
        @Field("email") email: String?
    ): Call<JsonElement>

    @FormUrlEncoded
    @POST("auto_suggest_business")
    fun autoSuggestBusiness(@Field("keywords") keywords: String?): Call<CommonBusinessResponse?>

    @GET("quick_filter_list")
    fun quickFilterList(): Call<FilterResponse?>

    @GET("guest_login")
    fun guestLogin(): Call<GuestLogin?>

    @Multipart
    @POST("send_enquiry")
    fun sendEnquiry(
        @Part("token") token: RequestBody?,
        @Part("description") description: RequestBody?,
        @Part("email_id") email_id: RequestBody?,
        @Part files: Array<MultipartBody.Part?>?
    ): Call<JsonElement>

    @FormUrlEncoded
    @POST("add_payment")
    fun purchaseSubscriptionApi(
        @Field("token") token: String?,
        @Field("order_id") order_id: String?,
        @Field("purchase_token") purchase_token: String?,
        @Field("package_name") package_name: String?,
        @Field("duration") duration: String?,
        @Field("amount") amount: String?,
        @Field("auto_renewing") auto_renewing: Boolean
    ): Call<CommonBusinessResponse>

    @FormUrlEncoded
    @POST("otp_verify")
    fun emailOtpVerify(@Field("otp") otp: String?): Call<JsonElement>

    @FormUrlEncoded
    @POST("check_trail")
    fun checkTrailPeriod(@Field("token") token: String?): Call<TrialDetailsResponse?>
}