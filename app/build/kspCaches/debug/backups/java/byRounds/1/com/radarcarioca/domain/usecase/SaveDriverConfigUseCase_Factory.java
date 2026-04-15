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
public final class SaveDriverConfigUseCase_Factory implements Factory<SaveDriverConfigUseCase> {
  private final Provider<DriverSettingsRepository> driverSettingsRepositoryProvider;

  public SaveDriverConfigUseCase_Factory(
      Provider<DriverSettingsRepository> driverSettingsRepositoryProvider) {
    this.driverSettingsRepositoryProvider = driverSettingsRepositoryProvider;
  }

  @Override
  public SaveDriverConfigUseCase get() {
    return newInstance(driverSettingsRepositoryProvider.get());
  }

  public static SaveDriverConfigUseCase_Factory create(
      Provider<DriverSettingsRepository> driverSettingsRepositoryProvider) {
    return new SaveDriverConfigUseCase_Factory(driverSettingsRepositoryProvider);
  }

  public static SaveDriverConfigUseCase newInstance(
      DriverSettingsRepository driverSettingsRepository) {
    return new SaveDriverConfigUseCase(driverSettingsRepository);
  }
}
