package org.helpinout.billonlights.view.activity

import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.util.Log
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.avneesh.crashreporter.ui.CrashReporterActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.layout_enable_location.*
import kotlinx.android.synthetic.main.layout_permission.*
import org.helpinout.billonlights.R
import org.helpinout.billonlights.model.database.entity.ActivityAddDetail
import org.helpinout.billonlights.model.database.entity.AddCategoryDbItem
import org.helpinout.billonlights.model.database.entity.MappingDetail
import org.helpinout.billonlights.utils.*
import org.helpinout.billonlights.view.fragments.HomeFragment
import org.helpinout.billonlights.view.fragments.MyOfferFragment
import org.helpinout.billonlights.view.fragments.MyRequestsFragment
import org.helpinout.billonlights.viewmodel.HomeViewModel
import org.helpinout.billonlights.viewmodel.OfferViewModel
import org.jetbrains.anko.startActivity
import timber.log.Timber


class HomeActivity : LocationActivity(), BottomNavigationView.OnNavigationItemSelectedListener, NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private var homeFragment: HomeFragment?=null
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
        btnPermission.setOnClickListener(this)
        enableLocation.setOnClickListener(this)
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
                     homeFragment = HomeFragment()
                    loadFragment(homeFragment!!)
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
            preferencesService.latitude = it.latitude.toString()
            preferencesService.longitude = it.longitude.toString()
            preferencesService.gpsAccuracy = it.accuracy.toString()
            homeFragment?.onLocationChanged(location)
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
                Timber.d("")
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
        val addDataList = ArrayList<AddCategoryDbItem>()
        offers.forEach { offer ->
            try {
                val item = AddCategoryDbItem()
                item.activity_type = offer.activity_type
                item.activity_uuid = offer.activity_uuid

                var itemDetail = ""
                offer.activity_detail?.forEachIndexed { index, it ->

                    if (offer.activity_category == CATEGORY_PEOPLE) {
                        //for people
                        item.volunters_required = it.volunters_required
                        item.volunters_detail = it.volunters_detail
                        item.volunters_quantity = it.volunters_quantity
                        item.technical_personal_required = it.technical_personal_required
                        item.technical_personal_detail = it.technical_personal_detail
                        item.technical_personal_quantity = it.technical_personal_quantity

                        if (!it.volunters_detail.isNullOrEmpty() || !it.volunters_quantity.isNullOrEmpty()) {
                            itemDetail += it.volunters_detail + "(" + it.volunters_quantity + ")"

                        }
                        if (!it.technical_personal_detail.isNullOrEmpty()) {
                            if (itemDetail.isNotEmpty()) {
                                itemDetail += ","
                            }
                            itemDetail += it.technical_personal_detail + "(" + it.technical_personal_quantity + ")"
                        }

                    } else if (offer.activity_category == CATEGORY_AMBULANCE) {
                        item.qty = it.quantity
                        itemDetail = ""
                    } else {
                        itemDetail += it.detail + "(" + it.quantity + ")"
                        if (offer.activity_detail!!.size - 1 != index) {
                            itemDetail += ","
                        }
                    }
                }

                offer.mapping?.forEach { mapping ->
                    if (mapping.offer_detail != null) {
                        mapping.offer_detail?.app_user_detail?.parent_uuid = offer.activity_uuid
                        mapping.offer_detail?.app_user_detail?.activity_type = mapping.offer_detail?.activity_type
                        mapping.offer_detail?.app_user_detail?.activity_uuid = mapping.offer_detail?.activity_uuid
                        mapping.offer_detail?.app_user_detail?.activity_category = mapping.offer_detail?.activity_category
                        mapping.offer_detail?.app_user_detail?.date_time = mapping.offer_detail?.date_time
                        mapping.offer_detail?.app_user_detail?.activity_type = mapping.offer_detail?.activity_type
                        mapping.offer_detail?.app_user_detail?.geo_location = mapping.offer_detail?.geo_location
                        mapping.offer_detail?.app_user_detail?.offer_condition = mapping.offer_detail?.offer_condition
                        mapping.offer_detail?.app_user_detail?.request_mapping_initiator = mapping.request_mapping_initiator
                    } else if (mapping.request_detail != null) {
                        mapping.request_detail?.app_user_detail?.parent_uuid = offer.activity_uuid
                        mapping.request_detail?.app_user_detail?.activity_type = mapping.request_detail?.activity_type
                        mapping.request_detail?.app_user_detail?.activity_uuid = mapping.request_detail?.activity_uuid
                        mapping.request_detail?.app_user_detail?.activity_category = mapping.request_detail?.activity_category
                        mapping.request_detail?.app_user_detail?.date_time = mapping.request_detail?.date_time
                        mapping.request_detail?.app_user_detail?.activity_type = mapping.request_detail?.activity_type
                        mapping.request_detail?.app_user_detail?.geo_location = mapping.request_detail?.geo_location
                        mapping.request_detail?.app_user_detail?.offer_condition = mapping.request_detail?.offer_condition
                        mapping.request_detail?.app_user_detail?.request_mapping_initiator = mapping.request_mapping_initiator
                    }
                }
                val mappingList = ArrayList<MappingDetail>()
                offer.mapping?.forEach {
                    if (it.offer_detail != null) {
                        mappingList.add(it.offer_detail!!.app_user_detail!!)
                    } else {
                        mappingList.add(it.request_detail!!.app_user_detail!!)
                    }
                }
                if (mappingList.isNotEmpty()) saveMapping(mappingList)

                item.detail = itemDetail
                item.activity_uuid = offer.activity_uuid
                item.date_time = offer.date_time
                item.activity_category = offer.activity_category
                item.activity_count = offer.activity_count
                item.geo_location = offer.geo_location
                item.address = getAddress(preferencesService.latitude.toDouble(), preferencesService.longitude.toDouble())
                item.status = 1
                addDataList.add(item)
            } catch (e: Exception) {
                Timber.d("")
            }
        }
        addDataList.reverse()
        val viewModel = ViewModelProvider(this).get(OfferViewModel::class.java)
        viewModel.saveFoodItemToDatabase(addDataList).observe(this, Observer {
            Timber.d("")
        })
    }

    private fun saveMapping(mappingList: ArrayList<MappingDetail>) {
        val viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        viewModel.saveMapping(mappingList).observe(this, Observer {
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
}
