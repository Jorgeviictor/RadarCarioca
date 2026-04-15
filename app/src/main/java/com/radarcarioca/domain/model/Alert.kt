package com.radarcarioca.domain.model

/**
 * Entidade de domínio pura para um Alerta de Segurança ativo.
 *
 * Sem anotações Room, sem dependências Firebase — testável em JVM puro.
 * Esta é a única representação de "alerta" que ViewModels e UseCases conhecem.
 *
 * Diferença conceitual em relação a [com.radarcarioca.data.model.GeoFeature]:
 *  - GeoFeature     → zona de risco **permanente** (GeoJSON estático, carregado na init).
 *  - SecurityAlert  → evento de segurança **dinâmico** (criado em tempo real pelo Firebase,
 *                     ex.: operação policial, bloqueio de via, tiroteio reportado).
 */
data class SecurityAlert(
    val id: String,
    val title: String,
    val description: String,
    val areaName: String,
    val lat: Double,
    val lng: Double,
    val radiusMeters: Int,
    val severity: SecurityAlertSeverity,
    val source: SecurityAlertSource,
    val isActive: Boolean,
    val createdAt: Long,
    val expiresAt: Long?,       // null = sem expiração automática
    val cachedAt: Long          // timestamp de quando foi salvo no Room
)

/** Grau de perigo do alerta — determina o overlay e o comportamento de vibração. */
enum class SecurityAlertSeverity {
    CRITICAL,   // Overlay ROXO + vibração imediata (ex.: tiroteio ativo)
    HIGH,       // Overlay ROXO (ex.: operação policial em curso)
    MEDIUM,     // Overlay AMARELO (ex.: bloqueio de pista, manifestação)
    LOW         // Apenas informativo no Dashboard
}

/** Origem do alerta — permite filtragens, auditoria e controle de confiança. */
enum class SecurityAlertSource {
    FIREBASE,       // Dado oficial vindo do Realtime Database
    LOCAL_MANUAL,   // Inserido manualmente pelo motorista (offline)
    COMMUNITY       // Reportado por outros motoristas (roadmap futuro)
}

/**
 * Exceções tipadas do módulo de alertas.
 *
 * Usar sealed class em vez de strings genéricas em catch permite que o
 * [AlertRepository] propague falhas semânticas, e a ViewModel decida
 * exibir mensagens diferenciadas (ex.: "sem sinal" vs "permissão negada").
 */
sealed class SecurityAlertException(message: String, cause: Throwable? = null) :
    Exception(message, cause) {

    /** Firebase retornou permissão negada (regras do Realtime Database). */
    class PermissionDenied(cause: Throwable? = null) :
        SecurityAlertException("Permissão negada ao acessar alertas no Firebase.", cause)

    /**
     * Sem conectividade de rede.
     * Cenário esperado em zonas de risco (Complexo da Maré, Baixada Fluminense)
     * onde o sinal 4G é intermitente.
     */
    class NetworkUnavailable(cause: Throwable? = null) :
        SecurityAlertException("Sem conexão de rede. Exibindo alertas do cache local.", cause)

    /** Erro genérico do Firebase (ex.: timeout, snapshot inválido). */
    class RemoteUnavailable(message: String, cause: Throwable? = null) :
        SecurityAlertException(message, cause)

    /** Falha de leitura/escrita no banco Room. */
    class LocalStorageFailure(message: String, cause: Throwable? = null) :
        SecurityAlertException(message, cause)
}
