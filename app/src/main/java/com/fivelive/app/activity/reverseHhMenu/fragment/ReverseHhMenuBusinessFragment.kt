package com.fivelive.app.activity.reverseHhMenu.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fivelive.app.Model.HHMenu
import com.fivelive.app.R
import com.fivelive.app.activity.ShowImagesActivity
import com.fivelive.app.activity.reverseHhMenu.adapter.RestaurantReverseHHMenuAdapter
import com.fivelive.app.util.AppConstant
import java.io.Serializable

class ReverseHhMenuBusinessFragment : Fragment() {
    var recyclerView: RecyclerView? = null
    var reverseHHMenuArrayList: ArrayList<HHMenu>? = null
    var activity: Activity? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity = getActivity()
        val bundle = arguments
        reverseHHMenuArrayList = bundle!!.getParcelableArrayList(AppConstant.MENU_LIST)
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_restaurant_hh_menu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById(R.id.recyclerView)
        setRecipesImagesList()
    }

    fun setRecipesImagesList() {
        val pantryListData: MutableList<String?> = ArrayList<String?>()
        for (i in 0..7) {
            pantryListData.add("sanju")
        }
        val adapter = RestaurantReverseHHMenuAdapter(
            getActivity(),
            reverseHHMenuArrayList,
            this@ReverseHhMenuBusinessFragment
        )
        val manager = GridLayoutManager(getActivity(), 2, GridLayoutManager.VERTICAL, false)
        recyclerView!!.layoutManager = manager
        recyclerView!!.adapter = adapter
    }

    fun showImagePreview(position: Int) {
        val menuList: MutableList<String> = ArrayList()
        for (menu in reverseHHMenuArrayList!!) {
            menuList.add(menu.image)
        }
        val intent = Intent(activity, ShowImagesActivity::class.java)
        intent.putExtra(AppConstant.IMAGES_LIST, menuList as Serializable)
        intent.putExtra(AppConstant.IMAGE_INDEX, position)
        startActivity(intent)
        // activity.overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
    }

    companion object {
        const val TAG = "RestaurantHhMenuFragment"
    }
}