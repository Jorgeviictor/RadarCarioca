package com.radarcarioca.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.radarcarioca.domain.model.DriverConfig
import com.radarcarioca.domain.model.FuelType
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "driver_config")

/**
 * Repositório das configurações do motorista usando DataStore.
 * Persiste: preço do combustível, consumo, metas financeiras, etc.
 */
@Singleton
class DriverPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        val KEY_FUEL_PRICE = doublePreferencesKey("fuel_price")
        val KEY_KM_PER_LITER = doublePreferencesKey("km_per_liter")
        val KEY_PLATFORM_FEE = doublePreferencesKey("platform_fee")
        val KEY_TARGET_KM = doublePreferencesKey("target_per_km")
        val KEY_TARGET_HOUR = doublePreferencesKey("target_per_hour")
        val KEY_FUEL_TYPE = stringPreferencesKey("fuel_type")
        val KEY_BUFFER_METERS = intPreferencesKey("buffer_meters")
        val KEY_RADAR_ENABLED = booleanPreferencesKey("radar_enabled")
        val KEY_ONBOARDING_DONE = booleanPreferencesKey("onboarding_done")
        val KEY_GEOJSON_VERSION = stringPreferencesKey("geojson_version")
        val KEY_SCREENSHOT_RETENTION_DAYS = intPreferencesKey("screenshot_retention_days")
    }

    val driverConfig: Flow<DriverConfig> = context.dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { prefs ->
            DriverConfig(
                fuelPricePerLiter = prefs[KEY_FUEL_PRICE] ?: 6.20,
                kmPerLiter = prefs[KEY_KM_PER_LITER] ?: 12.0,
                platformFeePercent = prefs[KEY_PLATFORM_FEE] ?: 0.20,
                targetProfitPerKm = prefs[KEY_TARGET_KM] ?: 2.50,
                targetProfitPerHour = prefs[KEY_TARGET_HOUR] ?: 35.0,
                fuelType = FuelType.valueOf(prefs[KEY_FUEL_TYPE] ?: FuelType.GASOLINA.name),
                bufferMeters = prefs[KEY_BUFFER_METERS] ?: 300,
                screenshotRetentionDays = prefs[KEY_SCREENSHOT_RETENTION_DAYS] ?: 30
            )
        }

    val isRadarEnabled: Flow<Boolean> = context.dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { it[KEY_RADAR_ENABLED] ?: false }

    val isOnboardingDone: Flow<Boolean> = context.dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { it[KEY_ONBOARDING_DONE] ?: false }

    suspend fun saveConfig(config: DriverConfig) {
        context.dataStore.edit { prefs ->
            prefs[KEY_FUEL_PRICE] = config.fuelPricePerLiter
            prefs[KEY_KM_PER_LITER] = config.kmPerLiter
            prefs[KEY_PLATFORM_FEE] = config.platformFeePercent
            prefs[KEY_TARGET_KM] = config.targetProfitPerKm
            prefs[KEY_TARGET_HOUR] = config.targetProfitPerHour
            prefs[KEY_FUEL_TYPE] = config.fuelType.name
            prefs[KEY_BUFFER_METERS] = config.bufferMeters
            prefs[KEY_SCREENSHOT_RETENTION_DAYS] = config.screenshotRetentionDays
        }
    }

    suspend fun setRadarEnabled(enabled: Boolean) {
        context.dataStore.edit { it[KEY_RADAR_ENABLED] = enabled }
    }

    suspend fun setOnboardingDone(done: Boolean) {
        context.dataStore.edit { it[KEY_ONBOARDING_DONE] = done }
    }

    suspend fun saveGeoJsonVersion(version: String) {
        context.dataStore.edit { it[KEY_GEOJSON_VERSION] = version }
    }
}
