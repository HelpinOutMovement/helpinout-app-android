package org.helpinout.billonlights.view.view

import android.widget.AdapterView

abstract class SpinnerSelector : AdapterView.OnItemSelectedListener {
    override fun onNothingSelected(parent: AdapterView<*>?) {}
}