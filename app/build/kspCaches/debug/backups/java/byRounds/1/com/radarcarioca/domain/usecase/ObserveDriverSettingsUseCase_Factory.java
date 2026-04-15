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
public final class ObserveDriverSettingsUseCase_Factory implements Factory<ObserveDriverSettingsUseCase> {
  private final Provider<DriverSettingsRepository> driverSettingsRepositoryProvider;

  public ObserveDriverSettingsUseCase_Factory(
      Provider<DriverSettingsRepository> driverSettingsRepositoryProvider) {
    this.driverSettingsRepositoryProvider = driverSettingsRepositoryProvider;
  }

  @Override
  public ObserveDriverSettingsUseCase get() {
    return newInstance(driverSettingsRepositoryProvider.get());
  }

  public static ObserveDriverSettingsUseCase_Factory create(
      Provider<DriverSettingsRepository> driverSettingsRepositoryProvider) {
    return new ObserveDriverSettingsUseCase_Factory(driverSettingsRepositoryProvider);
  }

  public static ObserveDriverSettingsUseCase newInstance(
      DriverSettingsRepository driverSettingsRepository) {
    return new ObserveDriverSettingsUseCase(driverSettingsRepository);
  }
}
