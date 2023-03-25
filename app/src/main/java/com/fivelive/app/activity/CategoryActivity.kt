package com.fivelive.app.activity


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.fivelive.app.R
import com.fivelive.app.dialog.CallDialog

class CategoryActivity : AppCompatActivity() {
    var parentRecyclerView: RecyclerView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category)
        // setContentView(R.layout.test_sanju);
        //  setContentView(R.layout.test_sanju);
        /*setContentView(R.layout.restaurant_details_lauout_new);
        // Initialize the SDK
        findViewById(R.id.businessName_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCallDialog();
            }
        });*/findViewById<View>(R.id.button4).setOnClickListener {
            //  AppUtil.showProgressDialogOther(CategoryActivity.this);
        }
    }

    fun openCallDialog() {
        CallDialog(this, "") { openDialPad() }.show()
    }

    fun openDialPad() {
        val u = Uri.parse("tel:" + "8800736083")
        val i = Intent(Intent.ACTION_DIAL, u)
        startActivity(i)
    }

    companion object {
        private const val TAG = "MyTag"
    }
}