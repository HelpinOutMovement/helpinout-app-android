package org.helpinout.billonlights.view.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.gson.Gson
import org.helpinout.billonlights.R
import org.helpinout.billonlights.model.BillionLightsApplication
import org.helpinout.billonlights.model.dagger.PreferencesService
import org.helpinout.billonlights.model.database.entity.OfferHelpItem
import org.helpinout.billonlights.model.database.entity.SuggestionRequest
import org.helpinout.billonlights.utils.*
import org.helpinout.billonlights.view.fragments.BottomSheetsRequestConfirmationFragment
import org.jetbrains.anko.startActivityForResult
import javax.inject.Inject

abstract class BaseActivity : AppCompatActivity() {
    var helpType: Int = 0
    var toolbar: Toolbar? = null
    var mLastClickTime: Long = 0
    val noClickResultCode = 56
    val showMapCode: Int = 43

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
                weCanPay?.setText(R.string.we_charge)
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

    fun onItemClick(item: OfferHelpItem, selfElse: Int) {
        when (item.type) {
            CATEGORY_FOOD, CATEGORY_SHELTER, CATEGORY_MED_PPE, CATEGORY_MEDICAL_EQUIPMENT, CATEGORY_MEDICINES, CATEGORY_TESTING, CATEGORY_OTHERS -> {
                startActivityForResult<FoodHelpActivity>(noClickResultCode, HELP_TYPE to helpType, CATEGORY_TYPE to item.type, SELF_ELSE to selfElse)
            }
            CATEGORY_MEDICAL_TRANSPORT, CATEGORY_MEDICAL_PAID_WORK, CATEGORY_MEDICAL_GIVEAWAYS, CATEGORY_MEDICAL_ANIMAL_SUPPORT, CATEGORY_MEDICAL_FRUITS_VEGETABLES, CATEGORY_MEDICAL_VOLUNTEERS, CATEGORY_AMBULANCE -> {
                startActivityForResult<AmbulanceHelpActivity>(noClickResultCode, HELP_TYPE to helpType, CATEGORY_TYPE to item.type, SELF_ELSE to selfElse)
            }
        }
        overridePendingTransition(R.anim.enter, R.anim.exit)
    }

    fun askForConfirmation(activity_uuid: String, suggestionData: SuggestionRequest) {
        val deleteDialog = BottomSheetsRequestConfirmationFragment(helpType, activity_uuid, suggestionData, { uuid, data -> onYesClick(uuid, data) }, { type, uuid -> onConfirmationNoClick(type, uuid) })
        deleteDialog.show(supportFragmentManager, null)
    }

    //this is shown when send request after the bottom dialog is shown
    private fun onYesClick(suggestionData: SuggestionRequest, activity_uuid: String) {
        suggestionData.activity_uuid = activity_uuid
        val suggestionDataAsString = Gson().toJson(suggestionData)
        startActivityForResult<HelpProviderRequestersActivity>(showMapCode, SUGGESTION_DATA to suggestionDataAsString, HELP_TYPE to helpType)
        overridePendingTransition(R.anim.enter, R.anim.exit)
        finishWithFade()
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