package com.fivelive.app.activity.brunchMenu.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fivelive.app.Model.HHDetails
import com.fivelive.app.R
import com.fivelive.app.activity.brunchMenu.adapter.BusinessBrunchDetailsAdapter
import com.fivelive.app.util.AppConstant

class BusinessBrunchDetailsFragment : Fragment() {
    var recyclerView: RecyclerView? = null
    var brunchDetailsArrayList: ArrayList<HHDetails>? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val bundle = arguments
        brunchDetailsArrayList = bundle!!.getParcelableArrayList(AppConstant.DETAILS_LIST)
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_restaurant_brunch_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById(R.id.recyclerView)
        setDataOnList()
    }

    fun setDataOnList() {
        val adapter = BusinessBrunchDetailsAdapter(activity, brunchDetailsArrayList)
        val llm = LinearLayoutManager(activity)
        llm.orientation = LinearLayoutManager.VERTICAL
        recyclerView!!.layoutManager = llm
        recyclerView!!.adapter = adapter
    }
}