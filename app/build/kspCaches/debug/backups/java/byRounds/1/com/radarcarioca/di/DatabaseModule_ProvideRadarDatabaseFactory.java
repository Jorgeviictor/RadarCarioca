package com.radarcarioca.di;

import android.content.Context;
import com.radarcarioca.data.local.RadarDatabase;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class DatabaseModule_ProvideRadarDatabaseFactory implements Factory<RadarDatabase> {
  private final Provider<Context> contextProvider;

  public DatabaseModule_ProvideRadarDatabaseFactory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public RadarDatabase get() {
    return provideRadarDatabase(contextProvider.get());
  }

  public static DatabaseModule_ProvideRadarDatabaseFactory create(
      Provider<Context> contextProvider) {
    return new DatabaseModule_ProvideRadarDatabaseFactory(contextProvider);
  }

  public static RadarDatabase provideRadarDatabase(Context context) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideRadarDatabase(context));
  }
}
