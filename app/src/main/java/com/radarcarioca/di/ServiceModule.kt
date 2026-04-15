package com.radarcarioca.di

import com.radarcarioca.domain.service.PermissionsChecker
import com.radarcarioca.domain.service.RadarController
import com.radarcarioca.service.PermissionsCheckerImpl
import com.radarcarioca.service.RadarControllerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Liga as interfaces de serviço do Domain às implementações concretas da camada Service.
 * Segue o mesmo padrão de inversão de dependência do RepositoryModule.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class ServiceModule {

    @Binds
    @Singleton
    abstract fun bindRadarController(impl: RadarControllerImpl): RadarController

    @Binds
    @Singleton
    abstract fun bindPermissionsChecker(impl: PermissionsCheckerImpl): PermissionsChecker
}
