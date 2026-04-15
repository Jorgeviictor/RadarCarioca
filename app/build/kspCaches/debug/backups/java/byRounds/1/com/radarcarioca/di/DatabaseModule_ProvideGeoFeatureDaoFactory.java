package com.radarcarioca.di;

import com.radarcarioca.data.local.GeoFeatureDao;
import com.radarcarioca.data.local.RadarDatabase;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class DatabaseModule_ProvideGeoFeatureDaoFactory implements Factory<GeoFeatureDao> {
  private final Provider<RadarDatabase> dbProvider;

  public DatabaseModule_ProvideGeoFeatureDaoFactory(Provider<RadarDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public GeoFeatureDao get() {
    return provideGeoFeatureDao(dbProvider.get());
  }

  public static DatabaseModule_ProvideGeoFeatureDaoFactory create(
      Provider<RadarDatabase> dbProvider) {
    return new DatabaseModule_ProvideGeoFeatureDaoFactory(dbProvider);
  }

  public static GeoFeatureDao provideGeoFeatureDao(RadarDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideGeoFeatureDao(db));
  }
}
