package com.fivelive.app.activity


import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.fivelive.app.Model.FilterList
import com.fivelive.app.Model.ModelTest
import com.fivelive.app.R
import com.fivelive.app.bottomSheet.QuickFilterBottomSheet
import com.fivelive.app.bottomSheet.QuickFilterBottomSheet.FilterClickListener
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

class AddHappyHourDetailsActivity : AppCompatActivity(), SelectDaySheet.ItemClickListener,
    View.OnClickListener, TimeCallbackListener, FilterClickListener {
    var myBottomSheet: SelectDaySheet? = null
    var mFilterBottomSheet: QuickFilterBottomSheet? = null
    var filterLists: MutableList<FilterList> = ArrayList()
    var modelTest = ModelTest()
    var businessId: String? = null
    var scheduleId: String? = null
    var type: String? = null
    var selectDayTextView: TextView? = null
    var select_quick_filter_tv: TextView? = null
    var startTime_tv: TextView? = null
    var endTime_tv: TextView? = null
    var drink_et: TextView? = null
    var food_et: TextView? = null
    var heading_tv: TextView? = null
    var chipGroup: ChipGroup? = null
    var qfChipGroup: ChipGroup? = null
    var addItemButton: Button? = null
    var backImageView: ImageView? = null
    var isEdit = false
    var modelTestArrayList: ArrayList<ModelTest>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_happy_hour_details)
        initView()
        dataFromIntent
    }

    // Type = 1 for HappyHours Details
    // Type = 2 for Reverse HappyHours details
    // Type = 3 for Brunch Details
    private val dataFromIntent: Unit
        private get() {
            // Type = 1 for HappyHours Details
            // Type = 2 for Reverse HappyHours details
            // Type = 3 for Brunch Details
            businessId = intent.getStringExtra(AppConstant.BUSINESS_ID)
            scheduleId = intent.getStringExtra(AppConstant.ID)
            type = intent.getStringExtra(AppConstant.TYPE)
            isEdit = intent.getBooleanExtra(AppConstant.EDIT, false)
            modelTestArrayList = intent.getParcelableArrayListExtra(AppConstant.MODEL_TEST_LIST)
            modelTest.type = type
            changeHeadingText(type)
            if (type == "1") {
                select_quick_filter_tv!!.visibility = View.VISIBLE
            } else {
                select_quick_filter_tv!!.visibility = View.GONE
            }
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

        // add quick filter chips
        for (chip in modelTest.hhFilterList) {
            createQuickFilterChips(chip)
        }
        startTime_tv!!.text = modelTest.startTime
        endTime_tv!!.text = modelTest.endTime
        food_et!!.text = modelTest.food
        drink_et!!.text = modelTest.drink
        addItemButton!!.setText(R.string.update_item)
    }

    private fun initView() {
        backImageView = findViewById(R.id.backImageView)
        selectDayTextView = findViewById(R.id.selectDayTextView)
        select_quick_filter_tv = findViewById(R.id.select_quick_filter_tv)
        chipGroup = findViewById<View>(R.id.chipGroup) as ChipGroup
        qfChipGroup = findViewById<View>(R.id.qfChipGroup) as ChipGroup
        startTime_tv = findViewById(R.id.startTime_tv)
        endTime_tv = findViewById(R.id.endTime_tv)
        food_et = findViewById(R.id.food_et)
        drink_et = findViewById(R.id.drink_et)
        heading_tv = findViewById(R.id.heading_tv)
        addItemButton = findViewById(R.id.addItemButton)
        selectDayTextView?.setOnClickListener(this)
        select_quick_filter_tv?.setOnClickListener(this)
        startTime_tv?.setOnClickListener(this)
        endTime_tv?.setOnClickListener(this)
        addItemButton?.setOnClickListener(this)
        backImageView?.setOnClickListener(this)
    }

    fun changeHeadingText(type: String?) {
        when (type) {
            "1" -> {
                //here we are getting quick filter list because quick filter adding only happy hours
                setUpQuickFilter()
                heading_tv!!.text = "Set Happy Hours Timing and Description"
            }
            "2" -> heading_tv!!.text = "Set Reverse Happy Hours Timing and Description"
            "3" -> heading_tv!!.text = "Set Brunch Timing and Description"
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.selectDayTextView -> openBottomSheet()
            R.id.select_quick_filter_tv -> openQuickFilterBottomSheet()
            R.id.startTime_tv -> openTimePicker(AppConstant.START_TIME)
            R.id.endTime_tv -> openTimePicker(AppConstant.END_TIME)
            R.id.addItemButton -> goForAddMenuScheduleService()
            R.id.backImageView -> finish()
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
        myBottomSheet = SelectDaySheet.newInstance(
            this@AddHappyHourDetailsActivity,
            null, modelTest, modelTestArrayList
        )
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
        chip.elevation = 8.0f
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
        val drink = drink_et!!.text.toString()
        val food = food_et!!.text.toString()
        modelTest.drink = drink
        modelTest.food = food
        //   addQfInModelTest();// add selected quick filter in modelTest list
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
        if (TextUtils.isEmpty(modelTest.drink)) {
            AppUtil.showErrorDialog(
                this,
                this.getString(R.string.error),
                "Enter drink description."
            )
            return false
        }
        if (TextUtils.isEmpty(modelTest.food)) {
            AppUtil.showErrorDialog(this, this.getString(R.string.error), "Enter food description.")
            return false
        }
        if (type == "1") {
            if (modelTest.hhFilterList.size < 1) {
                AppUtil.showErrorDialog(
                    this,
                    this.getString(R.string.error),
                    "Please select at least one Quick Filter."
                )
                return false
            }
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
        CustomSuccessDialog(this, getString(R.string.success), msg) {

            finish()


        }.show()
    }

    fun setUpQuickFilter() {
        dispatchToGetQuickFilterList()
    }

    fun dispatchToGetQuickFilterList() {
        if (AppUtil.isNetworkAvailable(this)) {
            val commonApi = CommonAPI(this)
            commonApi.quickFilterListService { `object` ->
                filterLists.addAll(`object`.filterList)
                Log.d("MyTag", "onSuccess: ")

            }
        } else {
            AppUtil.showConnectionError(this)
        }
    }

    fun openQuickFilterBottomSheet() {
        mFilterBottomSheet = QuickFilterBottomSheet.newInstance(
            this@AddHappyHourDetailsActivity,
            null,
            modelTest,
            filterLists
        )
        mFilterBottomSheet?.show(supportFragmentManager, QuickFilterBottomSheet.TAG)
    }

    override fun onFilterClickListener(item: String?) {
        mFilterBottomSheet!!.dismiss()
        qfChipGroup!!.removeAllViews()
        for (str in modelTest.hhFilterList) {
            createQuickFilterChips(str)
        }
    }

    fun createQuickFilterChips(chips: String?) {
        val tomatoChip = getChipForQuickFilter(chips)
        qfChipGroup!!.addView(tomatoChip)
    }

    private fun getChipForQuickFilter(text: String?): Chip {
        val chip = Chip(this)
        chip.setChipDrawable(ChipDrawable.createFromResource(this, R.xml.chip))
        val paddingDp =
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10f, resources.displayMetrics)
                .toInt()
        chip.setPadding(paddingDp, paddingDp, paddingDp, paddingDp)
        chip.elevation = 8.0f
        chip.text = text
        chip.setOnCloseIconClickListener {
            qfChipGroup!!.removeView(chip)
            for (str in modelTest.hhFilterList) {
                if (str == chip.text.toString()) {
                    modelTest.hhFilterList.remove(str)
                    break
                }
            }
        }
        return chip
    }
}