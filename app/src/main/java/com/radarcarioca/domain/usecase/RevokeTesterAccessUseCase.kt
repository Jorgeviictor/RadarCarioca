package com.radarcarioca.domain.usecase

import com.radarcarioca.core.DataResult
import com.radarcarioca.domain.repository.UserRepository
import javax.inject.Inject

class RevokeTesterAccessUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(targetUid: String): DataResult<Unit> =
        userRepository.revokeTesterAccess(targetUid)
}
