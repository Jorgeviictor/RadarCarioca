package com.radarcarioca.data.remote.dto

import com.google.firebase.database.IgnoreExtraProperties
import com.google.firebase.database.PropertyName

/**
 * Data Transfer Object — representa a estrutura JSON do Firebase Realtime Database.
 *
 * Estrutura esperada no Firebase:
 * ```
 * /security_alerts/{alertId}
 *   ├── title:          "Operação Policial - Complexo da Maré"
 *   ├── description:    "Confronto armado na Rua X. Evite a área."
 *   ├── area_name:      "Complexo da Maré"
 *   ├── latitude:       -22.8901
 *   ├── longitude:      -43.2491
 *   ├── radius_meters:  500
 *   ├── severity:       "CRITICAL"
 *   ├── source:         "FIREBASE"
 *   ├── is_active:      true
 *   ├── created_at:     1718000000000
 *   └── expires_at:     1718086400000  (ou null)
 * ```
 *
 * Regras de isolamento:
 *  - Esta classe NUNCA chega às ViewModels — é mapeada para [Alert] via [AlertMapper].
 *  - Campos opcionais têm valores padrão para suportar registros incompletos no Firebase.
 *  - @IgnoreExtraProperties garante que campos extras do Firebase não causem crash.
 */
@IgnoreExtraProperties
data class AlertDto(
    val id: String = "",

    val title: String = "",

    val description: String = "",

    @get:PropertyName("area_name")
    @set:PropertyName("area_name")
    var areaName: String = "",

    val latitude: Double = 0.0,

    val longitude: Double = 0.0,

    @get:PropertyName("radius_meters")
    @set:PropertyName("radius_meters")
    var radiusMeters: Int = 300,

    val severity: String = "HIGH",

    val source: String = "FIREBASE",

    @get:PropertyName("is_active")
    @set:PropertyName("is_active")
    var isActive: Boolean = true,

    @get:PropertyName("created_at")
    @set:PropertyName("created_at")
    var createdAt: Long = 0L,

    @get:PropertyName("expires_at")
    @set:PropertyName("expires_at")
    var expiresAt: Long? = null
) {
    // Construtor sem argumentos exigido pela desserialização do Firebase
    constructor() : this(id = "")
}
