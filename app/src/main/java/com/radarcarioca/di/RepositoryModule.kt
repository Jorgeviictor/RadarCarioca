package com.radarcarioca.di

import com.radarcarioca.billing.SubscriptionRepositoryImpl
import com.radarcarioca.data.local.DriverSettingsRepositoryImpl
import com.radarcarioca.data.repository.AlertRepositoryImpl
import com.radarcarioca.data.repository.GeoRepositoryImpl
import com.radarcarioca.data.repository.RideHistoryRepositoryImpl
import com.radarcarioca.data.repository.UserRepositoryImpl
import com.radarcarioca.domain.repository.AlertRepository
import com.radarcarioca.domain.repository.DriverSettingsRepository
import com.radarcarioca.domain.repository.GeoRepository
import com.radarcarioca.domain.repository.RideHistoryRepository
import com.radarcarioca.domain.repository.SubscriptionRepository
import com.radarcarioca.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Liga as interfaces do Domain às implementações concretas da camada Data.
 * Este é o ponto central da inversão de dependência da Clean Architecture:
 * o Domain declara o contrato, o Data implementa, o Hilt conecta.
 *
 * Nenhuma ViewModel ou UseCase importa uma classe Impl diretamente —
 * toda a resolução de tipo acontece aqui, em tempo de compilação.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindGeoRepository(impl: GeoRepositoryImpl): GeoRepository

    @Binds
    @Singleton
    abstract fun bindRideHistoryRepository(impl: RideHistoryRepositoryImpl): RideHistoryRepository

    // ─────────────────────────────────────────────────────────────────
    // Alertas de Segurança
    // ─────────────────────────────────────────────────────────────────

    /**
     * Vincula [AlertRepositoryImpl] à interface [AlertRepository].
     * ViewModels e UseCases injetam [AlertRepository] — nunca a impl.
     */
    @Binds
    @Singleton
    abstract fun bindAlertRepository(impl: AlertRepositoryImpl): AlertRepository

    // ─────────────────────────────────────────────────────────────────
    // Preferências do Motorista
    // ─────────────────────────────────────────────────────────────────

    /**
     * Vincula [DriverSettingsRepositoryImpl] à interface [DriverSettingsRepository].
     *
     * Antes desta binding, UseCases injetavam [DriverPreferences] diretamente,
     * criando dependência do Domain na camada Data. Agora o Domain conhece
     * apenas a interface — DataStore é detalhe de implementação.
     */
    @Binds
    @Singleton
    abstract fun bindDriverSettingsRepository(
        impl: DriverSettingsRepositoryImpl
    ): DriverSettingsRepository

    // ─────────────────────────────────────────────────────────────────
    // Assinatura (Google Play Billing)
    // ─────────────────────────────────────────────────────────────────

    /**
     * Vincula [SubscriptionRepositoryImpl] à interface [SubscriptionRepository].
     *
     * Antes desta binding, [ObserveSubscriptionAccessUseCase] injetava [BillingManager]
     * diretamente — dependência do Domain em infra de Google Play.
     * Agora o Domain conhece apenas a interface.
     */
    @Binds
    @Singleton
    abstract fun bindSubscriptionRepository(
        impl: SubscriptionRepositoryImpl
    ): SubscriptionRepository

    // ─────────────────────────────────────────────────────────────────
    // Perfil de Usuário (Firebase Auth + Firestore)
    // ─────────────────────────────────────────────────────────────────

    @Binds
    @Singleton
    abstract fun bindUserRepository(impl: UserRepositoryImpl): UserRepository
}
