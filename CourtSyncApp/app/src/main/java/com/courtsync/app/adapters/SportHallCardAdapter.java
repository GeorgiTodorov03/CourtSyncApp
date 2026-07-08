package com.courtsync.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.courtsync.app.R;
import com.courtsync.app.models.SportHall;
import java.util.ArrayList;
import java.util.List;

public class SportHallCardAdapter extends RecyclerView.Adapter<SportHallCardAdapter.ViewHolder> {
    private List<SportHall> halls = new ArrayList<>();
    private OnHallClickListener listener;

    public interface OnHallClickListener {
        void onHallClick(SportHall hall);
    }

    public void setListener(OnHallClickListener listener) {
        this.listener = listener;
    }

    public void submitList(List<SportHall> newHalls) {
        DiffUtil.DiffResult diff = DiffUtil.calculateDiff(new DiffUtil.Callback() {
            @Override public int getOldListSize() { return halls.size(); }
            @Override public int getNewListSize() { return newHalls.size(); }
            @Override public boolean areItemsTheSame(int o, int n) { return halls.get(o).getId() == newHalls.get(n).getId(); }
            @Override public boolean areContentsTheSame(int o, int n) { return halls.get(o).getName().equals(newHalls.get(n).getName()); }
        });
        halls = new ArrayList<>(newHalls);
        diff.dispatchUpdatesTo(this);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_hall_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(halls.get(position));
    }

    @Override
    public int getItemCount() { return halls.size(); }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivHall;
        TextView tvHallName, tvSport, tvRating, tvPrice, tvLocation, tvBookedCount;

        ViewHolder(View itemView) {
            super(itemView);
            ivHall = itemView.findViewById(R.id.ivHall);
            tvHallName = itemView.findViewById(R.id.tvHallName);
            tvSport = itemView.findViewById(R.id.tvSport);
            tvRating = itemView.findViewById(R.id.tvRating);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvBookedCount = itemView.findViewById(R.id.tvBookedCount);
        }

        void bind(SportHall hall) {
            Context ctx = itemView.getContext();

            tvHallName.setText(hall.getName());

            if (hall.getSportName() != null) {
                tvSport.setText(hall.getSportName());
                tvSport.setVisibility(View.VISIBLE);
            }

            tvRating.setText(String.format("★ %.1f", hall.getRating()));

            if (hall.getPricePerHour() != null) {
                tvPrice.setText(String.format("$%.0f", hall.getPricePerHour().doubleValue()));
            }

            StringBuilder location = new StringBuilder();
            if (hall.getDistrict() != null) location.append(hall.getDistrict());
            if (hall.getCity() != null) {
                if (location.length() > 0) location.append(", ");
                location.append(hall.getCity());
            }
            tvLocation.setText(location.toString());

            if (hall.getBookingsToday() > 0) {
                tvBookedCount.setText(String.format("+%d booked today", hall.getBookingsToday()));
            } else {
                tvBookedCount.setText("Available today");
            }

            Glide.with(ctx)
                    .load(hall.getImageUrl())
                    .centerCrop()
                    .placeholder(R.color.card_background)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(ivHall);

            itemView.setOnClickListener(v -> {
                if (listener != null) listener.onHallClick(hall);
            });
        }
    }
}
