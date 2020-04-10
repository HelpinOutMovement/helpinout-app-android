package org.helpinout.billonlights.model

import androidx.appcompat.app.AppCompatDelegate
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import com.crashlytics.android.Crashlytics
import io.fabric.sdk.android.Fabric
import org.helpinout.billonlights.model.dagger.ApiComponent
import org.helpinout.billonlights.model.dagger.AppModule
import org.helpinout.billonlights.model.dagger.DaggerApiComponent

class BillionLightsApplication : MultiDexApplication() {
    private lateinit var mAppComponent: ApiComponent
    override fun onCreate() {
        super.onCreate()
        MultiDex.install(this);
        Fabric.with(this, Crashlytics())
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        mAppComponent = DaggerApiComponent.builder().appModule(AppModule(this)).build()
    }

    fun getAppComponent(): ApiComponent {
        return mAppComponent
    }

}