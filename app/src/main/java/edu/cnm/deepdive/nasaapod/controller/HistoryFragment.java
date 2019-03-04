package edu.cnm.deepdive.nasaapod.controller;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import edu.cnm.deepdive.nasaapod.R;
import edu.cnm.deepdive.nasaapod.model.entity.Apod;
import edu.cnm.deepdive.nasaapod.service.ApodDBService.SelectAllApodTask;
import edu.cnm.deepdive.nasaapod.service.FragmentService;
import edu.cnm.deepdive.nasaapod.view.HistoryAdapter;
import java.util.ArrayList;
import java.util.List;

/**
 * Hosts a {@link RecyclerView} of {@link Apod} instances from the local database, allowing user
 * selection for display in the {@link ImageFragment} set by {@link #setImageFragment(ImageFragment)}.
 */
public class HistoryFragment extends Fragment implements View.OnClickListener {

  private List<Apod> history;
  private HistoryAdapter adapter;
  private ImageFragment imageFragment;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setRetainInstance(true);
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_history, container, false);
    RecyclerView historyView = view.findViewById(R.id.history_view);
    history = new ArrayList<>();
    adapter = new HistoryAdapter(this, history);
    historyView.setAdapter(adapter);
    refresh();
    return view;
  }

  @Override
  public void onHiddenChanged(boolean hidden) {
    super.onHiddenChanged(hidden);
    refresh();
  }

  /**
   * Handles a click on a {@link View} in the {@link RecyclerView} by extracting the {@link Apod}
   * reference returned by {@link View#getTag()}, showing the image fragment, invoking {@link
   * ImageFragment#setApod(Apod)}, and finally updating the {@link
   * android.support.design.widget.BottomNavigationView} in {@link NavActivity}.
   *
   * @param view visual presentation of a single {@link Apod} instance.
   */
  @Override
  public void onClick(View view) {
    Apod apod = (Apod) view.getTag();
    NavActivity activity = (NavActivity) getActivity();
    FragmentService.getInstance().showFragment(activity, R.id.fragment_container, imageFragment);
    imageFragment.setApod(apod);
    activity.getNavigation().setSelectedItemId(R.id.navigation_image);
  }

  /**
   * Sets the {@link ImageFragment} used for APOD image display.
   *
   * @param fragment display host {@link ImageFragment}.
   */
  public void setImageFragment(ImageFragment fragment) {
    imageFragment = fragment;
  }

  /**
   * Queries the local database for {@link Apod} instances, populating (indirectly) a {@link
   * RecyclerView} with the results.
   */
  public void refresh() {
    if (!isHidden()) {
      new SelectAllApodTask()
          .setSuccessListener((apods) -> {
            history.clear();
            history.addAll(apods);
            adapter.notifyDataSetChanged();
          })
          .execute();
    }
  }

}
