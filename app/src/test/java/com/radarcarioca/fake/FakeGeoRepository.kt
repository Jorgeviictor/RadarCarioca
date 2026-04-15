package com.radarcarioca.fake

import com.radarcarioca.data.model.GeoFeature
import com.radarcarioca.domain.repository.GeoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/**
 * Repositório fake para testes unitários.
 * Não usa Room — roda em JVM puro sem emulador.
 */
class FakeGeoRepository : GeoRepository {

    private val features = mutableListOf<GeoFeature>()

    override suspend fun count(): Int = features.size

    override suspend fun getAll(): List<GeoFeature> = features.toList()

    override suspend fun getNearby(lat: Double, lng: Double): List<GeoFeature> =
        features.filter {
            Math.abs(it.centerLat - lat) <= 0.05 && Math.abs(it.centerLng - lng) <= 0.05
        }

    override suspend fun insertAll(features: List<GeoFeature>) {
        this.features.addAll(features)
    }

    override suspend fun deleteAll() = features.clear()

    override fun getAllAreas(): Flow<List<String>> =
        flowOf(features.mapNotNull { it.area.ifBlank { null } }.distinct())
}
