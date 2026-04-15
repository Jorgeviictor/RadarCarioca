package com.radarcarioca.service;

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
public final class GeocodingService_Factory implements Factory<GeocodingService> {
  @Override
  public GeocodingService get() {
    return newInstance();
  }

  public static GeocodingService_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static GeocodingService newInstance() {
    return new GeocodingService();
  }

  private static final class InstanceHolder {
    private static final GeocodingService_Factory INSTANCE = new GeocodingService_Factory();
  }
}
