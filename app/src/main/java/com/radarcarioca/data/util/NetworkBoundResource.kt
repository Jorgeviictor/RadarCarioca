package com.radarcarioca.data.util

import com.radarcarioca.core.DataResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

/**
 * Utilitário de estratégia de cache — implementa **Single Source of Truth** com **offline-first**.
 *
 * ```
 * Sequência de emissões:
 *  1. DataResult.Loading(cachedData)    → cache local imediato, UI já responde
 *  2. [fetch] é chamado (Firebase/API)
 *     ├─ OK  → [saveFetchResult] → Room atualizado
 *     │         → DataResult.Success (via Room, não via DTO)
 *     └─ ERRO → DataResult.Error(exception, cachedData)
 *                → motorista sem sinal ainda vê dados do cache
 * ```
 *
 * Por que emitir via Room em vez do DTO diretamente?
 *  Room é a Single Source of Truth. Emitir dados frescos pelo mesmo pipeline
 *  do cache garante consistência de tipo e estado — o ViewModel não precisa
 *  distinguir se os dados vieram do disco ou da rede.
 *
 * @param LocalType  tipo retornado pelo Room   (ex.: List<AlertEntity>)
 * @param RemoteType tipo retornado pelo Firebase (ex.: List<AlertDto>)
 * @param DomainType modelo de domínio final     (ex.: List<SecurityAlert>)
 */
inline fun <LocalType, RemoteType, DomainType> networkBoundResource(
    crossinline query: () -> Flow<LocalType>,
    crossinline fetch: suspend () -> RemoteType,
    crossinline saveFetchResult: suspend (RemoteType) -> Unit,
    crossinline mapToDomain: (LocalType) -> DomainType,
    crossinline shouldFetch: (LocalType?) -> Boolean = { true },
    crossinline onFetchFailed: (Throwable) -> Unit = {}
): Flow<DataResult<DomainType>> = flow {

    // 1. Snapshot inicial do Room → Loading com cache (null se banco vazio)
    val cachedData = runCatching { query().first() }.getOrNull()
    emit(DataResult.Loading(cachedData?.let { mapToDomain(it) }))

    if (shouldFetch(cachedData)) {
        val fetchResult = runCatching { fetch() }

        if (fetchResult.isSuccess) {
            // 2. Sucesso: persiste no Room — o Flow abaixo emitirá o dado fresco
            saveFetchResult(fetchResult.getOrThrow())
        } else {
            val error = fetchResult.exceptionOrNull()!!
            onFetchFailed(error)
            // 3. Falha: Error com dados cacheados — UI degrada com graciosidade
            emit(DataResult.Error(error, cachedData?.let { mapToDomain(it) }))
        }
    }

    // 4. Observa Room indefinidamente — emite Success a cada gravação (upsert ou manual)
    emitAll(
        query().map { localData -> DataResult.Success(mapToDomain(localData)) }
    )
}
