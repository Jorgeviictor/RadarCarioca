package com.radarcarioca.di

import javax.inject.Qualifier

/**
 * Qualifier type-safe para o CoroutineDispatcher de I/O.
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class IoDispatcher

/**
 * Qualifier para o UID do Admin Master (injetado a partir do BuildConfig).
 * Evita hardcodar o UID em qualquer classe de implementação.
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AdminMasterUid
