package com.radarcarioca.data.mapper

import com.radarcarioca.data.local.entity.AlertEntity
import com.radarcarioca.data.remote.dto.AlertDto
import com.radarcarioca.domain.model.SecurityAlert
import com.radarcarioca.domain.model.SecurityAlertSeverity
import com.radarcarioca.domain.model.SecurityAlertSource

// ═══════════════════════════════════════════════════════════════════
// DTO (Firebase) → Entity (Room)
// ═══════════════════════════════════════════════════════════════════

/**
 * Converte o DTO recebido do Firebase para a entidade persistida no Room.
 *
 * Regra de isolamento:
 *  - [AlertDto] nunca ultrapassa esta função em direção ao domínio.
 *  - A conversão ocorre exclusivamente no [AlertRepositoryImpl] ao salvar
 *    o resultado do fetch remoto.
 */
fun AlertDto.toEntity(): AlertEntity = AlertEntity(
    id           = id,
    title        = title,
    description  = description,
    areaName     = areaName,
    lat          = latitude,
    lng          = longitude,
    radiusMeters = radiusMeters,
    severity     = severity,
    source       = source,
    isActive     = isActive,
    createdAt    = createdAt,
    expiresAt    = expiresAt,
    cachedAt     = System.currentTimeMillis()
)

fun List<AlertDto>.toEntityList(): List<AlertEntity> = map { it.toEntity() }

// ═══════════════════════════════════════════════════════════════════
// Entity (Room) → Domain Model
// ═══════════════════════════════════════════════════════════════════

/**
 * Converte a entidade Room para o modelo de domínio puro [SecurityAlert].
 *
 * Single Source of Truth: ViewModels e UseCases sempre recebem [SecurityAlert],
 * nunca [AlertEntity] ou [AlertDto]. O Room é a fonte canônica após o fetch.
 */
fun AlertEntity.toDomain(): SecurityAlert = SecurityAlert(
    id           = id,
    title        = title,
    description  = description,
    areaName     = areaName,
    lat          = lat,
    lng          = lng,
    radiusMeters = radiusMeters,
    severity     = severity.toAlertSeverity(),
    source       = source.toAlertSource(),
    isActive     = isActive,
    createdAt    = createdAt,
    expiresAt    = expiresAt,
    cachedAt     = cachedAt
)

fun List<AlertEntity>.toDomainList(): List<SecurityAlert> = map { it.toDomain() }

// ═══════════════════════════════════════════════════════════════════
// String → Enum (com fallback defensivo)
// ═══════════════════════════════════════════════════════════════════

/**
 * Converte a String armazenada no Room para o enum do domínio.
 *
 * Fallback para HIGH: se o Firebase publicar um novo valor de severity antes
 * que o app seja atualizado, o alerta ainda será exibido com nível alto em vez
 * de causar um crash no motorista em rota.
 */
private fun String.toAlertSeverity(): SecurityAlertSeverity =
    runCatching { SecurityAlertSeverity.valueOf(this) }
        .getOrDefault(SecurityAlertSeverity.HIGH)

private fun String.toAlertSource(): SecurityAlertSource =
    runCatching { SecurityAlertSource.valueOf(this) }
        .getOrDefault(SecurityAlertSource.FIREBASE)
