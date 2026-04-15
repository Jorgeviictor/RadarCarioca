package com.radarcarioca.domain.usecase

import com.radarcarioca.core.DataResult
import com.radarcarioca.domain.repository.UserRepository
import javax.inject.Inject

class GrantTesterAccessUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(email: String, grantedByUid: String): DataResult<Unit> =
        userRepository.grantTesterAccess(email, grantedByUid)
}
