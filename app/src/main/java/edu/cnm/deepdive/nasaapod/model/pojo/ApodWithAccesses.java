package edu.cnm.deepdive.nasaapod.model.pojo;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Relation;
import edu.cnm.deepdive.nasaapod.model.entity.Access;
import edu.cnm.deepdive.nasaapod.model.entity.Apod;
import java.util.List;

public class ApodWithAccesses {

  @Embedded
  private Apod apod;

  @Relation(parentColumn = "apod_id", entityColumn = "apod_id")
  private List<Access> accesses;

  public Apod getApod() {
    return apod;
  }

  public void setApod(Apod apod) {
    this.apod = apod;
  }

  public List<Access> getAccesses() {
    return accesses;
  }

  public void setAccesses(List<Access> accesses) {
    this.accesses = accesses;
  }

}
