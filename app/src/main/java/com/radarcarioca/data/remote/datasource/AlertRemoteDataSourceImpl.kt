package com.radarcarioca.data.remote.datasource

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.radarcarioca.data.remote.dto.AlertDto
import com.radarcarioca.domain.model.SecurityAlertException
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Implementação Firebase Realtime Database de [AlertRemoteDataSource].
 *
 * Padrão callbackFlow:
 *  Converte o modelo de eventos do Firebase (listeners baseados em callbacks)
 *  para o modelo reativo do Kotlin (Flow de coroutines), sem expor nenhum
 *  detalhe do Firebase para fora desta classe.
 *
 * Path no Firebase: /security_alerts
 *  └── {alertId}: AlertDto
 *
 * Regras do Firebase sugeridas (database.rules.json):
 * ```json
 * {
 *   "rules": {
 *     "security_alerts": {
 *       ".read": "auth != null",
 *       ".write": false
 *     }
 *   }
 * }
 * ```
 */
@Singleton
class AlertRemoteDataSourceImpl @Inject constructor(
    private val database: FirebaseDatabase
) : AlertRemoteDataSource {

    private val alertsRef: DatabaseReference
        get() = database.getReference(ALERTS_PATH)

    // ─────────────────────────────────────────────────────────────────
    // Flow em tempo real — usa callbackFlow para converter listener → Flow
    // ─────────────────────────────────────────────────────────────────

    override fun observeActiveAlerts(): Flow<List<AlertDto>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val alerts = snapshot.parseAlerts()
                trySend(alerts)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toAlertException())
            }
        }

        alertsRef
            .orderByChild(FIELD_IS_ACTIVE)
            .equalTo(true)
            .addValueEventListener(listener)

        // Remove o listener quando o Flow é cancelado (ex.: ViewModel destruída)
        awaitClose {
            alertsRef.removeEventListener(listener)
        }
    }

    // ─────────────────────────────────────────────────────────────────
    // Snapshot único — usado pelo refresh forçado
    // ─────────────────────────────────────────────────────────────────

    override suspend fun fetchActiveAlerts(): List<AlertDto> =
        suspendCancellableCoroutine { continuation ->
            val listener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    continuation.resume(snapshot.parseAlerts())
                }

                override fun onCancelled(error: DatabaseError) {
                    continuation.resumeWithException(error.toAlertException())
                }
            }

            val query = alertsRef
                .orderByChild(FIELD_IS_ACTIVE)
                .equalTo(true)

            query.addListenerForSingleValueEvent(listener)

            // Remove o listener se a coroutine for cancelada antes da resposta
            continuation.invokeOnCancellation {
                query.removeEventListener(listener)
            }
        }

    // ─────────────────────────────────────────────────────────────────
    // Extensões privadas
    // ─────────────────────────────────────────────────────────────────

    /**
     * Converte o snapshot do Firebase em uma lista de [AlertDto].
     * Ignora silenciosamente registros malformados para não derrubar o app
     * quando um admin inseriu dados incorretos no console.
     */
    private fun DataSnapshot.parseAlerts(): List<AlertDto> =
        children.mapNotNull { child ->
            runCatching {
                child.getValue(AlertDto::class.java)?.copy(id = child.key ?: "")
            }.getOrNull()
        }

    /** Converte [DatabaseError] para as exceções tipadas do domínio. */
    private fun DatabaseError.toAlertException(): SecurityAlertException = when (code) {
        DatabaseError.PERMISSION_DENIED ->
            SecurityAlertException.PermissionDenied()
        DatabaseError.NETWORK_ERROR ->
            SecurityAlertException.NetworkUnavailable()
        else ->
            SecurityAlertException.RemoteUnavailable("Firebase error $code: $message")
    }

    private companion object {
        const val ALERTS_PATH   = "security_alerts"
        const val FIELD_IS_ACTIVE = "is_active"
    }
}
