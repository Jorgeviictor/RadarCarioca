package com.radarcarioca.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entidade Room para cache local de alertas de segurança dinâmicos.
 *
 * Separação de responsabilidades:
 *  - [AlertDto]    → estrutura Firebase (camada remote)
 *  - [AlertEntity] → estrutura Room     (camada local)   ← este arquivo
 *  - [Alert]       → modelo de domínio  (camada domain)
 *
 * Design decisions:
 *  - PrimaryKey é String (ID vindo do Firebase) — não autoGenerate para
 *    garantir idempotência no upsert via OnConflictStrategy.REPLACE.
 *  - Index em (isActive, lat, lng) otimiza a query geoespacial frequente
 *    do RadarForegroundService ao checar destino de corrida.
 *  - [cachedAt] permite implementar TTL (Time-To-Live) no purge de cache.
 *  - severity/source são armazenados como String para evitar migrations ao
 *    adicionar novos valores nos enums.
 */
@Entity(
    tableName = "security_alerts",
    indices = [
        Index(value = ["isActive"]),
        Index(value = ["lat", "lng"])
    ]
)
data class AlertEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val areaName: String,
    val lat: Double,
    val lng: Double,
    val radiusMeters: Int,
    val severity: String,           // "CRITICAL" | "HIGH" | "MEDIUM" | "LOW"
    val source: String,             // "FIREBASE" | "LOCAL_MANUAL" | "COMMUNITY"
    val isActive: Boolean,
    val createdAt: Long,
    val expiresAt: Long?,           // null = sem expiração
    val cachedAt: Long = System.currentTimeMillis()
)
