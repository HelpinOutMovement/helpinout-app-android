package com.triline.billionlights.view.activity

import android.os.Bundle
import com.triline.billionlights.R

class PeopleHelpActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initTitle()
    }

    override fun getLayout(): Int {
        return R.layout.activity_people_help
    }

}
