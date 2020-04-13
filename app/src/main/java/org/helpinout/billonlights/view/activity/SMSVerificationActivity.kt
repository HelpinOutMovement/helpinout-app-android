package org.helpinout.billonlights.view.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.tasks.TaskExecutors
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.android.synthetic.main.activity_sms_verification.*
import org.helpinout.billonlights.R
import org.helpinout.billonlights.model.database.entity.LoginResponse
import org.helpinout.billonlights.model.database.entity.Registration
import org.helpinout.billonlights.utils.*
import org.helpinout.billonlights.viewmodel.LoginRegistrationViewModel
import org.jetbrains.anko.indeterminateProgressDialog
import org.jetbrains.anko.startActivity
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.concurrent.TimeUnit

class SMSVerificationActivity : BaseActivity() {

    private var timer: CountDownTimer? = null
    private var resendToken: String? = null
    private var mAuth: FirebaseAuth? = null
    private val timerTime = 60//sec

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mAuth = FirebaseAuth.getInstance()

        sendVerificationCode(preferencesService.countryCode + preferencesService.mobileNumber)
        btn_verify.setOnClickListener {
            if (edt_otp.text.toString().isNotEmpty()) {
                verifyCode(edt_otp.text.toString())
            } else {
                toastError(R.string.toast_error_please_enter_otp)
            }
        }
    }

    private fun sendVerificationCode(number: String) {
        if (number == ALLOW_NUMBER) {
            checkIfNotRegistered()
        } else PhoneAuthProvider.getInstance().verifyPhoneNumber(number, 60, TimeUnit.SECONDS, TaskExecutors.MAIN_THREAD, mCallBack)

        tv_message.text = getString(R.string.otp_description, number)
    }

    private val mCallBack = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onCodeSent(s: String, forceResendingToken: PhoneAuthProvider.ForceResendingToken) {
            super.onCodeSent(s, forceResendingToken)
            toastSuccess(R.string.otp_send_success)
            startTimer()
            resendToken = s
        }

        override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
            val code = phoneAuthCredential.smsCode
            if (code != null) {
                edt_otp!!.setText(code)
                verifyCode(code)
            } else {
                checkIfNotRegistered()
            }
        }

        override fun onVerificationFailed(e: FirebaseException) {
            toastError(e.message + "")
            btn_verify.isEnabled = true
        }
    }

    private fun startTimer() {
        timer = object : CountDownTimer(timerTime * 1000L, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                try {
                    val totalSec = millisUntilFinished / 1000
                    val seconds = (totalSec % 60).toInt()
                    val minutes = (totalSec / 60).toInt()
                    val f: NumberFormat = DecimalFormat("00")
                    tv_timer.text = f.format(minutes) + ":" + f.format(seconds)
                } catch (e: Exception) {
                }
            }
            override fun onFinish() {
                try {
                    tv_timer.text = "00:00"
                } catch (e: Exception) {
                }
            }
        }
        timer?.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel()
    }

    private fun verifyCode(code: String?) {
        if (resendToken != null) {
            val credential = PhoneAuthProvider.getCredential(resendToken!!, code!!)
            signInWithCredential(credential)
        }
    }

    private fun signInWithCredential(credential: PhoneAuthCredential) {
        mAuth!!.signInWithCredential(credential).addOnCompleteListener { task ->
            btn_verify.isEnabled = true
            if (task.isSuccessful) {
                preferencesService.step = REGISTRATION_STEP
                checkIfNotRegistered()
            } else {
                toastError(task.exception!!.message + "")
            }
        }
    }

    private fun checkIfNotRegistered() {
        val dialog = indeterminateProgressDialog(R.string.alert_msg_please_wait)
        dialog.setCancelable(false)
        dialog.show()
        val viewModel = ViewModelProvider(this).get(LoginRegistrationViewModel::class.java)
        viewModel.getLoginResult(preferencesService.countryCode, preferencesService.mobileNumber).observe(this, Observer {
            dialog.dismiss()
            if (it.first != null) {
                val data = (it.first as LoginResponse).data
                if (data == null) {//Registration required
                    preferencesService.step = REGISTRATION_STEP
                    startActivity<RegistrationActivity>()
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                    val returnIntent = Intent()
                    setResult(Activity.RESULT_OK, returnIntent)
                    finishWithFade()
                } else {//already registered
                    preferencesService.appId = data.app_id ?: ""
                    data.user_detail?.let {
                        saveRegistrationToDatabase(it)
                    }
                }
            } else {
                if (isNetworkAvailable()) toastError(it.second)
                else {
                    toastError(R.string.toast_error_internet_issue)
                }
            }
        })
    }

    private fun saveRegistrationToDatabase(registration: Registration) {
        val viewModel = ViewModelProvider(this).get(LoginRegistrationViewModel::class.java)
        viewModel.saveRegistration(registration).observe(this, Observer {
            if (it) {
                preferencesService.step = HOME_STEP
                startActivity<HomeActivity>()
                overridePendingTransition(R.anim.enter, R.anim.exit)
                val returnIntent = Intent()
                setResult(Activity.RESULT_OK, returnIntent)
                finishWithFade()
            }
        })
    }

    override fun getLayout(): Int {
        return R.layout.activity_sms_verification
    }
}
