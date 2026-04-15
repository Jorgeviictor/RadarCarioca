package com.radarcarioca.service

import android.content.Context
import android.os.PowerManager
import android.provider.Settings
import com.radarcarioca.domain.service.PermissionsChecker
import com.radarcarioca.domain.service.SystemStatus
import com.radarcarioca.geo.GeoSecurityManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementação de [PermissionsChecker] que consulta as APIs Android
 * e o cache de features geográficas para compor o [SystemStatus].
 */
@Singleton
class PermissionsCheckerImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val geoSecurityManager: GeoSecurityManager
) : PermissionsChecker {

    override fun getSystemStatus(): SystemStatus {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        return SystemStatus(
            isAccessibilityEnabled = RadarAccessibilityService.isServiceRunning,
            isOverlayEnabled = Settings.canDrawOverlays(context),
            geoFeaturesLoaded = geoSecurityManager.getCachedFeatureCount(),
            isBatteryOptimizationIgnored = powerManager.isIgnoringBatteryOptimizations(context.packageName)
        )
    }
}
