package com.radarcarioca.data.repository;

import com.radarcarioca.data.local.GeoFeatureDao;
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
public final class GeoRepositoryImpl_Factory implements Factory<GeoRepositoryImpl> {
  private final Provider<GeoFeatureDao> daoProvider;

  public GeoRepositoryImpl_Factory(Provider<GeoFeatureDao> daoProvider) {
    this.daoProvider = daoProvider;
  }

  @Override
  public GeoRepositoryImpl get() {
    return newInstance(daoProvider.get());
  }

  public static GeoRepositoryImpl_Factory create(Provider<GeoFeatureDao> daoProvider) {
    return new GeoRepositoryImpl_Factory(daoProvider);
  }

  public static GeoRepositoryImpl newInstance(GeoFeatureDao dao) {
    return new GeoRepositoryImpl(dao);
  }
}
