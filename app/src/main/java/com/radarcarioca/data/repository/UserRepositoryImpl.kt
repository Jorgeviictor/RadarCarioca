package com.radarcarioca.data.repository

import com.google.firebase.auth.FirebaseUser
import com.radarcarioca.core.DataResult
import com.radarcarioca.data.remote.datasource.UserRemoteDataSource
import com.radarcarioca.domain.model.UserProfile
import com.radarcarioca.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val remoteDataSource: UserRemoteDataSource
) : UserRepository {

    override fun observeCurrentUser(): Flow<DataResult<UserProfile>> =
        remoteDataSource.observeCurrentUser()

    override suspend fun createOrUpdateUserOnLogin(firebaseUser: FirebaseUser): DataResult<UserProfile> =
        remoteDataSource.createOrUpdateUserOnLogin(firebaseUser)

    override fun getTesters(): Flow<DataResult<List<UserProfile>>> =
        remoteDataSource.getTesters()

    override suspend fun grantTesterAccess(email: String, grantedByUid: String): DataResult<Unit> =
        remoteDataSource.grantTesterAccess(email, grantedByUid)

    override suspend fun revokeTesterAccess(targetUid: String): DataResult<Unit> =
        remoteDataSource.revokeTesterAccess(targetUid)

    override suspend fun signOut() =
        remoteDataSource.signOut()
}
