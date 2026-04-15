package com.radarcarioca.data.repository;

import com.radarcarioca.data.local.RideHistoryDao;
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
public final class RideHistoryRepositoryImpl_Factory implements Factory<RideHistoryRepositoryImpl> {
  private final Provider<RideHistoryDao> daoProvider;

  public RideHistoryRepositoryImpl_Factory(Provider<RideHistoryDao> daoProvider) {
    this.daoProvider = daoProvider;
  }

  @Override
  public RideHistoryRepositoryImpl get() {
    return newInstance(daoProvider.get());
  }

  public static RideHistoryRepositoryImpl_Factory create(Provider<RideHistoryDao> daoProvider) {
    return new RideHistoryRepositoryImpl_Factory(daoProvider);
  }

  public static RideHistoryRepositoryImpl newInstance(RideHistoryDao dao) {
    return new RideHistoryRepositoryImpl(dao);
  }
}
