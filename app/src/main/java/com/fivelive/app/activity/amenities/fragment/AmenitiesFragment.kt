package com.fivelive.app.activity.amenities.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fivelive.app.Model.Amenity
import com.fivelive.app.R
import com.fivelive.app.activity.amenities.adapter.BizAmenitiesAdapter

class AmenitiesFragment : Fragment() {
    var recyclerView: RecyclerView? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_restaurant_amenities, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById(R.id.recyclerView)
        setRecipesImagesList()
    }

    fun setRecipesImagesList() {
        val amenitiesList: List<Amenity> = ArrayList()
        val adapter = BizAmenitiesAdapter(activity, amenitiesList)
        val manager = GridLayoutManager(activity, 3, GridLayoutManager.VERTICAL, false)
        recyclerView!!.layoutManager = manager
        recyclerView!!.adapter = adapter
    }
}