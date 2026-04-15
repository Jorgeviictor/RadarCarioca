package com.radarcarioca.service

import android.util.Log
import com.radarcarioca.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "GeocodingService"
private val MAPS_API_KEY get() = BuildConfig.MAPS_API_KEY

@Singleton
class GeocodingService @Inject constructor() {

    private val geocodeCache = mutableMapOf<String, Pair<Double, Double>>()

    suspend fun geocode(address: String): Pair<Double, Double> = withContext(Dispatchers.IO) {
        geocodeCache[address]?.let { return@withContext it }

        val result = tryGoogleGeocode(address)
        if (result.first != 0.0) {
            geocodeCache[address] = result
            return@withContext result
        }

        val fallback = knownRioLocations(address)
        if (fallback.first != 0.0) {
            Log.d(TAG, "Fallback offline para: $address → $fallback")
            geocodeCache[address] = fallback
        }
        fallback
    }

    private suspend fun tryGoogleGeocode(address: String): Pair<Double, Double> {
        return try {
            val query = "$address, Rio de Janeiro, Brasil".replace(" ", "+")
            val url = "https://maps.googleapis.com/maps/api/geocode/json" +
                    "?address=$query" +
                    "&key=$MAPS_API_KEY" +
                    "&region=br"

            val response = URL(url).readText()
            val json = JSONObject(response)

            if (json.getString("status") == "OK") {
                val location = json
                    .getJSONArray("results")
                    .getJSONObject(0)
                    .getJSONObject("geometry")
                    .getJSONObject("location")
                Pair(location.getDouble("lat"), location.getDouble("lng"))
            } else {
                Pair(0.0, 0.0)
            }
        } catch (e: Exception) {
            Log.w(TAG, "Geocoding falhou para '$address': ${e.message}")
            Pair(0.0, 0.0)
        }
    }

    private fun knownRioLocations(address: String): Pair<Double, Double> {
        val lower = address.lowercase()
        return when {
            lower.contains("barra da tijuca") || lower.contains("barra tijuca") -> Pair(-22.9985, -43.3654)
            lower.contains("ipanema") -> Pair(-22.9838, -43.2096)
            lower.contains("copacabana") -> Pair(-22.9711, -43.1873)
            lower.contains("leblon") -> Pair(-22.9863, -43.2243)
            lower.contains("botafogo") -> Pair(-22.9519, -43.1793)
            lower.contains("flamengo") -> Pair(-22.9308, -43.1743)
            lower.contains("centro") && lower.contains("rio") -> Pair(-22.9068, -43.1729)
            lower.contains("galeão") || lower.contains("aeroporto") -> Pair(-22.8106, -43.2498)
            lower.contains("maracanã") || lower.contains("maracana") -> Pair(-22.9122, -43.2302)
            lower.contains("tijuca") -> Pair(-22.9200, -43.2450)
            lower.contains("méier") || lower.contains("meier") -> Pair(-22.8957, -43.2790)
            lower.contains("campo grande") -> Pair(-22.9032, -43.5614)
            lower.contains("santa cruz") -> Pair(-22.9220, -43.6890)
            lower.contains("bangu") -> Pair(-22.8700, -43.4700)
            lower.contains("penha") -> Pair(-22.8400, -43.2800)
            lower.contains("madureira") -> Pair(-22.8700, -43.3300)
            lower.contains("nova iguaçu") -> Pair(-22.7600, -43.4500)
            lower.contains("duque de caxias") -> Pair(-22.7800, -43.3100)
            lower.contains("nilópolis") || lower.contains("nilopolis") -> Pair(-22.8020, -43.4200)
            lower.contains("belford roxo") -> Pair(-22.7640, -43.3970)
            lower.contains("são joão de meriti") -> Pair(-22.8000, -43.3700)
            lower.contains("chapadão") || lower.contains("acari") -> Pair(-22.8421, -43.3512)
            lower.contains("maré") || lower.contains("mare") -> Pair(-22.8601, -43.2490)
            lower.contains("jacarezinho") -> Pair(-22.8710, -43.2680)
            else -> Pair(0.0, 0.0)
        }
    }

    fun clearCache() = geocodeCache.clear()
    fun getCacheSize() = geocodeCache.size
}