package com.fivelive.app.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.SkuDetails
import com.fivelive.app.Model.CommonBusinessResponse
import com.fivelive.app.Model.PlanDetails
import com.fivelive.app.R
import com.fivelive.app.dialog.SubscriptionPlanSucessFullyDialog
import com.fivelive.app.inAppPurchsed.BillingAgent
import com.fivelive.app.inAppPurchsed.BillingCallback
import com.fivelive.app.inAppPurchsed.InAppDataModel
import com.fivelive.app.inAppPurchsed.PurchaseDataModel
import com.fivelive.app.interfaces.DialogButtonListener
import com.fivelive.app.prefs.SharedPreferenceWriter
import com.fivelive.app.prefs.SharedPrefsKey
import com.fivelive.app.retrofit.APIServiceInterface
import com.fivelive.app.retrofit.ApiClient
import com.fivelive.app.util.AppConstant
import com.fivelive.app.util.AppUtil
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SubscriptionActivity constructor() : AppCompatActivity(), View.OnClickListener,
    BillingCallback {
    var backImageView: ImageView? = null
    var termsAndCondition: TextView? = null
    var silver_cardView: CardView? = null
    var gold_cardView: CardView? = null
    var pro_cardView: CardView? = null
    var silver_price_tv: TextView? = null
    var gold_price_tv: TextView? = null
    var pro_price_tv: TextView? = null
    var silver_cl: ConstraintLayout? = null
    var gold_cl: ConstraintLayout? = null
    var pro_cl: ConstraintLayout? = null
    var continue_to_pay_btn: Button? = null
    private var billingAgent: BillingAgent? = null
    var subscriptionSKUDetailsList: List<SkuDetails> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subscription)
        billingAgent = BillingAgent(this, this)
        initView()
        selectedSilverPlan()
    }

    private fun initView() {
        backImageView = findViewById(R.id.backImageView)
        continue_to_pay_btn = findViewById(R.id.continue_to_pay_btn)
        silver_cardView = findViewById(R.id.silver_cardView)
        gold_cardView = findViewById(R.id.gold_cardView)
        pro_cardView = findViewById(R.id.pro_cardView)
        silver_cl = findViewById(R.id.silver_cl)
        gold_cl = findViewById(R.id.gold_cl)
        pro_cl = findViewById(R.id.pro_cl)
        silver_cl?.setOnClickListener(this)
        gold_cl?.setOnClickListener(this)
        pro_cl?.setOnClickListener(this)
        pro_price_tv = findViewById(R.id.pro_price_tv)
        gold_price_tv = findViewById(R.id.gold_price_tv)
        silver_price_tv = findViewById(R.id.silver_price_tv)
        termsAndCondition = findViewById(R.id.termsAndCondition)
        termsAndCondition?.setOnClickListener(this)
        backImageView?.setOnClickListener(this)
    }

    private fun dispatchToTermAndCondition() {
        AppUtil.dispatchToTermANdConditionActivity(this@SubscriptionActivity)
        //  startActivity(new Intent(SubscriptionActivity.this, TermAndConditionActivity.class));
    }

    fun selectedSilverPlan() {
        silver_cardView!!.setBackground(getDrawable(R.drawable.selected_plan_bg))
        gold_cardView!!.setBackground(getDrawable(R.drawable.unselected_plan_bg))
        pro_cardView!!.setBackground(getDrawable(R.drawable.unselected_plan_bg))
    }

    fun selectedGoldPlan() {
        gold_cardView!!.setBackground(getDrawable(R.drawable.selected_plan_bg))
        silver_cardView!!.setBackground(getDrawable(R.drawable.unselected_plan_bg))
        pro_cardView!!.setBackground(getDrawable(R.drawable.unselected_plan_bg))
    }

    fun selectedProPlan() {
        pro_cardView!!.setBackground(getDrawable(R.drawable.selected_plan_bg))
        gold_cardView!!.setBackground(getDrawable(R.drawable.unselected_plan_bg))
        silver_cardView!!.setBackground(getDrawable(R.drawable.unselected_plan_bg))
    }

    public override fun onClick(view: View) {
        when (view.getId()) {
            R.id.backImageView -> finish()
            R.id.termsAndCondition -> dispatchToTermAndCondition()
            R.id.silver_cl -> {
                selectedSilverPlan()
                addListenerOnPlanButton(
                    silver_price_tv!!.getText().toString(),
                    AppConstant.SILVER_ID
                )
            }
            R.id.gold_cl -> {
                selectedGoldPlan()
                addListenerOnPlanButton(gold_price_tv!!.getText().toString(), AppConstant.GOLD_ID)
            }
            R.id.pro_cl -> {
                selectedProPlan()
                addListenerOnPlanButton(pro_price_tv!!.getText().toString(), AppConstant.PRO_ID)
            }
        }
    }

    fun updateContinueBtnText(price: String?) {
        continue_to_pay_btn!!.setText("Continue to Pay " + price)
    }

    fun addListenerOnPlanButton(price: String?, subscriptionID: String?) {
        updateContinueBtnText(price)
        continue_to_pay_btn!!.setOnClickListener(object : View.OnClickListener {
            public override fun onClick(v: View) {
                when (subscriptionID) {
                    AppConstant.SILVER_ID ->                        // showSuccessDialog(null);
                        billingAgent!!.purchasedSubscription(AppConstant.SILVER_ID)
                    AppConstant.GOLD_ID -> billingAgent!!.purchasedSubscription(AppConstant.GOLD_ID)
                    AppConstant.PRO_ID -> billingAgent!!.purchasedSubscription(AppConstant.PRO_ID)
                }
            }
        })
    }

    override fun onDestroy() {
        billingAgent!!.onDestroy()
        billingAgent = null
        super.onDestroy()
    }

    public override fun onTokenConsumed() {
        //TODO this method call when user has active subscription plan
        AppUtil.showErrorDialog(
            this@SubscriptionActivity,
            "Info!",
            "You've already subscribed a plan.\n" + "Enjoy your plan..!!"
        )
    }

    public override fun onBillingPriceUpdate(skuDetailsList: List<SkuDetails>?) {
        if (skuDetailsList != null) {
            subscriptionSKUDetailsList = skuDetailsList
        }
        for (i in subscriptionSKUDetailsList.indices) {
            val dataModel: InAppDataModel = Gson().fromJson(
                subscriptionSKUDetailsList.get(i).getOriginalJson(),
                InAppDataModel::class.java
            )
            updatePrice(dataModel)
        }
    }

    public override fun onPurchaseCompleted(purchase: Purchase?) {
        val model: PurchaseDataModel =
            Gson().fromJson(purchase?.getOriginalJson(), PurchaseDataModel::class.java)
        dispatchToSubscriptionAPI(model)
    }

    fun updatePrice(dataModel: InAppDataModel) {
        if (dataModel.productId.equals(AppConstant.SILVER_ID, ignoreCase = true)) {
            silver_price_tv!!.setText(dataModel.price + "")
            updateContinueBtnText(dataModel.price) // first time when user comes to screen
            addListenerOnPlanButton(dataModel.price, AppConstant.SILVER_ID)
        } else if (dataModel.productId.equals(AppConstant.GOLD_ID, ignoreCase = true)) {
            gold_price_tv!!.setText(dataModel.price + "")
        } else if (dataModel.productId.equals(AppConstant.PRO_ID, ignoreCase = true)) {
            pro_price_tv!!.setText(dataModel.price + "")
        }
    }

    fun getSubscriptionPlanName(subscriptionID: String?): String {
        var planName: String = ""
        when (subscriptionID) {
            AppConstant.SILVER_ID -> planName = AppConstant.SILVER
            AppConstant.GOLD_ID -> planName = AppConstant.GOLD
            AppConstant.PRO_ID -> planName = AppConstant.PRO
        }
        return planName
    }

    fun getSubscriptionPlanDuration(subscriptionID: String?): String {
        var planDuration: Int = 0
        when (subscriptionID) {
            AppConstant.SILVER_ID -> planDuration = AppConstant.SILVER_DURATION
            AppConstant.GOLD_ID -> planDuration = AppConstant.GOLD_DURATION
            AppConstant.PRO_ID -> planDuration = AppConstant.PRO_DURATION
        }
        return planDuration.toString()
    }

    fun getSKUDetails(subscriptionID: String): SkuDetails? {
        var subscriptionSKUDetails: SkuDetails? = null
        for (skuDetails: SkuDetails in subscriptionSKUDetailsList) {
            if ((subscriptionID == skuDetails.getSku())) {
                subscriptionSKUDetails = skuDetails
                break
            }
        }
        return subscriptionSKUDetails
    }

    private fun dispatchToSubscriptionAPI(model: PurchaseDataModel) {
        AppUtil.showProgressDialog(this@SubscriptionActivity)
        val sessionToken: String? = SharedPreferenceWriter.getInstance(this@SubscriptionActivity)
            .getString(SharedPrefsKey.TOKEN)
        val planName: String = getSubscriptionPlanName(model.productId)
        val planDuration: String = getSubscriptionPlanDuration(model.productId)
        Log.d("Purchase", "dispatchToSubscriptionAPI: token" + model.purchaseToken)
        val skuDetails: SkuDetails? = model.productId?.let { getSKUDetails(it) }

        // String amount = skuDetails.getPriceAmountMicros()  /1000000d+"";
        val apiServiceInterface: APIServiceInterface = ApiClient.instance.client
        val call: Call<CommonBusinessResponse> = apiServiceInterface.purchaseSubscriptionApi(
            sessionToken,
            model.orderId,
            model.purchaseToken,
            planName,
            planDuration,
            (skuDetails!!.priceAmountMicros / 1000000.0).toString() + "",
            model.autoRenewing
        )
        call.enqueue(object : Callback<CommonBusinessResponse?> {
            public override fun onResponse(
                call: Call<CommonBusinessResponse?>,
                response: Response<CommonBusinessResponse?>
            ) {
                AppUtil.dismissProgressDialog()
                try {
                    if (response.isSuccessful()) {
                        // JSONObject object = new JSONObject(response.body().getAsJsonObject().toString().trim());
                        val businessResponse: CommonBusinessResponse? = response.body()
                        if (businessResponse!!.status
                                .equals(AppConstant.SUCCESS, ignoreCase = true)
                        ) {
                            showSuccessDialog(businessResponse.details)
                        } else {
                            if (businessResponse.message
                                    .contains(AppConstant.SESSION_EXPIRED)
                            ) {
                                AppUtil.showLogoutDialog(
                                    this@SubscriptionActivity,
                                    getResources().getString(R.string.error),
                                    businessResponse.message
                                )
                            } else {
                                AppUtil.showErrorDialog(
                                    this@SubscriptionActivity,
                                    getResources().getString(R.string.error),
                                    businessResponse.message
                                )
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            public override fun onFailure(call: Call<CommonBusinessResponse?>, t: Throwable) {
                AppUtil.dismissProgressDialog()
                call.cancel()
                AppUtil.showErrorDialog(
                    this@SubscriptionActivity,
                    getResources().getString(R.string.error),
                    t.message
                )
            }
        })
    }

    fun showSuccessDialog(planDetails: PlanDetails?) {
        SubscriptionPlanSucessFullyDialog(
            this@SubscriptionActivity,
            planDetails,
            object : DialogButtonListener {
                public override fun onButtonClicked() {
                    dispatchToHomeScreen()
                }
            }).show()
    }

    fun dispatchToHomeScreen() {
        val intent: Intent = Intent(this@SubscriptionActivity, HomeActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }
}