package org.helpinout.billonlights.view.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import org.helpinout.billonlights.R
import org.helpinout.billonlights.model.BillionLightsApplication
import org.helpinout.billonlights.model.dagger.PreferencesService
import org.helpinout.billonlights.model.database.entity.OfferHelpItem
import org.helpinout.billonlights.utils.*
import org.jetbrains.anko.startActivityForResult
import javax.inject.Inject

abstract class BaseActivity : AppCompatActivity() {
    var helpType: Int = 0
    var toolbar: Toolbar? = null
    var mLastClickTime: Long = 0
    val noClickResultCode = 56

    @Inject
    lateinit var preferencesService: PreferencesService

    override fun onCreate(savedInstanceState: Bundle?) {

        helpType = intent.getIntExtra(HELP_TYPE, 0)
        if (helpType == HELP_TYPE_OFFER) setTheme(R.style.OfferTheme)
        (application as BillionLightsApplication).getAppComponent().inject(this)
        super.onCreate(savedInstanceState)
        changeAppLanguage(preferencesService.defaultLanguage)
        setContentView(getLayout())
        toolbar = findViewById(R.id.toolbar)
        if (toolbar != null) {
            setSupportActionBar(toolbar)
            val actionbar = supportActionBar
            val isUpdateProfile = intent.getBooleanExtra(UPDATE_PROFILE, false)
            val isUpdateLanguage = intent.getBooleanExtra(UPDATE_LANGAUGE, false)
            if (actionbar != null && (this !is LanguageChooserActivity || isUpdateLanguage) && (this !is HomeActivity || isUpdateProfile)) {
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
            HELP_TYPE_REQUEST -> {
                supportActionBar?.setTitle(R.string.toolbar_need_help_with)
                weCanPay?.setText(R.string.we_can_pay)
                weCanNotPay?.setText(R.string.we_can_not_pay)
            }
            HELP_TYPE_OFFER -> {
                supportActionBar?.setTitle(R.string.toolbar_offer_help_with)
                tvAvailability?.show()
                edtCondition?.show()
                weCanPay?.setText(R.string.we_change)
                weCanNotPay?.setText(R.string.for_free)
            }
        }
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

    fun onItemClick(item: OfferHelpItem) {
        when (item.type) {
            CATEGORY_FOOD -> {
                startActivityForResult<FoodHelpActivity>(noClickResultCode, HELP_TYPE to helpType, CATEGORY_TYPE to CATEGORY_FOOD)
            }
            CATEGORY_PEOPLE -> {
                startActivityForResult<PeopleHelpActivity>(noClickResultCode, HELP_TYPE to helpType)
            }
            CATEGORY_SHELTER -> {
                startActivityForResult<FoodHelpActivity>(noClickResultCode, HELP_TYPE to helpType, CATEGORY_TYPE to CATEGORY_SHELTER)
            }
            CATEGORY_MED_PPE -> {
                startActivityForResult<FoodHelpActivity>(noClickResultCode, HELP_TYPE to helpType, CATEGORY_TYPE to CATEGORY_MED_PPE)
            }
            CATEGORY_TESTING -> {
                startActivityForResult<FoodHelpActivity>(noClickResultCode, HELP_TYPE to helpType, CATEGORY_TYPE to CATEGORY_TESTING)
            }
            CATEGORY_MEDICINES -> {
                startActivityForResult<FoodHelpActivity>(noClickResultCode, HELP_TYPE to helpType, CATEGORY_TYPE to CATEGORY_MEDICINES)
            }
            CATEGORY_AMBULANCE -> {
                startActivityForResult<AmbulanceHelpActivity>(noClickResultCode, HELP_TYPE to helpType)
            }
            CATEGORY_MEDICAL_EQUIPMENT -> {
                startActivityForResult<FoodHelpActivity>(noClickResultCode, HELP_TYPE to helpType, CATEGORY_TYPE to CATEGORY_MEDICAL_EQUIPMENT)
            }
            CATEGORY_OTHERS -> {
                startActivityForResult<FoodHelpActivity>(noClickResultCode, HELP_TYPE to helpType, CATEGORY_TYPE to CATEGORY_OTHERS)
            }
        }
        overridePendingTransition(R.anim.enter, R.anim.exit)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data1: Intent?) {
        super.onActivityResult(requestCode, resultCode, data1)
        if (resultCode == Activity.RESULT_OK && requestCode == noClickResultCode) {
            val intent = Intent(baseContext!!, HomeActivity::class.java)
            intent.putExtra(SELECTED_INDEX, helpType)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
            overridePendingTransition(R.anim.enter, R.anim.exit)
            finish()
        }
    }

    fun onConfirmationNoClick(type: Int, uuid: String) {
        val intent = Intent(baseContext!!, HomeActivity::class.java)
        intent.putExtra(SELECTED_INDEX, helpType)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        overridePendingTransition(R.anim.enter, R.anim.exit)
        finishWithSlideAnimation()
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