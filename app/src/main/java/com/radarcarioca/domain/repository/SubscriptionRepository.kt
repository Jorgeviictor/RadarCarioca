package com.radarcarioca.domain.repository

import com.radarcarioca.domain.model.SubscriptionState
import kotlinx.coroutines.flow.StateFlow

/**
 * Contrato do domain para o estado de assinatura do usuário.
 *
 * ─── Por que esta interface existe? ──────────────────────────────────
 * [ObserveSubscriptionAccessUseCase] precisava importar [BillingManager] diretamente,
 * criando dependência do Domain em infra de billing (Google Play).
 * Esta interface inverte a dependência: o Domain declara o contrato,
 * [com.radarcarioca.billing.SubscriptionRepositoryImpl] implementa via BillingManager.
 *
 * ─── Por que StateFlow e não Flow? ───────────────────────────────────
 * [BillingManager] já mantém um [MutableStateFlow] internamente. Expor como
 * StateFlow preserva a semântica de "último valor sempre disponível" —
 * qualquer coletor recebe o estado atual imediatamente ao assinar.
 */
interface SubscriptionRepository {

    /** Estado atual da assinatura, reativo. Emite imediatamente ao coletar. */
    val subscriptionState: StateFlow<SubscriptionState>

    /**
     * Força reconexão com o billing client e revalidação do status.
     * Chamado no fluxo de "Restaurar Compra" na tela de paywall.
     */
    fun refresh()
}
