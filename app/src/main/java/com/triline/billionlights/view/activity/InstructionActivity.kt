package com.triline.billionlights.view.activity

import android.os.Bundle
import com.triline.billionlights.R
import com.triline.billionlights.utils.LOGIN_STEP
import kotlinx.android.synthetic.main.activity_instruction.*
import org.jetbrains.anko.startActivity

class InstructionActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        btnContinue.setOnClickListener {
            preferencesService.step = LOGIN_STEP
            startActivity<LoginActivity>()
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            finishWithFade()
        }
    }


    override fun getLayout(): Int {
        return R.layout.activity_instruction
    }
}
