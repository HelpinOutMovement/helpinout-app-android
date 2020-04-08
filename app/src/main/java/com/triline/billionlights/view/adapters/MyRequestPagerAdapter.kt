package com.triline.billionlights.view.adapters

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.triline.billionlights.R
import com.triline.billionlights.utils.OFFER_RECEIVED
import com.triline.billionlights.utils.REQUEST_SENT
import com.triline.billionlights.view.fragments.OffersReceivedFragment
import com.triline.billionlights.view.fragments.RequestSentFragment

class MyRequestPagerAdapter(fm: FragmentManager, private val context: Context) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    private val numItems = 2
    private var offerReceivedFragment: OffersReceivedFragment? = null
    private var requestSentFragment: RequestSentFragment? = null

    override fun getItem(position: Int): Fragment {
        when (position) {
            0 -> {
                if (offerReceivedFragment == null) {
                    offerReceivedFragment = OffersReceivedFragment.newInstance(OFFER_RECEIVED)
                }
                return offerReceivedFragment!!
            }
            1 -> {
                if (requestSentFragment == null) {
                    requestSentFragment = RequestSentFragment.newInstance(REQUEST_SENT)
                }
                return requestSentFragment!!
            }
        }
        return OffersReceivedFragment.newInstance(OFFER_RECEIVED)
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            0 -> context.getString(R.string.offers_received)
            else -> context.getString(R.string.requests_sent)
        }
    }

    override fun getCount(): Int {
        return numItems
    }
}