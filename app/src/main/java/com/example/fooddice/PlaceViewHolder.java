//package com.example.fooddice;
//
//import android.content.Context;
//import android.content.Intent;
//import android.location.Location;
//import android.net.Uri;
//import android.view.View;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.google.android.gms.maps.model.LatLng;
//
//import java.util.List;
//import android.location.Location;
//
//// RecyclerViewのViewHolderにOnClickListenerを設定する
//public class PlaceViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
//
//    private Context context;
//    TextView nameTextView;
//    TextView addressTextView;
//    TextView distanceTextView;
//    private PlaceAdapter.OnItemClickListener listener;
//    private List<Restaurant> restaurantList;
//
//    public PlaceViewHolder(@NonNull View itemView, PlaceAdapter.OnItemClickListener listener) { //, List<Restaurant> restaurantList, Context context) {
//        super(itemView);
////        this.restaurantList = restaurantList;
//        nameTextView = itemView.findViewById(R.id.name_text_view);
//        addressTextView = itemView.findViewById(R.id.address_text_view);
//        distanceTextView = itemView.findViewById(R.id.distance_text_view);
////        this.context = context;
//        itemView.setClickable(true);
//        itemView.setOnClickListener(this);
//    }
//
//    @Override
//    public void onClick(View v) {
//        // クリックされたアイテムの緯度経度を取得する
//        if (listener != null) {
//            int position = getAdapterPosition();
//            if (position != RecyclerView.NO_POSITION) {
//                Restaurant restaurant = restaurantList.get(position);
//                listener.onItemClick(restaurant);
//                Location location = restaurant.getLocation();
//
//                // 地図の表示処理を行う
//                Intent intent = new Intent(context, MapsActivity.class);
//                intent.putExtra("latitude", location.getLatitude());
//                intent.putExtra("longitude", location.getLongitude());
//                context.startActivity(intent);
//            }
//        }
//    }
//}

package com.example.fooddice;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PlaceViewHolder extends RecyclerView.ViewHolder {

    public TextView nameTextView;
    public TextView addressTextView;
    public TextView distanceTextView;
    public ImageView photoImageView;

    public PlaceViewHolder(@NonNull View itemView) {
        super(itemView);
        nameTextView = itemView.findViewById(R.id.name_text_view);
        addressTextView = itemView.findViewById(R.id.address_text_view);
        distanceTextView = itemView.findViewById(R.id.distance_text_view);
        photoImageView = itemView.findViewById(R.id.photo_image_view);
    }
}