package com.radarcarioca.billing;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
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
public final class SubscriptionRepositoryImpl_Factory implements Factory<SubscriptionRepositoryImpl> {
  private final Provider<BillingManager> billingManagerProvider;

  public SubscriptionRepositoryImpl_Factory(Provider<BillingManager> billingManagerProvider) {
    this.billingManagerProvider = billingManagerProvider;
  }

  @Override
  public SubscriptionRepositoryImpl get() {
    return newInstance(billingManagerProvider.get());
  }

  public static SubscriptionRepositoryImpl_Factory create(
      Provider<BillingManager> billingManagerProvider) {
    return new SubscriptionRepositoryImpl_Factory(billingManagerProvider);
  }

  public static SubscriptionRepositoryImpl newInstance(BillingManager billingManager) {
    return new SubscriptionRepositoryImpl(billingManager);
  }
}
