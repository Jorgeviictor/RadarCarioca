package com.radarcarioca.data.model

import com.radarcarioca.domain.model.SubscriptionPlan

// SubscriptionState e SubscriptionPlan → movidos para domain.model.SubscriptionModels
// Ficam aqui apenas os dados de configuração de produto (SKUs e planos pré-definidos),
// que são específicos do Google Play e não pertencem ao domain.

/** SKUs configurados no Google Play Console */
object SubscriptionSku {
    const val MENSAL      = "radar_carioca_mensal"       // R$ 15/mês
    const val TRIMESTRAL  = "radar_carioca_trimestral"   // R$ 36/3 meses (R$12/mês)
    const val SEMESTRAL   = "radar_carioca_semestral"    // R$ 60/6 meses (R$10/mês)

    val ALL = listOf(MENSAL, TRIMESTRAL, SEMESTRAL)
}

/** Planos pré-definidos (preços reais vêm do Play Console) */
object SubscriptionPlans {
    val MENSAL = SubscriptionPlan(
        sku            = SubscriptionSku.MENSAL,
        name           = "Mensal",
        pricePerMonth  = "R$ 15,00",
        totalPrice     = "R$ 15,00",
        durationMonths = 1,
        badge          = null,
        savings        = null
    )

    val TRIMESTRAL = SubscriptionPlan(
        sku            = SubscriptionSku.TRIMESTRAL,
        name           = "Trimestral",
        pricePerMonth  = "R$ 12,00/mês",
        totalPrice     = "R$ 36,00",
        durationMonths = 3,
        badge          = "MAIS POPULAR",
        savings        = "Economize R$ 9"
    )

    val SEMESTRAL = SubscriptionPlan(
        sku            = SubscriptionSku.SEMESTRAL,
        name           = "Semestral",
        pricePerMonth  = "R$ 10,00/mês",
        totalPrice     = "R$ 60,00",
        durationMonths = 6,
        badge          = "MELHOR VALOR",
        savings        = "Economize R$ 30"
    )

    val ALL = listOf(MENSAL, TRIMESTRAL, SEMESTRAL)
}
