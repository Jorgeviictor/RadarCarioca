package com.radarcarioca.data.repository;

import com.radarcarioca.data.local.datasource.AlertLocalDataSource;
import com.radarcarioca.data.remote.datasource.AlertRemoteDataSource;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import kotlinx.coroutines.CoroutineDispatcher;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("com.radarcarioca.di.IoDispatcher")
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
public final class AlertRepositoryImpl_Factory implements Factory<AlertRepositoryImpl> {
  private final Provider<AlertLocalDataSource> localDataSourceProvider;

  private final Provider<AlertRemoteDataSource> remoteDataSourceProvider;

  private final Provider<CoroutineDispatcher> ioDispatcherProvider;

  public AlertRepositoryImpl_Factory(Provider<AlertLocalDataSource> localDataSourceProvider,
      Provider<AlertRemoteDataSource> remoteDataSourceProvider,
      Provider<CoroutineDispatcher> ioDispatcherProvider) {
    this.localDataSourceProvider = localDataSourceProvider;
    this.remoteDataSourceProvider = remoteDataSourceProvider;
    this.ioDispatcherProvider = ioDispatcherProvider;
  }

  @Override
  public AlertRepositoryImpl get() {
    return newInstance(localDataSourceProvider.get(), remoteDataSourceProvider.get(), ioDispatcherProvider.get());
  }

  public static AlertRepositoryImpl_Factory create(
      Provider<AlertLocalDataSource> localDataSourceProvider,
      Provider<AlertRemoteDataSource> remoteDataSourceProvider,
      Provider<CoroutineDispatcher> ioDispatcherProvider) {
    return new AlertRepositoryImpl_Factory(localDataSourceProvider, remoteDataSourceProvider, ioDispatcherProvider);
  }

  public static AlertRepositoryImpl newInstance(AlertLocalDataSource localDataSource,
      AlertRemoteDataSource remoteDataSource, CoroutineDispatcher ioDispatcher) {
    return new AlertRepositoryImpl(localDataSource, remoteDataSource, ioDispatcher);
  }
}
