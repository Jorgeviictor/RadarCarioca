package com.radarcarioca.geo;

import android.content.Context;
import com.radarcarioca.domain.repository.GeoRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class GeoSecurityManager_Factory implements Factory<GeoSecurityManager> {
  private final Provider<Context> contextProvider;

  private final Provider<GeoRepository> geoRepositoryProvider;

  public GeoSecurityManager_Factory(Provider<Context> contextProvider,
      Provider<GeoRepository> geoRepositoryProvider) {
    this.contextProvider = contextProvider;
    this.geoRepositoryProvider = geoRepositoryProvider;
  }

  @Override
  public GeoSecurityManager get() {
    return newInstance(contextProvider.get(), geoRepositoryProvider.get());
  }

  public static GeoSecurityManager_Factory create(Provider<Context> contextProvider,
      Provider<GeoRepository> geoRepositoryProvider) {
    return new GeoSecurityManager_Factory(contextProvider, geoRepositoryProvider);
  }

  public static GeoSecurityManager newInstance(Context context, GeoRepository geoRepository) {
    return new GeoSecurityManager(context, geoRepository);
  }
}
