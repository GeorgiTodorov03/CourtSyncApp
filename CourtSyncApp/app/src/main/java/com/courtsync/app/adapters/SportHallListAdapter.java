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

public class SportHallListAdapter extends RecyclerView.Adapter<SportHallListAdapter.ViewHolder> {
    private List<SportHall> halls = new ArrayList<>();
    private SportHallCardAdapter.OnHallClickListener listener;

    public void setListener(SportHallCardAdapter.OnHallClickListener listener) {
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
                .inflate(R.layout.item_hall_list, parent, false);
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
        TextView tvHallName, tvSport, tvType, tvRating, tvPrice, tvLocation;

        ViewHolder(View itemView) {
            super(itemView);
            ivHall = itemView.findViewById(R.id.ivHall);
            tvHallName = itemView.findViewById(R.id.tvHallName);
            tvSport = itemView.findViewById(R.id.tvSport);
            tvType = itemView.findViewById(R.id.tvType);
            tvRating = itemView.findViewById(R.id.tvRating);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvLocation = itemView.findViewById(R.id.tvLocation);
        }

        void bind(SportHall hall) {
            Context ctx = itemView.getContext();
            tvHallName.setText(hall.getName());
            tvRating.setText(String.format("%.1f", hall.getRating()));

            if (hall.getSportName() != null) {
                tvSport.setText(hall.getSportName());
            }
            if (hall.getHallType() != null) {
                tvType.setText(hall.getHallType());
            }
            if (hall.getPricePerHour() != null) {
                tvPrice.setText(String.format("$%.0f", hall.getPricePerHour().doubleValue()));
            }

            StringBuilder loc = new StringBuilder();
            if (hall.getDistrict() != null) loc.append(hall.getDistrict());
            if (hall.getDistanceKm() != null) {
                if (loc.length() > 0) loc.append(" • ");
                loc.append(String.format("%.1f km", hall.getDistanceKm()));
            }
            tvLocation.setText(loc.toString());

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
