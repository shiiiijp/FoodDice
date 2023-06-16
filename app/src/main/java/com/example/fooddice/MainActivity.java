package com.example.fooddice;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.Manifest;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements LocationListener, View.OnClickListener{
    private LocationManager mLocationManager;
    private TextView mGpsLatitudeTextView;
    private TextView mGpsLongitudeTextView;
    private RecyclerView mRecyclerView;
    private List<Restaurant> mPlaceList;
    private PlaceAdapter mPlaceAdapter;
    private EditText mDistanceEditText;
    private TextView countTextView;

    private static int GPS = 1;
    private int mLocationType;
    private static double THRESHOLD_METERS = 2000;
    private static String API_KEY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mGpsLatitudeTextView = (TextView) findViewById(R.id.text_view_gps_latitude_value);
        mGpsLongitudeTextView = (TextView) findViewById(R.id.text_view_gps_longitude_value);
        countTextView = (TextView) findViewById(R.id.text_count);
        Button gpsButton = (Button) findViewById(R.id.button_gps);
        mRecyclerView = findViewById(R.id.placesRecyclerView);
        gpsButton.setOnClickListener(this);
        Button randomButton = findViewById(R.id.random_button);
        randomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSelectedRestaurantActivity();
            }
        });
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        mPlaceAdapter = new PlaceAdapter(MainActivity.this, new ArrayList<Restaurant>(), new PlaceAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Restaurant restaurant) {
                double latitude = restaurant.getLocation().getLatitude();
                double longitude = restaurant.getLocation().getLongitude();
                String label = restaurant.getName();

                Uri gmmIntentUri = Uri.parse("geo:" + latitude + "," + longitude + "?q=" + label);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");

                try{
                    startActivity(mapIntent);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(MainActivity.this, "Google Map がインストールされていません", Toast.LENGTH_SHORT).show();
                }
            }
        });
        mRecyclerView.setAdapter(mPlaceAdapter);

        mDistanceEditText = findViewById(R.id.distance_edit_text);

        getAPIKey();
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        Log.d("PlaceSample", "plog onLocationChanged");
        if (mLocationType==GPS) {
            mGpsLatitudeTextView.setText(String.valueOf(location.getLatitude()));
            mGpsLongitudeTextView.setText(String.valueOf(location.getLongitude()));
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        double user_distance = getDistance();
        THRESHOLD_METERS = user_distance*1000;

        String apiUrl = "https://maps.googleapis.com/maps/api/place/nearbysearch/json" +
                "?location=" + latitude + "," + longitude +
                "&radius=" + THRESHOLD_METERS +
                "&type=restaurant|cafe" +
                "&key=" + API_KEY;
        JsonObjectRequest request = new JsonObjectRequest(apiUrl, null,
                response -> {
                    try {
                        mPlaceList = new ArrayList<Restaurant>();
                        JSONArray results = response.getJSONArray("results");
                        for (int i = 0; i < results.length(); i++) {
                            JSONObject result = results.getJSONObject(i);
                            Log.d("JSON", result.toString());
                            String name = result.getString("name");
                            double placeLat = result.getJSONObject("geometry")
                                    .getJSONObject("location").getDouble("lat");
                            double placeLng = result.getJSONObject("geometry")
                                    .getJSONObject("location").getDouble("lng");
                            String formattedAddress = null;
                            if (result.has("vicinity")) {
                                formattedAddress = result.getString("vicinity");
                            }
                            boolean openNow = false;
                            if (result.has("opening_hours")) {
                                openNow = result.getJSONObject("opening_hours")
                                        .getBoolean("open_now");
                            }
                            String photoUrl = null;
                            if (result.has("photos")) {
                                JSONArray photos = result.getJSONArray("photos");
                                if (photos.length() > 0) {
                                    JSONObject photo = photos.getJSONObject(0);
                                    String photoReference = photo.getString("photo_reference");

                                    photoUrl = "https://maps.googleapis.com/maps/api/place/photo" +
                                            "?maxwidth=400" +
                                            "&photoreference=" + photoReference +
                                            "&key=" + API_KEY;
                                }
                            }

                            Location placeLocation = new Location("");
                            placeLocation.setLatitude(placeLat);
                            placeLocation.setLongitude(placeLng);
                            float distance = location.distanceTo(placeLocation);
                            if (distance <= THRESHOLD_METERS && openNow) {
                                Restaurant restaurant = new Restaurant(name, placeLocation, formattedAddress, distance, photoUrl);
                                mPlaceList.add(restaurant);
                            }
                        }

                        mPlaceAdapter.setPlaceList(mPlaceList);
                        countTextView.setText("店の数: " + mPlaceAdapter.getItemCount() + " (最大20件)");
                        mPlaceAdapter.notifyDataSetChanged();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    error.printStackTrace();
                });
        Volley.newRequestQueue(this).add(request);
        mLocationManager.removeUpdates(this);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
        switch(i) {
            case LocationProvider.AVAILABLE:
                Log.v("PlaceSample", "AVAILABLE");
                break;
            case LocationProvider.OUT_OF_SERVICE:
                Log.v("PlaceSample", "OUT_OF_SERVICE");
                break;
            case LocationProvider.TEMPORARILY_UNAVAILABLE:
                Log.v("PlaceSample", "TEMPORARILY_UNAVAILABLE");
                break;
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button_gps){
            mLocationType = GPS;
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,this);
        }
    }

    @Override
    protected void onPause() {
        Log.d("PlaceSample", "plog onPause");
        if (mLocationManager != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mLocationManager.removeUpdates(this);
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        Log.d("PlaceSample", "plog onResume()");
        super.onResume();
    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    private double getDistance() {
        String distanceString = mDistanceEditText.getText().toString();
        double distance;
        try {
            distance = Double.parseDouble(distanceString);
        } catch (NumberFormatException e) {
            distance = 2;
        }
        return distance;
    }

    private void getAPIKey() {
        ApplicationInfo ai = getApplicationInfo();
        Bundle bundle = ai.metaData;
        API_KEY = bundle.getString("com.google.android.geo.API_KEY");
    }

    public ApplicationInfo getApplicationInfo() {
        try {
            return getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "Failed to load meta-data, NameNotFound: " + e.getMessage());
            return null;
        }
    }

    private void startSelectedRestaurantActivity() {
        PlaceAdapter adapter = (PlaceAdapter) mRecyclerView.getAdapter();
        List<Restaurant> restaurants = adapter.getRestaurants();

        if (restaurants != null && restaurants.size() > 0) {
            Random random = new Random();
            Restaurant selectedRestaurant = restaurants.get(random.nextInt(restaurants.size()));

            Intent intent = new Intent(MainActivity.this, com.example.fooddice.SelectedRestaurantActivity.class);
            intent.putExtra("selectedRestaurantName", selectedRestaurant.getName());
            intent.putExtra("selectedRestaurantAddress", selectedRestaurant.getAddress());
            intent.putExtra("selectedRestaurantFormattedAddress", selectedRestaurant.getFormattedAddress());
            intent.putExtra("selectedRestaurantDistance", String.format("%.1f km", selectedRestaurant.getDistance() / 1000.0));
            intent.putExtra("selectedRestaurantLatitude", selectedRestaurant.getLocation().getLatitude());
            intent.putExtra("selectedRestaurantLongitude", selectedRestaurant.getLocation().getLongitude());
            intent.putExtra("selectedRestaurantPhotoUrl", selectedRestaurant.getPhotoUrl());

            startActivity(intent);
        } else {
            Toast.makeText(MainActivity.this, "まず飲食店を検索してください。または、周囲に飲食店がありません", Toast.LENGTH_SHORT).show();
        }
    }
}