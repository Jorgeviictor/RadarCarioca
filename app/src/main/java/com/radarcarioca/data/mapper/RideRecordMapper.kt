package com.radarcarioca.data.mapper

import com.radarcarioca.data.local.entity.RideRecordEntity
import com.radarcarioca.data.model.RideRecord

/**
 * Converte entre entidade Room (RideRecordEntity) e entidade de domínio (RideRecord).
 */
fun RideRecordEntity.toDomain(): RideRecord = RideRecord(
    id                = id,
    timestamp         = timestamp,
    destinationText   = destinationText,
    fareValue         = fareValue,
    netProfit         = netProfit,
    profitPerKm       = profitPerKm,
    wasAccepted       = wasAccepted,
    hadSecurityAlert  = hadSecurityAlert,
    securityZoneName  = securityZoneName,
    sourceApp         = sourceApp
)

fun RideRecord.toEntity(): RideRecordEntity = RideRecordEntity(
    id                = id,
    timestamp         = timestamp,
    destinationText   = destinationText,
    fareValue         = fareValue,
    netProfit         = netProfit,
    profitPerKm       = profitPerKm,
    wasAccepted       = wasAccepted,
    hadSecurityAlert  = hadSecurityAlert,
    securityZoneName  = securityZoneName,
    sourceApp         = sourceApp
)
