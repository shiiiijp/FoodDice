package com.example.fooddice;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;

public class SelectedRestaurantActivity extends AppCompatActivity {
    private TextView nameTextView;
    private TextView addressTextView;
    private TextView distanceTextView;
    private CardView selectedCardView;
    private ImageView selectedImageView;
    private String selectedRestaurantName;
    private String selectedRestaurantAddress;
    private String selectedRestaurantFormattedAddress;
    private String selectedRestaurantDistance;
    private double selectedRestaurantLatitude;
    private double selectedRestaurantLongitude;
    private String selectedRestaurantPhotoUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);

        nameTextView = findViewById(R.id.name_selected_view);
        addressTextView = findViewById(R.id.address_selected_view);
        distanceTextView = findViewById(R.id.distance_selected_view);
        selectedCardView = findViewById(R.id.placeSelectedCardView);
        selectedImageView = findViewById(R.id.photo_selected_view);

        Intent intent = getIntent();
        selectedRestaurantName = intent.getStringExtra("selectedRestaurantName");
        selectedRestaurantAddress = intent.getStringExtra("selectedRestaurantAddress");
        selectedRestaurantFormattedAddress = intent.getStringExtra("selectedRestaurantFormattedAddress");
        selectedRestaurantDistance = intent.getStringExtra("selectedRestaurantDistance");
        selectedRestaurantLatitude = intent.getDoubleExtra("selectedRestaurantLatitude", 0.0);
        selectedRestaurantLongitude = intent.getDoubleExtra("selectedRestaurantLongitude", 0.0);
        selectedRestaurantPhotoUrl = intent.getStringExtra("selectedRestaurantPhotoUrl");

        nameTextView.setText(selectedRestaurantName);
        addressTextView.setText(selectedRestaurantFormattedAddress);
        distanceTextView.setText(selectedRestaurantDistance);
        if (selectedRestaurantPhotoUrl != null && !selectedRestaurantPhotoUrl.isEmpty()) {
            Glide.with(this).load(selectedRestaurantPhotoUrl).into(selectedImageView);
        } else {
            selectedImageView.setImageResource(android.R.drawable.stat_notify_error);
        }

        selectedCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri gmmIntentUri = Uri.parse("geo:" + selectedRestaurantLatitude + "," + selectedRestaurantLongitude + "?q=" + selectedRestaurantName);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");

                try{
                    startActivity(mapIntent);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(SelectedRestaurantActivity.this, "Google Map がインストールされていません", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
