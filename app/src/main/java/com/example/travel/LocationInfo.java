package com.example.travel;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class LocationInfo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_info);
        TextView title = findViewById(R.id.Info_title);
        TextView context = findViewById(R.id.Info_context);

        Bundle bundle = this.getIntent().getExtras();
        System.out.println(bundle.getString("Marker"));
        title.setText(bundle.getString("Marker"));
    }
}