package com.radarcarioca.data.remote.datasource

import com.google.firebase.auth.FirebaseUser
import com.radarcarioca.core.DataResult
import com.radarcarioca.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

interface UserRemoteDataSource {
    fun observeCurrentUser(): Flow<DataResult<UserProfile>>
    suspend fun createOrUpdateUserOnLogin(firebaseUser: FirebaseUser): DataResult<UserProfile>
    fun getTesters(): Flow<DataResult<List<UserProfile>>>
    suspend fun grantTesterAccess(email: String, grantedByUid: String): DataResult<Unit>
    suspend fun revokeTesterAccess(targetUid: String): DataResult<Unit>
    suspend fun signOut()
}
