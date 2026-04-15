package com.radarcarioca.data.repository

import com.radarcarioca.data.local.GeoFeatureDao
import com.radarcarioca.data.mapper.toDomain
import com.radarcarioca.data.mapper.toEntity
import com.radarcarioca.data.model.GeoFeature
import com.radarcarioca.domain.repository.GeoRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementação concreta de GeoRepository.
 * Único lugar do projeto que conhece GeoFeatureDao e o mapper.
 * O Domain vê apenas a interface GeoRepository.
 */
@Singleton
class GeoRepositoryImpl @Inject constructor(
    private val dao: GeoFeatureDao
) : GeoRepository {

    override suspend fun count(): Int = dao.count()

    override suspend fun getAll(): List<GeoFeature> =
        dao.getAllFeatures().map { it.toDomain() }

    override suspend fun getNearby(lat: Double, lng: Double): List<GeoFeature> =
        dao.getFeaturesNearby(lat, lng).map { it.toDomain() }

    override suspend fun insertAll(features: List<GeoFeature>) =
        dao.insertAll(features.map { it.toEntity() })

    override suspend fun deleteAll() = dao.deleteAll()

    override fun getAllAreas(): Flow<List<String>> = dao.getAllAreas()
}
