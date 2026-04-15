package com.radarcarioca.domain.usecase;

import com.radarcarioca.domain.repository.DriverSettingsRepository;
import com.radarcarioca.domain.service.PermissionsChecker;
import com.radarcarioca.domain.service.RadarController;
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
public final class ToggleRadarUseCase_Factory implements Factory<ToggleRadarUseCase> {
  private final Provider<DriverSettingsRepository> driverSettingsRepositoryProvider;

  private final Provider<RadarController> radarControllerProvider;

  private final Provider<PermissionsChecker> permissionsCheckerProvider;

  public ToggleRadarUseCase_Factory(
      Provider<DriverSettingsRepository> driverSettingsRepositoryProvider,
      Provider<RadarController> radarControllerProvider,
      Provider<PermissionsChecker> permissionsCheckerProvider) {
    this.driverSettingsRepositoryProvider = driverSettingsRepositoryProvider;
    this.radarControllerProvider = radarControllerProvider;
    this.permissionsCheckerProvider = permissionsCheckerProvider;
  }

  @Override
  public ToggleRadarUseCase get() {
    return newInstance(driverSettingsRepositoryProvider.get(), radarControllerProvider.get(), permissionsCheckerProvider.get());
  }

  public static ToggleRadarUseCase_Factory create(
      Provider<DriverSettingsRepository> driverSettingsRepositoryProvider,
      Provider<RadarController> radarControllerProvider,
      Provider<PermissionsChecker> permissionsCheckerProvider) {
    return new ToggleRadarUseCase_Factory(driverSettingsRepositoryProvider, radarControllerProvider, permissionsCheckerProvider);
  }

  public static ToggleRadarUseCase newInstance(DriverSettingsRepository driverSettingsRepository,
      RadarController radarController, PermissionsChecker permissionsChecker) {
    return new ToggleRadarUseCase(driverSettingsRepository, radarController, permissionsChecker);
  }
}
