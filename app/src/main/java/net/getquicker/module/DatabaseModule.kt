package net.getquicker.module

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import net.getquicker.db.AppDatabase
import net.getquicker.db.dao.ActionDao
import net.getquicker.db.dao.DeviceDao
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DatabaseModule {

    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getInstance(context)
    }

    @Provides
    fun provideActionDao(appDatabase: AppDatabase): ActionDao {
        return appDatabase.actionDao()
    }

    @Provides
    fun provideDeviceDao(appDatabase: AppDatabase): DeviceDao {
        return appDatabase.deviceDao()
    }

}
