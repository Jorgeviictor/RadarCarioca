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
public final class RefreshAlertsUseCase_Factory implements Factory<RefreshAlertsUseCase> {
  private final Provider<AlertRepository> alertRepositoryProvider;

  public RefreshAlertsUseCase_Factory(Provider<AlertRepository> alertRepositoryProvider) {
    this.alertRepositoryProvider = alertRepositoryProvider;
  }

  @Override
  public RefreshAlertsUseCase get() {
    return newInstance(alertRepositoryProvider.get());
  }

  public static RefreshAlertsUseCase_Factory create(
      Provider<AlertRepository> alertRepositoryProvider) {
    return new RefreshAlertsUseCase_Factory(alertRepositoryProvider);
  }

  public static RefreshAlertsUseCase newInstance(AlertRepository alertRepository) {
    return new RefreshAlertsUseCase(alertRepository);
  }
}
