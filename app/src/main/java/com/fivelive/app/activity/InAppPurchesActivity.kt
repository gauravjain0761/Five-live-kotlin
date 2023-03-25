package com.fivelive.app.activity

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.SkuDetails
import com.fivelive.app.R
import com.fivelive.app.inAppPurchsed.BillingAgent
import com.fivelive.app.inAppPurchsed.BillingCallback
import com.fivelive.app.inAppPurchsed.InAppDataModel
import com.fivelive.app.util.AppConstant

class InAppPurchesActivity : AppCompatActivity(), BillingCallback {
    private var billingAgent: BillingAgent? = null
    var button: Button? = null
    var textView: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_in_app_purches)
        billingAgent = BillingAgent(this, this)
        findViewById<View>(R.id.button).setOnClickListener { //  billingAgent.purchasedView();
            billingAgent!!.purchasedSubscription(AppConstant.SILVER_ID)
        }
    }

    override fun onDestroy() {
        billingAgent!!.onDestroy()
        billingAgent = null
        super.onDestroy()
    }

    override fun onTokenConsumed() {
        //TODO here
        Toast.makeText(this, "Subscription completed", Toast.LENGTH_SHORT).show()
    }

    override fun onBillingPriceUpdate(skuDetailsList: List<SkuDetails>?) {
        // updatePrice(inAppDataModel);
    }

    override fun onPurchaseCompleted(purchase: Purchase?) {
        //todo call api here
        Toast.makeText(this, "", Toast.LENGTH_SHORT).show()
    }

    var silver_price_tv: TextView? = null
    var gold_price_tv: TextView? = null
    var pro_price_tv: TextView? = null
    fun updatePrice(dataModel: InAppDataModel) {
        if (dataModel.productId.equals(AppConstant.SILVER_ID, ignoreCase = true)) {
            silver_price_tv!!.text = dataModel.price + ""
        } else if (dataModel.productId.equals(AppConstant.GOLD_ID, ignoreCase = true)) {
            gold_price_tv!!.text = dataModel.price + ""
        } else if (dataModel.productId.equals(AppConstant.PRO_ID, ignoreCase = true)) {
            pro_price_tv!!.text = dataModel.price + ""
        }
    }
}