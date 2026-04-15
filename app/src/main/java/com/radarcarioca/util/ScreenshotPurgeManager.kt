package com.radarcarioca.util

import android.content.Context
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "ScreenshotPurgeManager"

/**
 * Gerencia a limpeza automática dos prints/capturas de tela do app.
 *
 * O diretório padrão é: getExternalFilesDir("screenshots") ou
 * filesDir/screenshots (se armazenamento externo indisponível).
 *
 * A limpeza ocorre no onCreate() do RadarForegroundService.
 */
@Singleton
class ScreenshotPurgeManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        const val SUBDIR = "screenshots"
        val ALLOWED_RETENTION_DAYS = listOf(30, 45, 90)
        const val DEFAULT_RETENTION_DAYS = 30
    }

    /**
     * Apaga arquivos de imagem na pasta de screenshots mais antigos que [retentionDays] dias.
     * @return número de arquivos removidos
     */
    fun purge(retentionDays: Int): Int {
        val dir = getScreenshotDir() ?: return 0
        if (!dir.exists()) {
            Log.d(TAG, "Pasta de screenshots não existe ainda, nada a limpar.")
            return 0
        }

        val cutoffMs = System.currentTimeMillis() - retentionDays.toLong() * 24 * 60 * 60 * 1000
        var deleted = 0

        dir.listFiles()
            ?.filter { it.isFile && it.isImageFile() && it.lastModified() < cutoffMs }
            ?.forEach { file ->
                if (file.delete()) {
                    deleted++
                    Log.d(TAG, "Removido: ${file.name} (${file.lastModified()})")
                } else {
                    Log.w(TAG, "Falha ao remover: ${file.name}")
                }
            }

        Log.i(TAG, "Purge concluído: $deleted arquivo(s) removido(s) (retenção: $retentionDays dias)")
        return deleted
    }

    /** Retorna o diretório de screenshots, criando-o se necessário. */
    fun getScreenshotDir(): File? {
        val base = context.getExternalFilesDir(null) ?: context.filesDir
        return File(base, SUBDIR).also {
            if (!it.exists()) it.mkdirs()
        }
    }

    private fun File.isImageFile(): Boolean {
        val ext = extension.lowercase()
        return ext in listOf("png", "jpg", "jpeg", "webp")
    }
}
