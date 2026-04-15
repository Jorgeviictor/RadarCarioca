package com.radarcarioca.domain.usecase;

import com.radarcarioca.domain.repository.AlertRepository;
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
public final class PurgeExpiredAlertsUseCase_Factory implements Factory<PurgeExpiredAlertsUseCase> {
  private final Provider<AlertRepository> alertRepositoryProvider;

  public PurgeExpiredAlertsUseCase_Factory(Provider<AlertRepository> alertRepositoryProvider) {
    this.alertRepositoryProvider = alertRepositoryProvider;
  }

  @Override
  public PurgeExpiredAlertsUseCase get() {
    return newInstance(alertRepositoryProvider.get());
  }

  public static PurgeExpiredAlertsUseCase_Factory create(
      Provider<AlertRepository> alertRepositoryProvider) {
    return new PurgeExpiredAlertsUseCase_Factory(alertRepositoryProvider);
  }

  public static PurgeExpiredAlertsUseCase newInstance(AlertRepository alertRepository) {
    return new PurgeExpiredAlertsUseCase(alertRepository);
  }
}
