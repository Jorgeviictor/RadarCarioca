package com.radarcarioca.overlay;

import android.content.Context;
import com.radarcarioca.data.local.GeoFeatureDao;
import com.radarcarioca.data.local.RideHistoryDao;
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
public final class FloatingButtonManager_Factory implements Factory<FloatingButtonManager> {
  private final Provider<Context> contextProvider;

  private final Provider<RideHistoryDao> rideHistoryDaoProvider;

  private final Provider<GeoFeatureDao> geoFeatureDaoProvider;

  public FloatingButtonManager_Factory(Provider<Context> contextProvider,
      Provider<RideHistoryDao> rideHistoryDaoProvider,
      Provider<GeoFeatureDao> geoFeatureDaoProvider) {
    this.contextProvider = contextProvider;
    this.rideHistoryDaoProvider = rideHistoryDaoProvider;
    this.geoFeatureDaoProvider = geoFeatureDaoProvider;
  }

  @Override
  public FloatingButtonManager get() {
    return newInstance(contextProvider.get(), rideHistoryDaoProvider.get(), geoFeatureDaoProvider.get());
  }

  public static FloatingButtonManager_Factory create(Provider<Context> contextProvider,
      Provider<RideHistoryDao> rideHistoryDaoProvider,
      Provider<GeoFeatureDao> geoFeatureDaoProvider) {
    return new FloatingButtonManager_Factory(contextProvider, rideHistoryDaoProvider, geoFeatureDaoProvider);
  }

  public static FloatingButtonManager newInstance(Context context, RideHistoryDao rideHistoryDao,
      GeoFeatureDao geoFeatureDao) {
    return new FloatingButtonManager(context, rideHistoryDao, geoFeatureDao);
  }
}
