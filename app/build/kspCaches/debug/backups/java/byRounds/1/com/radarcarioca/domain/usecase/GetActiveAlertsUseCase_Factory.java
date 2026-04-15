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
public final class GetActiveAlertsUseCase_Factory implements Factory<GetActiveAlertsUseCase> {
  private final Provider<AlertRepository> alertRepositoryProvider;

  public GetActiveAlertsUseCase_Factory(Provider<AlertRepository> alertRepositoryProvider) {
    this.alertRepositoryProvider = alertRepositoryProvider;
  }

  @Override
  public GetActiveAlertsUseCase get() {
    return newInstance(alertRepositoryProvider.get());
  }

  public static GetActiveAlertsUseCase_Factory create(
      Provider<AlertRepository> alertRepositoryProvider) {
    return new GetActiveAlertsUseCase_Factory(alertRepositoryProvider);
  }

  public static GetActiveAlertsUseCase newInstance(AlertRepository alertRepository) {
    return new GetActiveAlertsUseCase(alertRepository);
  }
}
