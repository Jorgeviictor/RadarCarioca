package com.radarcarioca.domain.repository

import com.radarcarioca.data.model.GeoFeature
import kotlinx.coroutines.flow.Flow

/**
 * Contrato de acesso a dados geográficos.
 * O Domain não conhece Room, só esta interface.
 */
interface GeoRepository {
    suspend fun count(): Int
    suspend fun getAll(): List<GeoFeature>
    suspend fun getNearby(lat: Double, lng: Double): List<GeoFeature>
    suspend fun insertAll(features: List<GeoFeature>)
    suspend fun deleteAll()
    fun getAllAreas(): Flow<List<String>>
}
