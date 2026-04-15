package com.radarcarioca.service

import android.content.Context
import com.radarcarioca.domain.service.RadarController
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementação de [RadarController] que interage com o ForegroundService
 * e persiste o estado de boot via SharedPreferences.
 *
 * Contém todo o código Android que não pode residir na camada de domínio.
 */
@Singleton
class RadarControllerImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : RadarController {

    override fun start() {
        RadarForegroundService.start(context)
    }

    override fun stop() {
        RadarForegroundService.stop(context)
    }

    override fun persistBootState(active: Boolean) {
        context.getSharedPreferences("radar_boot", Context.MODE_PRIVATE)
            .edit().putBoolean("was_active", active).apply()
    }
}
