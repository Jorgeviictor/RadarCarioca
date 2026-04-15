package com.radarcarioca.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.radarcarioca.BuildConfig
import com.radarcarioca.data.remote.datasource.UserRemoteDataSource
import com.radarcarioca.data.remote.datasource.UserRemoteDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthModule {

    @Binds
    @Singleton
    abstract fun bindUserRemoteDataSource(
        impl: UserRemoteDataSourceImpl
    ): UserRemoteDataSource

    companion object {

        @Provides
        @Singleton
        fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

        @Provides
        @Singleton
        fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

        /**
         * Injeta o UID do Admin Master a partir do BuildConfig.
         * O valor real é definido em local.properties (fora do controle de versão).
         */
        @Provides
        @AdminMasterUid
        fun provideAdminMasterUid(): String = BuildConfig.ADMIN_MASTER_UID
    }
}
