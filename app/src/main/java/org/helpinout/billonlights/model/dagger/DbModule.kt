package org.helpinout.billonlights.model.dagger

import android.app.Application
import dagger.Module
import dagger.Provides
import org.helpinout.billonlights.model.database.AppDatabase

@Module
class DbModule {

    @Provides
    fun provideDb(app: Application): AppDatabase {
        return AppDatabase.getInstance(app)
    }


}