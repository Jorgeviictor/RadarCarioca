package com.radarcarioca.data.local.datasource;

import com.radarcarioca.data.local.AlertDao;
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
public final class AlertLocalDataSourceImpl_Factory implements Factory<AlertLocalDataSourceImpl> {
  private final Provider<AlertDao> daoProvider;

  public AlertLocalDataSourceImpl_Factory(Provider<AlertDao> daoProvider) {
    this.daoProvider = daoProvider;
  }

  @Override
  public AlertLocalDataSourceImpl get() {
    return newInstance(daoProvider.get());
  }

  public static AlertLocalDataSourceImpl_Factory create(Provider<AlertDao> daoProvider) {
    return new AlertLocalDataSourceImpl_Factory(daoProvider);
  }

  public static AlertLocalDataSourceImpl newInstance(AlertDao dao) {
    return new AlertLocalDataSourceImpl(dao);
  }
}
