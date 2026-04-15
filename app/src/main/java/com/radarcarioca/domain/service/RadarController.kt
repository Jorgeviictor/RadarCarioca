package com.radarcarioca.domain.service

/**
 * Contrato para controle do ciclo de vida do serviço de radar.
 * Separa a lógica de start/stop de serviço Android da camada de domínio.
 */
interface RadarController {
    fun start()
    fun stop()
    /** Persiste o estado para o BootReceiver reativar o serviço após reinício do dispositivo. */
    fun persistBootState(active: Boolean)
}
