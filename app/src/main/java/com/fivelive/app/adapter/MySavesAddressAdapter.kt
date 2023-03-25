package com.fivelive.app.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.fivelive.app.Model.AddressList
import com.fivelive.app.R
import com.fivelive.app.activity.MySavedAddressActivity
import com.fivelive.app.interfaces.ItemClickListener

class MySavesAddressAdapter(
    var context: Context,
    var addressList: List<AddressList>,
    var listener: ItemClickListener
) : RecyclerView.Adapter<MySavesAddressAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.my_saved_address_single_row, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val model = addressList[position]
        val count = position + 1
        holder.address_tv.text = model.address
        holder.title_tv.text = count.toString() + ". " + model.title
    }

    override fun getItemCount(): Int {
        return addressList.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        var delete_tv: TextView
        var edit_tv: TextView
        var address_tv: TextView
        var title_tv: TextView

        init {
            title_tv = itemView.findViewById<View>(R.id.title_tv) as TextView
            address_tv = itemView.findViewById<View>(R.id.address_tv) as TextView
            edit_tv = itemView.findViewById<View>(R.id.edit_tv) as TextView
            delete_tv = itemView.findViewById<View>(R.id.delete_tv) as TextView
            edit_tv.setOnClickListener(this)
            delete_tv.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            when (v.id) {
                R.id.delete_tv -> (context as MySavedAddressActivity).deleteAddressService(
                    addressList[this.position].id
                )
                R.id.edit_tv -> (context as MySavedAddressActivity).editSavedAddress(addressList[this.position])
            }
        }
    }
}