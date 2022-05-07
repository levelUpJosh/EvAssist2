package com.lborof028685.evassist2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.HashMap;

public class ChargingStationActivity extends AppCompatActivity {
    FirebaseDatabase database = FirebaseDatabase.getInstance("https://ev-assistant-3d3e4-default-rtdb.europe-west1.firebasedatabase.app/");
    DatabaseReference ref = database.getReference("charging/stations");

    ValueEventListener availableListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            //only one thing needs to be actively checked and that's numAvailable
            int numAvailable = snapshot.getValue(Integer.class);
            final TextView availableText = (TextView) findViewById(R.id.availableText);
            availableText.setText(Integer.toString(numAvailable));


            String notiTitle;
            String notiText;
            notiTitle = "Charging Update";
            if (numAvailable > 0){
                String plural = "s";
                if (numAvailable == 1) {
                    plural="";
                }
                notiText = numAvailable+" charger"+plural+" just became available!";
            } else {
                notiText = "There are now 0 chargers";
            }
            NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "Availability")
                    .setSmallIcon(R.drawable.notification_icon)
                    .setContentTitle(notiTitle)
                    .setContentText(notiText)
                    .setPriority(NotificationCompat.PRIORITY_MAX);
            NotificationManagerCompat manCompat = NotificationManagerCompat.from(ChargingStationActivity.this);
            manCompat.notify(1,builder.build());


            Context context = getApplicationContext();
            CharSequence text = String.valueOf(numAvailable);

            Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };


    @Override
    protected void onStop() {
        super.onStop();
        ref.child("numAvailable").removeEventListener(availableListener);

    }
    @Override
    protected void onRestart() {
        super.onRestart();
        ref.child("numAvailable").addValueEventListener(availableListener);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charging_station);
        ActionBar actionBar = getSupportActionBar();

        // get the station info from the intent:
        Intent intent = getIntent();
        int stationID = intent.getIntExtra("stationID",0);

        // if 0 then end the activity (default, no data):
        if (stationID == 0) {
            finish();
        }

        // else continue

        ref = ref.child(String.valueOf(stationID));
        ref.child("numAvailable").addValueEventListener(availableListener);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                    //String keys = dataSnaps.getKey();

                    HashMap<String, Double> data = (HashMap<String, Double>) snapshot.child("coords").getValue();
                    double latitude = data.get("latitude");
                    double longitude = data.get("longitude");
                    String network = snapshot.child("network").getValue(String.class);
                    final TextView networkText = (TextView) findViewById(R.id.networkText);
                    networkText.setText(network);

                    String name = snapshot.child("name").getValue(String.class);
                    final TextView nameText = (TextView) findViewById(R.id.nameText);
                    nameText.setText(name);

                    Integer total = snapshot.child("numTotal").getValue(Integer.class);
                    final TextView totalText = (TextView) findViewById(R.id.totalText);
                    totalText.setText("out of "+total+" available");

                    String fullName = network+" "+name;


                    actionBar.setTitle(fullName);
                    ColorDrawable actionBarColor = new ColorDrawable(Color.parseColor("#c876f5"));
                    switch (network){
                        case "Instavolt":
                            actionBarColor = new ColorDrawable(Color.parseColor("#f51707"));

                    }
                    actionBar.setBackgroundDrawable(actionBarColor);



                }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}