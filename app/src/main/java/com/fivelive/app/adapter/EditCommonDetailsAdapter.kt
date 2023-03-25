package com.fivelive.app.adapter

import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.fivelive.app.Model.ModelTest
import com.fivelive.app.R
import com.fivelive.app.fragment.EditCommonDetailsFragment
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import com.google.android.material.chip.ChipGroup

class EditCommonDetailsAdapter constructor(
    var context: Context,
    var modelTestList: MutableList<ModelTest>,
    var fragment: Fragment,
    var type: String
) : RecyclerView.Adapter<EditCommonDetailsAdapter.MyViewHolder>() {
    public override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater: LayoutInflater = LayoutInflater.from(context)
        val view: View = inflater.inflate(R.layout.never_repeat_menu_single_row, parent, false)
        return MyViewHolder(view)
    }

    public override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val model: ModelTest = modelTestList.get(position)
        // create chips for days chip group
        holder.chipGroup.removeAllViews()
        for (chip: String? in model.days) {
            if (chip != null && !(chip == "")) {
                createDynamically(chip, holder.chipGroup, position)
            }
        }
        if ((type == "1")) {
            // create chips for quick filter chip group
            holder.qfChipGroup.removeAllViews()
            for (chip: String? in model.hhFilterList) {
                if (chip != null && !(chip == "")) {
                    createDynamically(chip, holder.qfChipGroup, position)
                }
            }
        } else {
            holder.qfChipGroup.setVisibility(View.GONE)
            holder.selected_qf_tv.setVisibility(View.GONE)
        }
        if (model.startTime == null) {
            holder.startTime_tv.setText(context.getResources().getString(R.string.start_time))
        } else {
            holder.startTime_tv.setText(model.startTime)
        }
        if (model.endTime == null) {
            holder.endTime_tv.setText(context.getResources().getString(R.string.end_time))
        } else {
            holder.endTime_tv.setText(model.endTime)
        }
        if (model.drink != null) {
            holder.drink_et.setText(model.drink)
        }
        if (model.food != null) {
            holder.food_et.setText(model.food)
        }
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

    fun createDynamically(chips: String, chipGroup: ChipGroup, position: Int) {
        val tomatoChip: Chip = getChip(chipGroup, chips, position)
        chipGroup.addView(tomatoChip)
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
        chip.setElevation(8.0f)
        chip.setCloseIconVisible(false)
        chip.setOnCloseIconClickListener(object : View.OnClickListener {
            public override fun onClick(v: View) {
                /*chipGroup.removeView(chip);
                ModelTest model = modelTestList.get(position);
                for (String chipName : model.daysList) {
                    if (chipName.equals(chip.getText().toString())) {
                        model.daysList.remove(chipName);
                        break;
                    }
                }*/
            }
        })
        return chip
    }

    inner class MyViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        var deleteImageView: ImageView
        var edit_imv: ImageView
        var selectDayTextView: TextView
        var selected_qf_tv: TextView
        var chipGroup: ChipGroup
        var qfChipGroup: ChipGroup
        var endTime_tv: TextView
        var startTime_tv: TextView
        var saveCkb: CheckBox
        var food_et: EditText
        var drink_et: EditText

        init {
            chipGroup = itemView.findViewById<View>(R.id.chipGroup) as ChipGroup
            qfChipGroup = itemView.findViewById<View>(R.id.qfChipGroup) as ChipGroup
            selectDayTextView = itemView.findViewById<View>(R.id.selectDayTextView) as TextView
            selected_qf_tv = itemView.findViewById<View>(R.id.selected_qf_tv) as TextView
            deleteImageView = itemView.findViewById<View>(R.id.deleteImageView) as ImageView
            edit_imv = itemView.findViewById<View>(R.id.edit_imv) as ImageView
            startTime_tv = itemView.findViewById<View>(R.id.startTime_tv) as TextView
            endTime_tv = itemView.findViewById<View>(R.id.endTime_tv) as TextView
            saveCkb = itemView.findViewById<View>(R.id.saveCkb) as CheckBox
            food_et = itemView.findViewById<View>(R.id.food_et) as EditText
            drink_et = itemView.findViewById<View>(R.id.drink_et) as EditText
            deleteImageView.setOnClickListener(this)
            edit_imv.setOnClickListener(this)
        }

        public override fun onClick(view: View) {
            val position: Int = getPosition()
            when (view.getId()) {
                R.id.deleteImageView -> (fragment as EditCommonDetailsFragment).showDeleteConfirmationDialog(
                    modelTestList.get(position).id
                )
                R.id.edit_imv -> (fragment as EditCommonDetailsFragment).dispatchToAddHHDetailsActivity(
                    true,
                    modelTestList.get(position).id
                )
            }
        }
    }
}