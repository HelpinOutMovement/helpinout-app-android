package org.helpinout.billonlights.model.dagger

import dagger.Component
import org.helpinout.billonlights.view.activity.BaseActivity
import org.helpinout.billonlights.view.firebase.MyFirebaseMessagingService
import org.helpinout.billonlights.view.fragments.HomeFragment
import org.helpinout.billonlights.viewmodel.HomeViewModel
import org.helpinout.billonlights.viewmodel.LoginRegistrationViewModel
import org.helpinout.billonlights.viewmodel.OfferViewModel
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
