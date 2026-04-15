package com.radarcarioca.overlay;

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
public final class OverlayManager_Factory implements Factory<OverlayManager> {
  private final Provider<Context> contextProvider;

  public OverlayManager_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public OverlayManager get() {
    return newInstance(contextProvider.get());
  }

  public static OverlayManager_Factory create(Provider<Context> contextProvider) {
    return new OverlayManager_Factory(contextProvider);
  }

  public static OverlayManager newInstance(Context context) {
    return new OverlayManager(context);
  }
}
