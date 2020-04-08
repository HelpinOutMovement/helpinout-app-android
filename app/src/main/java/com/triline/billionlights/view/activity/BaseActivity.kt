package com.triline.billionlights.view.activity

import android.app.TimePickerDialog
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.triline.billionlights.R
import com.triline.billionlights.model.BillionLightsApplication
import com.triline.billionlights.model.dagger.PreferencesService
import com.triline.billionlights.utils.*
import java.util.*
import javax.inject.Inject

abstract class BaseActivity : AppCompatActivity() {
    var helpType: Int = 0
    var toolbar: Toolbar? = null
    var mLastClickTime: Long = 0

    @Inject
    lateinit var preferencesService: PreferencesService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        helpType = intent.getIntExtra(HELP_TYPE, 0)
        if (helpType == OFFER_HELP) setTheme(R.style.OfferTheme)
        (application as BillionLightsApplication).getAppComponent().inject(this)
        setContentView(getLayout())
        toolbar = findViewById(R.id.toolbar)
        if (toolbar != null) {
            setSupportActionBar(toolbar)
            val actionbar = supportActionBar
            if (actionbar != null && this !is LanguageChooserActivity && this !is HomeActivity) {
                actionbar.setDisplayHomeAsUpEnabled(true)
                actionbar.setHomeButtonEnabled(true)
            }
        }
    }

    fun initTitle() {
        val weCanPay = findViewById<Button>(R.id.we_can_pay)
        val weCanNotPay = findViewById<Button>(R.id.we_can_not_pay)
        val tvAvailability = findViewById<TextView>(R.id.tv_availability)
        val edtCondition = findViewById<TextView>(R.id.edt_conditions)
        when (intent.getIntExtra(HELP_TYPE, 0)) {
            NEED_HELP -> {
                supportActionBar?.setTitle(R.string.toolbar_need_help_with)
                weCanPay?.setBackgroundResource(R.drawable.primary_border_background)
                weCanPay?.setTextColor(resources.getColor(R.color.colorPrimary))
                weCanNotPay?.setBackgroundResource(R.drawable.primary_revert_border_background)
                weCanPay?.setText(R.string.we_can_pay)
                weCanNotPay?.setText(R.string.we_can_not_pay)
            }
            OFFER_HELP -> {
                supportActionBar?.setTitle(R.string.toolbar_offer_help_with)
                tvAvailability?.show()
                edtCondition?.show()
                edtCondition?.hint = getString(R.string.hint_conditions).fromHtml()
                weCanPay?.setBackgroundResource(R.drawable.accent_border_background)
                weCanPay?.setTextColor(resources.getColor(R.color.colorAccent))
                weCanNotPay?.setBackgroundResource(R.drawable.accent_revert_border_background)
                weCanPay?.setText(R.string.we_change)
                weCanNotPay?.setText(R.string.for_free)

            }
        }
    }


    private fun showTimePicker(tvTime: TextView) {
        val currentTime = Calendar.getInstance()
        val hour = currentTime.get(Calendar.HOUR_OF_DAY)
        val minute = currentTime.get(Calendar.MINUTE)
        val timePicker = TimePickerDialog(this, TimePickerDialog.OnTimeSetListener { picker, selectedHour, selectedMinute ->
            val cal = Calendar.getInstance()
            cal.set(Calendar.HOUR_OF_DAY, selectedHour)
            cal.set(Calendar.MINUTE, selectedMinute)
            tvTime.text = cal.time.time.displayTime()
        }, hour, minute, false)
        timePicker.setTitle(resources!!.getString(R.string.select_time))
        timePicker.show()
    }

    abstract fun getLayout(): Int


    override fun onBackPressed() {
        super.onBackPressed()
        if (this !is HomeActivity) finishWithSlideAnimation()
        else finish()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item!!.itemId == android.R.id.home) {
            if (this !is HomeActivity) finishWithSlideAnimation()
            else finish()
        }
        return super.onOptionsItemSelected(item)
    }

    fun updateTitle(title: String) {
        toolbar?.title = title
    }

    fun finishWithSlideAnimation() {
        finish()
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)

    }

    fun finishWithFade() {
        finish()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

}