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
public final class PurgeOldRidesUseCase_Factory implements Factory<PurgeOldRidesUseCase> {
  private final Provider<RideHistoryRepository> rideHistoryRepositoryProvider;

  public PurgeOldRidesUseCase_Factory(
      Provider<RideHistoryRepository> rideHistoryRepositoryProvider) {
    this.rideHistoryRepositoryProvider = rideHistoryRepositoryProvider;
  }

  @Override
  public PurgeOldRidesUseCase get() {
    return newInstance(rideHistoryRepositoryProvider.get());
  }

  public static PurgeOldRidesUseCase_Factory create(
      Provider<RideHistoryRepository> rideHistoryRepositoryProvider) {
    return new PurgeOldRidesUseCase_Factory(rideHistoryRepositoryProvider);
  }

  public static PurgeOldRidesUseCase newInstance(RideHistoryRepository rideHistoryRepository) {
    return new PurgeOldRidesUseCase(rideHistoryRepository);
  }
}
