package com.triline.billionlights.model.dagger

import android.app.Application
import com.orhanobut.hawk.Hawk
import com.triline.billionlights.model.database.AppDatabase
import com.triline.billionlights.model.retrofit.NetworkApi
import com.triline.billionlights.model.retrofit.NetworkApiProvider
import com.triline.billionlights.service.LocationService
import com.triline.billionlights.service.LoginService
import dagger.Module
import dagger.Provides
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
        if (!Hawk.isBuilt())
            Hawk.init(app).build()
        return PreferencesService()
    }

    @Singleton
    @Provides
    fun provideString(app: Application): StringService {
        return StringService(app)
    }

    @Provides
    @Singleton
    fun provideLoginService(preferencesService: PreferencesService, service: NetworkApiProvider) =
        LoginService(preferencesService, service)

    @Provides
    @Singleton
    fun provideLocationService(
        preferencesService: PreferencesService,
        service: NetworkApiProvider,
        db: AppDatabase
    ) = LocationService(preferencesService, service, db)

    @Provides
    @Singleton
    fun provideNetworkApiProvider(networkApi: NetworkApi) = NetworkApiProvider(networkApi)

}