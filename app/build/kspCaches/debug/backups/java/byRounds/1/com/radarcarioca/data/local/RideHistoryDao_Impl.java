package com.radarcarioca.data.local;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.radarcarioca.data.local.entity.RideRecordEntity;
import java.lang.Class;
import java.lang.Double;
import java.lang.Exception;
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
public final class RideHistoryDao_Impl implements RideHistoryDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<RideRecordEntity> __insertionAdapterOfRideRecordEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteOlderThan;

  public RideHistoryDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfRideRecordEntity = new EntityInsertionAdapter<RideRecordEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `ride_history` (`id`,`timestamp`,`destinationText`,`fareValue`,`netProfit`,`profitPerKm`,`wasAccepted`,`hadSecurityAlert`,`securityZoneName`,`sourceApp`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final RideRecordEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getTimestamp());
        statement.bindString(3, entity.getDestinationText());
        statement.bindDouble(4, entity.getFareValue());
        statement.bindDouble(5, entity.getNetProfit());
        statement.bindDouble(6, entity.getProfitPerKm());
        final int _tmp = entity.getWasAccepted() ? 1 : 0;
        statement.bindLong(7, _tmp);
        final int _tmp_1 = entity.getHadSecurityAlert() ? 1 : 0;
        statement.bindLong(8, _tmp_1);
        statement.bindString(9, entity.getSecurityZoneName());
        statement.bindString(10, entity.getSourceApp());
      }
    };
    this.__preparedStmtOfDeleteOlderThan = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM ride_history WHERE timestamp < ?";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final RideRecordEntity record,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfRideRecordEntity.insert(record);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteOlderThan(final long before, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteOlderThan.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, before);
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
          __preparedStmtOfDeleteOlderThan.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<RideRecordEntity>> getRecentRides() {
    final String _sql = "SELECT * FROM ride_history ORDER BY timestamp DESC LIMIT 100";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"ride_history"}, new Callable<List<RideRecordEntity>>() {
      @Override
      @NonNull
      public List<RideRecordEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfDestinationText = CursorUtil.getColumnIndexOrThrow(_cursor, "destinationText");
          final int _cursorIndexOfFareValue = CursorUtil.getColumnIndexOrThrow(_cursor, "fareValue");
          final int _cursorIndexOfNetProfit = CursorUtil.getColumnIndexOrThrow(_cursor, "netProfit");
          final int _cursorIndexOfProfitPerKm = CursorUtil.getColumnIndexOrThrow(_cursor, "profitPerKm");
          final int _cursorIndexOfWasAccepted = CursorUtil.getColumnIndexOrThrow(_cursor, "wasAccepted");
          final int _cursorIndexOfHadSecurityAlert = CursorUtil.getColumnIndexOrThrow(_cursor, "hadSecurityAlert");
          final int _cursorIndexOfSecurityZoneName = CursorUtil.getColumnIndexOrThrow(_cursor, "securityZoneName");
          final int _cursorIndexOfSourceApp = CursorUtil.getColumnIndexOrThrow(_cursor, "sourceApp");
          final List<RideRecordEntity> _result = new ArrayList<RideRecordEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final RideRecordEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final String _tmpDestinationText;
            _tmpDestinationText = _cursor.getString(_cursorIndexOfDestinationText);
            final double _tmpFareValue;
            _tmpFareValue = _cursor.getDouble(_cursorIndexOfFareValue);
            final double _tmpNetProfit;
            _tmpNetProfit = _cursor.getDouble(_cursorIndexOfNetProfit);
            final double _tmpProfitPerKm;
            _tmpProfitPerKm = _cursor.getDouble(_cursorIndexOfProfitPerKm);
            final boolean _tmpWasAccepted;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfWasAccepted);
            _tmpWasAccepted = _tmp != 0;
            final boolean _tmpHadSecurityAlert;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfHadSecurityAlert);
            _tmpHadSecurityAlert = _tmp_1 != 0;
            final String _tmpSecurityZoneName;
            _tmpSecurityZoneName = _cursor.getString(_cursorIndexOfSecurityZoneName);
            final String _tmpSourceApp;
            _tmpSourceApp = _cursor.getString(_cursorIndexOfSourceApp);
            _item = new RideRecordEntity(_tmpId,_tmpTimestamp,_tmpDestinationText,_tmpFareValue,_tmpNetProfit,_tmpProfitPerKm,_tmpWasAccepted,_tmpHadSecurityAlert,_tmpSecurityZoneName,_tmpSourceApp);
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

  @Override
  public Flow<List<RideRecordEntity>> getRidesSince(final long since) {
    final String _sql = "SELECT * FROM ride_history WHERE timestamp > ? ORDER BY timestamp DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, since);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"ride_history"}, new Callable<List<RideRecordEntity>>() {
      @Override
      @NonNull
      public List<RideRecordEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfDestinationText = CursorUtil.getColumnIndexOrThrow(_cursor, "destinationText");
          final int _cursorIndexOfFareValue = CursorUtil.getColumnIndexOrThrow(_cursor, "fareValue");
          final int _cursorIndexOfNetProfit = CursorUtil.getColumnIndexOrThrow(_cursor, "netProfit");
          final int _cursorIndexOfProfitPerKm = CursorUtil.getColumnIndexOrThrow(_cursor, "profitPerKm");
          final int _cursorIndexOfWasAccepted = CursorUtil.getColumnIndexOrThrow(_cursor, "wasAccepted");
          final int _cursorIndexOfHadSecurityAlert = CursorUtil.getColumnIndexOrThrow(_cursor, "hadSecurityAlert");
          final int _cursorIndexOfSecurityZoneName = CursorUtil.getColumnIndexOrThrow(_cursor, "securityZoneName");
          final int _cursorIndexOfSourceApp = CursorUtil.getColumnIndexOrThrow(_cursor, "sourceApp");
          final List<RideRecordEntity> _result = new ArrayList<RideRecordEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final RideRecordEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final String _tmpDestinationText;
            _tmpDestinationText = _cursor.getString(_cursorIndexOfDestinationText);
            final double _tmpFareValue;
            _tmpFareValue = _cursor.getDouble(_cursorIndexOfFareValue);
            final double _tmpNetProfit;
            _tmpNetProfit = _cursor.getDouble(_cursorIndexOfNetProfit);
            final double _tmpProfitPerKm;
            _tmpProfitPerKm = _cursor.getDouble(_cursorIndexOfProfitPerKm);
            final boolean _tmpWasAccepted;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfWasAccepted);
            _tmpWasAccepted = _tmp != 0;
            final boolean _tmpHadSecurityAlert;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfHadSecurityAlert);
            _tmpHadSecurityAlert = _tmp_1 != 0;
            final String _tmpSecurityZoneName;
            _tmpSecurityZoneName = _cursor.getString(_cursorIndexOfSecurityZoneName);
            final String _tmpSourceApp;
            _tmpSourceApp = _cursor.getString(_cursorIndexOfSourceApp);
            _item = new RideRecordEntity(_tmpId,_tmpTimestamp,_tmpDestinationText,_tmpFareValue,_tmpNetProfit,_tmpProfitPerKm,_tmpWasAccepted,_tmpHadSecurityAlert,_tmpSecurityZoneName,_tmpSourceApp);
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

  @Override
  public Flow<Double> getTotalProfit(final long since) {
    final String _sql = "SELECT SUM(netProfit) FROM ride_history WHERE wasAccepted = 1 AND timestamp > ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, since);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"ride_history"}, new Callable<Double>() {
      @Override
      @Nullable
      public Double call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Double _result;
          if (_cursor.moveToFirst()) {
            final Double _tmp;
            if (_cursor.isNull(0)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getDouble(0);
            }
            _result = _tmp;
          } else {
            _result = null;
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

  @Override
  public Flow<Integer> getAcceptedCount(final long since) {
    final String _sql = "SELECT COUNT(*) FROM ride_history WHERE wasAccepted = 1 AND timestamp > ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, since);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"ride_history"}, new Callable<Integer>() {
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
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<Integer> getRejectedCount(final long since) {
    final String _sql = "SELECT COUNT(*) FROM ride_history WHERE wasAccepted = 0 AND timestamp > ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, since);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"ride_history"}, new Callable<Integer>() {
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
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<Integer> getAlertCount(final long since) {
    final String _sql = "SELECT COUNT(*) FROM ride_history WHERE hadSecurityAlert = 1 AND timestamp > ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, since);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"ride_history"}, new Callable<Integer>() {
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
}
