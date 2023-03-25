package com.fivelive.app.activity.reverseHhMenu.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fivelive.app.Model.Category
import com.fivelive.app.R
import com.fivelive.app.activity.reverseHhMenu.adapter.ReverseHHItemsParentAdapter
import com.fivelive.app.util.AppConstant

class BusinessReverseHhItemFragment : Fragment() {
    var recyclerView: RecyclerView? = null
    var categoryArrayList: ArrayList<Category>? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val bundle = arguments
        categoryArrayList = bundle!!.getParcelableArrayList(AppConstant.CATEGORY_LIST)
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_business_hh_item, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById(R.id.recyclerView)
        setDataOnList()
    }

    fun setDataOnList() {
        val adapter = ReverseHHItemsParentAdapter(activity, categoryArrayList)
        val llm = LinearLayoutManager(activity)
        llm.orientation = LinearLayoutManager.VERTICAL
        recyclerView!!.layoutManager = llm
        recyclerView!!.adapter = adapter
    }
}