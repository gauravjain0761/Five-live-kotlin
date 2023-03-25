package com.fivelive.app.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fivelive.app.Model.MenuListingResponse
import com.fivelive.app.Model.RegularMenu
import com.fivelive.app.R
import com.fivelive.app.activity.AddHappyHoursImageActivity
import com.fivelive.app.activity.ShowImagesActivity
import com.fivelive.app.adapter.EditReverseHHMenuAdapter
import com.fivelive.app.dialog.ConfirmationDialog
import com.fivelive.app.dialog.CustomSuccessDialog
import com.fivelive.app.interfaces.RecyclerViewClickListener
import com.fivelive.app.util.AppConstant
import com.fivelive.app.util.AppUtil
import com.fivelive.app.util.CommonAPI
import java.io.Serializable

class EditReverseHHMenuFragment : Fragment(), RecyclerViewClickListener {
    private val TYPE = "3"
    var recyclerView: RecyclerView? = null
    var businessId: String? = null
    var activity: Activity? = null
    var hhMenuList: List<RegularMenu>? = null
    var editReverseHHMenuAdapter: EditReverseHHMenuAdapter? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity = getActivity()
        val bundle = arguments
        businessId = bundle!!.getString(AppConstant.BUSINESS_ID)
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_reverse_h_h_menu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById<View>(R.id.recyclerView) as RecyclerView
    }

    override fun onResume() {
        super.onResume()
        goForMenuItemListingService()
    }

    fun goForMenuItemListingService() {
        if (AppUtil.isNetworkAvailable(requireContext())) {
            val commonApi = CommonAPI(requireContext())
            commonApi.menuItemListingService(businessId, TYPE) { `object` ->
                if (`object` is MenuListingResponse) {
                    hhMenuList = `object`.regularMenue
                    setDataInList()
                }
            }
        } else {
            AppUtil.showConnectionError(activity)
        }
    }

    fun setDataInList() {
        editReverseHHMenuAdapter = EditReverseHHMenuAdapter(
            requireContext(),
            hhMenuList!!.toMutableList(),
            this,
            this@EditReverseHHMenuFragment
        )
        val manager = GridLayoutManager(getActivity(), 2, GridLayoutManager.VERTICAL, false)
        recyclerView!!.layoutManager = manager
        recyclerView!!.adapter = editReverseHHMenuAdapter
    }

    override fun recyclerViewListClicked(v: View?, position: Int) {
        if (position == 0) {
            dispatchToAddHHImageActivity()
        }
    }

    /*when we add menu for happy hours we are Setting in type = 3*/
    fun dispatchToAddHHImageActivity() {
        val intent = Intent(getActivity(), AddHappyHoursImageActivity::class.java)
        intent.putExtra(AppConstant.BUSINESS_ID, businessId)
        intent.putExtra(AppConstant.TYPE, TYPE)
        startActivity(intent)
    }

    fun showDeleteConfirmationDialog(itemID: String) {
        ConfirmationDialog(activity) { goForDeleteMenuItemService(itemID) }.show()
    }

    fun goForDeleteMenuItemService(itemId: String) {
        if (AppUtil.isNetworkAvailable(requireContext())) {
            val commonApi = CommonAPI(requireContext())
            commonApi.deleteMenuItemService(itemId) { msg ->
                editReverseHHMenuAdapter!!.removeMenuItemFromList(itemId)
                showSuccessDialog(msg)
            }
        } else {
            AppUtil.showConnectionError(activity)
        }
    }

    fun showSuccessDialog(msg: String?) {
        CustomSuccessDialog(activity, getString(R.string.success), msg) { }.show()
    }

    fun showImagePreview(position: Int) {
        val menuList: MutableList<String> = ArrayList()
        for (menu in hhMenuList!!) {
            if (menu.image != null) menuList.add(menu.image!!)
        }
        val intent = Intent(activity, ShowImagesActivity::class.java)
        intent.putExtra(AppConstant.IMAGES_LIST, menuList as Serializable)
        intent.putExtra(AppConstant.IMAGE_INDEX, position)
        startActivity(intent)
        requireActivity().overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left)
    }

    companion object {
        const val TAG = "ReverseHHMenuFragment"
    }
}