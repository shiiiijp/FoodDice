package com.example.fooddice;

import android.location.Location;

import java.util.Locale;

public class Restaurant {
    private String name;
    private Location location;
    private String formattedAddress;
    private double distance;
    private String photoUrl;

    public Restaurant(String name, Location location, String formattedAddress, double distance, String photoUrl) {
        this.name = name;
        this.location = location;
        this.formattedAddress = formattedAddress;
        this.distance = distance;
        this.photoUrl = photoUrl;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        String address = String.format(Locale.getDefault(), "緯度: %f, 経度: %f", latitude, longitude);
        return address;
    }

    public String getFormattedAddress() {
        return formattedAddress;
    }

    public Location getLocation() {
        return location;
    }

    public double getDistance() {
        return distance;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }
}
