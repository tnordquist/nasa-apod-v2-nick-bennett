package edu.cnm.deepdive.nasaapod.controller;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import edu.cnm.deepdive.nasaapod.R;
import edu.cnm.deepdive.nasaapod.model.entity.Apod;
import edu.cnm.deepdive.nasaapod.service.ApodDBService.InsertApodTask;
import edu.cnm.deepdive.nasaapod.service.ApodDBService.SelectApodTask;
import edu.cnm.deepdive.nasaapod.service.ApodWebService.GetFromNasaTask;
import edu.cnm.deepdive.util.Date;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;

/**
 * Populates a {@link WebView} with the image or video URL of the APOD for the currently rselected
 * date. If the {@link Apod} instance for the selected date is not in the local database, a request
 * is made to retrieve it from the NASA APOD web service.
 */
public class ImageFragment extends Fragment {

  private static final String APOD_KEY = "apod";
  private static final int BUFFER_SIZE = 4096;
  private static final String SAVE_ERROR_LOG_MESSAGE = "Unable to save image";
  private static final String NO_PRIVATE_STORAGE_ERROR = "Unable to access private file storage.";

  private WebView webView;
  private Apod apod;
  private HistoryFragment historyFragment;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setHasOptionsMenu(true);
    setRetainInstance(true);
  }

  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_image, container, false);
    setupWebView(view);
    if (savedInstanceState != null) {
      apod = (Apod) savedInstanceState.getSerializable(APOD_KEY);
    }
    if (apod != null) {
      setApod(apod);
    } else {
      loadApod(Date.fromCalendar(Calendar.getInstance()));
    }
    return view;
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);
    inflater.inflate(R.menu.image_options, menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == R.id.info) {
      showInfo();
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  public void onSaveInstanceState(@NonNull Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putSerializable(APOD_KEY, apod);
  }

  /**
   * Returns the currently displayed {@link Apod} instance.
   *
   * @return current {@link Apod} instance.
   */
  public Apod getApod() {
    return apod;
  }

  /**
   * Sets the {@link Apod} instance to be displayed.
   *
   * @param apod current {@link Apod} instance.
   */
  public void setApod(Apod apod) {
    this.apod = apod;
    getNavActivity().getLoading().setVisibility(View.VISIBLE);
    String url = apod.getUrl();
    if (apod.isMediaImage() && fileExists(filenameFromUrl(url))) {
      url = urlFromFilename(filenameFromUrl(url));
    }
    webView.loadUrl(url);
  }

  /**
   * Sets the {@link HistoryFragment} to be refreshed on successful retrieval of an {@link Apod}
   * instance from the NASA APOD web service.
   *
   * @param historyFragment host {@link HistoryFragment} for list of {@link Apod} instances in local
   * database.
   */
  public void setHistoryFragment(HistoryFragment historyFragment) {
    this.historyFragment = historyFragment;
  }

  /**
   * Loads {@link Apod} instance for specified {@link Date} from local database, or&mdash;if the
   * {@link Apod} for the specified date is not stored locally&mdash;requests it from the NASA APOD
   * web service.
   *
   * @param date desired {@link Apod} date.
   */
  public void loadApod(Date date) {
    getNavActivity().getLoading().setVisibility(View.VISIBLE);
    new SelectApodTask()
        .setTransformer((apod) -> {
          saveIfNeeded(apod);
          return apod;
        })
        .setSuccessListener(this::setApod)
        .setFailureListener((nullApod) -> {
          new GetFromNasaTask()
              .setTransformer((apod) -> {
                new InsertApodTask().execute(apod);
                saveIfNeeded(apod);
                return apod;
              })
              .setSuccessListener((apod) -> {
                historyFragment.refresh();
                setApod(apod);
              })
              .setFailureListener((anotherNullApod) -> showFailure())
              .execute(date);
        })
        .execute(date);
  }

  @SuppressLint("SetJavaScriptEnabled")
  private void setupWebView(View view) {
    webView = view.findViewById(R.id.web_view);
    webView.setWebViewClient(new WebViewClient() {
      @Override
      public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        return false;
      }

      @Override
      public void onPageFinished(WebView view, String url) {
        getNavActivity().getLoading().setVisibility(View.GONE);
        if (isVisible()) {
          showInfo();
        }
      }
    });
    WebSettings settings = webView.getSettings();
    settings.setJavaScriptEnabled(true);
    settings.setSupportZoom(true);
    settings.setBuiltInZoomControls(true);
    settings.setDisplayZoomControls(false);
    settings.setUseWideViewPort(true);
    settings.setLoadWithOverviewMode(true);
  }

  private NavActivity getNavActivity() {
    return (NavActivity) getActivity();
  }

  private String filenameFromUrl(String url) {
    String[] parts = url.split("\\?")[0].split("/");
    return parts[parts.length - 1];
  }

  @Nullable
  private String urlFromFilename(String filename) {
    try {
      return "file://" + new File(getContext().getFilesDir(), filename).toString();
    } catch (NullPointerException e) {
      Log.e(getClass().getSimpleName(), NO_PRIVATE_STORAGE_ERROR, e);
      return null;
    }
  }

  private void saveIfNeeded(Apod apod) {
    try {
      if (apod.isMediaImage() && !fileExists(filenameFromUrl(apod.getUrl()))) {
        saveImage(apod);
      }
    } catch (IOException | NullPointerException e) {
      Log.e(getClass().getSimpleName(), SAVE_ERROR_LOG_MESSAGE, e);
    }
  }

  private void saveImage(Apod apod) throws IOException, NullPointerException {
    URL url = new URL(apod.getUrl());
    String filename = filenameFromUrl(apod.getUrl());
    URLConnection connection = url.openConnection();
    try (
        OutputStream output = getContext().openFileOutput(filename, Context.MODE_PRIVATE);
        InputStream input = connection.getInputStream()
    ) {
      byte[] buffer = new byte[BUFFER_SIZE];
      int bytesRead;
      while ((bytesRead = input.read(buffer)) > -1) {
        output.write(buffer, 0, bytesRead);
      }
    }
  }

  private boolean fileExists(String filename) {
    try {
      return new File(getContext().getFilesDir(), filename).exists();
    } catch (NullPointerException e) {
      Log.e(getClass().getSimpleName(), NO_PRIVATE_STORAGE_ERROR, e);
      return false;
    }
  }

  private void showInfo() {
    if (apod != null && isVisible()) {
      Toast.makeText(getContext(), apod.getTitle(), Toast.LENGTH_LONG).show();
    }
  }

  private void showFailure() {
    getNavActivity().getLoading().setVisibility(View.GONE);
    Toast.makeText(getContext(), R.string.error_message, Toast.LENGTH_LONG).show();
  }

}
