package com.radarcarioca.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.radarcarioca.core.DataResult
import com.radarcarioca.domain.model.UserProfile
import com.radarcarioca.domain.usecase.GetTestersUseCase
import com.radarcarioca.domain.usecase.GrantTesterAccessUseCase
import com.radarcarioca.domain.usecase.RevokeTesterAccessUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AdminUiState(
    val testers: List<UserProfile> = emptyList(),
    val isLoading: Boolean = true,
    val feedbackMessage: String? = null
)

@HiltViewModel
class AdminViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val getTesters: GetTestersUseCase,
    private val grantTesterAccess: GrantTesterAccessUseCase,
    private val revokeTesterAccess: RevokeTesterAccessUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminUiState())
    val uiState: StateFlow<AdminUiState> = _uiState.asStateFlow()

    init {
        observeTesters()
    }

    private fun observeTesters() {
        getTesters()
            .onEach { result ->
                when (result) {
                    is DataResult.Loading -> _uiState.update { it.copy(isLoading = true) }
                    is DataResult.Success -> _uiState.update {
                        it.copy(testers = result.data, isLoading = false)
                    }
                    is DataResult.Error -> _uiState.update {
                        it.copy(isLoading = false, feedbackMessage = result.exception.message)
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    fun grantAccess(email: String) {
        val adminUid = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = grantTesterAccess(email.trim(), adminUid)
            _uiState.update {
                it.copy(
                    isLoading = false,
                    feedbackMessage = when (result) {
                        is DataResult.Success -> "Acesso concedido para $email"
                        is DataResult.Error   -> result.exception.message ?: "Erro ao conceder acesso"
                        else -> null
                    }
                )
            }
        }
    }

    fun revokeAccess(targetUid: String, email: String) {
        viewModelScope.launch {
            val result = revokeTesterAccess(targetUid)
            _uiState.update {
                it.copy(
                    feedbackMessage = when (result) {
                        is DataResult.Success -> "Acesso revogado de $email"
                        is DataResult.Error   -> result.exception.message ?: "Erro ao revogar acesso"
                        else -> null
                    }
                )
            }
        }
    }

    fun clearFeedback() {
        _uiState.update { it.copy(feedbackMessage = null) }
    }
}
