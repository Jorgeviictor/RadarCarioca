package com.radarcarioca.domain.usecase;

import com.radarcarioca.financial.FinancialCalculator;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
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
public final class CalculateRideProfitUseCase_Factory implements Factory<CalculateRideProfitUseCase> {
  private final Provider<FinancialCalculator> calculatorProvider;

  public CalculateRideProfitUseCase_Factory(Provider<FinancialCalculator> calculatorProvider) {
    this.calculatorProvider = calculatorProvider;
  }

  @Override
  public CalculateRideProfitUseCase get() {
    return newInstance(calculatorProvider.get());
  }

  public static CalculateRideProfitUseCase_Factory create(
      Provider<FinancialCalculator> calculatorProvider) {
    return new CalculateRideProfitUseCase_Factory(calculatorProvider);
  }

  public static CalculateRideProfitUseCase newInstance(FinancialCalculator calculator) {
    return new CalculateRideProfitUseCase(calculator);
  }
}
