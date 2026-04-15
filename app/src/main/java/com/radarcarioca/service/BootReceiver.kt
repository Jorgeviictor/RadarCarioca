package com.radarcarioca.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

/**
 * Reinicia o RadarForegroundService após reboot do dispositivo.
 * Requer permissão RECEIVE_BOOT_COMPLETED no AndroidManifest.
 */
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            Log.i("BootReceiver", "Boot detectado — verificando se Radar estava ativo...")
            // Só reinicia se o motorista havia deixado o radar ativo
            val prefs = context.getSharedPreferences("radar_boot", Context.MODE_PRIVATE)
            if (prefs.getBoolean("was_active", false)) {
                Log.i("BootReceiver", "Reiniciando RadarForegroundService...")
                RadarForegroundService.start(context)
            }
        }
    }
}
