package edu.cnm.deepdive.nasaapod.view;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import edu.cnm.deepdive.nasaapod.R;
import edu.cnm.deepdive.nasaapod.controller.HistoryFragment;
import edu.cnm.deepdive.nasaapod.model.entity.Apod;
import java.text.DateFormat;
import java.util.List;

/**
 * Supplies {@link View} instances&mdash;each presenting an {@link Apod} instance, to a {@link
 * RecyclerView}.
 */
public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.Holder> {

  private HistoryFragment historyFragment;
  private List<Apod> items;
  private DateFormat format;

  /**
   * Initializes {@link HistoryAdapter} instance with the specified {@link HistoryFragment} host and
   * {@link List}&lt;{@link Apod}&gt; data source.
   *
   * @param historyFragment host fragment.
   * @param items source of {@link Apod} instances.
   */
  public HistoryAdapter(HistoryFragment historyFragment, List<Apod> items) {
    this.historyFragment = historyFragment;
    this.items = items;
    format = android.text.format.DateFormat.getDateFormat(historyFragment.getContext());
  }

  @NonNull
  @Override
  public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
    View view = LayoutInflater.from(historyFragment.getContext())
        .inflate(R.layout.history_item, viewGroup, false);
    return new Holder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull Holder holder, int i) {
    holder.bind(items.get(i));
  }

  @Override
  public int getItemCount() {
    return items.size();
  }

  /**
   * Maintains a connection between a {@link View} and an {@link Apod} instance. The {@link
   * HistoryAdapter} manages the creation and re-use of {@link Holder} instances as rows are
   * scrolled into and out of view.
   * <p>Each view item is clickable (the {@link View.OnClickListener} attached to each is the host
   * {@link HistoryFragment}), supporting display of an APOD selected from the list view.</p>
   */
  public class Holder extends RecyclerView.ViewHolder {

    private Apod apod;
    private View view;
    private TextView dateView;
    private TextView titleView;

    private Holder(@NonNull View itemView) {
      super(itemView);
      view = itemView;
      view.setOnClickListener(historyFragment);
      dateView = itemView.findViewById(R.id.date_view);
      titleView = itemView.findViewById(R.id.title_view);
    }

    private void bind(Apod apod) {
      this.apod = apod;
      view.setTag(apod);
      dateView.setText(format.format(apod.getDate().toDateTime()));
      titleView.setText(apod.getTitle());
    }

  }

}
