package org.helpinout.billonlights.view.activity

import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.avneesh.crashreporter.ui.CrashReporterActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_home.*
import org.helpinout.billonlights.R
import org.helpinout.billonlights.model.database.entity.ActivityAddDetail
import org.helpinout.billonlights.model.database.entity.AddItem
import org.helpinout.billonlights.utils.*
import org.helpinout.billonlights.view.fragments.HomeFragment
import org.helpinout.billonlights.view.fragments.MyOfferFragment
import org.helpinout.billonlights.view.fragments.MyRequestsFragment
import org.helpinout.billonlights.viewmodel.HomeViewModel
import org.helpinout.billonlights.viewmodel.OfferViewModel
import org.jetbrains.anko.startActivity
import timber.log.Timber


class HomeActivity : LocationActivity(), BottomNavigationView.OnNavigationItemSelectedListener, NavigationView.OnNavigationItemSelectedListener {

    private var pagerIndex: Int = 0
    private var doubleBackToExitPressedOnce = false
    private var selectedItem = -1

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
        val index = intent.getIntExtra(SELECTED_INDEX, 0)
        pagerIndex = intent.getIntExtra(PAGER_INDEX, 0)
        when (index) {
            0 -> {
                bottom_nav_view.selectedItemId = R.id.navigation_home
            }
            1 -> {
                bottom_nav_view.selectedItemId = R.id.navigation_my_request
            }
            2 -> {
                bottom_nav_view.selectedItemId = R.id.navigation_my_offers
            }
        }
        setLanguage()
        checkLocationPermission()
        checkOfferList()
    }

    private fun setLanguage() {
        when (preferencesService.defaultLanguage) {
            ENGLISH_CODE -> {
                val english = nav_view.menu.findItem(R.id.nav_english)
                english.isChecked = true
                nav_view.menu.findItem(R.id.nav_english).actionView = getMenuImageView()
            }
            HINDI_CODE -> {
                val hindi = nav_view.menu.findItem(R.id.nav_hindi)
                hindi.isChecked = true
                nav_view.menu.findItem(R.id.nav_hindi).actionView = getMenuImageView()
            }
            KANNAD_CODE -> {
                val kannad = nav_view.menu.findItem(R.id.nav_kannad)
                kannad.isChecked = true
                nav_view.menu.findItem(R.id.nav_kannad).actionView = getMenuImageView()
            }
            MARATHI_CODE -> {
                val marathi = nav_view.menu.findItem(R.id.nav_marathi)
                marathi.isChecked = true
                nav_view.menu.findItem(R.id.nav_marathi).actionView = getMenuImageView()
            }
            GUJRATI_CODE -> {
                val gujrati = nav_view.menu.findItem(R.id.nav_gujrati)
                gujrati.isChecked = true
                nav_view.menu.findItem(R.id.nav_gujrati).actionView = getMenuImageView()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menu_logs -> {
                startActivity<CrashReporterActivity>()
                overridePendingTransition(R.anim.enter, R.anim.exit)
                return true
            }
            R.id.menu_logout -> {
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
                    layout_toolbar.hide()
                    checkFragmentItems()
                    val home = nav_view.menu.findItem(R.id.nav_home)
                    home.isChecked = true
                    bottom_nav_view.menu.getItem(0).isChecked = true
                    home.actionView = getMenuDotView()
                    toolbar?.setTitle(R.string.title_home)
                    val fragment = HomeFragment()
                    loadFragment(fragment)
                }
            }
            R.id.navigation_my_request, R.id.nav_my_request -> {
                if (selectedItem != 1) {
                    selectedItem = 1
                    layout_toolbar.show()
                    checkFragmentItems()
                    val my_request = nav_view.menu.findItem(R.id.nav_my_request)
                    my_request.isChecked = true
                    bottom_nav_view.menu.getItem(1).isChecked = true
                    my_request.actionView = getMenuDotView()
                    toolbar?.setTitle(R.string.title_my_request)
                    val fragment = MyRequestsFragment(pagerIndex)
                    loadFragment(fragment)
                }
            }
            R.id.navigation_my_offers, R.id.nav_my_offers -> {
                if (selectedItem != 2) {
                    selectedItem = 2
                    layout_toolbar.show()
                    checkFragmentItems()
                    val my_offers = nav_view.menu.findItem(R.id.nav_my_offers)
                    my_offers.isChecked = true
                    bottom_nav_view.menu.getItem(2).isChecked = true
                    toolbar?.setTitle(R.string.title_my_offers)
                    my_offers.actionView = getMenuDotView()
                    val fragment = MyOfferFragment(pagerIndex)
                    loadFragment(fragment)
                }
            }

            R.id.nav_ask_help -> {
                startActivity<AskForHelpActivity>(HELP_TYPE to HELP_TYPE_REQUEST)
                overridePendingTransition(R.anim.enter, R.anim.exit)
            }

            R.id.nav_offer_help -> {
                startActivity<OfferHelpActivity>(HELP_TYPE to HELP_TYPE_OFFER)
                overridePendingTransition(R.anim.enter, R.anim.exit)
            }

            R.id.nav_profiles -> {
                startActivity<RegistrationActivity>(UPDATE_PROFILE to true)
                overridePendingTransition(R.anim.enter, R.anim.exit)
            }
            R.id.nav_about -> {
                selectedItem = 5
                checkFragmentItems()
                val about = nav_view.menu.findItem(R.id.nav_about)
                about.isChecked = true
                about.actionView = getMenuDotView()
            }

            R.id.nav_english -> {
                checkLanguageItem()
                val english = nav_view.menu.findItem(R.id.nav_english)
                english.isChecked = true
                item.actionView = getMenuImageView()
                if (preferencesService.defaultLanguage != ENGLISH_CODE) {
                    preferencesService.defaultLanguage = ENGLISH_CODE
                    changeAppLanguage(preferencesService.defaultLanguage)
                    restartActivity()
                }
            }

            R.id.nav_hindi -> {
                checkLanguageItem()
                val hindi = nav_view.menu.findItem(R.id.nav_hindi)
                hindi.isChecked = true
                item.actionView = getMenuImageView()
                if (preferencesService.defaultLanguage != HINDI_CODE) {
                    preferencesService.defaultLanguage = HINDI_CODE
                    changeAppLanguage(preferencesService.defaultLanguage)
                    restartActivity()
                }
            }

            R.id.nav_kannad -> {
                checkLanguageItem()
                val kannad = nav_view.menu.findItem(R.id.nav_kannad)
                kannad.isChecked = true
                item.actionView = getMenuImageView()
                if (preferencesService.defaultLanguage != KANNAD_CODE) {
                    preferencesService.defaultLanguage = KANNAD_CODE
                    changeAppLanguage(preferencesService.defaultLanguage)
                    restartActivity()
                }
            }

            R.id.nav_marathi -> {
                checkLanguageItem()
                val marathi = nav_view.menu.findItem(R.id.nav_marathi)
                marathi.isChecked = true
                item.actionView = getMenuImageView()
                if (preferencesService.defaultLanguage != MARATHI_CODE) {
                    preferencesService.defaultLanguage = MARATHI_CODE
                    changeAppLanguage(preferencesService.defaultLanguage)
                    restartActivity()
                }
            }

            R.id.nav_gujrati -> {
                checkLanguageItem()
                val gujrati = nav_view.menu.findItem(R.id.nav_gujrati)
                gujrati.isChecked = true
                item.actionView = getMenuImageView()
                if (preferencesService.defaultLanguage != GUJRATI_CODE) {
                    preferencesService.defaultLanguage = GUJRATI_CODE
                    changeAppLanguage(preferencesService.defaultLanguage)
                    restartActivity()
                }
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

    private fun checkLanguageItem() {
        val english = nav_view.menu.findItem(R.id.nav_english)
        english.isChecked = false
        val hindi = nav_view.menu.findItem(R.id.nav_hindi)
        hindi.isChecked = false

        val kannad = nav_view.menu.findItem(R.id.nav_kannad)
        kannad.isChecked = false

        val marathi = nav_view.menu.findItem(R.id.nav_marathi)
        marathi.isChecked = false

        val gujrati = nav_view.menu.findItem(R.id.nav_gujrati)
        gujrati.isChecked = false

        nav_view.menu.findItem(R.id.nav_english).actionView = null
        nav_view.menu.findItem(R.id.nav_hindi).actionView = null
        nav_view.menu.findItem(R.id.nav_kannad).actionView = null
        nav_view.menu.findItem(R.id.nav_marathi).actionView = null
        nav_view.menu.findItem(R.id.nav_gujrati).actionView = null
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
        buildGoogleApiClient()
    }

    override fun onLocationChanged(location: Location?) {
        location?.let {
            preferencesService.latitude = it.latitude.toString()
            preferencesService.longitude = it.longitude.toString()
            preferencesService.gpsAccuracy = it.accuracy.toString()
            sendLocationToServer()
            stopLocationUpdate()
        }
    }

    private fun sendLocationToServer() {
        val viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        viewModel.sendUserLocationToServer().observe(this, Observer {
            Log.d("", "")
        })
    }

    override fun getLayout(): Int {
        return R.layout.activity_home
    }

    private fun checkOfferList() {
        val viewModel = ViewModelProvider(this).get(OfferViewModel::class.java)
        viewModel.getUserRequestOfferList("0").observe(this, Observer {
            it.first?.let { response ->
                val offers = response.data?.offers
                val requests = response.data?.requests
                if (!offers.isNullOrEmpty()) {
                    insertItemToDatabase(offers)
                }
                if (!requests.isNullOrEmpty()) {
                    insertItemToDatabase(requests)
                }
            }
        })
    }

    private fun insertItemToDatabase(offers: List<ActivityAddDetail>) {
        val addDataList = ArrayList<AddItem>()
        offers.forEach { offer ->
            try {
                val singleItem = AddItem()
                singleItem.activity_type = offer.activity_type
                singleItem.activity_uuid = offer.activity_uuid

                val itemDetail = offer.detail + "(" + offer.activity_count + "),"

                singleItem.detail = itemDetail
                singleItem.activity_uuid = offer.activity_uuid
                singleItem.date_time = offer.date_time
                singleItem.activity_category = offer.activity_category
                singleItem.activity_count = offer.activity_count
                singleItem.geo_location = offer.geo_location
                singleItem.qty = "1"
                singleItem.address = getAddress(preferencesService.latitude.toDouble(), preferencesService.longitude.toDouble())
                singleItem.status = 1
                addDataList.add(singleItem)
            } catch (e: Exception) {
                Timber.d("")
            }
        }
        val viewModel = ViewModelProvider(this).get(OfferViewModel::class.java)
        viewModel.saveFoodItemToDatabase(addDataList).observe(this, Observer {
            Timber.d("")
        })
    }


    fun menuClick() {
        if (!drawer_layout.isDrawerOpen(Gravity.START)) {
            drawer_layout.openDrawer(Gravity.START)
        } else {
            drawer_layout.closeDrawer(Gravity.START)
        }
    }
}
