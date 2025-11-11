package com.eic.healthconnectdemo.di

import android.content.Context
import androidx.health.connect.client.HealthConnectClient
import com.eic.healthconnectdemo.data.repository.HealthConnectRepositoryImpl
import com.eic.healthconnectdemo.domain.repository.HealthConnectRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for providing application-level dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    /**
     * Provides HealthConnectClient instance.
     */
    @Provides
    @Singleton
    fun provideHealthConnectClient(
        @ApplicationContext context: Context
    ): HealthConnectClient {
        return HealthConnectClient.getOrCreate(context)
    }

    /**
     * Provides HealthConnectRepository implementation.
     */
    @Provides
    @Singleton
    fun provideHealthConnectRepository(
        healthConnectClient: HealthConnectClient,
        @ApplicationContext context: Context
    ): HealthConnectRepository {
        return HealthConnectRepositoryImpl(healthConnectClient, context)
    }
}
