package com.radarcarioca.data.remote.datasource;

import com.google.firebase.database.FirebaseDatabase;
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
public final class AlertRemoteDataSourceImpl_Factory implements Factory<AlertRemoteDataSourceImpl> {
  private final Provider<FirebaseDatabase> databaseProvider;

  public AlertRemoteDataSourceImpl_Factory(Provider<FirebaseDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public AlertRemoteDataSourceImpl get() {
    return newInstance(databaseProvider.get());
  }

  public static AlertRemoteDataSourceImpl_Factory create(
      Provider<FirebaseDatabase> databaseProvider) {
    return new AlertRemoteDataSourceImpl_Factory(databaseProvider);
  }

  public static AlertRemoteDataSourceImpl newInstance(FirebaseDatabase database) {
    return new AlertRemoteDataSourceImpl(database);
  }
}
