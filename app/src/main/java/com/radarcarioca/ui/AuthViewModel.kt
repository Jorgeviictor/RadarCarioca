package com.radarcarioca.ui

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.radarcarioca.BuildConfig
import com.radarcarioca.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit
import javax.inject.Inject

sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    object Success : AuthUiState()
    data class PhoneOtpSent(val hint: String) : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private val _isSignedIn = MutableStateFlow<Boolean?>(null)
    val isSignedIn: StateFlow<Boolean?> = _isSignedIn.asStateFlow()

    /** ID de verificação retornado pelo Firebase após enviar o SMS */
    private var phoneVerificationId: String? = null

    init {
        auth.addAuthStateListener { _isSignedIn.value = it.currentUser != null }
    }

    // ── Google Sign-In via Credential Manager ────────────────────────

    fun signInWithGoogle(activity: Activity) {
        _uiState.value = AuthUiState.Loading
        viewModelScope.launch {
            try {
                val credentialManager = CredentialManager.create(activity)
                val googleIdOption = GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(BuildConfig.GOOGLE_WEB_CLIENT_ID)
                    .setAutoSelectEnabled(false)
                    .build()
                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build()

                val result = credentialManager.getCredential(activity, request)
                val googleCredential = GoogleIdTokenCredential.createFrom(result.credential.data)
                val firebaseCredential = GoogleAuthProvider.getCredential(googleCredential.idToken, null)
                val authResult = auth.signInWithCredential(firebaseCredential).await()

                authResult.user?.let { userRepository.createOrUpdateUserOnLogin(it) }
                _uiState.value = AuthUiState.Success
            } catch (e: GetCredentialCancellationException) {
                _uiState.value = AuthUiState.Idle
            } catch (e: Exception) {
                _uiState.value = AuthUiState.Error(e.message ?: "Erro ao entrar com Google")
            }
        }
    }

    // ── Autenticação por Telefone — Passo 1: Enviar OTP ──────────────

    fun sendPhoneOtp(phoneNumber: String, activity: Activity) {
        _uiState.value = AuthUiState.Loading
        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                // Auto-verificação (ex: no emulador ou chip do mesmo aparelho)
                signInWithPhoneCredential(credential)
            }

            override fun onVerificationFailed(e: com.google.firebase.FirebaseException) {
                _uiState.value = AuthUiState.Error(e.message ?: "Falha ao enviar SMS")
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                phoneVerificationId = verificationId
                _uiState.value = AuthUiState.PhoneOtpSent(phoneNumber)
            }
        }

        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(callbacks)
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    // ── Autenticação por Telefone — Passo 2: Verificar OTP ───────────

    fun verifyPhoneOtp(otp: String) {
        val verificationId = phoneVerificationId ?: run {
            _uiState.value = AuthUiState.Error("Sessão expirada. Solicite um novo código.")
            return
        }
        val credential = PhoneAuthProvider.getCredential(verificationId, otp)
        signInWithPhoneCredential(credential)
    }

    private fun signInWithPhoneCredential(credential: PhoneAuthCredential) {
        _uiState.value = AuthUiState.Loading
        viewModelScope.launch {
            try {
                val authResult = auth.signInWithCredential(credential).await()
                authResult.user?.let { userRepository.createOrUpdateUserOnLogin(it) }
                _uiState.value = AuthUiState.Success
            } catch (e: Exception) {
                _uiState.value = AuthUiState.Error(e.message ?: "Código inválido ou expirado")
            }
        }
    }

    fun resetState() {
        _uiState.value = AuthUiState.Idle
        phoneVerificationId = null
    }
}
