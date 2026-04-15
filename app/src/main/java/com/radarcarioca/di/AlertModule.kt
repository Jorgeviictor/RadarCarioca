package com.radarcarioca.di

import com.google.firebase.database.FirebaseDatabase
import com.radarcarioca.data.local.datasource.AlertLocalDataSource
import com.radarcarioca.data.local.datasource.AlertLocalDataSourceImpl
import com.radarcarioca.data.remote.datasource.AlertRemoteDataSource
import com.radarcarioca.data.remote.datasource.AlertRemoteDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

/**
 * Módulo Hilt do subsistema de Alertas de Segurança.
 *
 * Separado em dois objetos complementares:
 *  - [AlertProvides]  → dependências que precisam de lógica de construção (@Provides)
 *  - [AlertBindings]  → interfaces ligadas às implementações concretas (@Binds)
 *
 * Alterações em relação à versão anterior:
 *  - AlertDao removido daqui → movido para DatabaseModule (coesão de DAOs)
 *  - @Named("IoDispatcher") substituído por @IoDispatcher (type-safe, sem risco de typo)
 *  - @Singleton adicionado ao dispatcher (evita instâncias redundantes no grafo)
 *
 * Princípio de Inversão de Dependência (SOLID-D):
 *  Nenhum código fora deste módulo conhece [AlertLocalDataSourceImpl]
 *  ou [AlertRemoteDataSourceImpl] — todo consumidor recebe a interface.
 */

@Module
@InstallIn(SingletonComponent::class)
object AlertProvides {

    // ─────────────────────────────────────────────────────────────────
    // Firebase
    // ─────────────────────────────────────────────────────────────────

    /**
     * Fornece a instância singleton do Firebase Realtime Database.
     * setPersistenceEnabled(true) mantém cache local, complementando Room
     * para ambientes com sinal intermitente (motoristas em tuneis, garagens).
     *
     * ATENÇÃO: setPersistenceEnabled deve ser chamado antes de qualquer
     * operação de banco — por isso está aqui no @Provides e não em Application.
     */
    @Provides
    @Singleton
    fun provideFirebaseDatabase(): FirebaseDatabase =
        FirebaseDatabase.getInstance().also { db ->
            db.setPersistenceEnabled(true)
        }

    // ─────────────────────────────────────────────────────────────────
    // Coroutine Dispatcher
    // ─────────────────────────────────────────────────────────────────

    /**
     * Dispatcher de I/O injetado nos repositórios via @IoDispatcher.
     *
     * @Singleton: Dispatchers.IO é internamente um singleton Kotlin,
     * mas sem o escopo o Hilt criaria um wrapper novo a cada injeção.
     *
     * @IoDispatcher (Qualifiers.kt) substitui @Named("IoDispatcher"):
     * erros de typo viram erro de compilação, não runtime.
     *
     * Substituível por TestCoroutineDispatcher nos testes unitários
     * sem alterar nenhuma classe de produção.
     */
    @Provides
    @Singleton
    @IoDispatcher
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO
}

@Module
@InstallIn(SingletonComponent::class)
abstract class AlertBindings {

    // ─────────────────────────────────────────────────────────────────
    // Data Sources
    // ─────────────────────────────────────────────────────────────────

    @Binds
    @Singleton
    abstract fun bindAlertLocalDataSource(
        impl: AlertLocalDataSourceImpl
    ): AlertLocalDataSource

    @Binds
    @Singleton
    abstract fun bindAlertRemoteDataSource(
        impl: AlertRemoteDataSourceImpl
    ): AlertRemoteDataSource
}
