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
public final class RecordRideDecisionUseCase_Factory implements Factory<RecordRideDecisionUseCase> {
  private final Provider<RideHistoryRepository> rideHistoryRepositoryProvider;

  public RecordRideDecisionUseCase_Factory(
      Provider<RideHistoryRepository> rideHistoryRepositoryProvider) {
    this.rideHistoryRepositoryProvider = rideHistoryRepositoryProvider;
  }

  @Override
  public RecordRideDecisionUseCase get() {
    return newInstance(rideHistoryRepositoryProvider.get());
  }

  public static RecordRideDecisionUseCase_Factory create(
      Provider<RideHistoryRepository> rideHistoryRepositoryProvider) {
    return new RecordRideDecisionUseCase_Factory(rideHistoryRepositoryProvider);
  }

  public static RecordRideDecisionUseCase newInstance(RideHistoryRepository rideHistoryRepository) {
    return new RecordRideDecisionUseCase(rideHistoryRepository);
  }
}
