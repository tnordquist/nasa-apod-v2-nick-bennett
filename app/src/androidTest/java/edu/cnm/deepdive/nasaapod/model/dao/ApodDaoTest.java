package edu.cnm.deepdive.nasaapod.model.dao;

import static org.junit.Assert.*;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import edu.cnm.deepdive.nasaapod.model.ApodDB;
import edu.cnm.deepdive.nasaapod.model.entity.Apod;
import edu.cnm.deepdive.util.Date;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ApodDaoTest {

  private ApodDB apodDB;
  private ApodDao apodDao;

  @Before
  public void setUp() throws Exception {
    Context context = InstrumentationRegistry.getTargetContext();
    apodDB = Room.inMemoryDatabaseBuilder(context, ApodDB.class).build();
    apodDao = apodDB.getApodDao();
  }

  @Test
  public void insert() {
    Apod apod = new Apod();
    Date date = Date.today();
    apod.setDate(date);
    String title = "Test APOD instance";
    apod.setTitle(title);
    List<Long> ids = apodDao.insert(apod);
    assertEquals(1, ids.size());
  }

  @Test
  public void insertConflict() {
    Apod apod = new Apod();
    Date date = Date.today();
    apod.setDate(date);
    String title = "Test APOD instance";
    apod.setTitle(title);
    List<Long> ids = apodDao.insert(apod);
    List<Long> testIds = apodDao.insert(apod);
    testIds.removeIf((item) -> item <= 0);
    assertTrue(testIds.isEmpty());
  }

  @Test
  public void select() {
    Apod apod = new Apod();
    Date date = Date.today();
    apod.setDate(date);
    String title = "Test APOD instance";
    apod.setTitle(title);
    List<Long> ids = apodDao.insert(apod);
    Apod testApod = apodDao.findFirstByDate(date);
    assertEquals(date.toEpochDays(), testApod.getDate().toEpochDays());
    assertEquals(title, testApod.getTitle());
  }

  @Test
  public void delete() {
    Apod apod = new Apod();
    Date date = Date.today();
    apod.setDate(date);
    String title = "Test APOD instance";
    apod.setTitle(title);
    List<Long> ids = apodDao.insert(apod);
    Apod testApod = apodDao.findFirstByDate(date);
    int rowsDeleted = apodDao.delete(testApod);
    assertEquals(1, rowsDeleted);
    assertTrue(apodDao.findAll().isEmpty());
  }

  @Test
  public void selectAll() {
    Apod[] apods = new Apod[10];
    Random rng = new Random();
    for (int i = 0; i < apods.length; i++) {
      Apod apod = new Apod();
      Date date = Date.fromEpochDays(rng.nextInt(20_000));
      apod.setDate(date);
      apod.setTitle("Test APOD instance " + i);
      apods[i] = apod;
    }
    apodDao.insert(apods);
    Apod[] testApods = apodDao.findAll().toArray(new Apod[0]);
    Arrays.sort(apods, (apod1, apod2) ->
        -Integer.compare(apod1.getDate().toEpochDays(), apod2.getDate().toEpochDays()));
    assertArrayEquals(apods, testApods);
  }

  @After
  public void tearDown() throws Exception {
    apodDB.close();
  }

}