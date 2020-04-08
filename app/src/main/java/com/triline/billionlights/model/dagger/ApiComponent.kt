package com.triline.billionlights.model.dagger

import com.triline.billionlights.view.activity.BaseActivity
import com.triline.billionlights.view.firebase.MyFirebaseMessagingService
import com.triline.billionlights.view.fragments.HomeFragment
import com.triline.billionlights.viewmodel.HomeViewModel
import com.triline.billionlights.viewmodel.LoginRegistrationViewModel
import com.triline.billionlights.viewmodel.OfferViewModel
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [(AppModule::class), (ApiModule::class), (DbModule::class)])
interface ApiComponent {
    fun inject(model: LoginRegistrationViewModel)
    fun inject(activity: BaseActivity)
    fun inject(service: MyFirebaseMessagingService)
    fun inject(service: HomeFragment)
    fun inject(service: HomeViewModel)
    fun inject(service: OfferViewModel)
}
