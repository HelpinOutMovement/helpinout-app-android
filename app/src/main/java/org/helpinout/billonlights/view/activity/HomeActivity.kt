package org.helpinout.billonlights.view.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.provider.Settings
import android.view.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.avneesh.crashreporter.ui.CrashReporterActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.tasks.OnSuccessListener
import com.google.android.play.core.tasks.TaskExecutors
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.layout_ask_for_email.view.*
import kotlinx.android.synthetic.main.layout_enable_location.*
import kotlinx.android.synthetic.main.layout_permission.*
import org.helpinout.billonlights.BuildConfig
import org.helpinout.billonlights.R
import org.helpinout.billonlights.utils.*
import org.helpinout.billonlights.view.fragments.FragmentMyRequests
import org.helpinout.billonlights.view.fragments.HomeFragment
import org.helpinout.billonlights.viewmodel.HomeViewModel
import org.helpinout.billonlights.viewmodel.OfferViewModel
import org.jetbrains.anko.indeterminateProgressDialog
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import timber.log.Timber
import java.util.concurrent.Executor


class HomeActivity : LocationActivity(), BottomNavigationView.OnNavigationItemSelectedListener, NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private var mailMenu: MenuItem? = null
    private var homeFragment: HomeFragment? = null
    private var doubleBackToExitPressedOnce = false
    private var selectedItem = -1
    private var selectedPosition = -1
    private var updateLanguage = 349
    var radius: Float = 0.0F
    val REQUEST_UPDATE_CODE = 1
    lateinit var installStateUpdatedListener: InstallStateUpdatedListener

    lateinit var appUpdateManager: AppUpdateManager

    lateinit var playServiceExecutor: Executor


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appUpdateManager = AppUpdateManagerFactory.create(this)
        playServiceExecutor = TaskExecutors.MAIN_THREAD
        val drawerToggle: ActionBarDrawerToggle = object : ActionBarDrawerToggle(this, drawer_layout, toolbar, R.string.app_name, R.string.app_name) {
            override fun onDrawerClosed(view: View) {
                invalidateOptionsMenu()
            }

            override fun onDrawerOpened(drawerView: View) {
                invalidateOptionsMenu()
            }
        }
        drawer_layout.setDrawerListener(drawerToggle)
        drawerToggle.syncState()
        nav_view.setNavigationItemSelectedListener(this)
        bottom_nav_view.setOnNavigationItemSelectedListener(this)
        when (intent.getIntExtra(SELECTED_INDEX, 0)) {
            0 -> {
                selectedPosition = 0
                bottom_nav_view.selectedItemId = R.id.navigation_home
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    val window: Window = window
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                    window.statusBarColor = resources.getColor(R.color.colorPrimaryDark)
                }
            }
            1 -> {
                mailMenu?.isVisible = false
                selectedPosition = 1
                bottom_nav_view.selectedItemId = R.id.navigation_my_request
                supportActionBar?.setTitle(R.string.title_my_request)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    val window: Window = window
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                    window.statusBarColor = resources.getColor(R.color.colorPrimaryDark)
                    mailMenu?.isVisible = false
                }
            }
            2 -> {
                mailMenu?.isVisible = true
                selectedPosition = 2
                bottom_nav_view.selectedItemId = R.id.navigation_my_offers
                supportActionBar?.setTitle(R.string.title_my_offers)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    val window: Window = window
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                    window.statusBarColor = resources.getColor(R.color.colorAccentDark)
                    mailMenu?.isVisible = true
                }
            }
        }
        checkLocationPermission()
        checkOfferList()
        btnPermission.setOnClickListener(this)
        enableLocation.setOnClickListener(this)
        setLanguage()
        refreshBedge()
        val filter = IntentFilter()
        filter.addAction(BEDGE_REFRESH)
        LocalBroadcastManager.getInstance(this).registerReceiver(bedgeRefreshReceiver, filter)
        updateChecker()
    }


    @SuppressLint("SwitchIntDef")
    private fun updateChecker() {
        installStateUpdatedListener = InstallStateUpdatedListener { installState ->
            when (installState.installStatus()) {
                InstallStatus.DOWNLOADED -> {
                    updaterDownloadCompleted()
                }
                InstallStatus.INSTALLED -> {
                    appUpdateManager.unregisterListener(installStateUpdatedListener)
                }
            }
        }
        appUpdateManager.registerListener(installStateUpdatedListener)

        val appUpdateInfoTask = appUpdateManager.appUpdateInfo
        appUpdateInfoTask.addOnSuccessListener(playServiceExecutor, OnSuccessListener { appUpdateInfo ->
            when (appUpdateInfo.updateAvailability()) {
                UpdateAvailability.UPDATE_AVAILABLE -> {
                    val updateTypes = arrayOf(AppUpdateType.FLEXIBLE, AppUpdateType.IMMEDIATE)
                    run loop@{
                        updateTypes.forEach { type ->
                            if (appUpdateInfo.isUpdateTypeAllowed(type)) {
                                appUpdateManager.startUpdateFlowForResult(appUpdateInfo, type, this, REQUEST_UPDATE_CODE)
                                return@loop
                            }
                        }
                    }
                }
            }
        })
    }

    private fun updaterDownloadCompleted() {
        Snackbar.make(activity_main_layout, R.string.update_downloaded, Snackbar.LENGTH_INDEFINITE).apply {
            setAction(R.string.restart) { appUpdateManager.completeUpdate() }
            show()
        }
    }

    override fun onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(bedgeRefreshReceiver)
        super.onDestroy()
    }

    private val bedgeRefreshReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == BEDGE_REFRESH) {
                refreshBedge()
            }
        }
    }

    fun refreshBedge() {
        loadRequestList(HELP_TYPE_REQUEST, 1)
        loadRequestList(HELP_TYPE_OFFER, 2)
    }

    override fun onResume() {
        super.onResume()
        appUpdateManager.appUpdateInfo.addOnSuccessListener(playServiceExecutor, OnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
                if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) updaterDownloadCompleted()
            } else {
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                    appUpdateManager.startUpdateFlowForResult(appUpdateInfo, AppUpdateType.FLEXIBLE, this, REQUEST_UPDATE_CODE)
                }
            }
        })
        refreshBedge()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)

        val menu_add_item = menu.findItem(R.id.menu_add_item)
        menu_add_item.actionView.setOnClickListener {
            onNavigationItemSelected(nav_view.menu.getItem(0))
        }

        mailMenu = menu.findItem(R.id.menu_email)

        mailMenu?.actionView?.setOnClickListener {
            showEmailPopup()
        }
        mailMenu?.isVisible = selectedPosition == 2
        val logsMenu = menu.findItem(R.id.menu_logs)
        logsMenu.isVisible = BuildConfig.BUILD_TYPE == "beta_debug"
        return super.onCreateOptionsMenu(menu)
    }

    private fun showEmailPopup() {
        if (SystemClock.elapsedRealtime() - mLastClickTime < DOUBLE_CLICK_TIME) {
            return
        }
        mLastClickTime = SystemClock.elapsedRealtime()
        val alertLayout: View = layoutInflater.inflate(R.layout.layout_ask_for_email, null)
        val alert: AlertDialog.Builder = AlertDialog.Builder(this)
        alert.setView(alertLayout)
        val dialog: AlertDialog = alert.create()

        alertLayout.submit.setOnClickListener {
            if (!alertLayout.edt_email.text.toString().isEmailValid()) {
                toastError(R.string.invalid_email_id)
            } else {
                sendEmailToServer(alertLayout.edt_email.text.toString())
                dialog.dismiss()
            }
        }

        dialog.show()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

    }

    private fun sendEmailToServer(email: String) {
        val dialog = indeterminateProgressDialog(R.string.alert_msg_please_wait)
        dialog.setCancelable(false)
        dialog.show()
        val viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        viewModel.sendEmailToServer(email).observe(this, Observer {
            if (it.first != null && it.first!!.status == 1) {
                toast(R.string.email_send_success)
            } else {
                toastError(it.second)
            }
            dialog.dismiss()
        })
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menu_logs -> {
                startActivity<CrashReporterActivity>()
                overridePendingTransition(R.anim.enter, R.anim.exit)
                return true
            }

        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        closeDrawer()
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }
        this.doubleBackToExitPressedOnce = true
        toastInfo(R.string.toast_press_again_to_exit)
        Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 2000)

    }

    private fun closeDrawer() {
        if (drawer_layout.isDrawerOpen(Gravity.START)) {
            drawer_layout.closeDrawer(Gravity.START)
            return
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.navigation_home, R.id.nav_home -> {
                if (selectedItem != 0) {
                    selectedItem = 0
                    selectedPosition = 0
                    layout_toolbar.hide()
                    checkFragmentItems()
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        val window: Window = window
                        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                        window.statusBarColor = resources.getColor(R.color.colorPrimaryDark)
                    }
                    val home = nav_view.menu.findItem(R.id.nav_home)
                    home.isChecked = true
                    bottom_nav_view.menu.getItem(0).isChecked = true
                    home.actionView = getMenuDotView()
                    homeFragment = HomeFragment()
                    loadFragment(homeFragment!!)
                }
            }
            R.id.navigation_my_request, R.id.nav_my_request -> {
                if (selectedItem != 1) {
                    selectedPosition = 1
                    selectedItem = 1
                    layout_toolbar.show()
                    checkFragmentItems()
                    supportActionBar?.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(this, R.color.colorPrimary)))
                    val my_request = nav_view.menu.findItem(R.id.nav_my_request)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        val window: Window = window
                        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                        window.statusBarColor = resources.getColor(R.color.colorPrimaryDark)
                    }
                    mailMenu?.isVisible = false
                    my_request.isChecked = true
                    bottom_nav_view.menu.getItem(1).isChecked = true
                    my_request.actionView = getMenuDotView()
                    toolbar?.setTitle(R.string.title_my_request)
                    val fragment = FragmentMyRequests.newInstance(HELP_TYPE_REQUEST, HELP_TYPE_REQUEST, HELP_TYPE_REQUEST)
                    loadFragment(fragment)
                }
            }
            R.id.navigation_my_offers, R.id.nav_my_offers -> {
                if (selectedItem != 2) {
                    selectedItem = 2
                    layout_toolbar.show()
                    checkFragmentItems()
                    mailMenu?.isVisible = true
                    selectedPosition = 2
                    supportActionBar?.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(this, R.color.colorAccent)))
                    val my_offers = nav_view.menu.findItem(R.id.nav_my_offers)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        val window: Window = window
                        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                        window.statusBarColor = resources.getColor(R.color.colorAccentDark)
                    }
                    my_offers.isChecked = true
                    bottom_nav_view.menu.getItem(2).isChecked = true
                    toolbar?.setTitle(R.string.title_my_offers)
                    my_offers.actionView = getMenuDotView()
                    val fragment = FragmentMyRequests.newInstance(HELP_TYPE_OFFER, HELP_TYPE_OFFER, HELP_TYPE_OFFER)
                    loadFragment(fragment)
                }
            }

            R.id.nav_profiles -> {
                startActivity<RegistrationActivity>(UPDATE_PROFILE to true)
                overridePendingTransition(R.anim.enter, R.anim.exit)
            }

            R.id.nav_about -> {
                startActivity<AboutActivity>()
                overridePendingTransition(R.anim.enter, R.anim.exit)
            }
            R.id.nav_feedback -> {
                startActivity<WebViewActivity>(WEB_URL to FEEDBACK_URL, TITLE to getString(R.string.title_feedback))
                overridePendingTransition(R.anim.enter, R.anim.exit)
            }
            R.id.nav_form -> {
                val url = "https://core.helpinout.org/custom/rra/data?lat=" + preferencesService.latitude + "&lng=" + preferencesService.longitude + "&org=" + preferencesService.orgName
                startActivity<WebViewActivity>(WEB_URL to url, TITLE to getString(R.string.title_form))
                overridePendingTransition(R.anim.enter, R.anim.exit)
            }

            //Language
            else -> {
                languageList.forEachIndexed { index, id ->
                    if (id == item.itemId) {
                        checkLanguageItem()
                        val language = nav_view.menu.findItem(id)
                        language.isChecked = true
                        item.actionView = getMenuImageView()
                        if (preferencesService.defaultLanguage != languageCode[index]) {
                            preferencesService.defaultLanguage = languageCode[index]
                            changeAppLanguage(preferencesService.defaultLanguage)
                            restartActivity()
                        }
                        return@forEachIndexed
                    }
                }
            }
        }
        closeDrawer()
        return false
    }


    private fun setLanguage() {
        languageCode.forEachIndexed { index, code ->
            if (code == preferencesService.defaultLanguage) {
                val languageId = languageList[index]
                val language = nav_view.menu.findItem(languageId)
                language.isChecked = true
                language.actionView = getMenuImageView()
            }
        }
    }

    private fun checkLanguageItem() {
        languageList.forEach {
            val language = nav_view.menu.findItem(it)
            language.isChecked = false
            language.actionView = null
        }
    }

    private fun restartActivity() {
        val intent = intent
        finish()
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        startActivity(intent)
    }

    private fun checkFragmentItems() {
        val home = nav_view.menu.findItem(R.id.nav_home)
        home.isChecked = false

        val my_request = nav_view.menu.findItem(R.id.nav_my_request)
        my_request.isChecked = false

        val my_offers = nav_view.menu.findItem(R.id.nav_my_offers)
        my_offers.isChecked = false


        val profiles = nav_view.menu.findItem(R.id.nav_profiles)
        profiles.isChecked = false

        val about = nav_view.menu.findItem(R.id.nav_about)
        about.isChecked = false

        val feedback = nav_view.menu.findItem(R.id.nav_feedback)
        feedback.isChecked = false


        nav_view.menu.findItem(R.id.nav_home).actionView = null
        nav_view.menu.findItem(R.id.nav_my_request).actionView = null
        nav_view.menu.findItem(R.id.nav_my_offers).actionView = null
        nav_view.menu.findItem(R.id.nav_profiles).actionView = null
        nav_view.menu.findItem(R.id.nav_about).actionView = null
        nav_view.menu.findItem(R.id.nav_feedback).actionView = null
    }


    private fun getMenuDotView(): View {
        return layoutInflater.inflate(R.layout.drawer_dot, null)
    }

    private fun getMenuImageView(): View {
        return layoutInflater.inflate(R.layout.menu_image, null)
    }

    private fun loadFragment(fragment: Fragment): Boolean {
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit()
        return true
    }

    override fun onPermissionAllow() {
        bottom_nav_view.show()
        drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
        layoutPermission.hide()
        buildGoogleApiClient()
    }

    override fun onPermissionCancel() {
        bottom_nav_view.hide()
        drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        layoutPermission.show()
    }

    override fun onLocationOnOff(isEnable: Boolean) {
        if (isEnable) {
            bottom_nav_view.show()
            drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
            layoutLocation.hide()
            checkLocationPermission()
        } else {
            layoutLocation.show()
            bottom_nav_view.hide()
            drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        }
    }

    override fun onLocationChanged(location: Location?) {
        location?.let {
            preferencesService.latitude = it.latitude
            preferencesService.longitude = it.longitude
            preferencesService.gpsAccuracy = it.accuracy.toString()
            homeFragment?.onLocationChanged(location)
            stopLocationUpdate()
        }
    }

    private fun checkOfferList() {
        val viewModel = ViewModelProvider(this).get(OfferViewModel::class.java)
        viewModel.getUserRequestOfferList(this, 0)
    }

    private fun loadRequestList(offerType: Int, initiator: Int) {
        val viewModel = ViewModelProvider(this).get(OfferViewModel::class.java)
        viewModel.getMyRequestsOrOffers(offerType, initiator, this).observe(this, Observer { list ->
            try {
                val count = list.filter { it.show_notification == 1 }.size
                if (count > 0) {
                    addBadge(offerType)
                } else {
                    removeBadge(offerType)
                }
            } catch (e: Exception) {
                Timber.d("")
            }
        })
    }

    fun menuClick() {
        if (!drawer_layout.isDrawerOpen(Gravity.START)) {
            drawer_layout.openDrawer(Gravity.START)
        } else {
            drawer_layout.closeDrawer(Gravity.START)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) { // do not delete this
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == updateLanguage) {
            restartActivity()
        } else for (fragment in supportFragmentManager.fragments) {
            fragment.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onClick(v: View?) {
        when (v) {
            btnPermission -> {
                showSetting = true
                checkLocationPermission()
            }
            enableLocation -> {
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)

            }
        }
    }

    private fun addBadge(offerType: Int) {
        try {
            val bedgeId = if (offerType == HELP_TYPE_REQUEST) R.id.navigation_my_request else R.id.navigation_my_offers
            var badge = bottom_nav_view.getBadge(bedgeId)
            if (badge == null) {
                badge = bottom_nav_view.getOrCreateBadge(bedgeId)
                badge.badgeGravity = Gravity.RIGHT or Gravity.TOP
            }
        } catch (e: Exception) {
            Timber.d("")
        }
    }

    private fun removeBadge(offerType: Int) {
        val bedgeId = if (offerType == HELP_TYPE_REQUEST) R.id.navigation_my_request else R.id.navigation_my_offers
        bottom_nav_view.removeBadge(bedgeId)
    }

    override fun getLayout(): Int {
        return R.layout.activity_home
    }

}
