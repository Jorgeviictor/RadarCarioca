package com.radarcarioca.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidade Room para persistência do histórico de corridas.
 * Separada da entidade de domínio (RideRecord).
 */
@Entity(tableName = "ride_history")
data class RideRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val destinationText: String,
    val fareValue: Double,
    val netProfit: Double,
    val profitPerKm: Double,
    val wasAccepted: Boolean,
    val hadSecurityAlert: Boolean,
    val securityZoneName: String = "",
    val sourceApp: String = "uber"
)
