package com.lborof028685.evassist2;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;
import java.util.List;

public class GuideActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    // Write a message to the database
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = auth.getCurrentUser();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://ev-assistant-3d3e4-default-rtdb.europe-west1.firebasedatabase.app/");
        ActionBar actionBar = getSupportActionBar();
        //actionBar.setTitle(user.getDisplayName());
        if (user == null) {
            // if not logged in then create a temporary account
            auth.signInAnonymously()
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                //Log.d(TAG, "signInAnonymously:success");
                                user = auth.getCurrentUser();


                            } else {
                                // If sign in fails, display a message to the user.
                                //Log.w(TAG, "signInAnonymously:failure", task.getException());
                                Toast.makeText(GuideActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();

                            }
                        }
                    });;
            user = auth.getCurrentUser();
        }


        setContentView(R.layout.activity_guide);

        //startActivity(new Intent(getApplicationContext(),FirebaseLoginUIActivity.class));
        bottomNavigationView = findViewById(R.id.bottom_navigation);


        bottomNavigationView.setSelectedItemId(R.id.guideSelector);

        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.guideSelector:
                        return true;
                    case R.id.chargingSelector:
                        startActivity(new Intent(getApplicationContext(), ChargingActivity.class));
                        overridePendingTransition(0, 0);
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

    }
}