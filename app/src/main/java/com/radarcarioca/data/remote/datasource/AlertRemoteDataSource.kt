package com.radarcarioca.data.remote.datasource

import com.radarcarioca.data.remote.dto.AlertDto
import kotlinx.coroutines.flow.Flow

/**
 * Contrato da fonte de dados remota para alertas de segurança.
 *
 * Isolamento intencional:
 *  - Retorna [AlertDto], nunca modelos de domínio.
 *  - O [AlertRepositoryImpl] é o único consumidor desta interface.
 *  - A implementação concreta ([AlertRemoteDataSourceImpl]) conhece Firebase;
 *    qualquer outro código do projeto não precisa saber disso.
 *
 * Isso permite substituir Firebase por outro backend (ex.: REST API) sem
 * tocar em nada fora da camada data/.
 */
interface AlertRemoteDataSource {

    /**
     * Escuta todos os alertas ativos em tempo real via Firebase.
     * Emite uma nova lista sempre que qualquer alerta muda no servidor.
     * O Flow termina quando o colector é cancelado (lifecycle-aware via viewModelScope).
     */
    fun observeActiveAlerts(): Flow<List<AlertDto>>

    /**
     * Busca snapshot único de todos os alertas ativos.
     * Usado pelo [AlertRepositoryImpl.refreshAlerts] para sincronização forçada.
     *
     * @throws [com.radarcarioca.domain.model.AlertException.PermissionDenied] se as
     *   regras do Firebase negarem acesso.
     * @throws [com.radarcarioca.domain.model.AlertException.NetworkUnavailable] se
     *   não houver conectividade.
     * @throws [com.radarcarioca.domain.model.AlertException.RemoteUnavailable] para
     *   outros erros do Firebase.
     */
    suspend fun fetchActiveAlerts(): List<AlertDto>
}
