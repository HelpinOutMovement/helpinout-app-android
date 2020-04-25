package org.helpinout.billonlights.view.activity

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.activity_help.*
import org.helpinout.billonlights.R
import org.helpinout.billonlights.model.database.entity.OfferHelpItem
import org.helpinout.billonlights.view.adapters.AskForHelpAdapter
import org.helpinout.billonlights.view.view.ItemOffsetDecoration
import org.helpinout.billonlights.viewmodel.HomeViewModel


class AskForHelpActivity : BaseActivity() {

    private var itemList = ArrayList<OfferHelpItem>()
    private lateinit var adapter: AskForHelpAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setTitle(R.string.toolbar_need_help_with)
        mRecyclerView
    }

    private val mRecyclerView by lazy {
        recycler_view.itemAnimator = DefaultItemAnimator()
        recycler_view.setHasFixedSize(true)
        val layoutManager = GridLayoutManager(this@AskForHelpActivity, 2)
        recycler_view.layoutManager = layoutManager

        adapter = AskForHelpAdapter(itemList, onItemClick = { item -> onItemClick(item) })
        val itemDecorator = ItemOffsetDecoration(this@AskForHelpActivity, R.dimen.item_offset)
        recycler_view.addItemDecoration(itemDecorator)
        recycler_view.adapter = adapter
        loadAskForHelpList()
    }

    @Synchronized
    private fun loadAskForHelpList() {
        val viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        viewModel.getAskForHelpItems(this).observe(this, Observer { list ->
            progress_bar.hide()
            list?.let {
                itemList.clear()
                itemList.addAll(list)
            }
            adapter.notifyDataSetChanged()
        })
    }

    override fun getLayout(): Int {
        return R.layout.activity_help
    }
}