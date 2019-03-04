package edu.cnm.deepdive.nasaapod.model.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import edu.cnm.deepdive.nasaapod.model.entity.Access;
import java.util.List;

@Dao
public interface AccessDao {

  @Insert
  List<Long> insert(Access... accesses);

  @Insert
  List<Long> insert(List<Access> accesses);

}
