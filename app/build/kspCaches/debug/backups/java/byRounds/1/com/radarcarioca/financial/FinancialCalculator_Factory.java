package com.radarcarioca.financial;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

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
public final class FinancialCalculator_Factory implements Factory<FinancialCalculator> {
  @Override
  public FinancialCalculator get() {
    return newInstance();
  }

  public static FinancialCalculator_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static FinancialCalculator newInstance() {
    return new FinancialCalculator();
  }

  private static final class InstanceHolder {
    private static final FinancialCalculator_Factory INSTANCE = new FinancialCalculator_Factory();
  }
}
