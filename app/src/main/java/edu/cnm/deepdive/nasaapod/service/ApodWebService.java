package edu.cnm.deepdive.nasaapod.service;

import android.annotation.SuppressLint;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import edu.cnm.deepdive.android.BaseFluentAsyncTask;
import edu.cnm.deepdive.nasaapod.ApodApplication;
import edu.cnm.deepdive.nasaapod.BuildConfig;
import edu.cnm.deepdive.nasaapod.R;
import edu.cnm.deepdive.nasaapod.model.entity.Apod;
import edu.cnm.deepdive.util.Date;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Declares the {@link #get(String, String)} Retrofit service method () for communicating with the
 * NASA APOD web service, and defines nested classes in support of making these requests and deserializing the JSON data returned.
 */
public interface ApodWebService {

  /**
   * Constructs and returns a {@link okhttp3.Call} encapsulating a request to the NASA APOD web
   * service. Note that the implementation of this method is completed by Retrofit.
   *
   * @param apiKey NASA Open API key.
   * @param date APOD date.
   * @return {@link Call} object.
   */
  @GET("planetary/apod")
  Call<Apod> get(@Query("api_key") String apiKey, @Query("date") String date);

  /**
   * Implements the initialization-on-demand holder idiom for a singleton of {@link
   * ApodWebService}.
   */
  class InstanceHolder {

    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final ApodWebService INSTANCE;
    private static final String API_KEY;

    static {
      ApodApplication application = ApodApplication.getInstance();
      Gson gson = new GsonBuilder()
          .excludeFieldsWithoutExposeAnnotation()
          .setDateFormat(DATE_FORMAT)
          .registerTypeAdapter(Date.class, new DateJsonConverter(DATE_FORMAT))
          .create();
      Retrofit retrofit = new Retrofit.Builder()
          .baseUrl(application.getApplicationContext().getString(R.string.base_url))
          .addConverterFactory(GsonConverterFactory.create(gson))
          .build();
      INSTANCE = retrofit.create(ApodWebService.class);
      API_KEY = BuildConfig.API_KEY;
    }

  }

  /**
   * Encapsulates the request lifecycle for the NASA APOD web service as a {@link
   * BaseFluentAsyncTask} subclass.
   */
  class GetFromNasaTask extends BaseFluentAsyncTask<Date, Void, Apod, Apod> {

    @Override
    protected Apod perform(Date... dates) throws TaskException {
      Apod apod = null;
      try {
        @SuppressLint("SimpleDateFormat") DateFormat format =
            new SimpleDateFormat(InstanceHolder.DATE_FORMAT);
        Response<Apod> response = InstanceHolder.INSTANCE.get(
            InstanceHolder.API_KEY, format.format(dates[0].toDateTime())).execute();
        if (!response.isSuccessful()) {
          throw new TaskException();
        }
        return response.body();
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }

  }

  /**
   * Provides serialization/deserialization for using the {@link Date} (date-only) class with {@link
   * Gson}.
   */
  class DateJsonConverter implements JsonSerializer<Date>, JsonDeserializer<Date> {

    private DateFormat format;

    /**
     * Initializes the serializer/deserializer using the specified format string. The supported
     * format string syntax is described in the documentation for {@link SimpleDateFormat}.
     *
     * @param format date-only format string containing tokens for year, month, and day.
     */
    public DateJsonConverter(String format) {
      this.format = new SimpleDateFormat(format);
    }

    @Override
    public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
        throws JsonParseException {
      try {
        java.util.Date dateTime = format.parse(json.getAsJsonPrimitive().getAsString());
        return Date.fromDateTime(dateTime);
      } catch (ParseException e) {
        throw new JsonParseException(e);
      }
    }

    @Override
    public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
      return new JsonPrimitive(format.format(src.toDateTime()));
    }

  }

}
