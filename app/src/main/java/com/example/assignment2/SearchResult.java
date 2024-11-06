package com.example.assignment2;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Objects;

public class SearchResult extends AppCompatActivity {

    String searchAddress;
    DBHandler dbHandler;

    int id = 0;
    String address = "", latitude = "", longitude = "";

    EditText addressText;
    EditText latitudeText;
    EditText longitudeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search_result);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        addressText = (EditText) findViewById(R.id.addressText);
        latitudeText = (EditText) findViewById(R.id.latitudeText);
        longitudeText = (EditText) findViewById(R.id.longitudeText);

        Intent intent = getIntent();
        searchAddress = Objects.requireNonNull(intent.getExtras()).getString("ADDRESS");

        dbHandler = new DBHandler(this);
        Cursor cursor = dbHandler.getAddress(searchAddress);

        if (cursor != null && cursor.moveToFirst()) {
            id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            address = cursor.getString(cursor.getColumnIndexOrThrow("address"));
            latitude = cursor.getString(cursor.getColumnIndexOrThrow("latitude"));
            longitude = cursor.getString(cursor.getColumnIndexOrThrow("longitude"));
        } else {
            Toast.makeText(this, "No search results.", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
        }

        addressText.setText(address);
        latitudeText.setText(latitude);
        longitudeText.setText(longitude);
    }

    public void editAddress(View view) {
        address = String.valueOf(addressText.getText());
        latitude = String.valueOf(latitudeText.getText());
        longitude = String.valueOf(longitudeText.getText());
        dbHandler.updateAddress(id, address, latitude, longitude);
        finish();
    }

    public void deleteAddress(View view) {
        dbHandler.deleteAddress(id);
        finish();
    }

    public void returnToMain(View view) {
        finish();
    }
}