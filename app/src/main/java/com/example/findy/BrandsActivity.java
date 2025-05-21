package com.example.findy;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BrandsActivity extends AppCompatActivity {

    private Map<String, List<String>> categoryBrands = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_brands);

        initializeBrandsData();

        String category = getIntent().getStringExtra("Kategoria");
        TextView categoryTitle = findViewById(R.id.categoryTitle);
        ListView brandsListView = findViewById(R.id.brandsListView);

        categoryTitle.setText("Wybierz firmę (" + category + ")");

        List<String> brands = categoryBrands.get(category);
        if (brands == null) {
            brands = Arrays.asList("Brak danych");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                brands
        );
        brandsListView.setAdapter(adapter);

        brandsListView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedBrand = (String) parent.getItemAtPosition(position);
            Intent resultIntent = new Intent();
            resultIntent.putExtra("selectedBrand", selectedBrand);
            setResult(RESULT_OK, resultIntent);
            finish();
        });
    }

    private void initializeBrandsData() {
        categoryBrands.put("Jedzenie", Arrays.asList("McDonald's", "KFC", "Burger King", "Subway", "Pizza Hut"));
        categoryBrands.put("Sklepy", Arrays.asList("7-Eleven", "Target", "Dollar Tree", "Walmart", "Costco"));
        categoryBrands.put("Kawiarnie", Arrays.asList("Starbucks", "Costa Coffee", "Dunkin' Donuts"));
    }
}
