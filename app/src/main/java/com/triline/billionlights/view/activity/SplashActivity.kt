package com.triline.billionlights.view.activity

import android.content.pm.PackageInfo
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import com.triline.billionlights.BuildConfig.DEBUG
import com.triline.billionlights.R
import com.triline.billionlights.utils.INSTRUCTION_STEP
import com.triline.billionlights.utils.LANGUAGE_STEP
import com.triline.billionlights.utils.LOGIN_STEP
import com.triline.billionlights.utils.REGISTRATION_STEP
import org.jetbrains.anko.startActivity

class SplashActivity : BaseActivity() {

    private var mDelayHandler: Handler? = null
    private val delay: Long = if (DEBUG) 200 else 2000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferencesService.imeiNumber = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        try {
            val pInfo: PackageInfo = packageManager.getPackageInfo(packageName, 0)
            preferencesService.appVersion = pInfo.versionName
        } catch (e: Exception) {
        }
        mDelayHandler = Handler()
        mDelayHandler!!.postDelayed(mRunnable, delay)
    }

    override fun getLayout(): Int {
        return R.layout.activity_splash
    }

    private val mRunnable: Runnable = Runnable {
        if (!isFinishing) {
            when (preferencesService.step) {
                LANGUAGE_STEP -> {
                    startActivity<LanguageChooserActivity>()
                }
                INSTRUCTION_STEP -> {
                    startActivity<InstructionActivity>()
                }
                LOGIN_STEP -> {
                    startActivity<LoginActivity>()
                }
                REGISTRATION_STEP -> {
                    startActivity<RegistrationActivity>()
                }
                else -> {
                    startActivity<HomeActivity>()
                }
            }
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            finish()
        }
    }

    public override fun onDestroy() {
        if (mDelayHandler != null) {
            mDelayHandler!!.removeCallbacks(mRunnable)
        }
        super.onDestroy()
    }

    override fun onBackPressed() {
        if (mDelayHandler != null) {
            mDelayHandler!!.removeCallbacks(mRunnable)
        }
        super.onBackPressed()
    }
}