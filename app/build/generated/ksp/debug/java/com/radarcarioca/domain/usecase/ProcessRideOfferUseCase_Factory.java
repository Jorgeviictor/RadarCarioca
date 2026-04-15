package com.radarcarioca.domain.usecase;

import com.radarcarioca.data.local.DriverPreferences;
import com.radarcarioca.financial.FinancialCalculator;
import com.radarcarioca.service.GeocodingService;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast",
    "deprecation"
})
public final class ProcessRideOfferUseCase_Factory implements Factory<ProcessRideOfferUseCase> {
  private final Provider<CheckRideSafetyUseCase> checkSafetyProvider;

  private final Provider<CalculateRideProfitUseCase> calculateProfitProvider;

  private final Provider<FinancialCalculator> financialCalculatorProvider;

  private final Provider<DriverPreferences> driverPreferencesProvider;

  private final Provider<GeocodingService> geocodingServiceProvider;

  public ProcessRideOfferUseCase_Factory(Provider<CheckRideSafetyUseCase> checkSafetyProvider,
      Provider<CalculateRideProfitUseCase> calculateProfitProvider,
      Provider<FinancialCalculator> financialCalculatorProvider,
      Provider<DriverPreferences> driverPreferencesProvider,
      Provider<GeocodingService> geocodingServiceProvider) {
    this.checkSafetyProvider = checkSafetyProvider;
    this.calculateProfitProvider = calculateProfitProvider;
    this.financialCalculatorProvider = financialCalculatorProvider;
    this.driverPreferencesProvider = driverPreferencesProvider;
    this.geocodingServiceProvider = geocodingServiceProvider;
  }

  @Override
  public ProcessRideOfferUseCase get() {
    return newInstance(checkSafetyProvider.get(), calculateProfitProvider.get(), financialCalculatorProvider.get(), driverPreferencesProvider.get(), geocodingServiceProvider.get());
  }

  public static ProcessRideOfferUseCase_Factory create(
      Provider<CheckRideSafetyUseCase> checkSafetyProvider,
      Provider<CalculateRideProfitUseCase> calculateProfitProvider,
      Provider<FinancialCalculator> financialCalculatorProvider,
      Provider<DriverPreferences> driverPreferencesProvider,
      Provider<GeocodingService> geocodingServiceProvider) {
    return new ProcessRideOfferUseCase_Factory(checkSafetyProvider, calculateProfitProvider, financialCalculatorProvider, driverPreferencesProvider, geocodingServiceProvider);
  }

  public static ProcessRideOfferUseCase newInstance(CheckRideSafetyUseCase checkSafety,
      CalculateRideProfitUseCase calculateProfit, FinancialCalculator financialCalculator,
      DriverPreferences driverPreferences, GeocodingService geocodingService) {
    return new ProcessRideOfferUseCase(checkSafety, calculateProfit, financialCalculator, driverPreferences, geocodingService);
  }
}
