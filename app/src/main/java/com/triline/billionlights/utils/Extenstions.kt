package com.triline.billionlights.utils

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.Rect
import android.location.Address
import android.location.Geocoder
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.telephony.TelephonyManager
import android.text.Editable
import android.text.Html
import android.text.Spanned
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.triline.billionlights.R
import com.triline.billionlights.view.activity.RegistrationActivity
import es.dmoral.toasty.Toasty
import org.jetbrains.anko.clearTop
import org.jetbrains.anko.configuration
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.newTask
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

@BindingAdapter("load_image")
fun ImageView.setImage(id: Int) {
    Glide.with(context).load(id).into(this)
}

fun Exception.getStringException(): String {
    val writer = StringWriter()
    this.printStackTrace(PrintWriter(writer))
    return writer.toString()
}

fun Long.displayTime(): String {
    return try {
        val formatter = SimpleDateFormat("hh:mma", Locale.getDefault())
        formatter.format(this)
    } catch (e: Exception) {
        ""
    }
}

fun Context.isSimInserted(): Boolean {
    val tm: TelephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager //gets the current TelephonyManager
    return !(tm.simState === TelephonyManager.SIM_STATE_ABSENT)
}

fun Spinner.spinnerHideKeyboard() {
    setOnTouchListener { _, _ ->
        hideKeyboard()
        false
    }
}

fun Activity.getAddress(latitude: Double, longitude: Double): String {
    return try {
        val geocoder = Geocoder(this, Locale.getDefault())
        val addresses: List<Address>
        addresses = geocoder.getFromLocation(latitude, longitude, 1) // Here 1 represent max location result to returned, by documents it recommended 1 to 5
        addresses[0].getAddressLine(0)
    } catch (e: Exception) {
        "$latitude,$longitude"
    }
}

fun Activity.getUuid(): String {
    return UUID.randomUUID().toString()
}

fun Activity.logout() {
//    val app_id = Hawk.get(APP_ID, 0)
//    val name = Hawk.get(LOGIN_NAME, "")
//    Hawk.deleteAll()
//    Hawk.put(APP_ID, app_id)
//    Hawk.put(LOGIN_NAME, name)
    //Hawk.delete(IS_LOGIN)

    var pInfo: PackageInfo? = null
    try {
        pInfo = packageManager.getPackageInfo(packageName, 0)
    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
    }
    val verName = pInfo!!.versionName
    //Hawk.put(VERSION_NAME, verName)
    startActivity(intentFor<RegistrationActivity>("id" to 5).clearTop())
    overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    finish()
}

fun Activity.changeAppLanguage(languageCode: String) {
    val dm = resources.displayMetrics
    val conf = resources.configuration
    val locale = Locale(languageCode.toLowerCase())
    conf.locale = locale
    configuration.setLayoutDirection(locale)
    resources.updateConfiguration(conf, dm)
}

fun Context.currentDateTime(): String {
    val msTime = System.currentTimeMillis()
    val curDateTime = Date(msTime)
    val formatter = SimpleDateFormat(SERVER_DATE_FORMAT)
    return formatter.format(curDateTime)
}

fun Fragment.currentDateTime(): String {
    val msTime = System.currentTimeMillis()
    val curDateTime = Date(msTime)
    val formatter = SimpleDateFormat(CALENDER_FORMAT)
    return formatter.format(curDateTime)
}


fun Activity.isNetworkAvailable(): Boolean {
    val connMgr = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
    val networkInfo = connMgr!!.activeNetworkInfo
    return networkInfo != null && networkInfo.isConnected
}

fun Context.hasLocationPermission(): Boolean {
    val permission = android.Manifest.permission.ACCESS_FINE_LOCATION
    val res = checkCallingOrSelfPermission(permission)
    return res == PackageManager.PERMISSION_GRANTED
}

fun Context.hasStoragePermission(): Boolean {
    val permission = android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    val res = checkCallingOrSelfPermission(permission)
    return res == PackageManager.PERMISSION_GRANTED
}

fun Context.hasCameraPermission(): Boolean {
    val permission = android.Manifest.permission.CAMERA
    val res = checkCallingOrSelfPermission(permission)
    return res == PackageManager.PERMISSION_GRANTED
}

fun Context.hasAudioPermission(): Boolean {
    val permission = android.Manifest.permission.RECORD_AUDIO
    val res = checkCallingOrSelfPermission(permission)
    return res == PackageManager.PERMISSION_GRANTED
}

fun Fragment.isNetworkAvailable(): Boolean {
    val connMgr = activity?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
    val networkInfo = connMgr!!.activeNetworkInfo
    return networkInfo != null && networkInfo.isConnected
}

fun Context.toast(messageId: Int, duration: Int = Toast.LENGTH_SHORT) {
    Toasty.normal(this, getString(messageId), duration).show()
}

fun Activity.getUriFromFile(file: File): Uri {
    if (Build.VERSION.SDK_INT < 24) {
        return Uri.fromFile(file)
    } else {
        return FileProvider.getUriForFile(this, "$packageName.provider", file)
    }
}

fun Context.toastError(messageId: Int, duration: Int = Toast.LENGTH_LONG) {
    Toasty.error(this, getString(messageId), duration, true).show()
}

fun Fragment.toastError(messageId: Int, duration: Int = Toast.LENGTH_SHORT) {
    Toasty.error(activity!!, getString(messageId), duration, true).show()
}

fun Context.toastError(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toasty.error(this, message, duration, true).show()
}

fun Fragment.toastError(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toasty.error(activity!!, message, duration, true).show()
}

fun Context.toastInfo(message: Int, duration: Int = Toast.LENGTH_SHORT) {
    Toasty.info(this, getString(message), duration, true).show()
}

fun Fragment.toastInfo(message: Int, duration: Int = Toast.LENGTH_SHORT) {
    Toasty.info(activity!!, getString(message), duration, true).show()
}

fun Context.toastInfo(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toasty.info(this, message, duration, true).show()
}

fun androidx.fragment.app.Fragment.toastInfo(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toasty.info(activity!!, message, duration, true).show()
}

fun Context.toastSuccess(messageId: Int, duration: Int = Toast.LENGTH_SHORT) {
    Toasty.success(this, getString(messageId, true), duration).show()
}

fun androidx.fragment.app.Fragment.toastSuccess(messageId: Int, duration: Int = Toast.LENGTH_SHORT) {
    Toasty.success(activity!!, getString(messageId, true), duration).show()
}

fun Context.toastSuccess(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toasty.success(this, message, duration, true).show()
}

fun androidx.fragment.app.Fragment.toastSuccess(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toasty.success(activity!!, message, duration, true).show()
}


fun Context.callPhoneNumber(phoneNumber: String) {
    val callIntent = Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneNumber, null))
    startActivity(callIntent.newTask())
}

fun Context.toast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toasty.normal(this, message, duration).show()
}

fun androidx.fragment.app.Fragment.toast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toasty.normal(activity!!, message, duration).show()
}


fun CharSequence.fromHtml(): Spanned {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        Html.fromHtml(this.toString(), Html.FROM_HTML_MODE_LEGACY)
    } else {
        Html.fromHtml(this.toString())
    }
}

fun String.fromHtml(): Spanned {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        Html.fromHtml(this, Html.FROM_HTML_MODE_LEGACY)
    } else {
        Html.fromHtml(this)
    }
}

fun String.isNumberValid(): Boolean {
    if (isEmpty() || length < 10) return false
    if (startsWith("+91")) {
        return substring(3, length).length == 10
    }
    return (length == 10 && (startsWith("0") || startsWith("5") || startsWith("6") || startsWith("7") || startsWith("8") || startsWith("9")))
}

fun String.onlyPhoneNumber(): String {
    if (isEmpty() || length < 10) {
        return this
    }
    if (length > 10) {
        return substring(length - 10, length)
    }
    return this
}

fun String.getValidPhoneNumber(): String {
    if (length >= 10) {
        if (startsWith("+91")) {
            return "+91" + substring(3, length)
        }
        return "+91" + substring(length - 10, length)
    }
    return this
}

fun String.isEmailValid(email: String): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
}

fun String.isValidPinCode(): Boolean {
    return length == 6
}

infix fun View.visibleIf(condition: Boolean) {
    visibility = if (condition) View.VISIBLE else View.GONE
}

fun View.visibleIf(condition: Boolean, otherwise: Int = View.GONE) {
    visibility = if (condition) View.VISIBLE else otherwise
}

infix fun View.goneIf(condition: Boolean) {
    visibility = if (condition) View.GONE else View.VISIBLE
}

fun View.show() {
    visibility = View.VISIBLE
}

fun View.hide() {
    visibility = View.GONE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

fun View.hideKeyboard() {
    val inputMethodManager = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
}


fun Activity.hideKeyboard() {
    if (currentFocus == null) View(this).hideKeyboard() else currentFocus!!.hideKeyboard()
}

fun Fragment.hideKeyboard() {
    val view: View = activity!!.findViewById(android.R.id.content)
    if (view != null) {
        val imm = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view!!.windowToken, 0)
    }
}

fun View.fixWebViewKeyboardDetection() {
    var usableHeightPrevious = 0
    viewTreeObserver.addOnGlobalLayoutListener {
        val usableHeightNow = Rect().let {
            getWindowVisibleDisplayFrame(it)
            it.bottom - it.top
        }

        if (usableHeightNow != usableHeightPrevious) {
            val usableHeightSansKeyboard = rootView.height
            val heightDifference = usableHeightSansKeyboard - usableHeightNow
            if (heightDifference > usableHeightSansKeyboard / 5) {
                layoutParams.height = usableHeightSansKeyboard - heightDifference
            } else {
                layoutParams.height = usableHeightNow - actionbarHeight()
            }
            requestLayout()
            usableHeightPrevious = usableHeightNow
        }
    }
}

fun View.actionbarHeight(): Int {
    val tv = TypedValue()
    context.theme.resolveAttribute(android.R.attr.actionBarSize, tv, true)
    return resources.getDimensionPixelSize(tv.resourceId)
}

fun <T : ViewDataBinding> ViewGroup.inflate(layoutId: Int): T {
    return DataBindingUtil.inflate(LayoutInflater.from(context), layoutId, this, false)
}


fun EditText.showKeyboard() {
    if (requestFocus()) {
        val inputMethodManager = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.showSoftInput(this, 0)
    }
}

fun String.dateInFormat(format: String): Date? {
    val dateFormat = SimpleDateFormat(format, Locale.US)
    var parsedDate: Date? = null
    try {
        parsedDate = dateFormat.parse(this)
    } catch (ignored: ParseException) {
        ignored.printStackTrace()
    }
    return parsedDate
}

fun Date.isSame(to: Date): Boolean {
    val sdf = SimpleDateFormat("yyyMMdd", Locale.getDefault())
    return sdf.format(this) == sdf.format(to)
}

fun getClickableSpan(color: Int, action: (view: View) -> Unit): ClickableSpan {
    return object : ClickableSpan() {
        override fun onClick(view: View) {
            action
        }

        override fun updateDrawState(ds: TextPaint) {
            super.updateDrawState(ds)
            ds.color = color
        }
    }
}

fun Editable.replaceAll(newValue: String) {
    replace(0, length, newValue)
}

fun Boolean.toInt() = if (this) 1 else 0

fun Context.share(text: String, subject: String = "") {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, text)
        putExtra(Intent.EXTRA_SUBJECT, subject)
    }
    startActivity(Intent.createChooser(intent, null).newTask())
}

fun Context.goToMarket(packageName: String) {
    val uri = Uri.parse("market://details?id=$packageName")
    val goToMarket = Intent(Intent.ACTION_VIEW, uri).apply {
        addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_NEW_DOCUMENT or Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
    }
    try {
        startActivity(goToMarket.newTask())
    } catch (e: ActivityNotFoundException) {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=$packageName")).newTask())
    }
}

fun Fragment.goToMarket(packageName: String) {
    val uri = Uri.parse("market://details?id=$packageName")
    val goToMarket = Intent(Intent.ACTION_VIEW, uri).apply {
        addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_NEW_DOCUMENT or Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
    }
    try {
        startActivity(goToMarket.newTask())
    } catch (e: ActivityNotFoundException) {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=$packageName")).newTask())
    }
}



