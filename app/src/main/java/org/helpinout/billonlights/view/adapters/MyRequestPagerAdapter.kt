package org.helpinout.billonlights.view.adapters

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import org.helpinout.billonlights.R
import org.helpinout.billonlights.utils.HELP_TYPE_REQUEST
import org.helpinout.billonlights.utils.OFFER_RECEIVED
import org.helpinout.billonlights.view.fragments.OffersReceivedFragment
import org.helpinout.billonlights.view.fragments.RequestSentFragment

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
                    requestSentFragment = RequestSentFragment.newInstance(HELP_TYPE_REQUEST)
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