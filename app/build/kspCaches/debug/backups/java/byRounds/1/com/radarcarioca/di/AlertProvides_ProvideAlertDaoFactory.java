package com.radarcarioca.di;

import com.radarcarioca.data.local.AlertDao;
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
public final class AlertProvides_ProvideAlertDaoFactory implements Factory<AlertDao> {
  private final Provider<RadarDatabase> dbProvider;

  public AlertProvides_ProvideAlertDaoFactory(Provider<RadarDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public AlertDao get() {
    return provideAlertDao(dbProvider.get());
  }

  public static AlertProvides_ProvideAlertDaoFactory create(Provider<RadarDatabase> dbProvider) {
    return new AlertProvides_ProvideAlertDaoFactory(dbProvider);
  }

  public static AlertDao provideAlertDao(RadarDatabase db) {
    return Preconditions.checkNotNullFromProvides(AlertProvides.INSTANCE.provideAlertDao(db));
  }
}
