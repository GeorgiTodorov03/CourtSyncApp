package com.courtsync.app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.courtsync.app.R;
import com.courtsync.app.models.Reservation;
import java.util.ArrayList;
import java.util.List;

public class ReservationAdapter extends RecyclerView.Adapter<ReservationAdapter.ViewHolder> {
    private List<Reservation> reservations = new ArrayList<>();
    private OnActionListener listener;

    public interface OnActionListener {
        void onCancel(Reservation reservation);
        void onModify(Reservation reservation);
    }

    public void setListener(OnActionListener listener) {
        this.listener = listener;
    }

    public void submitList(List<Reservation> newList) {
        DiffUtil.DiffResult diff = DiffUtil.calculateDiff(new DiffUtil.Callback() {
            @Override public int getOldListSize() { return reservations.size(); }
            @Override public int getNewListSize() { return newList.size(); }
            @Override public boolean areItemsTheSame(int o, int n) { return reservations.get(o).getId() == newList.get(n).getId(); }
            @Override public boolean areContentsTheSame(int o, int n) { return reservations.get(o).getStatus().equals(newList.get(n).getStatus()); }
        });
        reservations = new ArrayList<>(newList);
        diff.dispatchUpdatesTo(this);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_reservation, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(reservations.get(position));
    }

    @Override
    public int getItemCount() { return reservations.size(); }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvRelativeDate, tvHallName, tvStatus, tvDate, tvTime, tvSport;
        View btnModify, btnCancel;

        ViewHolder(View itemView) {
            super(itemView);
            tvRelativeDate = itemView.findViewById(R.id.tvRelativeDate);
            tvHallName = itemView.findViewById(R.id.tvHallName);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvSport = itemView.findViewById(R.id.tvSport);
            btnModify = itemView.findViewById(R.id.btnModify);
            btnCancel = itemView.findViewById(R.id.btnCancel);
        }

        void bind(Reservation r) {
            tvRelativeDate.setText(r.getRelativeDate());
            tvHallName.setText(r.getHallName());
            tvStatus.setText(r.getStatus());
            tvDate.setText(r.getDate());
            tvTime.setText(r.getStartTime() + " - " + r.getEndTime());
            tvSport.setText(r.getSportName());

            boolean isCancelled = "CANCELLED".equals(r.getStatus());
            btnCancel.setEnabled(!isCancelled);
            btnModify.setEnabled(!isCancelled);

            btnCancel.setOnClickListener(v -> {
                if (listener != null) listener.onCancel(r);
            });
            btnModify.setOnClickListener(v -> {
                if (listener != null) listener.onModify(r);
            });
        }
    }
}
