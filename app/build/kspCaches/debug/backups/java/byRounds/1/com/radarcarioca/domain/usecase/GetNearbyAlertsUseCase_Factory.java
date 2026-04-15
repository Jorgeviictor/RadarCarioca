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
public final class GetNearbyAlertsUseCase_Factory implements Factory<GetNearbyAlertsUseCase> {
  private final Provider<AlertRepository> alertRepositoryProvider;

  public GetNearbyAlertsUseCase_Factory(Provider<AlertRepository> alertRepositoryProvider) {
    this.alertRepositoryProvider = alertRepositoryProvider;
  }

  @Override
  public GetNearbyAlertsUseCase get() {
    return newInstance(alertRepositoryProvider.get());
  }

  public static GetNearbyAlertsUseCase_Factory create(
      Provider<AlertRepository> alertRepositoryProvider) {
    return new GetNearbyAlertsUseCase_Factory(alertRepositoryProvider);
  }

  public static GetNearbyAlertsUseCase newInstance(AlertRepository alertRepository) {
    return new GetNearbyAlertsUseCase(alertRepository);
  }
}
