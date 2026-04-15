package com.radarcarioca.domain.usecase

import com.radarcarioca.core.DataResult
import com.radarcarioca.domain.model.UserProfile
import com.radarcarioca.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTestersUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    operator fun invoke(): Flow<DataResult<List<UserProfile>>> =
        userRepository.getTesters()
}
