package com.example.assignment2;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class MainActivity extends AppCompatActivity {

    private static final String COMMA_DELIMITER = ",";
    DBHandler dbHandler;
    TextView addressList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Set TextView to be able to scroll
        addressList = findViewById(R.id.addressList);
        addressList.setMovementMethod(new ScrollingMovementMethod());

        // create DBHandler instance
        dbHandler = new DBHandler(this);

        // If database is empty, propagate database
        if (dbHandler.isDatabaseEmpty()) {
            propagateAddresses();
        }
        // Load addresses from database into TextView
        loadAddresses();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAddresses();
    }

    private void propagateAddresses() {
        InputStream is = getResources().openRawResource(R.raw.addresses);
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(COMMA_DELIMITER);
                dbHandler.addAddress(values[0], values[1], values[2]);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadAddresses() {
        addressList.setText("");
        Cursor cursor =dbHandler.getAll();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String address = cursor.getString(cursor.getColumnIndexOrThrow("address"));
                String latitude = cursor.getString(cursor.getColumnIndexOrThrow("latitude"));
                String longitude = cursor.getString(cursor.getColumnIndexOrThrow("longitude"));
                addressList.append("Address: " + address + "\nLatitude: " + latitude +
                        "\nLongitude: " + longitude + "\n\n");
            } while (cursor.moveToNext());
        }
    }

    public void performSearch(View view) {
        Intent i = new Intent(this, SearchResult.class);

        EditText searchText = (EditText) findViewById(R.id.searchText);
        String searchAddress = String.valueOf(searchText.getText());

        i.putExtra("ADDRESS", searchAddress);
        startActivity(i);
    }

    public void addNewAddress(View view) {
        EditText addressText = (EditText) findViewById(R.id.addressText);
        EditText latitudeText= (EditText) findViewById(R.id.latitudeText);
        EditText longitudeText= (EditText) findViewById(R.id.longitudeText);

        String address = String.valueOf(addressText.getText());
        String latitude = String.valueOf(latitudeText.getText());
        String longitude = String.valueOf(longitudeText.getText());

        dbHandler.addAddress(address, latitude, longitude);
        loadAddresses();
    }
}