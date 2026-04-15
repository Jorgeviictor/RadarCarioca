package com.radarcarioca.service;

import com.radarcarioca.data.local.DriverPreferences;
import com.radarcarioca.domain.usecase.ProcessRideOfferUseCase;
import com.radarcarioca.domain.usecase.RecordRideDecisionUseCase;
import com.radarcarioca.geo.GeoSecurityManager;
import com.radarcarioca.overlay.FloatingButtonManager;
import com.radarcarioca.overlay.OverlayManager;
import com.radarcarioca.util.ScreenshotPurgeManager;
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
public final class RadarForegroundService_MembersInjector implements MembersInjector<RadarForegroundService> {
  private final Provider<ProcessRideOfferUseCase> processRideOfferUseCaseProvider;

  private final Provider<RecordRideDecisionUseCase> recordRideDecisionUseCaseProvider;

  private final Provider<GeoSecurityManager> geoSecurityManagerProvider;

  private final Provider<OverlayManager> overlayManagerProvider;

  private final Provider<FloatingButtonManager> floatingButtonManagerProvider;

  private final Provider<ScreenshotPurgeManager> screenshotPurgeManagerProvider;

  private final Provider<DriverPreferences> driverPreferencesProvider;

  public RadarForegroundService_MembersInjector(
      Provider<ProcessRideOfferUseCase> processRideOfferUseCaseProvider,
      Provider<RecordRideDecisionUseCase> recordRideDecisionUseCaseProvider,
      Provider<GeoSecurityManager> geoSecurityManagerProvider,
      Provider<OverlayManager> overlayManagerProvider,
      Provider<FloatingButtonManager> floatingButtonManagerProvider,
      Provider<ScreenshotPurgeManager> screenshotPurgeManagerProvider,
      Provider<DriverPreferences> driverPreferencesProvider) {
    this.processRideOfferUseCaseProvider = processRideOfferUseCaseProvider;
    this.recordRideDecisionUseCaseProvider = recordRideDecisionUseCaseProvider;
    this.geoSecurityManagerProvider = geoSecurityManagerProvider;
    this.overlayManagerProvider = overlayManagerProvider;
    this.floatingButtonManagerProvider = floatingButtonManagerProvider;
    this.screenshotPurgeManagerProvider = screenshotPurgeManagerProvider;
    this.driverPreferencesProvider = driverPreferencesProvider;
  }

  public static MembersInjector<RadarForegroundService> create(
      Provider<ProcessRideOfferUseCase> processRideOfferUseCaseProvider,
      Provider<RecordRideDecisionUseCase> recordRideDecisionUseCaseProvider,
      Provider<GeoSecurityManager> geoSecurityManagerProvider,
      Provider<OverlayManager> overlayManagerProvider,
      Provider<FloatingButtonManager> floatingButtonManagerProvider,
      Provider<ScreenshotPurgeManager> screenshotPurgeManagerProvider,
      Provider<DriverPreferences> driverPreferencesProvider) {
    return new RadarForegroundService_MembersInjector(processRideOfferUseCaseProvider, recordRideDecisionUseCaseProvider, geoSecurityManagerProvider, overlayManagerProvider, floatingButtonManagerProvider, screenshotPurgeManagerProvider, driverPreferencesProvider);
  }

  @Override
  public void injectMembers(RadarForegroundService instance) {
    injectProcessRideOfferUseCase(instance, processRideOfferUseCaseProvider.get());
    injectRecordRideDecisionUseCase(instance, recordRideDecisionUseCaseProvider.get());
    injectGeoSecurityManager(instance, geoSecurityManagerProvider.get());
    injectOverlayManager(instance, overlayManagerProvider.get());
    injectFloatingButtonManager(instance, floatingButtonManagerProvider.get());
    injectScreenshotPurgeManager(instance, screenshotPurgeManagerProvider.get());
    injectDriverPreferences(instance, driverPreferencesProvider.get());
  }

  @InjectedFieldSignature("com.radarcarioca.service.RadarForegroundService.processRideOfferUseCase")
  public static void injectProcessRideOfferUseCase(RadarForegroundService instance,
      ProcessRideOfferUseCase processRideOfferUseCase) {
    instance.processRideOfferUseCase = processRideOfferUseCase;
  }

  @InjectedFieldSignature("com.radarcarioca.service.RadarForegroundService.recordRideDecisionUseCase")
  public static void injectRecordRideDecisionUseCase(RadarForegroundService instance,
      RecordRideDecisionUseCase recordRideDecisionUseCase) {
    instance.recordRideDecisionUseCase = recordRideDecisionUseCase;
  }

  @InjectedFieldSignature("com.radarcarioca.service.RadarForegroundService.geoSecurityManager")
  public static void injectGeoSecurityManager(RadarForegroundService instance,
      GeoSecurityManager geoSecurityManager) {
    instance.geoSecurityManager = geoSecurityManager;
  }

  @InjectedFieldSignature("com.radarcarioca.service.RadarForegroundService.overlayManager")
  public static void injectOverlayManager(RadarForegroundService instance,
      OverlayManager overlayManager) {
    instance.overlayManager = overlayManager;
  }

  @InjectedFieldSignature("com.radarcarioca.service.RadarForegroundService.floatingButtonManager")
  public static void injectFloatingButtonManager(RadarForegroundService instance,
      FloatingButtonManager floatingButtonManager) {
    instance.floatingButtonManager = floatingButtonManager;
  }

  @InjectedFieldSignature("com.radarcarioca.service.RadarForegroundService.screenshotPurgeManager")
  public static void injectScreenshotPurgeManager(RadarForegroundService instance,
      ScreenshotPurgeManager screenshotPurgeManager) {
    instance.screenshotPurgeManager = screenshotPurgeManager;
  }

  @InjectedFieldSignature("com.radarcarioca.service.RadarForegroundService.driverPreferences")
  public static void injectDriverPreferences(RadarForegroundService instance,
      DriverPreferences driverPreferences) {
    instance.driverPreferences = driverPreferences;
  }
}
