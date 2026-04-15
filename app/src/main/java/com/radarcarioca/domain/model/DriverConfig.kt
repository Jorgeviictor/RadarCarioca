package com.radarcarioca.domain.model

/**
 * Configurações persistidas do motorista — entidade de domínio pura.
 *
 * Movida de [com.radarcarioca.data.model.Models] para o domain porque:
 * - UseCases (ObserveDriverSettings, SaveDriverConfig, ToggleRadar) dependem dela.
 * - FinancialCalculator, que implementa regras de negócio, usa DriverConfig como entrada.
 * - A camada Data depende do Domain (correto), não o contrário.
 *
 * [FuelType] foi movida junto pois é parte integrante de DriverConfig.
 */
data class DriverConfig(
    val fuelPricePerLiter: Double = 6.20,       // R$ por litro
    val kmPerLiter: Double = 12.0,               // Consumo do veículo
    val platformFeePercent: Double = 0.20,       // 20% Uber padrão
    val targetProfitPerKm: Double = 2.50,        // Benchmark mínimo R$/KM
    val targetProfitPerHour: Double = 35.0,      // Benchmark horário R$/hora
    val targetProfitPerMinute: Double = 0.50,    // Benchmark R$/minuto (sinaleiro financeiro)
    val fuelType: FuelType = FuelType.GASOLINA,
    val bufferMeters: Int = 300,                 // Raio de alerta em metros
    val screenshotRetentionDays: Int = 30        // Retenção de prints: 30, 45 ou 90 dias
)

enum class FuelType { GASOLINA, ETANOL, GNV }
