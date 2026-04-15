package com.radarcarioca.domain.usecase;

import com.radarcarioca.domain.repository.RideHistoryRepository;
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
public final class GetRecentRidesUseCase_Factory implements Factory<GetRecentRidesUseCase> {
  private final Provider<RideHistoryRepository> rideHistoryRepositoryProvider;

  public GetRecentRidesUseCase_Factory(
      Provider<RideHistoryRepository> rideHistoryRepositoryProvider) {
    this.rideHistoryRepositoryProvider = rideHistoryRepositoryProvider;
  }

  @Override
  public GetRecentRidesUseCase get() {
    return newInstance(rideHistoryRepositoryProvider.get());
  }

  public static GetRecentRidesUseCase_Factory create(
      Provider<RideHistoryRepository> rideHistoryRepositoryProvider) {
    return new GetRecentRidesUseCase_Factory(rideHistoryRepositoryProvider);
  }

  public static GetRecentRidesUseCase newInstance(RideHistoryRepository rideHistoryRepository) {
    return new GetRecentRidesUseCase(rideHistoryRepository);
  }
}
