package com.radarcarioca.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.radarcarioca.domain.model.DriverConfig
import com.radarcarioca.domain.model.SubscriptionState
import com.radarcarioca.domain.model.UserRole
import com.radarcarioca.domain.service.SystemStatus
import com.radarcarioca.domain.usecase.CheckPermissionsStatusUseCase
import com.radarcarioca.domain.usecase.CompleteOnboardingUseCase
import com.radarcarioca.domain.usecase.GetTodayStatsUseCase
import com.radarcarioca.domain.usecase.ObserveDriverSettingsUseCase
import com.radarcarioca.domain.usecase.ObserveSubscriptionAccessUseCase
import com.radarcarioca.domain.usecase.SaveDriverConfigUseCase
import com.radarcarioca.domain.usecase.ToggleRadarUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DashboardUiState(
    val isRadarActive: Boolean = false,
    val isAccessibilityEnabled: Boolean = false,
    val isOverlayEnabled: Boolean = false,
    val geoFeaturesLoaded: Int = 0,
    val isOnboardingDone: Boolean = false,
    val config: DriverConfig = DriverConfig(),
    // Estatísticas do turno (desde meia-noite)
    val todayEarnings: Double = 0.0,
    val todayAccepted: Int = 0,
    val todayRejected: Int = 0,
    val todayAlerts: Int = 0,
    // Assinatura + papel do usuário
    val subscriptionState: SubscriptionState = SubscriptionState.Loading,
    val hasAccess: Boolean = false,
    val userRole: UserRole = UserRole.FREE,
    // Notificação única para erros de permissão ao tentar ativar o radar
    val permissionErrorEvent: Boolean = false,
    val isBatteryOptimizationIgnored: Boolean = false
)

/**
 * ViewModel principal do Dashboard.
 *
 * BillingManager foi removido daqui — ele exige referência à Activity para
 * lançar o fluxo de compra. A Activity injeta BillingManager diretamente
 * via @AndroidEntryPoint e o passa como parâmetro ao NavGraph.
 * O ViewModel não deve expor infra de billing; seu contrato com billing
 * é via [ObserveSubscriptionAccessUseCase] → [SubscriptionRepository].
 */
@HiltViewModel
class MainViewModel @Inject constructor(
    private val getTodayStats: GetTodayStatsUseCase,
    private val observeSubscriptionAccess: ObserveSubscriptionAccessUseCase,
    private val observeDriverSettings: ObserveDriverSettingsUseCase,
    private val toggleRadarUseCase: ToggleRadarUseCase,
    private val checkPermissionsStatus: CheckPermissionsStatusUseCase,
    private val saveDriverConfigUseCase: SaveDriverConfigUseCase,
    private val completeOnboardingUseCase: CompleteOnboardingUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        observeSettings()
        observeStats()
        observeSubscription()
        refreshPermissionsStatus()
    }

    // ─── OBSERVADORES ────────────────────────────────────────────────

    private fun observeSubscription() {
        viewModelScope.launch {
            observeSubscriptionAccess().collect { access ->
                _uiState.update {
                    it.copy(
                        subscriptionState = access.state,
                        hasAccess = access.hasAccess,
                        userRole = access.userRole
                    )
                }
            }
        }
    }

    private fun observeSettings() {
        viewModelScope.launch {
            observeDriverSettings().collect { settings ->
                _uiState.update {
                    it.copy(
                        config = settings.config,
                        isOnboardingDone = settings.isOnboardingDone,
                        isRadarActive = settings.isRadarEnabled
                    )
                }
            }
        }
    }

    private fun observeStats() {
        viewModelScope.launch {
            getTodayStats().collect { stats ->
                _uiState.update {
                    it.copy(
                        todayEarnings = stats.earnings,
                        todayAccepted = stats.acceptedCount,
                        todayRejected = stats.rejectedCount,
                        todayAlerts = stats.alertCount
                    )
                }
            }
        }
    }

    // ─── AÇÕES ────────────────────────────────────────────────────────

    fun toggleRadar() {
        viewModelScope.launch {
            val success = toggleRadarUseCase(_uiState.value.isRadarActive)
            if (!success) {
                refreshPermissionsStatus()
                _uiState.update { it.copy(permissionErrorEvent = true) }
            }
        }
    }

    /** Chamado pela UI após exibir o snackbar de erro de permissão. */
    fun consumePermissionErrorEvent() {
        _uiState.update { it.copy(permissionErrorEvent = false) }
    }

    fun refreshPermissionsStatus() {
        val status: SystemStatus = checkPermissionsStatus()
        _uiState.update {
            it.copy(
                isAccessibilityEnabled = status.isAccessibilityEnabled,
                isOverlayEnabled = status.isOverlayEnabled,
                geoFeaturesLoaded = status.geoFeaturesLoaded,
                isBatteryOptimizationIgnored = status.isBatteryOptimizationIgnored
            )
        }
    }

    fun saveConfig(config: DriverConfig) {
        viewModelScope.launch { saveDriverConfigUseCase(config) }
    }

    fun completeOnboarding() {
        viewModelScope.launch { completeOnboardingUseCase() }
    }
}
