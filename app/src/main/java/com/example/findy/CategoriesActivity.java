package com.example.findy;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;
import java.util.List;

public class CategoriesActivity extends AppCompatActivity {

    private final List<String> categories = Arrays.asList("Jedzenie", "Sklepy", "Kawiarnie");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

        ListView listView = findViewById(R.id.categoriesListView);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                categories
        );
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedCategory = categories.get(position);
            Intent intent = new Intent(this, BrandsActivity.class);
            intent.putExtra("Kategoria", selectedCategory);
            startActivityForResult(intent, 101); //
        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && resultCode == RESULT_OK && data != null) {
            String selectedBrand = data.getStringExtra("selectedBrand");

            // ZWRÓĆ to do MainActivity!
            Intent resultIntent = new Intent();
            resultIntent.putExtra("selectedBrand", selectedBrand);
            setResult(RESULT_OK, resultIntent);
            finish(); // zakończ CategoriesActivity i wróć do MainActivity
        }
    }

}
