package com.radarcarioca.domain.usecase;

import com.radarcarioca.domain.service.PermissionsChecker;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
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
public final class CheckPermissionsStatusUseCase_Factory implements Factory<CheckPermissionsStatusUseCase> {
  private final Provider<PermissionsChecker> permissionsCheckerProvider;

  public CheckPermissionsStatusUseCase_Factory(
      Provider<PermissionsChecker> permissionsCheckerProvider) {
    this.permissionsCheckerProvider = permissionsCheckerProvider;
  }

  @Override
  public CheckPermissionsStatusUseCase get() {
    return newInstance(permissionsCheckerProvider.get());
  }

  public static CheckPermissionsStatusUseCase_Factory create(
      Provider<PermissionsChecker> permissionsCheckerProvider) {
    return new CheckPermissionsStatusUseCase_Factory(permissionsCheckerProvider);
  }

  public static CheckPermissionsStatusUseCase newInstance(PermissionsChecker permissionsChecker) {
    return new CheckPermissionsStatusUseCase(permissionsChecker);
  }
}
