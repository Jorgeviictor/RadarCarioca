package com.radarcarioca.domain.usecase

import com.radarcarioca.domain.repository.UserRepository
import javax.inject.Inject

class SignOutUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke() = userRepository.signOut()
}
