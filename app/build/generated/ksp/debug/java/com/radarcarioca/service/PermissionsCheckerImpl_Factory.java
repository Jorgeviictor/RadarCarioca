package com.radarcarioca.service;

import android.content.Context;
import com.radarcarioca.geo.GeoSecurityManager;
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
public final class PermissionsCheckerImpl_Factory implements Factory<PermissionsCheckerImpl> {
  private final Provider<Context> contextProvider;

  private final Provider<GeoSecurityManager> geoSecurityManagerProvider;

  public PermissionsCheckerImpl_Factory(Provider<Context> contextProvider,
      Provider<GeoSecurityManager> geoSecurityManagerProvider) {
    this.contextProvider = contextProvider;
    this.geoSecurityManagerProvider = geoSecurityManagerProvider;
  }

  @Override
  public PermissionsCheckerImpl get() {
    return newInstance(contextProvider.get(), geoSecurityManagerProvider.get());
  }

  public static PermissionsCheckerImpl_Factory create(Provider<Context> contextProvider,
      Provider<GeoSecurityManager> geoSecurityManagerProvider) {
    return new PermissionsCheckerImpl_Factory(contextProvider, geoSecurityManagerProvider);
  }

  public static PermissionsCheckerImpl newInstance(Context context,
      GeoSecurityManager geoSecurityManager) {
    return new PermissionsCheckerImpl(context, geoSecurityManager);
  }
}
