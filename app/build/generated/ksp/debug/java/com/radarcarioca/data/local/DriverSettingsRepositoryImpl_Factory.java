package com.radarcarioca.data.local;

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
public final class DriverSettingsRepositoryImpl_Factory implements Factory<DriverSettingsRepositoryImpl> {
  private final Provider<DriverPreferences> driverPreferencesProvider;

  public DriverSettingsRepositoryImpl_Factory(
      Provider<DriverPreferences> driverPreferencesProvider) {
    this.driverPreferencesProvider = driverPreferencesProvider;
  }

  @Override
  public DriverSettingsRepositoryImpl get() {
    return newInstance(driverPreferencesProvider.get());
  }

  public static DriverSettingsRepositoryImpl_Factory create(
      Provider<DriverPreferences> driverPreferencesProvider) {
    return new DriverSettingsRepositoryImpl_Factory(driverPreferencesProvider);
  }

  public static DriverSettingsRepositoryImpl newInstance(DriverPreferences driverPreferences) {
    return new DriverSettingsRepositoryImpl(driverPreferences);
  }
}
