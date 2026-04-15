package com.radarcarioca.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.radarcarioca.data.model.AlertLevel

/**
 * Entidade Room para persistência de features geográficas.
 * Separada da entidade de domínio (GeoFeature) para isolar
 * o framework Room da camada de negócio.
 */
@Entity(tableName = "geo_features")
data class GeoFeatureEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val description: String = "",
    val area: String = "",
    val geometryType: String,
    val coordinatesJson: String,
    val centerLat: Double,
    val centerLng: Double,
    val bufferKm: Double = 0.2,
    val alertLevel: AlertLevel = AlertLevel.DANGER
)
