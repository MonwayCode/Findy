package com.example.findy;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class BrandsActivity extends AppCompatActivity {

    private ViewGroup dietaryOptionsGroup;
    private LinearLayout dietaryOptionsLayout;
    private DatabaseManager databaseManager;
    private String currentCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_brands);

        databaseManager = new DatabaseManager(this);
        databaseManager.open();

        currentCategory = getIntent().getStringExtra("Kategoria");
        TextView categoryTitle = findViewById(R.id.categoryTitle);
        ListView brandsListView = findViewById(R.id.brandsListView);
        dietaryOptionsGroup = findViewById(R.id.dietaryOptionsGrid);
        dietaryOptionsLayout = findViewById(R.id.dietaryOptionsLayout);

        categoryTitle.setText("Wybierz firmę (" + currentCategory + ")");

        if (currentCategory.equals("Jedzenie") || currentCategory.equals("Kawiarnie")) {
            dietaryOptionsLayout.setVisibility(View.VISIBLE);
        } else {
            dietaryOptionsLayout.setVisibility(View.GONE);
        }

        loadBrands(currentCategory);
        setUpDietaryOptionSelection();

        brandsListView.setOnItemClickListener((parent, view, position, id) -> {
            Brand selectedBrand = (Brand) parent.getItemAtPosition(position);
            Intent resultIntent = new Intent();
            resultIntent.putExtra("selectedBrand", selectedBrand.getName());
            setResult(RESULT_OK, resultIntent);
            finish();
        });

        brandsListView.setOnItemLongClickListener((parent, view, position, id) -> {
            Brand selectedBrand = (Brand) parent.getItemAtPosition(position);

            new AlertDialog.Builder(BrandsActivity.this)
                    .setTitle("Usuń firmę")
                    .setMessage("Czy na pewno chcesz usunąć \"" + selectedBrand.getName() + "\"?")
                    .setPositiveButton("Tak", (dialog, which) -> {
                        databaseManager.deleteBrand(selectedBrand.getId());
                        Toast.makeText(this, "Usunięto firmę", Toast.LENGTH_SHORT).show();
                        loadBrands(currentCategory);
                    })
                    .setNegativeButton("Anuluj", null)
                    .show();

            return true;
        });
    }

    private void loadBrands(String category) {
        List<Brand> brands = databaseManager.getBrandsByCategory(category);
        displayBrands(brands);
    }

    private void displayBrands(List<Brand> brands) {
        ArrayAdapter<Brand> adapter = new ArrayAdapter<Brand>(
                this,
                android.R.layout.simple_list_item_1,
                brands) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = (TextView) view;

                Brand brand = getItem(position);
                textView.setText(brand.getName());

                return view;
            }
        };

        ListView brandsListView = findViewById(R.id.brandsListView);
        brandsListView.setAdapter(adapter);
    }

    private void filterBrandsByDietaryOptions() {
        if (dietaryOptionsLayout.getVisibility() != View.VISIBLE) return;

        boolean vegetarian = ((RadioButton) findViewById(R.id.vegetarianOption)).isChecked();
        boolean vegan = ((RadioButton) findViewById(R.id.veganOption)).isChecked();
        boolean glutenFree = ((RadioButton) findViewById(R.id.glutenFreeOption)).isChecked();
        boolean meat = ((RadioButton) findViewById(R.id.meatOption)).isChecked();

        List<Brand> allBrands = databaseManager.getBrandsByCategory(currentCategory);
        List<Brand> filteredBrands = new ArrayList<>();

        for (Brand brand : allBrands) {
            boolean matches = true;

            if (vegetarian && !brand.isVegetarianOption()) matches = false;
            if (vegan && !brand.isVeganOption()) matches = false;
            if (glutenFree && !brand.isGlutenFreeOption()) matches = false;
            if (meat && !brand.isMeatOption()) matches = false;

            if (matches) {
                filteredBrands.add(brand);
            }
        }

        displayBrands(filteredBrands);
    }

    private void setUpDietaryOptionSelection() {
        int[] radioButtonIds = {
                R.id.vegetarianOption,
                R.id.veganOption,
                R.id.glutenFreeOption,
                R.id.meatOption
        };

        for (int id : radioButtonIds) {
            RadioButton rb = findViewById(id);
            rb.setOnClickListener(v -> {
                for (int otherId : radioButtonIds) {
                    if (otherId != id) {
                        RadioButton other = findViewById(otherId);
                        other.setChecked(false);
                    }
                }
                filterBrandsByDietaryOptions();
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        databaseManager.close();
    }
}
