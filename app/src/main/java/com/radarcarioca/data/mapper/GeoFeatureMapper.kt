package com.radarcarioca.data.mapper

import com.radarcarioca.data.local.entity.GeoFeatureEntity
import com.radarcarioca.data.model.GeoFeature

/**
 * Converte entre entidade Room (GeoFeatureEntity) e entidade de domínio (GeoFeature).
 * Garante que o framework Room não vaze para fora da camada de dados.
 */
fun GeoFeatureEntity.toDomain(): GeoFeature = GeoFeature(
    id              = id,
    name            = name,
    description     = description,
    area            = area,
    geometryType    = geometryType,
    coordinatesJson = coordinatesJson,
    centerLat       = centerLat,
    centerLng       = centerLng,
    bufferKm        = bufferKm,
    alertLevel      = alertLevel
)

fun GeoFeature.toEntity(): GeoFeatureEntity = GeoFeatureEntity(
    id              = id,
    name            = name,
    description     = description,
    area            = area,
    geometryType    = geometryType,
    coordinatesJson = coordinatesJson,
    centerLat       = centerLat,
    centerLng       = centerLng,
    bufferKm        = bufferKm,
    alertLevel      = alertLevel
)
