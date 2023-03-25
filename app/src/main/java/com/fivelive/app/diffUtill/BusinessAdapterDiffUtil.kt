package com.fivelive.app.diffUtill

import androidx.recyclerview.widget.DiffUtil
import com.fivelive.app.Model.HomeBusiness
import com.google.android.gms.ads.formats.UnifiedNativeAd

class BusinessAdapterDiffUtil(var oldBusinessList: List<Any>, var newBusinessList: List<Any>) :
    DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldBusinessList.size
    }

    override fun getNewListSize(): Int {
        return newBusinessList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        // return false;
        val oldObj = oldBusinessList[oldItemPosition]
        val newObj = newBusinessList[newItemPosition]
        return if (oldObj is HomeBusiness && newObj is HomeBusiness) {
            oldObj.id == newObj.id
        } else if (oldObj is HomeBusiness && newObj is UnifiedNativeAd) {
            false
        } else {
            false
        }
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldObj = oldBusinessList[oldItemPosition]
        val newObj = newBusinessList[newItemPosition]
        return if (oldObj is HomeBusiness && newObj is HomeBusiness) {
            //  return ((HomeBusiness) oldObj).getName().equals(((HomeBusiness) newObj).getName());
            oldObj.businessCount == newObj.businessCount
        } else if (oldObj is HomeBusiness && newObj is UnifiedNativeAd) {
            false
        } else {
            false
        }
    }

    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
        return super.getChangePayload(oldItemPosition, newItemPosition)
    }
}