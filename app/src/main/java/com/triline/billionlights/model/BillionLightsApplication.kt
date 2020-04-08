package com.triline.billionlights.model

import androidx.appcompat.app.AppCompatDelegate
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import com.crashlytics.android.Crashlytics
import com.triline.billionlights.model.dagger.ApiComponent
import com.triline.billionlights.model.dagger.AppModule
import com.triline.billionlights.model.dagger.DaggerApiComponent
import io.fabric.sdk.android.Fabric

class BillionLightsApplication : MultiDexApplication() {
    private lateinit var mAppComponent: ApiComponent
    override fun onCreate() {
        super.onCreate()
        MultiDex.install(this);
        Fabric.with(this, Crashlytics())
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        mAppComponent = DaggerApiComponent.builder()
            .appModule(AppModule(this)).build()
    }

    fun getAppComponent(): ApiComponent {
        return mAppComponent
    }

}