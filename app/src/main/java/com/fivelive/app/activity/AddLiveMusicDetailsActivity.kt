package com.fivelive.app.activity


import android.os.Bundle
import android.text.TextUtils
import android.util.TypedValue
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.fivelive.app.Model.ModelTest
import com.fivelive.app.R
import com.fivelive.app.bottomSheet.SelectDaySheet
import com.fivelive.app.dialog.CustomSuccessDialog
import com.fivelive.app.dialog.TimePickerFragment
import com.fivelive.app.dialog.TimePickerFragment.TimeCallbackListener
import com.fivelive.app.util.AppConstant
import com.fivelive.app.util.AppUtil
import com.fivelive.app.util.CommonAPI
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import com.google.android.material.chip.ChipGroup


class AddLiveMusicDetailsActivity : AppCompatActivity(), SelectDaySheet.ItemClickListener,
    View.OnClickListener, TimeCallbackListener {
    var myBottomSheet: SelectDaySheet? = null
    var modelTest = ModelTest()
    var businessId: String? = null
    var scheduleId: String? = null
    var type: String? = null
    var selectDayTextView: TextView? = null
    var startTime_tv: TextView? = null
    var endTime_tv: TextView? = null
    var description_tv: EditText? = null
    var chipGroup: ChipGroup? = null
    var addItemButton: Button? = null
    var isEdit = false
    var modelTestArrayList: ArrayList<ModelTest>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_live_music_details)
        initView()
        dataFromIntent
    }

    private val dataFromIntent: Unit
        private get() {
            businessId = intent.getStringExtra(AppConstant.BUSINESS_ID)
            scheduleId = intent.getStringExtra(AppConstant.ID)
            type = intent.getStringExtra(AppConstant.TYPE)
            isEdit = intent.getBooleanExtra(AppConstant.EDIT, false)
            modelTestArrayList = intent.getParcelableArrayListExtra(AppConstant.MODEL_TEST_LIST)
            modelTest.type = type
            if (isEdit) {
                for (model in modelTestArrayList!!) {
                    if (model.id == scheduleId) {
                        modelTest = model
                        updateUI()
                        break
                    }
                }
            }
        }

    private fun updateUI() {
        /*add days chips*/
        for (chip in modelTest.days) {
            createDynamically(chip)
        }
        startTime_tv!!.text = modelTest.startTime
        endTime_tv!!.text = modelTest.endTime
        description_tv!!.setText(modelTest.description)
        addItemButton!!.setText(R.string.update_item)
    }

    private fun initView() {
        selectDayTextView = findViewById(R.id.selectDayTextView)
        chipGroup = findViewById<View>(R.id.chipGroup) as ChipGroup
        description_tv = findViewById(R.id.description_tv)
        startTime_tv = findViewById(R.id.startTime_tv)
        endTime_tv = findViewById(R.id.endTime_tv)
        addItemButton = findViewById(R.id.addItemButton)
        selectDayTextView?.setOnClickListener(this)
        startTime_tv?.setOnClickListener(this)
        endTime_tv?.setOnClickListener(this)
        addItemButton?.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.selectDayTextView -> openBottomSheet()
            R.id.startTime_tv -> openTimePicker(AppConstant.START_TIME)
            R.id.endTime_tv -> openTimePicker(AppConstant.END_TIME)
            R.id.addItemButton -> goForAddMenuScheduleService()
        }
    }

    fun openTimePicker(fromTime: String?) {
        modelTest.from = fromTime
        val fragment = TimePickerFragment.newInstance(this, null)
        fragment.show(supportFragmentManager, TimePickerFragment.TAG)
    }

    override fun getSelectedTime(item: String?) {
        if (modelTest.from == AppConstant.START_TIME) {
            modelTest.startTime = item
        } else if (modelTest.from == AppConstant.END_TIME) {
            modelTest.endTime = item
        }
        if (modelTest.startTime != null && modelTest.endTime != null) {
            if (AppUtil.compareStartOrEndTime(modelTest.startTime, modelTest.endTime)) {
                updateStartEndTime()
            } else {
                AppUtil.showErrorDialog(
                    this,
                    getString(R.string.error),
                    "End time should be grater than Start Time."
                )
            }
        } else {
            updateStartEndTime()
        }
    }

    fun updateStartEndTime() {
        if (modelTest.startTime != null) {
            startTime_tv!!.text = modelTest.startTime
        }
        if (modelTest.endTime != null) {
            endTime_tv!!.text = modelTest.endTime
        }
    }

    fun openBottomSheet() {
        myBottomSheet = SelectDaySheet.newInstance(this, null, modelTest, modelTestArrayList)
        myBottomSheet?.show(supportFragmentManager, SelectDaySheet.TAG)
    }

    override fun onItemClick(item: String?) {
        myBottomSheet!!.dismiss()
        chipGroup!!.removeAllViews()
        for (chip in modelTest.days) {
            createDynamically(chip)
        }
    }

    fun createDynamically(chips: String?) {
        val tomatoChip = getChip(chips)
        chipGroup!!.addView(tomatoChip)
    }

    private fun getChip(text: String?): Chip {
        val chip = Chip(this)
        chip.setChipDrawable(ChipDrawable.createFromResource(this, R.xml.chip))
        val paddingDp =
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10f, resources.displayMetrics)
                .toInt()
        chip.setPadding(paddingDp, paddingDp, paddingDp, paddingDp)
        chip.text = text
        chip.setOnCloseIconClickListener {
            chipGroup!!.removeView(chip)
            for (chipName in modelTest.days) {
                if (chipName == chip.text.toString()) {
                    modelTest.days.remove(chipName)
                    break
                }
            }
        }
        return chip
    }

    fun validate(): Boolean {
        val description = description_tv!!.text.toString()
        modelTest.description = description
        if (modelTest.days.size < 1) {
            AppUtil.showErrorDialog(this, this.getString(R.string.error), "Please select days.")
            return false
        }
        if (TextUtils.isEmpty(modelTest.startTime)) {
            AppUtil.showErrorDialog(this, this.getString(R.string.error), "Select start Time.")
            return false
        }
        if (TextUtils.isEmpty(modelTest.endTime)) {
            AppUtil.showErrorDialog(this, this.getString(R.string.error), "Select end Time.")
            return false
        }
        if (TextUtils.isEmpty(modelTest.description)) {
            AppUtil.showErrorDialog(
                this,
                this.getString(R.string.error),
                "Enter drink description."
            )
            return false
        }
        return true
    }

    fun goForAddMenuScheduleService() {
        val commonApi = CommonAPI(this)
        if (AppUtil.isNetworkAvailable(this)) {
            if (validate()) {
                if (!isEdit) {
                    /*in case of add details*/
                    commonApi.addMenuScheduleService(
                        modelTest,
                        businessId
                    ) { msg -> showSuccessDialog(msg) }
                } else {
                    /*in case of edit details*/
                    commonApi.updateScheduleDetailsService(
                        modelTest,
                        type
                    ) { msg -> showSuccessDialog(msg) }
                }
            }
        } else {
            AppUtil.showConnectionError(this)
        }
    }

    fun showSuccessDialog(msg: String?) {
        CustomSuccessDialog(this, getString(R.string.success), msg) { finish() }.show()
    }
}