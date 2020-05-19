package org.helpinout.billonlights.model.dagger

import android.app.Application
import com.orhanobut.hawk.Hawk
import dagger.Module
import dagger.Provides
import org.helpinout.billonlights.model.database.AppDatabase
import org.helpinout.billonlights.model.retrofit.NetworkApi
import org.helpinout.billonlights.model.retrofit.NetworkApiProvider
import org.helpinout.billonlights.service.LocationService
import org.helpinout.billonlights.service.LoginService
import org.helpinout.billonlights.service.OfferRequestDetailService
import org.helpinout.billonlights.service.OfferRequestListService
import javax.inject.Singleton

@Module
class AppModule(private var app: Application) {
    @Singleton
    @Provides
    fun provideApplication(): Application {
        return app
    }

    @Singleton
    @Provides
    fun providePreferenceService(): PreferencesService {
        if (!Hawk.isBuilt()) Hawk.init(app).build()
        return PreferencesService()
    }

    @Singleton
    @Provides
    fun provideString(app: Application): StringService {
        return StringService(app)
    }

    @Provides
    @Singleton
    fun provideLoginService(preferencesService: PreferencesService, service: NetworkApiProvider, db: AppDatabase) = LoginService(preferencesService, service, db)

    @Provides
    @Singleton
    fun provideLocationService(preferencesService: PreferencesService, service: NetworkApiProvider, db: AppDatabase) = LocationService(preferencesService, service, db)

    @Provides
    @Singleton
    fun provideOfferRequestService(preferencesService: PreferencesService, service: NetworkApiProvider, db: AppDatabase, app: Application) = OfferRequestListService(preferencesService, service, db, app)

    @Provides
    @Singleton
    fun provideOfferRequestDetailService(preferencesService: PreferencesService, service: NetworkApiProvider, db: AppDatabase, app: Application) = OfferRequestDetailService(preferencesService, service, db, app)


    @Provides
    @Singleton
    fun provideNetworkApiProvider(networkApi: NetworkApi) = NetworkApiProvider(networkApi)

}