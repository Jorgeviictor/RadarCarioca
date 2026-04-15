package com.radarcarioca.data.local;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.radarcarioca.data.local.entity.GeoFeatureEntity;
import com.radarcarioca.data.model.AlertLevel;
import java.lang.Class;
import java.lang.Exception;
import java.lang.IllegalArgumentException;
import java.lang.Integer;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class GeoFeatureDao_Impl implements GeoFeatureDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<GeoFeatureEntity> __insertionAdapterOfGeoFeatureEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAll;

  public GeoFeatureDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfGeoFeatureEntity = new EntityInsertionAdapter<GeoFeatureEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `geo_features` (`id`,`name`,`description`,`area`,`geometryType`,`coordinatesJson`,`centerLat`,`centerLng`,`bufferKm`,`alertLevel`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final GeoFeatureEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getName());
        statement.bindString(3, entity.getDescription());
        statement.bindString(4, entity.getArea());
        statement.bindString(5, entity.getGeometryType());
        statement.bindString(6, entity.getCoordinatesJson());
        statement.bindDouble(7, entity.getCenterLat());
        statement.bindDouble(8, entity.getCenterLng());
        statement.bindDouble(9, entity.getBufferKm());
        statement.bindString(10, __AlertLevel_enumToString(entity.getAlertLevel()));
      }
    };
    this.__preparedStmtOfDeleteAll = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM geo_features";
        return _query;
      }
    };
  }

  @Override
  public Object insertAll(final List<GeoFeatureEntity> features,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfGeoFeatureEntity.insert(features);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteAll(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteAll.acquire();
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteAll.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object getAllFeatures(final Continuation<? super List<GeoFeatureEntity>> $completion) {
    final String _sql = "SELECT * FROM geo_features";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<GeoFeatureEntity>>() {
      @Override
      @NonNull
      public List<GeoFeatureEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfArea = CursorUtil.getColumnIndexOrThrow(_cursor, "area");
          final int _cursorIndexOfGeometryType = CursorUtil.getColumnIndexOrThrow(_cursor, "geometryType");
          final int _cursorIndexOfCoordinatesJson = CursorUtil.getColumnIndexOrThrow(_cursor, "coordinatesJson");
          final int _cursorIndexOfCenterLat = CursorUtil.getColumnIndexOrThrow(_cursor, "centerLat");
          final int _cursorIndexOfCenterLng = CursorUtil.getColumnIndexOrThrow(_cursor, "centerLng");
          final int _cursorIndexOfBufferKm = CursorUtil.getColumnIndexOrThrow(_cursor, "bufferKm");
          final int _cursorIndexOfAlertLevel = CursorUtil.getColumnIndexOrThrow(_cursor, "alertLevel");
          final List<GeoFeatureEntity> _result = new ArrayList<GeoFeatureEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final GeoFeatureEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final String _tmpArea;
            _tmpArea = _cursor.getString(_cursorIndexOfArea);
            final String _tmpGeometryType;
            _tmpGeometryType = _cursor.getString(_cursorIndexOfGeometryType);
            final String _tmpCoordinatesJson;
            _tmpCoordinatesJson = _cursor.getString(_cursorIndexOfCoordinatesJson);
            final double _tmpCenterLat;
            _tmpCenterLat = _cursor.getDouble(_cursorIndexOfCenterLat);
            final double _tmpCenterLng;
            _tmpCenterLng = _cursor.getDouble(_cursorIndexOfCenterLng);
            final double _tmpBufferKm;
            _tmpBufferKm = _cursor.getDouble(_cursorIndexOfBufferKm);
            final AlertLevel _tmpAlertLevel;
            _tmpAlertLevel = __AlertLevel_stringToEnum(_cursor.getString(_cursorIndexOfAlertLevel));
            _item = new GeoFeatureEntity(_tmpId,_tmpName,_tmpDescription,_tmpArea,_tmpGeometryType,_tmpCoordinatesJson,_tmpCenterLat,_tmpCenterLng,_tmpBufferKm,_tmpAlertLevel);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getFeaturesNearby(final double lat, final double lng,
      final Continuation<? super List<GeoFeatureEntity>> $completion) {
    final String _sql = "\n"
            + "        SELECT * FROM geo_features\n"
            + "        WHERE centerLat BETWEEN ? - 0.05 AND ? + 0.05\n"
            + "          AND centerLng BETWEEN ? - 0.05 AND ? + 0.05\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 4);
    int _argIndex = 1;
    _statement.bindDouble(_argIndex, lat);
    _argIndex = 2;
    _statement.bindDouble(_argIndex, lat);
    _argIndex = 3;
    _statement.bindDouble(_argIndex, lng);
    _argIndex = 4;
    _statement.bindDouble(_argIndex, lng);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<GeoFeatureEntity>>() {
      @Override
      @NonNull
      public List<GeoFeatureEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfArea = CursorUtil.getColumnIndexOrThrow(_cursor, "area");
          final int _cursorIndexOfGeometryType = CursorUtil.getColumnIndexOrThrow(_cursor, "geometryType");
          final int _cursorIndexOfCoordinatesJson = CursorUtil.getColumnIndexOrThrow(_cursor, "coordinatesJson");
          final int _cursorIndexOfCenterLat = CursorUtil.getColumnIndexOrThrow(_cursor, "centerLat");
          final int _cursorIndexOfCenterLng = CursorUtil.getColumnIndexOrThrow(_cursor, "centerLng");
          final int _cursorIndexOfBufferKm = CursorUtil.getColumnIndexOrThrow(_cursor, "bufferKm");
          final int _cursorIndexOfAlertLevel = CursorUtil.getColumnIndexOrThrow(_cursor, "alertLevel");
          final List<GeoFeatureEntity> _result = new ArrayList<GeoFeatureEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final GeoFeatureEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final String _tmpArea;
            _tmpArea = _cursor.getString(_cursorIndexOfArea);
            final String _tmpGeometryType;
            _tmpGeometryType = _cursor.getString(_cursorIndexOfGeometryType);
            final String _tmpCoordinatesJson;
            _tmpCoordinatesJson = _cursor.getString(_cursorIndexOfCoordinatesJson);
            final double _tmpCenterLat;
            _tmpCenterLat = _cursor.getDouble(_cursorIndexOfCenterLat);
            final double _tmpCenterLng;
            _tmpCenterLng = _cursor.getDouble(_cursorIndexOfCenterLng);
            final double _tmpBufferKm;
            _tmpBufferKm = _cursor.getDouble(_cursorIndexOfBufferKm);
            final AlertLevel _tmpAlertLevel;
            _tmpAlertLevel = __AlertLevel_stringToEnum(_cursor.getString(_cursorIndexOfAlertLevel));
            _item = new GeoFeatureEntity(_tmpId,_tmpName,_tmpDescription,_tmpArea,_tmpGeometryType,_tmpCoordinatesJson,_tmpCenterLat,_tmpCenterLng,_tmpBufferKm,_tmpAlertLevel);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object count(final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM geo_features";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final int _tmp;
            _tmp = _cursor.getInt(0);
            _result = _tmp;
          } else {
            _result = 0;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<String>> getAllAreas() {
    final String _sql = "SELECT DISTINCT area FROM geo_features WHERE area != ''";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"geo_features"}, new Callable<List<String>>() {
      @Override
      @NonNull
      public List<String> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final List<String> _result = new ArrayList<String>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final String _item;
            _item = _cursor.getString(0);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }

  private String __AlertLevel_enumToString(@NonNull final AlertLevel _value) {
    switch (_value) {
      case DANGER: return "DANGER";
      case CAUTION: return "CAUTION";
      default: throw new IllegalArgumentException("Can't convert enum to string, unknown enum value: " + _value);
    }
  }

  private AlertLevel __AlertLevel_stringToEnum(@NonNull final String _value) {
    switch (_value) {
      case "DANGER": return AlertLevel.DANGER;
      case "CAUTION": return AlertLevel.CAUTION;
      default: throw new IllegalArgumentException("Can't convert value to enum, unknown value: " + _value);
    }
  }
}
