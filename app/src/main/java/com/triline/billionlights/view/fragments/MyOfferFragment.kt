package com.triline.billionlights.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.triline.billionlights.R
import com.triline.billionlights.view.adapters.MyOffersPagerAdapter
import kotlinx.android.synthetic.main.fragment_my_offer.*

class MyOfferFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_my_offer, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setViewPager()
    }

    private fun setViewPager() {
        viewPager.adapter = MyOffersPagerAdapter(childFragmentManager, activity!!)
        tabs.setupWithViewPager(viewPager)
        viewPager.offscreenPageLimit = 2
    }
}