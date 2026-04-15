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
public final class GetTodayStatsUseCase_Factory implements Factory<GetTodayStatsUseCase> {
  private final Provider<RideHistoryRepository> rideHistoryRepositoryProvider;

  public GetTodayStatsUseCase_Factory(
      Provider<RideHistoryRepository> rideHistoryRepositoryProvider) {
    this.rideHistoryRepositoryProvider = rideHistoryRepositoryProvider;
  }

  @Override
  public GetTodayStatsUseCase get() {
    return newInstance(rideHistoryRepositoryProvider.get());
  }

  public static GetTodayStatsUseCase_Factory create(
      Provider<RideHistoryRepository> rideHistoryRepositoryProvider) {
    return new GetTodayStatsUseCase_Factory(rideHistoryRepositoryProvider);
  }

  public static GetTodayStatsUseCase newInstance(RideHistoryRepository rideHistoryRepository) {
    return new GetTodayStatsUseCase(rideHistoryRepository);
  }
}
