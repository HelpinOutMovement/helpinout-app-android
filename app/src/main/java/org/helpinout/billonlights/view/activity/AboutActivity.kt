package org.helpinout.billonlights.view.activity

import android.os.Bundle
import kotlinx.android.synthetic.main.activity_about.*
import org.helpinout.billonlights.BuildConfig
import org.helpinout.billonlights.R
import org.helpinout.billonlights.utils.getInstallTime

class AboutActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setTitle(R.string.about)
        tvAppVersion.text = getString(R.string.label_app_version, BuildConfig.VERSION_NAME)
        tvVersionDate.text = getString(R.string.version_date, BuildConfig.RELEASE_DATE)
        tvInstallDate.text = getString(R.string.app_install_date, getInstallTime())
    }


    override fun getLayout(): Int {
        return R.layout.activity_about
    }
}
