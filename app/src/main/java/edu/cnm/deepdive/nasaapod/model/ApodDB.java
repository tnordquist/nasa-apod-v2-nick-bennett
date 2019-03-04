package edu.cnm.deepdive.nasaapod.model;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverter;
import android.arch.persistence.room.TypeConverters;
import android.support.annotation.Nullable;
import edu.cnm.deepdive.nasaapod.ApodApplication;
import edu.cnm.deepdive.nasaapod.model.ApodDB.Converters;
import edu.cnm.deepdive.nasaapod.model.dao.AccessDao;
import edu.cnm.deepdive.nasaapod.model.dao.ApodDao;
import edu.cnm.deepdive.nasaapod.model.entity.Access;
import edu.cnm.deepdive.nasaapod.model.entity.Apod;
import edu.cnm.deepdive.util.Date;
import java.util.Calendar;

/**
 * Defines the local database as a collection of its entities and converters, with the singleton
 * pattern implemented for app-wide use of a single connection, and declares methods to retrieve
 * data access objects (DAOs) for the database entities.
 */
@Database(
    entities = {Apod.class, Access.class},
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters.class)
public abstract class ApodDB extends RoomDatabase {

  private static final String DB_NAME = "apod_db";

  /**
   * Returns the single instance of {@link ApodDB} for the current application context.
   *
   * @return single {@link ApodDB} instance reference.
   */
  public synchronized static ApodDB getInstance() {
    return InstanceHolder.INSTANCE;
  }

  /**
   * Returns an instance of a Room-generated implementation of {@link ApodDao}.
   *
   * @return data access object for CRUD operations involving {@link Apod} instances.
   */
  public abstract ApodDao getApodDao();

  public abstract AccessDao getAccessDao();

  private static class InstanceHolder {

    private static final ApodDB INSTANCE = Room.databaseBuilder(
        ApodApplication.getInstance().getApplicationContext(), ApodDB.class, DB_NAME)
        .build();

  }

  /**
   * Supports conversion operations for persistence of relevant types not natively supported by
   * Room/SQLite.
   */
  public static class Converters {

    /**
     * Converts a {@link Long} value containing the number of milliseconds since the start of the
     * Unix epoch (1970-01-01 00:00:00.000 UTC) to an instance of {@link Calendar}, and returns the
     * latter.
     *
     * @param milliseconds date-time as a number of milliseconds since the start of the Unix epoch.
     * @return date-time as a {@link Calendar} instance.
     */
    @Nullable
    @TypeConverter
    public static Calendar calendarFromLong(@Nullable Long milliseconds) {
      if (milliseconds != null) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliseconds);
        return calendar;
      }
      return null;
    }

    /**
     * Converts a {@link Calendar} date-time value number of milliseconds since the start of the
     * Unix epoch (1970-01-01 00:00:00.000 UTC), and returns the latter.
     *
     * @param calendar date-time as a {@link Calendar} instance.
     * @return date-time as a number of milliseconds since the start of the Unix epoch.
     */
    @Nullable
    @TypeConverter
    public static Long longFromCalendar(@Nullable Calendar calendar) {
      return (calendar != null) ? calendar.getTimeInMillis() : null;
    }

    /**
     * Converts an {@link Integer} value containing the days since the start of the Unix epoch
     * (1970-01-01) to an instance of {@link Date}, and returns the latter. Both of these are
     * interpreted as local dates, with no reference to time zone.
     *
     * @param days local date as a number of days since the start of the Unix epoch.
     * @return local date as a {@link Date} instance.
     */
    @Nullable
    @TypeConverter
    public static Date dateFromInt(@Nullable Integer days) {
      return (days != null) ? Date.fromEpochDays(days) : null;
    }

    /**
     * Converts a {@link Date} local date value to a number of days since the start of the Unix
     * epoch (1970-01-01), and returns the latter. Both of these are interpreted as local dates,
     * with no reference to time zone.
     *
     * @param date local date as a {@link Date} instance.
     * @return local date as a number of days since the start of the Unix epoch.
     */
    @Nullable
    @TypeConverter
    public static Integer intFromDate(@Nullable Date date) {
      return (date != null) ? date.toEpochDays() : null;
    }

  }

}
