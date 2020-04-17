package org.helpinout.billonlights.view.activity

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.avneesh.crashreporter.ui.CrashReporterActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.layout_enable_location.*
import kotlinx.android.synthetic.main.layout_permission.*
import org.helpinout.billonlights.BuildConfig
import org.helpinout.billonlights.R
import org.helpinout.billonlights.utils.*
import org.helpinout.billonlights.view.fragments.FragmentMyRequests
import org.helpinout.billonlights.view.fragments.HomeFragment
import org.helpinout.billonlights.viewmodel.OfferViewModel
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.startActivityForResult


class HomeActivity : LocationActivity(), BottomNavigationView.OnNavigationItemSelectedListener, NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private var homeFragment: HomeFragment? = null
    private var doubleBackToExitPressedOnce = false
    private var selectedItem = -1
    private var updateLanguage = 349

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
                bottom_nav_view.selectedItemId = R.id.navigation_home
            }
            1 -> {
                bottom_nav_view.selectedItemId = R.id.navigation_my_request
                supportActionBar?.setTitle(R.string.title_my_request)
            }
            2 -> {
                bottom_nav_view.selectedItemId = R.id.navigation_my_offers
                supportActionBar?.setTitle(R.string.title_my_offers)
            }
        }
        checkLocationPermission()
        checkOfferList()
        btnPermission.setOnClickListener(this)
        enableLocation.setOnClickListener(this)
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        if (BuildConfig.DEBUG) menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
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
            R.id.navigation_home, R.id.nav_home, R.id.nav_ask_help, R.id.nav_offer_help -> {
                if (selectedItem != 0) {
                    selectedItem = 0
                    layout_toolbar.hide()
                    checkFragmentItems()

                    val home = nav_view.menu.findItem(R.id.nav_home)
                    home.isChecked = true
                    bottom_nav_view.menu.getItem(0).isChecked = true
                    home.actionView = getMenuDotView()
                    toolbar?.setTitle(R.string.title_home)
                    homeFragment = HomeFragment()
                    loadFragment(homeFragment!!)
                }
            }
            R.id.navigation_my_request, R.id.nav_my_request -> {
                if (selectedItem != 1) {
                    selectedItem = 1
                    layout_toolbar.show()
                    checkFragmentItems()
                    supportActionBar?.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(this, R.color.colorPrimary)))
                    val my_request = nav_view.menu.findItem(R.id.nav_my_request)
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
                    supportActionBar?.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(this, R.color.colorAccent)))
                    val my_offers = nav_view.menu.findItem(R.id.nav_my_offers)
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

            R.id.nav_language -> {
                startActivityForResult<LanguageChooserActivity>(updateLanguage, UPDATE_LANGAUGE to true)
                overridePendingTransition(R.anim.enter, R.anim.exit)
            }

            R.id.nav_about -> {
                selectedItem = 5
                checkFragmentItems()
                val about = nav_view.menu.findItem(R.id.nav_about)
                about.isChecked = true
                about.actionView = getMenuDotView()
            }
        }
        closeDrawer()
        return false
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

        val ask_help = nav_view.menu.findItem(R.id.nav_ask_help)
        ask_help.isChecked = false

        val offer_help = nav_view.menu.findItem(R.id.nav_offer_help)
        offer_help.isChecked = false

        val profiles = nav_view.menu.findItem(R.id.nav_profiles)
        profiles.isChecked = false

        val about = nav_view.menu.findItem(R.id.nav_about)
        about.isChecked = false


        nav_view.menu.findItem(R.id.nav_home).actionView = null
        nav_view.menu.findItem(R.id.nav_my_request).actionView = null
        nav_view.menu.findItem(R.id.nav_my_offers).actionView = null
        nav_view.menu.findItem(R.id.nav_ask_help).actionView = null
        nav_view.menu.findItem(R.id.nav_offer_help).actionView = null
        nav_view.menu.findItem(R.id.nav_profiles).actionView = null
        nav_view.menu.findItem(R.id.nav_about).actionView = null
    }


    private fun getMenuDotView(): View {
        return layoutInflater.inflate(R.layout.drawer_dot, null)
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

    fun menuClick() {
        if (!drawer_layout.isDrawerOpen(Gravity.START)) {
            drawer_layout.openDrawer(Gravity.START)
        } else {
            drawer_layout.closeDrawer(Gravity.START)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {// do not delete this
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

    override fun getLayout(): Int {
        return R.layout.activity_home
    }
}
