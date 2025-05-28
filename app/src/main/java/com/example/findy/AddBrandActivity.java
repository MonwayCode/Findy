package com.example.findy;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddBrandActivity extends AppCompatActivity {

    private EditText nameInput;
    private Spinner categorySpinner;
    private CheckBox vegCheckbox, veganCheckbox, glutenCheckbox, meatCheckbox;
    private DatabaseManager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_brand);

        dbManager = new DatabaseManager(this);
        dbManager.open();

        nameInput = findViewById(R.id.nameInput);
        categorySpinner = findViewById(R.id.categorySpinner);
        vegCheckbox = findViewById(R.id.vegCheckbox);
        veganCheckbox = findViewById(R.id.veganCheckbox);
        glutenCheckbox = findViewById(R.id.glutenCheckbox);
        meatCheckbox = findViewById(R.id.meatCheckbox);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.categories_array,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);

        Button saveBtn = findViewById(R.id.saveBtn);
        saveBtn.setOnClickListener(v -> saveBrand());
    }

    private void saveBrand() {
        String name = nameInput.getText().toString().trim();
        String category = categorySpinner.getSelectedItem().toString();

        if (name.isEmpty()) {
            Toast.makeText(this, "Wpisz nazwę firmy", Toast.LENGTH_SHORT).show();
            return;
        }

        Brand brand = new Brand();
        brand.setName(name);
        brand.setCategory(category);
        brand.setVegetarianOption(vegCheckbox.isChecked());
        brand.setVeganOption(veganCheckbox.isChecked());
        brand.setGlutenFreeOption(glutenCheckbox.isChecked());
        brand.setMeatOption(meatCheckbox.isChecked());


        dbManager.addBrand(brand);
        Toast.makeText(this, "Dodano nową firmę: " + name, Toast.LENGTH_SHORT).show();
        finish();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbManager.close();
    }
}