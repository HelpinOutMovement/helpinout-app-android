package com.triline.billionlights.view.activity

import android.os.Bundle
import android.os.SystemClock
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import com.triline.billionlights.R
import com.triline.billionlights.model.database.entity.LanguageItem
import com.triline.billionlights.utils.DOUBLE_CLICK_TIME
import com.triline.billionlights.utils.INSTRUCTION_STEP
import com.triline.billionlights.utils.changeAppLanguage
import com.triline.billionlights.view.adapters.LanguageChooserAdapter
import com.triline.billionlights.view.view.ItemOffsetDecoration
import com.triline.billionlights.viewmodel.HomeViewModel
import kotlinx.android.synthetic.main.activity_help.*
import org.jetbrains.anko.startActivity


class LanguageChooserActivity : BaseActivity() {

    private var languageList = ArrayList<LanguageItem>()
    private lateinit var languageAdapter: LanguageChooserAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mRecyclerView
    }

    private val mRecyclerView by lazy {
        recycler_view.itemAnimator = DefaultItemAnimator()
        recycler_view.setHasFixedSize(true)

        languageAdapter =
            LanguageChooserAdapter(languageList, onItemClick = { item -> onItemClick(item) })
        val itemDecorator = ItemOffsetDecoration(this@LanguageChooserActivity, R.dimen.item_offset)
        recycler_view.addItemDecoration(itemDecorator)
        recycler_view.adapter = languageAdapter
        loadLanguageList()
    }

    @Synchronized
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
        preferencesService.step = INSTRUCTION_STEP
        changeAppLanguage(item.code)
        startActivity<InstructionActivity>()
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        finishWithFade()
    }

    override fun getLayout(): Int {
        return R.layout.activity_language_choose
    }
}