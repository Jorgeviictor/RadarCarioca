package com.radarcarioca

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.radarcarioca.billing.BillingManager
import com.radarcarioca.domain.model.SubscriptionState
import com.radarcarioca.domain.model.UserRole
import com.radarcarioca.overlay.NAV_EXTRA
import com.radarcarioca.overlay.NAV_HISTORY
import com.radarcarioca.overlay.NAV_MAP
import com.radarcarioca.overlay.NAV_SETTINGS
import com.radarcarioca.ui.AuthViewModel
import com.radarcarioca.ui.MainViewModel
import com.radarcarioca.ui.screens.*
import com.radarcarioca.ui.theme.RadarCariocaTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

sealed class Screen(val route: String) {
    object Auth       : Screen("auth")
    object Splash     : Screen("splash")
    object Paywall    : Screen("paywall")
    object Onboarding : Screen("onboarding")
    object Dashboard  : Screen("dashboard")
    object Settings   : Screen("settings")
    object Stats      : Screen("stats")
    object AdminPanel : Screen("admin_panel")
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()
    private var navController: NavHostController? = null

    @Inject
    lateinit var billingManager: BillingManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RadarCariocaTheme {
                val controller = rememberNavController()
                navController = controller
                RadarCariocaNavGraph(
                    navController = controller,
                    viewModel = viewModel,
                    authViewModel = authViewModel,
                    activity = this,
                    billingManager = billingManager,
                    pendingNavigation = intent?.getStringExtra(NAV_EXTRA)
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        val destination = intent.getStringExtra(NAV_EXTRA) ?: return
        navigateFromOverlay(destination)
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshPermissionsStatus()
    }

    private fun navigateFromOverlay(destination: String) {
        val route = when (destination) {
            NAV_MAP      -> Screen.Dashboard.route
            NAV_SETTINGS -> Screen.Settings.route
            NAV_HISTORY  -> Screen.Stats.route
            else         -> return
        }
        navController?.navigate(route) { launchSingleTop = true }
    }
}

@Composable
fun RadarCariocaNavGraph(
    navController: NavHostController,
    viewModel: MainViewModel,
    authViewModel: AuthViewModel,
    activity: MainActivity,
    billingManager: BillingManager,
    pendingNavigation: String? = null
) {
    val uiState by viewModel.uiState.collectAsState()
    val isSignedIn by authViewModel.isSignedIn.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(pendingNavigation) {
        if (pendingNavigation != null) {
            val route = when (pendingNavigation) {
                NAV_MAP      -> Screen.Dashboard.route
                NAV_SETTINGS -> Screen.Settings.route
                NAV_HISTORY  -> Screen.Stats.route
                else         -> null
            }
            route?.let { navController.navigate(it) { launchSingleTop = true } }
        }
    }

    NavHost(navController = navController, startDestination = Screen.Splash.route) {

        // ── Auth ─────────────────────────────────────────────────────────
        composable(Screen.Auth.route) {
            AuthScreen(
                onAuthSuccess = {
                    navController.navigate(Screen.Splash.route) {
                        popUpTo(Screen.Auth.route) { inclusive = true }
                    }
                }
            )
        }

        // ── Splash ───────────────────────────────────────────────────────
        composable(Screen.Splash.route) {
            SplashScreen(onFinished = {
                when {
                    // Não logado → tela de autenticação
                    isSignedIn == false -> navController.navigate(Screen.Auth.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                    // Admin/Tester → pula paywall direto para o app
                    uiState.userRole.hasPrivilegedAccess -> {
                        val next = if (uiState.isOnboardingDone) Screen.Dashboard.route
                                   else Screen.Onboarding.route
                        navController.navigate(next) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    }
                    // Sem assinatura → paywall
                    !uiState.hasAccess -> navController.navigate(Screen.Paywall.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                    // Com acesso → dashboard ou onboarding
                    uiState.isOnboardingDone -> navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                    else -> navController.navigate(Screen.Onboarding.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            })
        }

        // ── Paywall ──────────────────────────────────────────────────────
        composable(Screen.Paywall.route) {
            PaywallScreen(
                subscriptionState = uiState.subscriptionState,
                onSelectPlan = { plan ->
                    billingManager.getProductDetails(plan.sku)?.let {
                        billingManager.launchBillingFlow(activity, it)
                    }
                },
                onStartTrial = {
                    val next = if (uiState.isOnboardingDone) Screen.Dashboard.route
                               else Screen.Onboarding.route
                    navController.navigate(next) {
                        popUpTo(Screen.Paywall.route) { inclusive = true }
                    }
                },
                onRestorePurchase = { billingManager.connectAndLoad() }
            )
            LaunchedEffect(uiState.hasAccess) {
                if (uiState.hasAccess) {
                    val next = if (uiState.isOnboardingDone) Screen.Dashboard.route
                               else Screen.Onboarding.route
                    navController.navigate(next) {
                        popUpTo(Screen.Paywall.route) { inclusive = true }
                    }
                }
            }
        }

        // ── Onboarding ───────────────────────────────────────────────────
        composable(Screen.Onboarding.route) {
            var localConfig by remember { mutableStateOf(uiState.config) }
            OnboardingScreen(
                config = localConfig,
                onConfigChange = { localConfig = it; viewModel.saveConfig(it) },
                isAccessibilityEnabled = uiState.isAccessibilityEnabled,
                isOverlayEnabled = uiState.isOverlayEnabled,
                isBatteryOptimizationIgnored = uiState.isBatteryOptimizationIgnored,
                onAccessibilityClick = {
                    context.startActivity(
                        android.content.Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS)
                    )
                },
                onOverlayClick = {
                    context.startActivity(
                        android.content.Intent(
                            android.provider.Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            android.net.Uri.parse("package:${context.packageName}")
                        )
                    )
                },
                onBatteryClick = {
                    context.startActivity(
                        android.content.Intent(
                            android.provider.Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS,
                            android.net.Uri.parse("package:${context.packageName}")
                        )
                    )
                },
                onComplete = {
                    viewModel.completeOnboarding()
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }

        // ── Dashboard ────────────────────────────────────────────────────
        composable(Screen.Dashboard.route) {
            DashboardScreen(
                uiState = uiState,
                onToggleRadar = { viewModel.toggleRadar() },
                onNavigateSettings = { navController.navigate(Screen.Settings.route) },
                onNavigateStats = { navController.navigate(Screen.Stats.route) },
                onNavigateAdminPanel = {
                    if (uiState.userRole == UserRole.ADMIN_MASTER) {
                        navController.navigate(Screen.AdminPanel.route)
                    }
                },
                onRefreshPermissions = { viewModel.refreshPermissionsStatus() },
                onPermissionErrorConsumed = { viewModel.consumePermissionErrorEvent() }
            )
        }

        // ── Settings ─────────────────────────────────────────────────────
        composable(Screen.Settings.route) {
            SettingsScreen(
                config = uiState.config,
                onConfigChange = { viewModel.saveConfig(it) },
                onBack = { navController.popBackStack() }
            )
        }

        // ── Stats ────────────────────────────────────────────────────────
        composable(Screen.Stats.route) {
            StatsScreen(
                uiState = uiState,
                onBack = { navController.popBackStack() }
            )
        }

        // ── Admin Panel ───────────────────────────────────────────────────
        composable(Screen.AdminPanel.route) {
            AdminPanelScreen(onBack = { navController.popBackStack() })
        }
    }
}
