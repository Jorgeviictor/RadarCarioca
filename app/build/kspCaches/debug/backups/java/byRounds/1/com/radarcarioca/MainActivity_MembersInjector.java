package com.radarcarioca;

import com.radarcarioca.billing.BillingManager;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class MainActivity_MembersInjector implements MembersInjector<MainActivity> {
  private final Provider<BillingManager> billingManagerProvider;

  public MainActivity_MembersInjector(Provider<BillingManager> billingManagerProvider) {
    this.billingManagerProvider = billingManagerProvider;
  }

  public static MembersInjector<MainActivity> create(
      Provider<BillingManager> billingManagerProvider) {
    return new MainActivity_MembersInjector(billingManagerProvider);
  }

  @Override
  public void injectMembers(MainActivity instance) {
    injectBillingManager(instance, billingManagerProvider.get());
  }

  @InjectedFieldSignature("com.radarcarioca.MainActivity.billingManager")
  public static void injectBillingManager(MainActivity instance, BillingManager billingManager) {
    instance.billingManager = billingManager;
  }
}
