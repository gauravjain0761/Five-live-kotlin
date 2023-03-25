package com.fivelive.app.activity


import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.*
import android.widget.ExpandableListView.OnGroupExpandListener
import androidx.appcompat.app.AppCompatActivity
import com.fivelive.app.Model.FaqList
import com.fivelive.app.Model.FaqResponse
import com.fivelive.app.R
import com.fivelive.app.adapter.FaqsExpandableListAdapter
import com.fivelive.app.retrofit.ApiClient
import com.fivelive.app.util.AppConstant
import com.fivelive.app.util.AppUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FaqsActivity : AppCompatActivity() {
    private val context: Context? = null
    var list_view: ExpandableListView? = null
    var backImageView: ImageView? = null
    var listDataParent: MutableList<String>? = null
    var listDataChild: HashMap<String, List<String>>? = null
    var faqList: List<FaqList>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_faqs)
        backImageView = findViewById(R.id.backImageView)
        backImageView?.setOnClickListener(View.OnClickListener { onBackPressed() })
        initView()
        setUpExpendableList()
        dispatchToFaqListService()
    }

    fun initView() {
        list_view = findViewById(R.id.list_view)
    }

    private fun setUpExpendableList() {

// Listview Group click listener
        list_view!!.setOnGroupClickListener { parent, v, groupPosition, id -> // TODO GroupClickListener work
            false
        }

        // Listview Group expanded listener
        list_view!!.setOnGroupExpandListener(object : OnGroupExpandListener {
            var lastExpandedPosition = -1
            override fun onGroupExpand(groupPosition: Int) {
                if (lastExpandedPosition != -1 && groupPosition != lastExpandedPosition) {
                    list_view!!.collapseGroup(lastExpandedPosition)
                }
                lastExpandedPosition = groupPosition
            }
        })

        // Listview Group collasped listener
        list_view!!.setOnGroupCollapseListener {
            // TODO GroupCollapseListener work
        }

        // Listview on child click listener
        list_view!!.setOnChildClickListener { parent, v, groupPosition, childPosition, id -> //  String str = listDataChild.get(listDataParent.get(groupPosition)).get(childPosition);
            //Toast.makeText(context, "" + str, Toast.LENGTH_SHORT).show();
            false
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        list_view!!.setIndicatorBounds(list_view!!.right - 100, list_view!!.width)
    }

    fun dispatchToFaqListService() {
        if (AppUtil.isNetworkAvailable(this)) {
            faqListService()
        } else {
            AppUtil.showConnectionError(this)
        }
    }

    fun faqListService() {
        AppUtil.showProgressDialog(this@FaqsActivity)
        val apiServiceInterface = ApiClient.instance.client
        val call = apiServiceInterface.faqList
        call.enqueue(object : Callback<FaqResponse?> {
            override fun onResponse(call: Call<FaqResponse?>?, response: Response<FaqResponse?>) {
                AppUtil.dismissProgressDialog()
                try {
                    if (response.isSuccessful) {
                        val faqResponse = response.body()
                        if (faqResponse!!.status.equals(AppConstant.SUCCESS, ignoreCase = true)) {
                            faqList = faqResponse.faqList
                            preparedListForFAQ()
                        } else {
                            AppUtil.showErrorDialog(
                                this@FaqsActivity,
                                this@FaqsActivity.resources.getString(R.string.error),
                                faqResponse.message
                            )
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<FaqResponse?>, t: Throwable) {
                AppUtil.dismissProgressDialog()
                call.cancel()
                AppUtil.showErrorDialog(
                    this@FaqsActivity,
                    this@FaqsActivity.resources.getString(R.string.error),
                    t.message
                )
            }
        })
    }

    fun preparedListForFAQ() {
        listDataParent = ArrayList()
        listDataChild = HashMap()
        for (i in faqList!!.indices) {
            val list: MutableList<String> = ArrayList()
            val faqData = faqList!![i]
            listDataParent?.add(faqData.question)
            list.add(faqData.answer)
            listDataChild!![listDataParent!!.get(i)] = list
        }
        val listAdapter =
            FaqsExpandableListAdapter(this@FaqsActivity, listDataParent!!, listDataChild!!)
        list_view!!.setAdapter(listAdapter)
    }

    private fun createListData() {
        listDataParent = ArrayList()
        listDataChild = HashMap()

        // Adding child data
        listDataParent?.add("What is the Lorem Ipsum.")
        listDataParent?.add("Why we using Lorem Ipsum.")
        listDataParent?.add("Why we not use any other text.")

        // Adding child data List one
        val colors: MutableList<String> = ArrayList()
        colors.add(resources.getString(R.string.dummy_text))


        // Adding child data List two
        val fruits: MutableList<String> = ArrayList()
        fruits.add(resources.getString(R.string.dummy_text))

        // Adding child data List three
        val animals: MutableList<String> = ArrayList()
        animals.add(resources.getString(R.string.dummy_text))
        listDataChild!![listDataParent!!.get(0)] = colors // Header, Child data
        listDataChild!![listDataParent!!.get(1)] = fruits // Header, Child data
        listDataChild!![listDataParent!!.get(2)] = animals // Header, Child data
        val listAdapter =
            FaqsExpandableListAdapter(this@FaqsActivity, listDataParent!!, listDataChild!!)
        list_view!!.setAdapter(listAdapter)
    }
}