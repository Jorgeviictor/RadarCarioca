package com.radarcarioca.domain.service

/**
 * Estado agregado do sistema: permissões Android e dados carregados.
 */
data class SystemStatus(
    val isAccessibilityEnabled: Boolean,
    val isOverlayEnabled: Boolean,
    val geoFeaturesLoaded: Int,
    val isBatteryOptimizationIgnored: Boolean = false
)

/**
 * Contrato para verificação de permissões e status do sistema.
 * Abstrai as APIs Android (Settings, AccessibilityService) da camada de domínio.
 */
interface PermissionsChecker {
    fun getSystemStatus(): SystemStatus
}
