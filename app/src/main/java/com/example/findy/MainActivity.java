package com.example.findy;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private final int LOCATION_PERMISSION_REQUEST = 100;
    private final int REQUEST_CATEGORY_AND_BRAND = 101;

    private RequestQueue requestQueue;
    private String selectedBrand = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestQueue = Volley.newRequestQueue(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        findViewById(R.id.btnChooseBrand).setOnClickListener(v -> {
            Intent intent = new Intent(this, CategoriesActivity.class);
            startActivityForResult(intent, REQUEST_CATEGORY_AND_BRAND);
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            if (!selectedBrand.isEmpty()) {
                findPlaces();
            }
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST);
        }
    }

    private void findPlaces() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        double lat = location.getLatitude();
                        double lng = location.getLongitude();

                        LatLng userLatLng = new LatLng(lat, lng);
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 12));

                        String apiKey = "AIzaSyCG6Qvu1gWlnbe-u-Fwm5iA6Xq52N6oe10";
                        String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json" +
                                "?location=" + lat + "," + lng +
                                "&radius=10000" +
                                "&name=" + selectedBrand +
                                "&key=" + apiKey;

                        Log.d("FINDY_DEBUG", "findPlaces() - Wyszukiwana marka: " + selectedBrand);
                        Log.d("FINDY_DEBUG", "findPlaces() - URL: " + url);

                        JsonObjectRequest request = new JsonObjectRequest(
                                Request.Method.GET,
                                url,
                                null,
                                response -> {
                                    try {
                                        mMap.clear();
                                        JSONArray results = response.getJSONArray("results");

                                        for (int i = 0; i < results.length(); i++) {
                                            JSONObject place = results.getJSONObject(i);
                                            JSONObject locationObj = place.getJSONObject("geometry")
                                                    .getJSONObject("location");
                                            String name = place.getString("name");
                                            String address = place.optString("vicinity", "Brak adresu");

                                            LatLng latLng = new LatLng(
                                                    locationObj.getDouble("lat"),
                                                    locationObj.getDouble("lng")
                                            );

                                            mMap.addMarker(new MarkerOptions()
                                                    .position(latLng)
                                                    .title(name)
                                                    .snippet(address));
                                        }

                                        Toast.makeText(MainActivity.this,
                                                "Znaleziono " + results.length() + " miejsc: " + selectedBrand,
                                                Toast.LENGTH_SHORT).show();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        Toast.makeText(MainActivity.this,
                                                "Błąd przetwarzania danych",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                },
                                error -> {
                                    error.printStackTrace();
                                    Toast.makeText(MainActivity.this,
                                            "Błąd połączenia z serwerem",
                                            Toast.LENGTH_SHORT).show();
                                });

                        requestQueue.add(request);
                    } else {
                        Toast.makeText(this, "Nie można pobrać lokalizacji", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    mMap.setMyLocationEnabled(true);
                    if (!selectedBrand.isEmpty()) {
                        findPlaces();
                    }
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CATEGORY_AND_BRAND && resultCode == RESULT_OK && data != null) {
            if (data.hasExtra("selectedBrand")) {
                selectedBrand = data.getStringExtra("selectedBrand");
                Log.d("FINDY_DEBUG", "Wybrana marka z BrandsActivity: " + selectedBrand);
                if (mMap != null) {
                    findPlaces();
                }
            }
        }
    }
}
