package org.helpinout.billonlights.view.activity

import android.os.Bundle
import kotlinx.android.synthetic.main.activity_instruction.*
import org.helpinout.billonlights.R
import org.jetbrains.anko.startActivity

class InstructionActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        btnContinue.setOnClickListener {
            startActivity<LoginActivity>()
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            finishWithFade()
        }
    }

    override fun getLayout(): Int {
        return R.layout.activity_instruction
    }
}
