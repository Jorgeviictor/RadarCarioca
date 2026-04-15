package com.radarcarioca.billing

import android.app.Activity
import android.content.Context
import android.util.Log
import com.android.billingclient.api.*
import com.radarcarioca.data.model.SubscriptionPlans
import com.radarcarioca.data.model.SubscriptionSku
import com.radarcarioca.domain.model.SubscriptionPlan
import com.radarcarioca.domain.model.SubscriptionState
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "BillingManager"
private const val TRIAL_DURATION_DAYS = 14
private const val PREF_INSTALL_DATE = "install_date_ms"

@Singleton
class BillingManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _subscriptionState = MutableStateFlow<SubscriptionState>(SubscriptionState.Loading)
    val subscriptionState: StateFlow<SubscriptionState> = _subscriptionState.asStateFlow()

    private val _availableProducts = MutableStateFlow<List<ProductDetails>>(emptyList())
    val availableProducts: StateFlow<List<ProductDetails>> = _availableProducts.asStateFlow()

    private val purchasesUpdatedListener = PurchasesUpdatedListener { billingResult, purchases ->
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            scope.launch { processPurchases(purchases) }
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
            Log.d(TAG, "Usuário cancelou a compra")
        } else {
            Log.e(TAG, "Erro na compra: ${billingResult.debugMessage}")
        }
    }

    private val billingClient = BillingClient.newBuilder(context)
        .setListener(purchasesUpdatedListener)
        .enablePendingPurchases(PendingPurchasesParams.newBuilder().enableOneTimeProducts().build())
        .build()

    init {
        connectAndLoad()
    }

    fun connectAndLoad() {
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    Log.d(TAG, "Billing conectado com sucesso")
                    scope.launch {
                        loadProducts()
                        checkSubscriptionStatus()
                    }
                } else {
                    Log.e(TAG, "Falha ao conectar Billing: ${billingResult.debugMessage}")
                    _subscriptionState.value = checkTrialOffline()
                }
            }

            override fun onBillingServiceDisconnected() {
                Log.w(TAG, "Billing desconectado, tentando reconectar...")
                connectAndLoad()
            }
        })
    }

    private suspend fun loadProducts() {
        val productList = SubscriptionSku.ALL.map { sku ->
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(sku)
                .setProductType(BillingClient.ProductType.SUBS)
                .build()
        }

        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(productList)
            .build()

        val result = billingClient.queryProductDetails(params)
        if (result.billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
            _availableProducts.value = result.productDetailsList ?: emptyList()
            Log.d(TAG, "Produtos carregados: ${result.productDetailsList?.size}")
        }
    }

    private suspend fun checkSubscriptionStatus() {
        val params = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.SUBS)
            .build()

        val result = billingClient.queryPurchasesAsync(params)

        if (result.billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
            val activePurchases = result.purchasesList.filter {
                it.purchaseState == Purchase.PurchaseState.PURCHASED
            }
            if (activePurchases.isNotEmpty()) {
                processPurchases(activePurchases)
            } else {
                _subscriptionState.value = checkTrialOffline()
            }
        } else {
            _subscriptionState.value = checkTrialOffline()
        }
    }

    private suspend fun processPurchases(purchases: List<Purchase>) {
        for (purchase in purchases) {
            if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                if (!purchase.isAcknowledged) {
                    val ackParams = AcknowledgePurchaseParams.newBuilder()
                        .setPurchaseToken(purchase.purchaseToken)
                        .build()
                    billingClient.acknowledgePurchase(ackParams)
                }
                val sku = purchase.products.firstOrNull() ?: continue
                val plan = SubscriptionPlans.ALL
                    .firstOrNull { it.sku == sku }
                if (plan != null) {
                    _subscriptionState.value = SubscriptionState.Active(plan)
                    return
                }
            }
        }
        _subscriptionState.value = checkTrialOffline()
    }

    /** Verifica trial offline via SharedPreferences */
    private fun checkTrialOffline(): SubscriptionState {
        val prefs = context.getSharedPreferences("radar_billing", Context.MODE_PRIVATE)
        val installDate = prefs.getLong(PREF_INSTALL_DATE, 0L)

        if (installDate == 0L) {
            // Primeiro acesso — registra data de instalação e inicia trial
            prefs.edit().putLong(PREF_INSTALL_DATE, System.currentTimeMillis()).apply()
            return SubscriptionState.InTrial(TRIAL_DURATION_DAYS)
        }

        val elapsedDays = ((System.currentTimeMillis() - installDate) / (1000 * 60 * 60 * 24)).toInt()
        val remaining = TRIAL_DURATION_DAYS - elapsedDays

        return when {
            remaining > 0 -> SubscriptionState.InTrial(remaining)
            else          -> SubscriptionState.Expired
        }
    }

    /** Inicia o fluxo de compra de uma assinatura */
    fun launchBillingFlow(activity: Activity, productDetails: ProductDetails) {
        val offerToken = productDetails.subscriptionOfferDetails
            ?.firstOrNull()?.offerToken ?: return

        val productDetailsParams = BillingFlowParams.ProductDetailsParams.newBuilder()
            .setProductDetails(productDetails)
            .setOfferToken(offerToken)
            .build()

        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(listOf(productDetailsParams))
            .build()

        billingClient.launchBillingFlow(activity, billingFlowParams)
    }

    /** Busca ProductDetails por SKU */
    fun getProductDetails(sku: String): ProductDetails? =
        _availableProducts.value.firstOrNull { it.productId == sku }

    fun isActive(): Boolean = _subscriptionState.value is SubscriptionState.Active
    fun isInTrial(): Boolean = _subscriptionState.value is SubscriptionState.InTrial
    fun hasAccess(): Boolean = isActive() || isInTrial()
}
