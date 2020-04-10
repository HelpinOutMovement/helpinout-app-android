package org.helpinout.billonlights.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_my_requests.*
import org.helpinout.billonlights.R
import org.helpinout.billonlights.view.adapters.MyRequestPagerAdapter

class MyRequestsFragment(val pagerIndex: Int) : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_my_requests, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setViewPager()
    }

    private fun setViewPager() {
        viewPager.adapter = MyRequestPagerAdapter(childFragmentManager, activity!!)
        tabs.setupWithViewPager(viewPager)
        viewPager.currentItem = pagerIndex
        viewPager.offscreenPageLimit = 2
    }
}