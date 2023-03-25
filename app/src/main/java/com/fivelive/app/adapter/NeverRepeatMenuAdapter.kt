package com.fivelive.app.adapter

import android.content.Context
import android.text.TextUtils
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.fivelive.app.Model.ModelTest
import com.fivelive.app.R
import com.fivelive.app.fragment.EditHappyHourDetailsFragment
import com.fivelive.app.util.AppConstant
import com.fivelive.app.util.AppUtil
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import com.google.android.material.chip.ChipGroup

class NeverRepeatMenuAdapter constructor(
    var context: Context,
    var dayList: MutableList<ModelTest>,
    var fragment: Fragment
) : RecyclerView.Adapter<NeverRepeatMenuAdapter.MyViewHolder>() {
    public override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater: LayoutInflater = LayoutInflater.from(context)
        val view: View = inflater.inflate(R.layout.never_repeat_menu_single_row, parent, false)
        return MyViewHolder(view)
    }

    public override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val model: ModelTest = dayList.get(position)
        holder.chipGroup.removeAllViews()
        for (chip: String? in model.days) {
            if (chip != null && !(chip == "")) {
                createDynamically(chip, holder, position)
            }
        }
        if (model.startTime == null) {
            holder.startTime_tv.setText("Start Time")
        } else {
            holder.startTime_tv.setText(model.startTime)
        }
        if (model.endTime == null) {
            holder.endTime_tv.setText("End Time")
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

    fun addMenuInList() {
        val model: ModelTest = ModelTest()
        dayList.add(model)
        notifyDataSetChanged()
    }

    fun updateList() {}
    public override fun getItemCount(): Int {
        return dayList.size
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
            public override fun onClick(v: View) {
                chipGroup.removeView(chip)
                val model: ModelTest = dayList.get(position)
                for (chipName: String in model.days) {
                    if ((chipName == chip.getText().toString())) {
                        model.days.remove(chipName)
                        break
                    }
                }
            }
        })
        return chip
    }

    inner class MyViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener, CompoundButton.OnCheckedChangeListener {
        var deleteImageView: ImageView
        var selectDayTextView: TextView
        var chipGroup: ChipGroup
        var endTime_tv: TextView
        var startTime_tv: TextView
        var saveCkb: CheckBox
        var food_et: EditText
        var drink_et: EditText

        init {
            chipGroup = itemView.findViewById<View>(R.id.chipGroup) as ChipGroup
            selectDayTextView = itemView.findViewById<View>(R.id.selectDayTextView) as TextView
            deleteImageView = itemView.findViewById<View>(R.id.deleteImageView) as ImageView
            startTime_tv = itemView.findViewById<View>(R.id.startTime_tv) as TextView
            endTime_tv = itemView.findViewById<View>(R.id.endTime_tv) as TextView
            saveCkb = itemView.findViewById<View>(R.id.saveCkb) as CheckBox
            food_et = itemView.findViewById<View>(R.id.food_et) as EditText
            drink_et = itemView.findViewById<View>(R.id.drink_et) as EditText
            deleteImageView.setOnClickListener(this)
            selectDayTextView.setOnClickListener(this)
            startTime_tv.setOnClickListener(this)
            endTime_tv.setOnClickListener(this)
            saveCkb.setOnCheckedChangeListener(this)
        }

        public override fun onClick(view: View) {
            val position: Int = getPosition()
            when (view.getId()) {
                R.id.deleteImageView -> (fragment as EditHappyHourDetailsFragment).deleteMenuCard(
                    position
                )
                R.id.selectDayTextView -> (fragment as EditHappyHourDetailsFragment).openBottomSheet(
                    position
                )
                R.id.startTime_tv -> (fragment as EditHappyHourDetailsFragment).openTimePicker(
                    position,
                    AppConstant.START_TIME
                )
                R.id.endTime_tv -> (fragment as EditHappyHourDetailsFragment).openTimePicker(
                    position,
                    AppConstant.END_TIME
                )
            }
        }

        public override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
            if (isChecked) {
                val model: ModelTest = dayList.get(getPosition())
                if (validate(model)) {
                    (fragment as EditHappyHourDetailsFragment).goForAddMenuScheduleService(model)
                }
            } else {
                saveCkb.setChecked(false)
            }
        }

        fun validate(model: ModelTest): Boolean {
            val drink: String = drink_et.getText().toString()
            val food: String = food_et.getText().toString()
            model.drink = drink
            model.food = food
            if (model.days.size < 1) {
                AppUtil.showErrorDialog(
                    context,
                    context.getString(R.string.error),
                    "Please select days."
                )
                return false
            }
            if (TextUtils.isEmpty(model.startTime)) {
                AppUtil.showErrorDialog(
                    context,
                    context.getString(R.string.error),
                    "Select start Time."
                )
                return false
            }
            if (TextUtils.isEmpty(model.endTime)) {
                AppUtil.showErrorDialog(
                    context,
                    context.getString(R.string.error),
                    "Select end Time."
                )
                return false
            }
            if (TextUtils.isEmpty(model.drink)) {
                AppUtil.showErrorDialog(
                    context,
                    context.getString(R.string.error),
                    "Enter drink description."
                )
                return false
            }
            if (TextUtils.isEmpty(model.food)) {
                AppUtil.showErrorDialog(
                    context,
                    context.getString(R.string.error),
                    "Enter food description."
                )
                return false
            }
            return true
        }
    }
}