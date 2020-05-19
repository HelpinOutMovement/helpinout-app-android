package org.helpinout.billonlights.view.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import kotlinx.android.synthetic.main.activity_help.*
import org.helpinout.billonlights.R
import org.helpinout.billonlights.model.database.entity.LanguageItem
import org.helpinout.billonlights.utils.DOUBLE_CLICK_TIME
import org.helpinout.billonlights.utils.UPDATE_LANGAUGE
import org.helpinout.billonlights.utils.toastSuccess
import org.helpinout.billonlights.utils.visibleIf
import org.helpinout.billonlights.view.adapters.LanguageChooserAdapter
import org.helpinout.billonlights.view.view.ItemOffsetDecoration
import org.helpinout.billonlights.viewmodel.HomeViewModel
import org.jetbrains.anko.startActivity


class LanguageChooserActivity : BaseActivity() {

    private var isUpdate: Boolean = false
    private var languageList = ArrayList<LanguageItem>()
    private lateinit var languageAdapter: LanguageChooserAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isUpdate = intent.getBooleanExtra(UPDATE_LANGAUGE, false)
        layout_toolbar.visibleIf(isUpdate)
        mRecyclerView
    }

    private val mRecyclerView by lazy {
        recycler_view.itemAnimator = DefaultItemAnimator()
        recycler_view.setHasFixedSize(true)

        languageAdapter = LanguageChooserAdapter(languageList, onItemClick = { item -> onItemClick(item) })
        val itemDecorator = ItemOffsetDecoration(this@LanguageChooserActivity, R.dimen.item_offset)
        recycler_view.addItemDecoration(itemDecorator)
        recycler_view.adapter = languageAdapter
        loadLanguageList()
    }

    private fun loadLanguageList() {
        val viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        viewModel.getLanguageItems(this).observe(this, Observer { list ->
            list?.let {
                languageList.clear()
                languageList.addAll(list)
            }
            languageAdapter.notifyDataSetChanged()
        })
    }

    private fun onItemClick(item: LanguageItem) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < DOUBLE_CLICK_TIME) {
            return
        }
        mLastClickTime = SystemClock.elapsedRealtime()
        preferencesService.defaultLanguage = item.code
        if (isUpdate) {
            toastSuccess(R.string.language_update_success)
            val returnIntent = Intent()
            setResult(Activity.RESULT_OK, returnIntent)
            finishWithFade()
        } else {
            startActivity<InstructionActivity>()
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            finishWithFade()
        }
    }

    override fun getLayout(): Int {
        return R.layout.activity_language_choose
    }
}