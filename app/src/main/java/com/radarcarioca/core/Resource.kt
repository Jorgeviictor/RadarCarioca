package com.radarcarioca.core

/**
 * Wrapper genérico para estados reativos de dados assíncronos — a "Result<T>" do projeto.
 *
 * Por que não usar kotlin.Result<T> diretamente?
 *  kotlin.Result modela apenas Success/Failure (2 estados). Repositórios com
 *  estratégia offline-first precisam de um terceiro estado — Loading — para emitir
 *  dados cacheados enquanto o refresh remoto está em voo. Sem isso, a UI fica em
 *  branco durante o fetch, o que é inaceitável para o motorista em rota.
 *
 * Uso padrão nos repositórios:
 *   Flow<DataResult<List<SecurityAlert>>>
 *     ├── Loading(cachedData = null)          → primeiro emit, buscando cache
 *     ├── Loading(cachedData = cachedList)    → cache disponível, refresh em andamento
 *     ├── Success(data = freshList)           → dados sincronizados com Firebase
 *     └── Error(exception, cachedData = ...)  → falha de rede, cache preservado
 *
 * O typealias [Result] expõe este tipo com o nome pedido pelo contrato de repositório,
 * sem colidir com kotlin.Result (packages distintos).
 */
sealed class DataResult<out T> {

    /** Dados disponíveis e íntegros. */
    data class Success<T>(val data: T) : DataResult<T>()

    /**
     * Operação em progresso.
     * [cachedData] expõe dados locais enquanto o refresh remoto está em voo,
     * permitindo que a UI já exiba informação útil ao motorista.
     */
    data class Loading<T>(val cachedData: T? = null) : DataResult<T>()

    /**
     * Operação falhou.
     * [cachedData] permite à UI exibir dados desatualizados + banner de erro —
     * fundamental quando o sinal 4G oscila em zonas de risco do Rio.
     */
    data class Error<T>(
        val exception: Throwable,
        val cachedData: T? = null
    ) : DataResult<T>()
}

/**
 * Alias público — permite declarar [Flow<Result<List<SecurityAlert>>>] nas interfaces
 * do domínio sem usar kotlin.Result (que não possui estado Loading).
 *
 * Uso:
 * ```kotlin
 * import com.radarcarioca.core.Result   // importa o typealias, não kotlin.Result
 *
 * fun getAlerts(): Flow<Result<List<SecurityAlert>>>
 * ```
 */
typealias Result<T> = DataResult<T>

/** Transforma o dado interno sem alterar o estado do wrapper. */
inline fun <T, R> DataResult<T>.mapData(transform: (T) -> R): DataResult<R> = when (this) {
    is DataResult.Success -> DataResult.Success(transform(data))
    is DataResult.Loading -> DataResult.Loading(cachedData?.let { transform(it) })
    is DataResult.Error   -> DataResult.Error(exception, cachedData?.let { transform(it) })
}
