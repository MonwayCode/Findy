package com.example.findy;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
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

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String FILENAME = "recent_brands.txt";
    private static final int MAX_RECENT_BRANDS = 5;

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private final int LOCATION_PERMISSION_REQUEST = 100;
    private final int REQUEST_CATEGORY_AND_BRAND = 101;

    private RequestQueue requestQueue;
    private String selectedBrand = "";

    private AutoCompleteTextView searchInput;
    private CheckBox onlyOpenNowCheckbox;
    private Spinner radiusSpinner;
    private Button searchButton;

    private List<String> recentBrands = new LinkedList<>();
    private ArrayAdapter<String> suggestionsAdapter;

    private void saveRecentBrand(String brandName) {
        if (TextUtils.isEmpty(brandName)) return;

        recentBrands.remove(brandName);
        recentBrands.add(0, brandName);

        if (recentBrands.size() > MAX_RECENT_BRANDS) {
            recentBrands = recentBrands.subList(0, MAX_RECENT_BRANDS);
        }

        try (FileOutputStream fos = openFileOutput(FILENAME, MODE_PRIVATE)) {
            for (String brand : recentBrands) {
                fos.write((brand + "\n").getBytes());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        suggestionsAdapter.clear();
        suggestionsAdapter.addAll(recentBrands);
        suggestionsAdapter.notifyDataSetChanged();
    }

    private List<String> readRecentBrands() {
        List<String> brands = new ArrayList<>();
        try (FileInputStream fis = openFileInput(FILENAME);
             InputStreamReader isr = new InputStreamReader(fis);
             BufferedReader br = new BufferedReader(isr)) {

            String line;
            while ((line = br.readLine()) != null) {
                brands.add(line);
            }
        } catch (Exception e) {
        }
        return brands;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestQueue = Volley.newRequestQueue(this);

        searchInput = findViewById(R.id.searchInput);
        onlyOpenNowCheckbox = findViewById(R.id.onlyOpenNowCheckbox);
        radiusSpinner = findViewById(R.id.radiusSpinner);
        searchButton = findViewById(R.id.searchButton);
        Button btnChooseBrand = findViewById(R.id.btnChooseBrand);

        recentBrands = readRecentBrands();
        suggestionsAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, recentBrands);
        searchInput.setAdapter(suggestionsAdapter);

        searchInput.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus && TextUtils.isEmpty(searchInput.getText())) {
                searchInput.showDropDown();
            }
        });

        searchInput.setOnClickListener(v -> {
            if (TextUtils.isEmpty(searchInput.getText())) {
                searchInput.showDropDown();
            }
        });

        ArrayAdapter<CharSequence> radiusAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.radius_options,
                android.R.layout.simple_spinner_item
        );
        radiusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        radiusSpinner.setAdapter(radiusAdapter);

        onlyOpenNowCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (mMap != null) {
                performSearch();
            }
        });

        searchButton.setOnClickListener(v -> {
            if (mMap != null) {
                selectedBrand = searchInput.getText().toString().trim();
                saveRecentBrand(selectedBrand);
                performSearch();
            }
        });

        btnChooseBrand.setOnClickListener(v -> {
            Intent intent = new Intent(this, CategoriesActivity.class);
            startActivityForResult(intent, REQUEST_CATEGORY_AND_BRAND);
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
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
            performSearch();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST);
        }
    }

    private void performSearch() {
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

                        String brandToSearch = selectedBrand;
                        if (brandToSearch.isEmpty()) {
                            Toast.makeText(this, "Wpisz nazwę miejsca lub wybierz markę", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        String selectedRadius = radiusSpinner.getSelectedItem().toString();
                        int radiusInMeters = Integer.parseInt(selectedRadius.replace(" km", "")) * 1000;

                        boolean onlyOpenNow = onlyOpenNowCheckbox.isChecked();

                        String apiKey = "AIzaSyCG6Qvu1gWlnbe-u-Fwm5iA6Xq52N6oe10";
                        String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json" +
                                "?location=" + lat + "," + lng +
                                "&radius=" + radiusInMeters +
                                "&name=" + brandToSearch +
                                (onlyOpenNow ? "&opennow" : "") +
                                "&key=" + apiKey;

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
                                                "Znaleziono " + results.length() + " miejsc: " + brandToSearch,
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
                    performSearch();
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
                searchInput.setText(selectedBrand);
                saveRecentBrand(selectedBrand);
                if (mMap != null) {
                    performSearch();
                }
            }
        }
    }
}
