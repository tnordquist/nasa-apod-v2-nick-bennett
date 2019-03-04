package edu.cnm.deepdive.nasaapod.service;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

/**
 * Provides app-wide fragment management services, intended primarily for use in apps without
 * extensive back stack requirements, where a {@link android.support.design.widget.NavigationView}
 * or {@link android.support.design.widget.BottomNavigationView} is used to move between the primary
 * UI fragments.
 * <p>The singleton pattern is implemented by this class, exposing its capabilities via a single
 * instance returned by the {@link #getInstance()} method.</p>
 */
public class FragmentService {

  private FragmentService() {
  }

  /**
   * Returns the single instance of {@link FragmentService}.
   *
   * @return instance.
   */
  public static FragmentService getInstance() {
    return InstanceHolder.INSTANCE;
  }

  /**
   * Attaches the specified fragment to the specified activity and container, with a specified tag.
   * This method simply invokes {@link #loadFragment(FragmentActivity, int, Fragment, String,
   * boolean) loadFragment(FragmentActivity, int, Fragment, String, true)}.
   *
   * @param activity host of fragment.
   * @param containerId id of {@link android.view.ViewGroup} to which the fragment will be
   * attached.
   * @param fragment fragment to be loaded.
   * @param tag <code>String</code> identifier of fragment.
   */
  public void loadFragment(
      FragmentActivity activity, int containerId, Fragment fragment, String tag) {
    loadFragment(activity, containerId, fragment, tag, true);
  }

  /**
   * Attaches the specified fragment to the specified activity and container, with a specified tag,
   * in an initially visible or hidden state. Note that while {@link #showFragment(FragmentActivity,
   * int, Fragment)} ensures that only one fragment attached to a single container (at most) is
   * visible at a time, this method performs no such checks.
   *
   * @param activity host of fragment.
   * @param containerId id of {@link android.view.ViewGroup} to which the fragment will be
   * attached.
   * @param fragment fragment to be loaded.
   * @param tag <code>String</code> identifier of fragment.
   * @param visible initial visible state of activity.
   */
  public void loadFragment(
      FragmentActivity activity, int containerId, Fragment fragment, String tag, boolean visible) {
    FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
    transaction.add(containerId, fragment, tag);
    if (!visible) {
      transaction.hide(fragment);
    }
    transaction.commit();
  }

  /**
   * Makes the specified fragment visible, if it is attached to the specified activity and
   * container. Any other fragments attached to the specified activity and container are hidden. In
   * any event, no visibility changes will be made to fragments not attached to both the specified
   * host activity and container.
   *
   * @param activity host activity.
   * @param containerId host container.
   * @param fragment fragment to show.
   */
  public void showFragment(FragmentActivity activity, int containerId, Fragment fragment) {
    FragmentManager manager = activity.getSupportFragmentManager();
    FragmentTransaction transaction = manager.beginTransaction();
    boolean modified = false;
    for (Fragment frag : manager.getFragments()) {
      if (frag.getId() == containerId) {
        if (frag == fragment && !frag.isVisible()) {
          transaction.show(frag);
          modified = true;
        } else if (frag != fragment && frag.isVisible()) {
          transaction.hide(frag);
          modified = true;
        }
      }
    }
    if (modified) {
      transaction.commit();
    }
  }

  /**
   * Returns a reference to a fragment with the specified tag, if it is attached to the specified
   * activity and container. If a fragment meeting those criteria is not found, <code>null</code> is
   * returned.
   *
   * @param activity host activity.
   * @param containerId host container.
   * @param tag <code>String</code> identifier of fragment.
   * @return fragment (null if not found).
   */
  public Fragment findFragment(FragmentActivity activity, int containerId, String tag) {
    FragmentManager manager = activity.getSupportFragmentManager();
    Fragment fragment = manager.findFragmentByTag(tag);
    return (fragment != null && fragment.getId() == containerId) ? fragment : null;
  }

  private static class InstanceHolder {

    private static final FragmentService INSTANCE = new FragmentService();

  }

}
