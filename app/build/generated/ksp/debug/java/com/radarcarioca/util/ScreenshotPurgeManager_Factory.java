package com.radarcarioca.util;

import android.content.Context;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
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
public final class ScreenshotPurgeManager_Factory implements Factory<ScreenshotPurgeManager> {
  private final Provider<Context> contextProvider;

  public ScreenshotPurgeManager_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public ScreenshotPurgeManager get() {
    return newInstance(contextProvider.get());
  }

  public static ScreenshotPurgeManager_Factory create(Provider<Context> contextProvider) {
    return new ScreenshotPurgeManager_Factory(contextProvider);
  }

  public static ScreenshotPurgeManager newInstance(Context context) {
    return new ScreenshotPurgeManager(context);
  }
}
