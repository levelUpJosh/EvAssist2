package com.example.evassist2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.here.sdk.core.GeoCoordinates;
import com.here.sdk.mapview.MapError;
import com.here.sdk.mapview.MapScene;
import com.here.sdk.mapview.MapScheme;
import com.here.sdk.mapview.MapView;
import com.here.sdk.mapview.VisibilityState;

public class ChargingActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    private static final String TAG = GuideActivity.class.getSimpleName();
    private PermissionsRequestor permissionsRequestor;
    private MapView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charging);

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
                        startActivity(new Intent(getApplicationContext(), ChargingActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.newsSelector:

                        return true;
                }
                return false;
            }
        });



        // Get a MapView instance from the layout.
        mapView = findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);

        mapView.setOnReadyListener(new MapView.OnReadyListener() {
            @Override
            public void onMapViewReady() {
                // Call each time the activity is resumed
                Log.d(TAG,"HERE Rendering Engine attached");
            }
        });

        handleAndroidPermissions();
    }
    private void handleAndroidPermissions() {
        permissionsRequestor = new PermissionsRequestor(this);
        permissionsRequestor.request(new PermissionsRequestor.ResultListener(){

            @Override
            public void permissionsGranted() {
                loadMapScene();
            }

            @Override
            public void permissionsDenied() {
                Log.e(TAG, "Permissions denied by user.");
            }
        });

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionsRequestor.onRequestPermissionsResult(requestCode, grantResults);
    }

    private void loadMapScene() {
        // Get scene from HERE SDK, based on HERE SDK documentation
        mapView.getMapScene().loadScene(MapScheme.NORMAL_DAY, new MapScene.LoadSceneCallback() {
            @Override
            public void onLoadScene(@Nullable MapError mapError) {
                if (mapError ==null) {
                    double distanceInMeters = 1000*10;
                    mapView.getMapScene().setLayerVisibility(MapScene.Layers.TRAFFIC_FLOW, VisibilityState.VISIBLE);
                    mapView.getCamera().lookAt(new GeoCoordinates(52.530932,13.384915,distanceInMeters));
                } else {
                    Log.d(TAG,"Loading map failed: mapError: "+mapError.name());
                }
            }
        });
    }
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

}