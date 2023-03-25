package com.fivelive.app.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.fivelive.app.Model.RegularMenu
import com.fivelive.app.R
import com.fivelive.app.fragment.EditReverseHHMenuFragment
import com.fivelive.app.interfaces.RecyclerViewClickListener
import com.fivelive.app.util.AppUtil

class EditReverseHHMenuAdapter constructor(
    var context: Context,
    var hhMenuList: MutableList<RegularMenu>,
    var clickListener: RecyclerViewClickListener,
    var fragment: Fragment
) : RecyclerView.Adapter<EditReverseHHMenuAdapter.MyViewHolder>() {
    init {
        /*This blank model add because 0 index show Add image layout*/
        val menu: RegularMenu = RegularMenu()
        hhMenuList.add(0, menu)
    }

    public override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater: LayoutInflater = LayoutInflater.from(context)
        val view: View = inflater.inflate(R.layout.reverse_hh_menu_single_row, parent, false)
        return MyViewHolder(view)
    }

    public override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val menu: RegularMenu = hhMenuList.get(position)
        holder.itemNameTextView.setText(menu.title)
        AppUtil.loadMenuImage(context, menu.image, holder.itemImageView)
        if (position == 0) {
            holder.addImageConstraintLayout.setVisibility(View.VISIBLE)
            holder.imageConstraintLayout.setVisibility(View.GONE)
        } else {
            holder.addImageConstraintLayout.setVisibility(View.GONE)
            holder.imageConstraintLayout.setVisibility(View.VISIBLE)
        }
        holder.deleteImageView.setOnClickListener(object : View.OnClickListener {
            public override fun onClick(v: View) {
                (fragment as EditReverseHHMenuFragment).showDeleteConfirmationDialog(menu.id)
            }
        })
        holder.itemImageView.setOnClickListener(object : View.OnClickListener {
            public override fun onClick(v: View) {
                (fragment as EditReverseHHMenuFragment).showImagePreview(position)
            }
        })
    }

    public override fun getItemCount(): Int {
        return hhMenuList.size
    }

    fun removeMenuItemFromList(itemId: String) {
        for (menu: RegularMenu in hhMenuList) {
            if (menu.id != null) if ((menu.id == itemId)) {
                hhMenuList.remove(menu)
                notifyDataSetChanged()
            }
        }
    }

    inner class MyViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        var imagesRecyclerView: RecyclerView? = null
        var addImageConstraintLayout: ConstraintLayout
        var imageConstraintLayout: ConstraintLayout
        var itemImageView: ImageView
        var deleteImageView: ImageView
        var itemNameTextView: TextView

        init {
            addImageConstraintLayout = itemView.findViewById(R.id.addImageConstraintLayout)
            imageConstraintLayout = itemView.findViewById(R.id.imageConstraintLayout)
            itemImageView = itemView.findViewById(R.id.itemImageView)
            deleteImageView = itemView.findViewById(R.id.deleteImageView)
            itemNameTextView = itemView.findViewById(R.id.itemNameTextVIew)
            itemView.setOnClickListener(this)
        }

        public override fun onClick(view: View) {
            clickListener.recyclerViewListClicked(view, getPosition())
        }
    }
}