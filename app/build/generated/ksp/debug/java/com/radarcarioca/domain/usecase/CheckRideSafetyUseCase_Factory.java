package com.radarcarioca.domain.usecase;

import com.radarcarioca.geo.GeoSecurityManager;
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
public final class CheckRideSafetyUseCase_Factory implements Factory<CheckRideSafetyUseCase> {
  private final Provider<GeoSecurityManager> geoSecurityManagerProvider;

  public CheckRideSafetyUseCase_Factory(Provider<GeoSecurityManager> geoSecurityManagerProvider) {
    this.geoSecurityManagerProvider = geoSecurityManagerProvider;
  }

  @Override
  public CheckRideSafetyUseCase get() {
    return newInstance(geoSecurityManagerProvider.get());
  }

  public static CheckRideSafetyUseCase_Factory create(
      Provider<GeoSecurityManager> geoSecurityManagerProvider) {
    return new CheckRideSafetyUseCase_Factory(geoSecurityManagerProvider);
  }

  public static CheckRideSafetyUseCase newInstance(GeoSecurityManager geoSecurityManager) {
    return new CheckRideSafetyUseCase(geoSecurityManager);
  }
}
