package com.example.findy;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private ListView shopsListView;
    private Button addShopButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        shopsListView = findViewById(R.id.shopsListView);
        addShopButton = findViewById(R.id.addShopButton);

        // Tymczasowe dane testowe
        String[] testShops = {"Sklep A", "Sklep B", "Sklep C"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                testShops
        );
        shopsListView.setAdapter(adapter);

        addShopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddShopActivity.class);
                startActivity(intent);
            }
        });
    }
}