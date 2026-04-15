package com.radarcarioca.ui;

import com.radarcarioca.domain.usecase.CheckPermissionsStatusUseCase;
import com.radarcarioca.domain.usecase.CompleteOnboardingUseCase;
import com.radarcarioca.domain.usecase.GetTodayStatsUseCase;
import com.radarcarioca.domain.usecase.ObserveDriverSettingsUseCase;
import com.radarcarioca.domain.usecase.ObserveSubscriptionAccessUseCase;
import com.radarcarioca.domain.usecase.SaveDriverConfigUseCase;
import com.radarcarioca.domain.usecase.ToggleRadarUseCase;
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
public final class MainViewModel_Factory implements Factory<MainViewModel> {
  private final Provider<GetTodayStatsUseCase> getTodayStatsProvider;

  private final Provider<ObserveSubscriptionAccessUseCase> observeSubscriptionAccessProvider;

  private final Provider<ObserveDriverSettingsUseCase> observeDriverSettingsProvider;

  private final Provider<ToggleRadarUseCase> toggleRadarUseCaseProvider;

  private final Provider<CheckPermissionsStatusUseCase> checkPermissionsStatusProvider;

  private final Provider<SaveDriverConfigUseCase> saveDriverConfigUseCaseProvider;

  private final Provider<CompleteOnboardingUseCase> completeOnboardingUseCaseProvider;

  public MainViewModel_Factory(Provider<GetTodayStatsUseCase> getTodayStatsProvider,
      Provider<ObserveSubscriptionAccessUseCase> observeSubscriptionAccessProvider,
      Provider<ObserveDriverSettingsUseCase> observeDriverSettingsProvider,
      Provider<ToggleRadarUseCase> toggleRadarUseCaseProvider,
      Provider<CheckPermissionsStatusUseCase> checkPermissionsStatusProvider,
      Provider<SaveDriverConfigUseCase> saveDriverConfigUseCaseProvider,
      Provider<CompleteOnboardingUseCase> completeOnboardingUseCaseProvider) {
    this.getTodayStatsProvider = getTodayStatsProvider;
    this.observeSubscriptionAccessProvider = observeSubscriptionAccessProvider;
    this.observeDriverSettingsProvider = observeDriverSettingsProvider;
    this.toggleRadarUseCaseProvider = toggleRadarUseCaseProvider;
    this.checkPermissionsStatusProvider = checkPermissionsStatusProvider;
    this.saveDriverConfigUseCaseProvider = saveDriverConfigUseCaseProvider;
    this.completeOnboardingUseCaseProvider = completeOnboardingUseCaseProvider;
  }

  @Override
  public MainViewModel get() {
    return newInstance(getTodayStatsProvider.get(), observeSubscriptionAccessProvider.get(), observeDriverSettingsProvider.get(), toggleRadarUseCaseProvider.get(), checkPermissionsStatusProvider.get(), saveDriverConfigUseCaseProvider.get(), completeOnboardingUseCaseProvider.get());
  }

  public static MainViewModel_Factory create(Provider<GetTodayStatsUseCase> getTodayStatsProvider,
      Provider<ObserveSubscriptionAccessUseCase> observeSubscriptionAccessProvider,
      Provider<ObserveDriverSettingsUseCase> observeDriverSettingsProvider,
      Provider<ToggleRadarUseCase> toggleRadarUseCaseProvider,
      Provider<CheckPermissionsStatusUseCase> checkPermissionsStatusProvider,
      Provider<SaveDriverConfigUseCase> saveDriverConfigUseCaseProvider,
      Provider<CompleteOnboardingUseCase> completeOnboardingUseCaseProvider) {
    return new MainViewModel_Factory(getTodayStatsProvider, observeSubscriptionAccessProvider, observeDriverSettingsProvider, toggleRadarUseCaseProvider, checkPermissionsStatusProvider, saveDriverConfigUseCaseProvider, completeOnboardingUseCaseProvider);
  }

  public static MainViewModel newInstance(GetTodayStatsUseCase getTodayStats,
      ObserveSubscriptionAccessUseCase observeSubscriptionAccess,
      ObserveDriverSettingsUseCase observeDriverSettings, ToggleRadarUseCase toggleRadarUseCase,
      CheckPermissionsStatusUseCase checkPermissionsStatus,
      SaveDriverConfigUseCase saveDriverConfigUseCase,
      CompleteOnboardingUseCase completeOnboardingUseCase) {
    return new MainViewModel(getTodayStats, observeSubscriptionAccess, observeDriverSettings, toggleRadarUseCase, checkPermissionsStatus, saveDriverConfigUseCase, completeOnboardingUseCase);
  }
}
