package com.triline.billionlights.model.dagger

import android.app.Application
import com.triline.billionlights.model.database.AppDatabase
import dagger.Module
import dagger.Provides

@Module
class DbModule {

    @Provides
    fun provideDb(app: Application): AppDatabase {
        return AppDatabase.getInstance(app)
    }


}