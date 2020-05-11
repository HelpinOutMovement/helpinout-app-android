package org.helpinout.billonlights.view.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.webkit.*
import android.widget.ProgressBar
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_webview.*
import org.helpinout.billonlights.R
import org.helpinout.billonlights.utils.*
import java.net.URLEncoder


class WebViewActivity : BaseActivity() {

    var uploadMessage: ValueCallback<Array<Uri>>? = null
    var mUploadMessage: ValueCallback<Uri>? = null
    var REQUEST_SELECT_FILE = 100
    var FILECHOOSER_RESULTCODE = 1

    @SuppressLint("SetTextI18n", "SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.title = intent.getStringExtra(TITLE)

        val url = intent.getStringExtra(WEB_URL)!!
        webView.webViewClient = AppWebViewClients(progress)
        webView.webChromeClient = MyWebChromeClient()
        webView.loadUrl(url)
        val webSettings = webView.settings
        webSettings.javaScriptEnabled = true
        webSettings.domStorageEnabled = true
        webSettings.setPluginState(WebSettings.PluginState.ON)
        webSettings.allowContentAccess = true
        webSettings.allowFileAccess = true
    }

    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }

    inner class AppWebViewClients(private val progressBar: ProgressBar) : WebViewClient() {

        init {
            progressBar.show()
        }

        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            view.loadUrl(url)
            return true
        }

        override fun onPageFinished(view: WebView, url: String) {
            super.onPageFinished(view, url)
            val handler = Handler()
            handler.postDelayed({
                try {
                    progressBar.hide()
                } catch (e: Exception) {

                }
            }, 100)
        }
    }


    public override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (requestCode == REQUEST_SELECT_FILE) {
                if (uploadMessage == null) return
                uploadMessage!!.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, intent))
                uploadMessage = null
            }
        } else if (requestCode == FILECHOOSER_RESULTCODE) {
            if (null == mUploadMessage) return
            val result = if (intent == null || resultCode != Activity.RESULT_OK) null else intent.data
            mUploadMessage!!.onReceiveValue(result)
            mUploadMessage = null
        } else toastError("Failed to Upload Image")
    }

    inner class MyWebChromeClient : WebChromeClient() {

        // For Android 3.0+
        fun openFileChooser(uploadMsg: ValueCallback<Uri>, acceptType: String) {
            mUploadMessage = uploadMsg
            val i = Intent(Intent.ACTION_GET_CONTENT)
            i.addCategory(Intent.CATEGORY_OPENABLE)
            i.type = "image/*"
            startActivityForResult(Intent.createChooser(i, "File Browser"), FILECHOOSER_RESULTCODE)
        }

        //For Android 4.1+ only
        protected fun openFileChooser(uploadMsg: ValueCallback<Uri>, acceptType: String, capture: String) {
            mUploadMessage = uploadMsg
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "image/*"
            startActivityForResult(Intent.createChooser(intent, "File Browser"), FILECHOOSER_RESULTCODE)
        }

        protected fun openFileChooser(uploadMsg: ValueCallback<Uri>) {
            mUploadMessage = uploadMsg
            val i = Intent(Intent.ACTION_GET_CONTENT)
            i.addCategory(Intent.CATEGORY_OPENABLE)
            i.type = "image/*"
            startActivityForResult(Intent.createChooser(i, "File Chooser"), FILECHOOSER_RESULTCODE)
        }

        override fun onShowFileChooser(mWebView: WebView, filePathCallback: ValueCallback<Array<Uri>>, fileChooserParams: WebChromeClient.FileChooserParams): Boolean {
            if (uploadMessage != null) {
                uploadMessage!!.onReceiveValue(null)
                uploadMessage = null
            }

            uploadMessage = filePathCallback
            var intent: Intent? = null
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                intent = fileChooserParams.createIntent()
            }
            try {
                startActivityForResult(intent, REQUEST_SELECT_FILE)
            } catch (e: ActivityNotFoundException) {
                uploadMessage = null
                Toast.makeText(applicationContext, "Cannot Open File Chooser", Toast.LENGTH_LONG).show()
                return false
            }

            return true
        }
    }

    override fun getLayout(): Int {
        return R.layout.activity_webview
    }
}
