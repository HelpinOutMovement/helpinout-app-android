package org.helpinout.billonlights.view.fragments

import androidx.fragment.app.Fragment

abstract class BaseFragment : Fragment() {
    var mLastClickTime: Long = 0
    val activityResult = 33

}