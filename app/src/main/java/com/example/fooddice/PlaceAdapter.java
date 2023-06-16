package com.example.fooddice;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class PlaceAdapter extends RecyclerView.Adapter<PlaceViewHolder> {

    private Context context;
    private List<Restaurant> restaurantList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Restaurant restaurant);
    }

    public PlaceAdapter(Context context, List<Restaurant> restaurantList, OnItemClickListener listener) {
        this.context = context;
        this.restaurantList = restaurantList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PlaceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.places_item_view, parent, false);
        return new PlaceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaceViewHolder holder, int position) {
        Restaurant restaurant = restaurantList.get(position);
        holder.nameTextView.setText(restaurant.getName());
        if (restaurant.getFormattedAddress() == null) {
            holder.addressTextView.setText(restaurant.getAddress());
        } else {
            holder.addressTextView.setText(restaurant.getFormattedAddress());
        }
        holder.distanceTextView.setText(String.format("%.1f km", restaurant.getDistance() / 1000.0));

        String photoUrl = restaurant.getPhotoUrl();
        if (photoUrl != null && !photoUrl.isEmpty()) {
            Glide.with(holder.itemView.getContext()).load(photoUrl).into(holder.photoImageView);
        } else {
            holder.photoImageView.setImageResource(android.R.drawable.stat_notify_error);
        }

        holder.itemView.setOnClickListener(view -> {
            listener.onItemClick(restaurant);
        });
    }

    public void setPlaceList(List<Restaurant> restaurantList) {
        this.restaurantList = restaurantList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return restaurantList.size();
    }

    public List<Restaurant> getRestaurants() {
        return restaurantList;
    }
}

