package com.radarcarioca.domain.usecase

import com.radarcarioca.core.DataResult
import com.radarcarioca.domain.model.UserRole
import com.radarcarioca.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ObserveUserRoleUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    operator fun invoke(): Flow<UserRole> =
        userRepository.observeCurrentUser().map { result ->
            when (result) {
                is DataResult.Success -> result.data.role
                else -> UserRole.FREE
            }
        }
}
