package com.lborof028685.evassist2;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

// Implement OnMapReadyCallback.
public class ChargingActivity extends AppCompatActivity implements GoogleMap.OnMarkerClickListener,OnMapReadyCallback {
    FirebaseDatabase database = FirebaseDatabase.getInstance("https://ev-assistant-3d3e4-default-rtdb.europe-west1.firebasedatabase.app/");

    private FusedLocationProviderClient fusedLocationClient;

    BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // Set the layout file as the content view.
        setContentView(R.layout.activity_charging);

        getSupportActionBar().hide();

        // Get a handle to the fragment and register the callback.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //startActivity(new Intent(getApplicationContext(),FirebaseLoginUIActivity.class));
        bottomNavigationView = findViewById(R.id.bottom_navigation);


        bottomNavigationView.setSelectedItemId(R.id.chargingSelector);

        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.guideSelector:
                        startActivity(new Intent(getApplicationContext(), GuideActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.chargingSelector:

                        return true;
                    case R.id.newsSelector:
                        startActivity(new Intent(getApplicationContext(), NewsActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.settingSelector:
                        startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                }
                return false;
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel =  new NotificationChannel("Availability","Charge Available", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager man =  getSystemService(NotificationManager.class);
            man.createNotificationChannel(channel);
        }

    }

    // Get a handle to the GoogleMap object and display marker.
    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(53.9091995,-2.4746993), (float) 5.5));

        DatabaseReference ref_to_stations = database.getReference("charging/stations");
        ref_to_stations.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnaps : snapshot.getChildren()) {
                    //String keys = dataSnaps.getKey();

                    HashMap<String, Double> data = (HashMap<String, Double>) dataSnaps.child("coords").getValue();
                    double latitude = data.get("latitude");
                    double longitude = data.get("longitude");
                    String fullName = dataSnaps.child("network").getValue(String.class)+" "+dataSnaps.child("name").getValue(String.class);
                    // your further code.

                    Marker marker = googleMap.addMarker(new MarkerOptions()
                            .title(fullName)
                            .position(new LatLng(latitude,longitude)));
                    marker.setTag(Integer.parseInt(dataSnaps.getKey()));


                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        // Set a listener for marker click.
        googleMap.setOnMarkerClickListener(this);
        //DatabaseReference ref = database.getReference("charging/stations/1/numAvailable");






        //
        googleMap.setPadding(0,0,0,200);
    }
    @Override
    public boolean onMarkerClick (final Marker marker) {
        // get the tag
        Integer stationID = (Integer) marker.getTag();

        // create intent
        Intent intent = new Intent(getApplicationContext(),ChargingStationActivity.class);
        intent.putExtra("stationID",stationID);
        startActivity(intent);

        return false;
    }
}