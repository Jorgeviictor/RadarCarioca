package com.radarcarioca.domain.model

/**
 * Modelos de assinatura — entidades de domínio puras.
 *
 * Movidas de [com.radarcarioca.data.model.SubscriptionModels] para o domain porque:
 * - [SubscriptionState] é uma regra de negócio (quem tem acesso ao app?).
 * - [ObserveSubscriptionAccessUseCase] precisa de [SubscriptionState] → domain não
 *   pode importar da camada Data.
 * - [BillingManager] (Data) importa daqui — direção correta: Data → Domain.
 *
 * O que ficou em data.model:
 * - [SubscriptionSku] — strings de produto do Google Play (detalhe de infra).
 * - [SubscriptionPlans] — valores de exibição (preços, badges) — configuração de produto.
 */

/** Plano de assinatura disponível para compra */
data class SubscriptionPlan(
    val sku: String,
    val name: String,
    val pricePerMonth: String,     // "R$ 15,00" (exibição)
    val totalPrice: String,        // "R$ 15,00", "R$ 36,00", "R$ 60,00"
    val durationMonths: Int,
    val badge: String? = null,     // "MAIS POPULAR", "MELHOR VALOR", null
    val savings: String? = null    // "Economize R$ 18", null
)

/** Estado atual da assinatura do usuário */
sealed class SubscriptionState {
    /** Nunca assinou — período trial disponível (14 dias grátis) */
    object TrialAvailable : SubscriptionState()

    /** Em período trial gratuito — exibe dias restantes */
    data class InTrial(val daysRemaining: Int) : SubscriptionState()

    /** Assinatura ativa */
    data class Active(val plan: SubscriptionPlan) : SubscriptionState()

    /** Trial expirado ou assinatura cancelada — exibe paywall */
    object Expired : SubscriptionState()

    /** Carregando status da assinatura */
    object Loading : SubscriptionState()
}
