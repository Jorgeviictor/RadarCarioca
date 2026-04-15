package com.radarcarioca.domain.repository

import com.google.firebase.auth.FirebaseUser
import com.radarcarioca.core.DataResult
import com.radarcarioca.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    /** Emite o perfil do usuário autenticado em tempo real (Firestore listener). */
    fun observeCurrentUser(): Flow<DataResult<UserProfile>>

    /**
     * Chamado após login bem-sucedido via Firebase Auth.
     * Cria o documento no Firestore se for o primeiro acesso,
     * ou atualiza email/displayName se já existir (preserva o campo role).
     */
    suspend fun createOrUpdateUserOnLogin(firebaseUser: FirebaseUser): DataResult<UserProfile>

    /** Retorna todos os usuários com role == TESTER em tempo real. */
    fun getTesters(): Flow<DataResult<List<UserProfile>>>

    /**
     * Concede acesso de Tester ao usuário com o e-mail informado.
     * Requer que o usuário já tenha feito login ao menos uma vez
     * (documento existente no Firestore).
     */
    suspend fun grantTesterAccess(email: String, grantedByUid: String): DataResult<Unit>

    /** Revoga o acesso de Tester, resetando role para FREE. */
    suspend fun revokeTesterAccess(targetUid: String): DataResult<Unit>

    suspend fun signOut()
}
