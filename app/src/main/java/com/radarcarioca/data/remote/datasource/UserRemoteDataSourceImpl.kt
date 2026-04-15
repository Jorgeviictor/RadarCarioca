package com.radarcarioca.data.remote.datasource

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.radarcarioca.core.DataResult
import com.radarcarioca.data.remote.dto.UserDto
import com.radarcarioca.di.AdminMasterUid
import com.radarcarioca.domain.model.UserProfile
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

private const val COLLECTION = "users"

@Singleton
class UserRemoteDataSourceImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    @AdminMasterUid private val adminMasterUid: String
) : UserRemoteDataSource {

    /**
     * Observa o perfil do usuário autenticado em tempo real via Firestore.
     * Reage automaticamente a mudanças de sessão (login/logout).
     */
    override fun observeCurrentUser(): Flow<DataResult<UserProfile>> = channelFlow {
        var firestoreReg: ListenerRegistration? = null

        val authListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            firestoreReg?.remove()
            val user = firebaseAuth.currentUser

            if (user == null) {
                trySend(DataResult.Error(Exception("Usuário não autenticado")))
                return@AuthStateListener
            }

            firestoreReg = firestore.collection(COLLECTION)
                .document(user.uid)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        trySend(DataResult.Error(error))
                        return@addSnapshotListener
                    }
                    val dto = snapshot?.toObject(UserDto::class.java)
                    if (dto != null) {
                        trySend(DataResult.Success(dto.toDomain()))
                    } else {
                        trySend(DataResult.Loading)
                    }
                }
        }

        auth.addAuthStateListener(authListener)
        awaitClose {
            auth.removeAuthStateListener(authListener)
            firestoreReg?.remove()
        }
    }

    /**
     * Cria o documento do usuário no primeiro acesso ou atualiza dados de contato
     * em logins subsequentes (preserva o campo role existente).
     */
    override suspend fun createOrUpdateUserOnLogin(firebaseUser: FirebaseUser): DataResult<UserProfile> {
        return try {
            val docRef = firestore.collection(COLLECTION).document(firebaseUser.uid)
            val snapshot = docRef.get().await()

            if (!snapshot.exists()) {
                val role = if (firebaseUser.uid == adminMasterUid) "ADMIN_MASTER" else "FREE"
                val data = hashMapOf(
                    "uid"          to firebaseUser.uid,
                    "email"        to (firebaseUser.email ?: ""),
                    "display_name" to (firebaseUser.displayName ?: ""),
                    "photo_url"    to (firebaseUser.photoUrl?.toString() ?: ""),
                    "role"         to role,
                    "created_at"   to System.currentTimeMillis()
                )
                docRef.set(data).await()
                val dto = UserDto(
                    uid = firebaseUser.uid,
                    email = firebaseUser.email ?: "",
                    displayName = firebaseUser.displayName ?: "",
                    photoUrl = firebaseUser.photoUrl?.toString() ?: "",
                    role = role,
                    createdAt = System.currentTimeMillis()
                )
                DataResult.Success(dto.toDomain())
            } else {
                val updates = hashMapOf<String, Any>(
                    "email"        to (firebaseUser.email ?: ""),
                    "display_name" to (firebaseUser.displayName ?: ""),
                    "photo_url"    to (firebaseUser.photoUrl?.toString() ?: "")
                )
                docRef.update(updates).await()
                val dto = snapshot.toObject(UserDto::class.java)
                    ?: return DataResult.Error(Exception("Falha ao ler perfil"))
                DataResult.Success(dto.toDomain())
            }
        } catch (e: Exception) {
            DataResult.Error(e)
        }
    }

    override fun getTesters(): Flow<DataResult<List<UserProfile>>> = channelFlow {
        val reg = firestore.collection(COLLECTION)
            .whereEqualTo("role", "TESTER")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(DataResult.Error(error))
                    return@addSnapshotListener
                }
                val testers = snapshot?.documents
                    ?.mapNotNull { it.toObject(UserDto::class.java)?.toDomain() }
                    ?: emptyList()
                trySend(DataResult.Success(testers))
            }
        awaitClose { reg.remove() }
    }

    override suspend fun grantTesterAccess(email: String, grantedByUid: String): DataResult<Unit> {
        return try {
            val query = firestore.collection(COLLECTION)
                .whereEqualTo("email", email)
                .limit(1)
                .get()
                .await()
            val doc = query.documents.firstOrNull()
                ?: return DataResult.Error(Exception("Usuário com e-mail '$email' não encontrado. Ele precisa ter feito login ao menos uma vez."))

            doc.reference.update(
                mapOf(
                    "role"              to "TESTER",
                    "tester_granted_by" to grantedByUid,
                    "tester_granted_at" to System.currentTimeMillis()
                )
            ).await()
            DataResult.Success(Unit)
        } catch (e: Exception) {
            DataResult.Error(e)
        }
    }

    override suspend fun revokeTesterAccess(targetUid: String): DataResult<Unit> {
        return try {
            firestore.collection(COLLECTION).document(targetUid)
                .update(
                    mapOf(
                        "role"              to "FREE",
                        "tester_granted_by" to null,
                        "tester_granted_at" to null,
                        "tester_expires_at" to null
                    )
                ).await()
            DataResult.Success(Unit)
        } catch (e: Exception) {
            DataResult.Error(e)
        }
    }

    override suspend fun signOut() {
        auth.signOut()
    }
}
