package com.radarcarioca.domain.usecase;

import com.radarcarioca.domain.repository.SubscriptionRepository;
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
public final class ObserveSubscriptionAccessUseCase_Factory implements Factory<ObserveSubscriptionAccessUseCase> {
  private final Provider<SubscriptionRepository> subscriptionRepositoryProvider;

  public ObserveSubscriptionAccessUseCase_Factory(
      Provider<SubscriptionRepository> subscriptionRepositoryProvider) {
    this.subscriptionRepositoryProvider = subscriptionRepositoryProvider;
  }

  @Override
  public ObserveSubscriptionAccessUseCase get() {
    return newInstance(subscriptionRepositoryProvider.get());
  }

  public static ObserveSubscriptionAccessUseCase_Factory create(
      Provider<SubscriptionRepository> subscriptionRepositoryProvider) {
    return new ObserveSubscriptionAccessUseCase_Factory(subscriptionRepositoryProvider);
  }

  public static ObserveSubscriptionAccessUseCase newInstance(
      SubscriptionRepository subscriptionRepository) {
    return new ObserveSubscriptionAccessUseCase(subscriptionRepository);
  }
}
