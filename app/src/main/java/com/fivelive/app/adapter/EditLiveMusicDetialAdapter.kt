package com.fivelive.app.adapter

import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.fivelive.app.Model.ModelTest
import com.fivelive.app.R
import com.fivelive.app.fragment.EditLiveMusicFragment
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import com.google.android.material.chip.ChipGroup

class EditLiveMusicDetialAdapter constructor(
    var context: Context,
    var modelTestList: MutableList<ModelTest>,
    var fragment: Fragment
) : RecyclerView.Adapter<EditLiveMusicDetialAdapter.MyViewHolder>() {
    public override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater: LayoutInflater = LayoutInflater.from(context)
        val view: View =
            inflater.inflate(R.layout.edit_live_music_detials_single_row, parent, false)
        return MyViewHolder(view)
    }

    public override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val model: ModelTest = modelTestList.get(position)
        holder.chipGroup.removeAllViews()
        for (chip: String? in model.days) {
            if (chip != null && !(chip == "")) {
                createDynamically(chip, holder, position)
            }
        }
        holder.endTime_tv.setText(model.endTime)
        holder.startTime_tv.setText(model.startTime)
        holder.description_et.setText(model.description)
    }

    public override fun getItemCount(): Int {
        return modelTestList.size
    }

    fun removeDataFromList(id: String) {
        for (modelTest: ModelTest in modelTestList) {
            if ((modelTest.id == id)) {
                modelTestList.remove(modelTest)
                break
            }
        }
        notifyDataSetChanged()
    }

    fun createDynamically(chips: String, holder: MyViewHolder, position: Int) {
        val tomatoChip: Chip = getChip(holder.chipGroup, chips, position)
        holder.chipGroup.addView(tomatoChip)
    }

    private fun getChip(chipGroup: ChipGroup, text: String, position: Int): Chip {
        val chip: Chip = Chip(context)
        chip.setChipDrawable(ChipDrawable.createFromResource(context, R.xml.chip))
        val paddingDp: Int = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            10f,
            context.getResources().getDisplayMetrics()
        ).toInt()
        chip.setPadding(paddingDp, paddingDp, paddingDp, paddingDp)
        chip.setText(text)
        chip.setOnCloseIconClickListener(object : View.OnClickListener {
            public override fun onClick(v: View) {}
        })
        return chip
    }

    inner class MyViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        var deleteImageView: ImageView
        var edit_imv: ImageView

        //TextInputEditText selectDayEditText;
        var selectDayTextView: TextView
        var chipGroup: ChipGroup
        var endTime_tv: TextView
        var startTime_tv: TextView
        var description_et: TextView

        init {
            chipGroup = itemView.findViewById<View>(R.id.chipGroup) as ChipGroup
            selectDayTextView = itemView.findViewById<View>(R.id.selectDayTextView) as TextView
            deleteImageView = itemView.findViewById<View>(R.id.deleteImageView) as ImageView
            edit_imv = itemView.findViewById<View>(R.id.edit_imv) as ImageView
            startTime_tv = itemView.findViewById<View>(R.id.startTime_tv) as TextView
            endTime_tv = itemView.findViewById<View>(R.id.endTime_tv) as TextView
            description_et = itemView.findViewById<View>(R.id.description_et) as TextView
            deleteImageView.setOnClickListener(this)
            edit_imv.setOnClickListener(this)
        }

        public override fun onClick(view: View) {
            val position: Int = getPosition()
            when (view.getId()) {
                R.id.deleteImageView -> (fragment as EditLiveMusicFragment).showDeleteConfirmationDialog(
                    modelTestList.get(position).id
                )
                R.id.edit_imv -> (fragment as EditLiveMusicFragment).dispatchToAddLiveMusicDetailsActivity(
                    true,
                    modelTestList.get(position).id
                )
            }
        }
    }
}