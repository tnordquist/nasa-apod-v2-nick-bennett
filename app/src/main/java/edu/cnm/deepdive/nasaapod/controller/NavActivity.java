package edu.cnm.deepdive.nasaapod.controller;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.BottomNavigationView.OnNavigationItemSelectedListener;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import edu.cnm.deepdive.nasaapod.R;
import edu.cnm.deepdive.nasaapod.controller.DateTimePickerFragment.Mode;
import edu.cnm.deepdive.nasaapod.service.FragmentService;
import edu.cnm.deepdive.util.Date;
import java.util.Calendar;

/**
 * Primary controller class of the NASA APOD client app. This activity configures and then responds
 * to clicks in a {@link BottomNavigationView} to hide and show one of 2 main {@link
 * android.support.v4.app.Fragment} instances. It also responds to clicks on a single options {@link
 * MenuItem} (the fragments add more items to the options menu), to display a date picker for
 * selecting an APOD.
 */
public class NavActivity extends AppCompatActivity
    implements OnNavigationItemSelectedListener {

  private ImageFragment imageFragment;
  private HistoryFragment historyFragment;
  private ProgressBar loading;
  private BottomNavigationView navigation;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_nav);
    loading = findViewById(R.id.loading);
    setupFragments(savedInstanceState);
  }

  @Override
  public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
    FragmentService fragmentService = FragmentService.getInstance();
    boolean handled = true;
    switch (menuItem.getItemId()) {
      case R.id.navigation_image:
        fragmentService.showFragment(this, R.id.fragment_container, imageFragment);
        break;
      case R.id.navigation_history:
        fragmentService.showFragment(this, R.id.fragment_container, historyFragment);
        break;
      default:
        handled = false;
    }
    return handled;
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.main_options, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == R.id.calendar) {
      pickApodDate();
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  private void setupFragments(Bundle savedInstanceState) {
    navigation = findViewById(R.id.navigation);
    navigation.setOnNavigationItemSelectedListener(this);
    FragmentService fragmentService = FragmentService.getInstance();
    if (savedInstanceState == null) {
      imageFragment = new ImageFragment();
      fragmentService.loadFragment(this, R.id.fragment_container, imageFragment,
          imageFragment.getClass().getSimpleName(), true);
      historyFragment = new HistoryFragment();
      fragmentService.loadFragment(this, R.id.fragment_container, historyFragment,
          historyFragment.getClass().getSimpleName(), false);
    } else {
      imageFragment = (ImageFragment) fragmentService.findFragment(
          this, R.id.fragment_container, ImageFragment.class.getSimpleName());
      historyFragment = (HistoryFragment) fragmentService.findFragment(
          this, R.id.fragment_container, HistoryFragment.class.getSimpleName());
    }
    imageFragment.setHistoryFragment(historyFragment);
    historyFragment.setImageFragment(imageFragment);
  }

  /**
   * Returns a reference to the {@link BottomNavigationView} of this activity, allowing hosted
   * fragments to get/set the selected item.
   *
   * @return main navigation view of this activity.
   */
  public BottomNavigationView getNavigation() {
    return navigation;
  }

  /**
   * Returns a reference to the {@link ProgressBar} of this activity, allowing hosted fragments to
   * hide/show it.
   *
   * @return indeterminate progress spinner.
   */
  public ProgressBar getLoading() {
    return loading;
  }

  private void pickApodDate() {
    Calendar calendar = Calendar.getInstance();
    if (imageFragment.isVisible() && imageFragment.getApod() != null) {
      calendar = imageFragment.getApod().getDate().toCalendar();
    }
    new DateTimePickerFragment()
        .setMode(Mode.DATE)
        .setCalendar(calendar)
        .setOnChangeListener((cal) -> imageFragment.loadApod(Date.fromCalendar(cal)))
        .show(getSupportFragmentManager(), DateTimePickerFragment.class.getSimpleName());
  }

}
