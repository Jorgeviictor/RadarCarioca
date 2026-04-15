package com.radarcarioca.data.local;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class RadarDatabase_Impl extends RadarDatabase {
  private volatile GeoFeatureDao _geoFeatureDao;

  private volatile RideHistoryDao _rideHistoryDao;

  private volatile AlertDao _alertDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(2) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `geo_features` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `description` TEXT NOT NULL, `area` TEXT NOT NULL, `geometryType` TEXT NOT NULL, `coordinatesJson` TEXT NOT NULL, `centerLat` REAL NOT NULL, `centerLng` REAL NOT NULL, `bufferKm` REAL NOT NULL, `alertLevel` TEXT NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `ride_history` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `timestamp` INTEGER NOT NULL, `destinationText` TEXT NOT NULL, `fareValue` REAL NOT NULL, `netProfit` REAL NOT NULL, `profitPerKm` REAL NOT NULL, `wasAccepted` INTEGER NOT NULL, `hadSecurityAlert` INTEGER NOT NULL, `securityZoneName` TEXT NOT NULL, `sourceApp` TEXT NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `security_alerts` (`id` TEXT NOT NULL, `title` TEXT NOT NULL, `description` TEXT NOT NULL, `areaName` TEXT NOT NULL, `lat` REAL NOT NULL, `lng` REAL NOT NULL, `radiusMeters` INTEGER NOT NULL, `severity` TEXT NOT NULL, `source` TEXT NOT NULL, `isActive` INTEGER NOT NULL, `createdAt` INTEGER NOT NULL, `expiresAt` INTEGER, `cachedAt` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_security_alerts_isActive` ON `security_alerts` (`isActive`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_security_alerts_lat_lng` ON `security_alerts` (`lat`, `lng`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '2180f50b85a06a822ddf83c5d01b3815')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `geo_features`");
        db.execSQL("DROP TABLE IF EXISTS `ride_history`");
        db.execSQL("DROP TABLE IF EXISTS `security_alerts`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsGeoFeatures = new HashMap<String, TableInfo.Column>(10);
        _columnsGeoFeatures.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsGeoFeatures.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsGeoFeatures.put("description", new TableInfo.Column("description", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsGeoFeatures.put("area", new TableInfo.Column("area", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsGeoFeatures.put("geometryType", new TableInfo.Column("geometryType", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsGeoFeatures.put("coordinatesJson", new TableInfo.Column("coordinatesJson", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsGeoFeatures.put("centerLat", new TableInfo.Column("centerLat", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsGeoFeatures.put("centerLng", new TableInfo.Column("centerLng", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsGeoFeatures.put("bufferKm", new TableInfo.Column("bufferKm", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsGeoFeatures.put("alertLevel", new TableInfo.Column("alertLevel", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysGeoFeatures = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesGeoFeatures = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoGeoFeatures = new TableInfo("geo_features", _columnsGeoFeatures, _foreignKeysGeoFeatures, _indicesGeoFeatures);
        final TableInfo _existingGeoFeatures = TableInfo.read(db, "geo_features");
        if (!_infoGeoFeatures.equals(_existingGeoFeatures)) {
          return new RoomOpenHelper.ValidationResult(false, "geo_features(com.radarcarioca.data.local.entity.GeoFeatureEntity).\n"
                  + " Expected:\n" + _infoGeoFeatures + "\n"
                  + " Found:\n" + _existingGeoFeatures);
        }
        final HashMap<String, TableInfo.Column> _columnsRideHistory = new HashMap<String, TableInfo.Column>(10);
        _columnsRideHistory.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRideHistory.put("timestamp", new TableInfo.Column("timestamp", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRideHistory.put("destinationText", new TableInfo.Column("destinationText", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRideHistory.put("fareValue", new TableInfo.Column("fareValue", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRideHistory.put("netProfit", new TableInfo.Column("netProfit", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRideHistory.put("profitPerKm", new TableInfo.Column("profitPerKm", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRideHistory.put("wasAccepted", new TableInfo.Column("wasAccepted", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRideHistory.put("hadSecurityAlert", new TableInfo.Column("hadSecurityAlert", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRideHistory.put("securityZoneName", new TableInfo.Column("securityZoneName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRideHistory.put("sourceApp", new TableInfo.Column("sourceApp", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysRideHistory = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesRideHistory = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoRideHistory = new TableInfo("ride_history", _columnsRideHistory, _foreignKeysRideHistory, _indicesRideHistory);
        final TableInfo _existingRideHistory = TableInfo.read(db, "ride_history");
        if (!_infoRideHistory.equals(_existingRideHistory)) {
          return new RoomOpenHelper.ValidationResult(false, "ride_history(com.radarcarioca.data.local.entity.RideRecordEntity).\n"
                  + " Expected:\n" + _infoRideHistory + "\n"
                  + " Found:\n" + _existingRideHistory);
        }
        final HashMap<String, TableInfo.Column> _columnsSecurityAlerts = new HashMap<String, TableInfo.Column>(13);
        _columnsSecurityAlerts.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSecurityAlerts.put("title", new TableInfo.Column("title", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSecurityAlerts.put("description", new TableInfo.Column("description", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSecurityAlerts.put("areaName", new TableInfo.Column("areaName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSecurityAlerts.put("lat", new TableInfo.Column("lat", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSecurityAlerts.put("lng", new TableInfo.Column("lng", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSecurityAlerts.put("radiusMeters", new TableInfo.Column("radiusMeters", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSecurityAlerts.put("severity", new TableInfo.Column("severity", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSecurityAlerts.put("source", new TableInfo.Column("source", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSecurityAlerts.put("isActive", new TableInfo.Column("isActive", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSecurityAlerts.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSecurityAlerts.put("expiresAt", new TableInfo.Column("expiresAt", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSecurityAlerts.put("cachedAt", new TableInfo.Column("cachedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysSecurityAlerts = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesSecurityAlerts = new HashSet<TableInfo.Index>(2);
        _indicesSecurityAlerts.add(new TableInfo.Index("index_security_alerts_isActive", false, Arrays.asList("isActive"), Arrays.asList("ASC")));
        _indicesSecurityAlerts.add(new TableInfo.Index("index_security_alerts_lat_lng", false, Arrays.asList("lat", "lng"), Arrays.asList("ASC", "ASC")));
        final TableInfo _infoSecurityAlerts = new TableInfo("security_alerts", _columnsSecurityAlerts, _foreignKeysSecurityAlerts, _indicesSecurityAlerts);
        final TableInfo _existingSecurityAlerts = TableInfo.read(db, "security_alerts");
        if (!_infoSecurityAlerts.equals(_existingSecurityAlerts)) {
          return new RoomOpenHelper.ValidationResult(false, "security_alerts(com.radarcarioca.data.local.entity.AlertEntity).\n"
                  + " Expected:\n" + _infoSecurityAlerts + "\n"
                  + " Found:\n" + _existingSecurityAlerts);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "2180f50b85a06a822ddf83c5d01b3815", "a9996d6bb3c813f1fad5490eefb17d5d");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "geo_features","ride_history","security_alerts");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    try {
      super.beginTransaction();
      _db.execSQL("DELETE FROM `geo_features`");
      _db.execSQL("DELETE FROM `ride_history`");
      _db.execSQL("DELETE FROM `security_alerts`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(GeoFeatureDao.class, GeoFeatureDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(RideHistoryDao.class, RideHistoryDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(AlertDao.class, AlertDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public GeoFeatureDao geoFeatureDao() {
    if (_geoFeatureDao != null) {
      return _geoFeatureDao;
    } else {
      synchronized(this) {
        if(_geoFeatureDao == null) {
          _geoFeatureDao = new GeoFeatureDao_Impl(this);
        }
        return _geoFeatureDao;
      }
    }
  }

  @Override
  public RideHistoryDao rideHistoryDao() {
    if (_rideHistoryDao != null) {
      return _rideHistoryDao;
    } else {
      synchronized(this) {
        if(_rideHistoryDao == null) {
          _rideHistoryDao = new RideHistoryDao_Impl(this);
        }
        return _rideHistoryDao;
      }
    }
  }

  @Override
  public AlertDao alertDao() {
    if (_alertDao != null) {
      return _alertDao;
    } else {
      synchronized(this) {
        if(_alertDao == null) {
          _alertDao = new AlertDao_Impl(this);
        }
        return _alertDao;
      }
    }
  }
}
