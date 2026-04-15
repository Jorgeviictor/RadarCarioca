package com.radarcarioca.domain.usecase;

import com.radarcarioca.domain.repository.DriverSettingsRepository;
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
public final class CompleteOnboardingUseCase_Factory implements Factory<CompleteOnboardingUseCase> {
  private final Provider<DriverSettingsRepository> driverSettingsRepositoryProvider;

  public CompleteOnboardingUseCase_Factory(
      Provider<DriverSettingsRepository> driverSettingsRepositoryProvider) {
    this.driverSettingsRepositoryProvider = driverSettingsRepositoryProvider;
  }

  @Override
  public CompleteOnboardingUseCase get() {
    return newInstance(driverSettingsRepositoryProvider.get());
  }

  public static CompleteOnboardingUseCase_Factory create(
      Provider<DriverSettingsRepository> driverSettingsRepositoryProvider) {
    return new CompleteOnboardingUseCase_Factory(driverSettingsRepositoryProvider);
  }

  public static CompleteOnboardingUseCase newInstance(
      DriverSettingsRepository driverSettingsRepository) {
    return new CompleteOnboardingUseCase(driverSettingsRepository);
  }
}
