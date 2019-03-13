package edu.cnm.deepdive.nasaapod.service;

import android.support.annotation.Nullable;
import edu.cnm.deepdive.android.BaseFluentAsyncTask;
import edu.cnm.deepdive.nasaapod.model.ApodDB;
import edu.cnm.deepdive.nasaapod.model.entity.Access;
import edu.cnm.deepdive.nasaapod.model.entity.Apod;
import edu.cnm.deepdive.nasaapod.model.pojo.ApodWithAccesses;
import edu.cnm.deepdive.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Provides a service layer for accessing the {@link ApodDB} Room/SQLite database. Each operation is
 * implemented as a static nested class that extends {@link BaseFluentAsyncTask}.
 */
public final class ApodDBService {

  private ApodDBService() {
  }

  /**
   * Implements an asynchronous <code>INSERT</code> of one or more {@link Apod} instances, with
   * related {@link Access} instances, into the local database.
   */
  public static class InsertApodTask
      extends BaseFluentAsyncTask<Apod, Void, List<Long>, List<Long>> {

    private boolean foreground;

    /**
     * Initializes <code>INSERT</code> task with <code>foreground</code> indicating intention to
     * present image immediately in user interface
     *
     * @param foreground <code>true</code> if image will be displayed immediately;
     * <code>false</code> otherwise.
     */
    public InsertApodTask(boolean foreground) {
      this.foreground = foreground;
    }

    @Override
    protected List<Long> perform(Apod... apods) {
      List<Long> apodIds = ApodDB.getInstance().getApodDao().insert(apods);
      if (foreground) {
        List<Access> accesses = new LinkedList<>();
        for (long id : apodIds) {
          Access access = new Access();
          access.setApodId(id);
          accesses.add(access);
        }
        ApodDB.getInstance().getAccessDao().insert(accesses);
      }
      return apodIds;
    }

  }

  /**
   * Implements an asynchronous <code>SELECT</code> of a single {@link Apod} instance, and an
   * <code>INSERT</code> of a related {@link Access} instance, in the local database.
   */
  public static class SelectApodTask extends BaseFluentAsyncTask<Date, Void, Apod, Apod> {

    @Override
    protected Apod perform(Date... dates) {
      Apod apod = ApodDB.getInstance().getApodDao().findFirstByDate(dates[0]);
      if (apod == null) {
        throw new TaskException();
      }
      Access access = new Access();
      access.setApodId(apod.getId());
      ApodDB.getInstance().getAccessDao().insert(access);
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

  public static class SelectAllApodWithAccessesTask
      extends BaseFluentAsyncTask<Void, Void, List<ApodWithAccesses>, List<ApodWithAccesses>> {

    @Override
    protected List<ApodWithAccesses> perform(Void... voids) {
      return ApodDB.getInstance().getApodDao().findAllWithAccesses();
    }

  }

  /**
   * Implements an asynchronous <code>DELETE</code> of one or more {@link Access} instances from the
   * local database.
   */
  public static class DeleteApodTask extends BaseFluentAsyncTask<Apod, Void, Void, Void> {

    @Nullable
    @Override
    protected Void perform(Apod... apods) throws TaskException {
      ApodDB.getInstance().getApodDao().delete(apods);
      return null;
    }

  }

  /**
   * Implements an asynchronous <code>INSERT</code> of one or more {@link Access} instances into the
   * local database.
   */
  public static class InsertAccessTask
      extends BaseFluentAsyncTask<Access, Void, List<Long>, List<Long>> {

    @Nullable
    @Override
    protected List<Long> perform(Access... accesses) throws TaskException {
      return ApodDB.getInstance().getAccessDao().insert(accesses);
    }

  }

}
