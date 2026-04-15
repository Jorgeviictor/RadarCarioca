package com.radarcarioca.billing

import com.radarcarioca.domain.model.SubscriptionState
import com.radarcarioca.domain.repository.SubscriptionRepository
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementação de [SubscriptionRepository] que delega ao [BillingManager].
 *
 * Delegação pura — nenhuma lógica de negócio aqui.
 * O vínculo interface ↔ implementação é feito no [com.radarcarioca.di.RepositoryModule].
 *
 * Localizada no pacote [billing] porque depende de [BillingManager],
 * que é infra de Google Play — correto ficar fora do domain.
 */
@Singleton
class SubscriptionRepositoryImpl @Inject constructor(
    private val billingManager: BillingManager
) : SubscriptionRepository {

    override val subscriptionState: StateFlow<SubscriptionState>
        get() = billingManager.subscriptionState

    override fun refresh() = billingManager.connectAndLoad()
}
