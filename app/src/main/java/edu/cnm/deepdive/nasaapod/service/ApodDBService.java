package edu.cnm.deepdive.nasaapod.service;

import edu.cnm.deepdive.nasaapod.model.ApodDB;
import edu.cnm.deepdive.nasaapod.model.entity.Apod;
import edu.cnm.deepdive.util.Date;
import java.util.List;

/**
 * Provides a service layer for accessing the {@link ApodDB} Room/SQLite database. Each operation is
 * implemented as a static nested class that extends {@link BaseFluentAsyncTask}.
 */
public final class ApodDBService {

  private ApodDBService() {
  }

  /**
   * Implements an asynchronous <code>INSERT</code> of an {@link Apod} instance into the local
   * database.
   */
  public static class InsertApodTask
      extends BaseFluentAsyncTask<Apod, Void, List<Long>, List<Long>> {

    @Override
    protected List<Long> perform(Apod... apods) {
      return ApodDB.getInstance().getApodDao().insert(apods);
    }

  }

  /**
   * Implements an asynchronous <code>SELECT</code> of a single {@link Apod} instance from the local
   * database.
   */
  public static class SelectApodTask extends BaseFluentAsyncTask<Date, Void, Apod, Apod> {

    @Override
    protected Apod perform(Date... dates) {
      Apod apod = ApodDB.getInstance().getApodDao().findFirstByDate(dates[0]);
      if (apod == null) {
        throw new TaskException();
      }
      return apod;
    }

  }

  /**
   * Implements an asynchronous <code>SELECT</code> of all {@link Apod} instances (sorted in
   * descending date order) from the local database.
   */
  public static class SelectAllApodTask
      extends BaseFluentAsyncTask<Void, Void, List<Apod>, List<Apod>> {

    @Override
    protected List<Apod> perform(Void... voids) {
      return ApodDB.getInstance().getApodDao().findAll();
    }

  }

}
