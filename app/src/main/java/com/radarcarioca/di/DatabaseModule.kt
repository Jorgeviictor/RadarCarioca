package com.radarcarioca.di

import android.content.Context
import androidx.room.Room
import com.radarcarioca.data.local.AlertDao
import com.radarcarioca.data.local.GeoFeatureDao
import com.radarcarioca.data.local.RadarDatabase
import com.radarcarioca.data.local.RideHistoryDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Módulo exclusivo para infraestrutura de banco de dados.
 * Responsabilidade única: criar e fornecer Room Database e DAOs.
 *
 * Todos os DAOs ficam aqui — AlertDao foi movido do AlertModule para manter
 * a coesão: qualquer DAO é responsabilidade deste módulo, não dos módulos
 * de feature que os consomem.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideRadarDatabase(@ApplicationContext context: Context): RadarDatabase =
        Room.databaseBuilder(
            context,
            RadarDatabase::class.java,
            "radar_carioca.db"
        )
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    @Singleton
    fun provideAlertDao(db: RadarDatabase): AlertDao = db.alertDao()

    @Provides
    @Singleton
    fun provideGeoFeatureDao(db: RadarDatabase): GeoFeatureDao = db.geoFeatureDao()

    @Provides
    @Singleton
    fun provideRideHistoryDao(db: RadarDatabase): RideHistoryDao = db.rideHistoryDao()
}
