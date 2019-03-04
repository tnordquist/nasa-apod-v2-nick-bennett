package edu.cnm.deepdive.nasaapod.model.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import edu.cnm.deepdive.nasaapod.model.entity.Access;
import java.util.List;

@Dao
public interface AccessDao {

  /**
   * Inserts one or more {@link Access} instances into the local database. Any primary key or
   * foreign key constraint violations will result in the existing records being retained.
   *
   * @param accesses 0 or more {@link Access} instance(s) (or an array of them) to be inserted.
   * @return list of inserted record ID(s).
   */
  @Insert
  List<Long> insert(Access... accesses);

  /**
   * Inserts one or more {@link Access} instances into the local database. Any primary key or
   * foreign key constraint violations will result in the existing records being retained.
   *
   * @param accesses list of {@link Access} instance(s) to be inserted.
   * @return list of inserted record ID(s).
   */
  @Insert
  List<Long> insert(List<Access> accesses);

}
