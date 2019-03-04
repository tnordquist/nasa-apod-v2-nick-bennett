package edu.cnm.deepdive.nasaapod.model.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import edu.cnm.deepdive.nasaapod.model.entity.Apod;
import edu.cnm.deepdive.util.Date;
import java.util.List;

/**
 * Declares basic CRUD operations for {@link Apod} instances in the local database, using Room
 * annotations.
 */
@Dao
public interface ApodDao {

  /**
   * Inserts one or more {@link Apod} instances into the local database. Any primary or unique key
   * constraint violations will result in the existing records being retained.
   *
   * @param apods {@link Apod} instance(s) to be inserted.
   * @return inserted record ID(s).
   */
  @Insert(onConflict = OnConflictStrategy.IGNORE)
  List<Long> insert(Apod... apods);

  /**
   * Selects and returns the single {@link Apod} instance (or null) for the specified {@link Date}.
   *
   * @param date desired {@link Apod} {@link Date}.
   * @return {@link Apod} instance if found in database; <code>null</code> otherwise.
   */
  @Query("SELECT * FROM Apod WHERE date = :date")
  Apod findFirstByDate(Date date);

  /**
   * Selects and returns all {@link Apod} instances in the local database, sorting the result in
   * descending date order.
   *
   * @return all {@link Apod} instances in local database.
   */
  @Query("SELECT * FROM Apod ORDER BY date DESC")
  List<Apod> findAll();

  /**
   * Deletes one or more {@link Apod} instances from local database.
   *
   * @param apods instances of {@link Apod} to be deleted from database.
   * @return number of records deleted.
   */
  @Delete
  int delete(Apod... apods);

}
