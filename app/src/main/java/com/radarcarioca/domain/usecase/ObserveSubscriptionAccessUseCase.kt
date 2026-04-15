package com.radarcarioca.domain.usecase

import com.radarcarioca.core.DataResult
import com.radarcarioca.domain.model.SubscriptionState
import com.radarcarioca.domain.model.UserRole
import com.radarcarioca.domain.repository.SubscriptionRepository
import com.radarcarioca.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

/**
 * Resultado da verificação de acesso por assinatura + papel do usuário.
 */
data class SubscriptionAccess(
    val state: SubscriptionState,
    val hasAccess: Boolean,
    val userRole: UserRole = UserRole.FREE
)

/**
 * Combina estado da assinatura com o papel do usuário.
 *
 * Regras de acesso:
 *  - ADMIN_MASTER e TESTER → acesso livre independente de assinatura.
 *  - PREMIUM / FREE        → depende de Active ou InTrial no Play Billing.
 */
class ObserveSubscriptionAccessUseCase @Inject constructor(
    private val subscriptionRepository: SubscriptionRepository,
    private val userRepository: UserRepository
) {
    operator fun invoke(): Flow<SubscriptionAccess> =
        combine(
            subscriptionRepository.subscriptionState,
            userRepository.observeCurrentUser()
        ) { subState, userResult ->
            val role = when (userResult) {
                is DataResult.Success -> userResult.data.role
                else -> UserRole.FREE
            }
            val hasAccess = role.hasPrivilegedAccess
                || subState is SubscriptionState.Active
                || subState is SubscriptionState.InTrial
            SubscriptionAccess(state = subState, hasAccess = hasAccess, userRole = role)
        }
}
