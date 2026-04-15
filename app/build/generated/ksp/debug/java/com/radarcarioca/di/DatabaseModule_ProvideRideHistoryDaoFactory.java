package com.radarcarioca.di;

import com.radarcarioca.data.local.RadarDatabase;
import com.radarcarioca.data.local.RideHistoryDao;
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
public final class DatabaseModule_ProvideRideHistoryDaoFactory implements Factory<RideHistoryDao> {
  private final Provider<RadarDatabase> dbProvider;

  public DatabaseModule_ProvideRideHistoryDaoFactory(Provider<RadarDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public RideHistoryDao get() {
    return provideRideHistoryDao(dbProvider.get());
  }

  public static DatabaseModule_ProvideRideHistoryDaoFactory create(
      Provider<RadarDatabase> dbProvider) {
    return new DatabaseModule_ProvideRideHistoryDaoFactory(dbProvider);
  }

  public static RideHistoryDao provideRideHistoryDao(RadarDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideRideHistoryDao(db));
  }
}
