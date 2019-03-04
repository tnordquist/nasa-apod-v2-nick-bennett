package edu.cnm.deepdive.nasaapod.model.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;
import java.util.Calendar;

@Entity(
    foreignKeys = @ForeignKey(
        entity = Apod.class,
        parentColumns = "apod_id", childColumns = "apod_id",
        onDelete = ForeignKey.CASCADE
    )
)
public class Access {

  @ColumnInfo(name = "access_id")
  @PrimaryKey(autoGenerate = true)
  private long id;

  @ColumnInfo(name = "apod_id", index = true)
  private long apodId;

  private Calendar timestamp = Calendar.getInstance();

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public long getApodId() {
    return apodId;
  }

  public void setApodId(long apodId) {
    this.apodId = apodId;
  }

  public Calendar getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(Calendar timestamp) {
    this.timestamp = timestamp;
  }

}
