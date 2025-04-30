package com.example.findy;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AddShopActivity extends AppCompatActivity {
    private EditText nameEditText, addressEditText;
    private Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_shop);

        nameEditText = findViewById(R.id.nameEditText);
        addressEditText = findViewById(R.id.addressEditText);
        saveButton = findViewById(R.id.saveButton);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameEditText.getText().toString();
                String address = addressEditText.getText().toString();

                if(!name.isEmpty() && !address.isEmpty()) {
                    // Na razie tylko pokażemy dane
                    Toast.makeText(AddShopActivity.this,
                            "Zapisano: " + name + ", " + address,
                            Toast.LENGTH_SHORT).show();
                    finish(); // Zamknięcie aktywności i powrót do głównej
                } else {
                    Toast.makeText(AddShopActivity.this,
                            "Wypełnij wszystkie pola",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}