package com.lborof028685.evassist2;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class ChargingStationActivity extends AppCompatActivity {
    FirebaseDatabase database = FirebaseDatabase.getInstance("https://ev-assistant-3d3e4-default-rtdb.europe-west1.firebasedatabase.app/");
    DatabaseReference ref = database.getReference("charging/stations");
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    Integer stationID;

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
        stationID = intent.getIntExtra("stationID",0);

        // if 0 then end the activity (default, no data):
        if (stationID == 0) {
            finish();
        }

        // else continue

        // get the station
        ref = ref.child(String.valueOf(stationID));

        //lets check if the station has this user's UUID
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ToggleButton hereBtn = findViewById(R.id.hereBtn);
                if(!dataSnapshot.exists()) {
                    // The user isn't marked as being "at the station"
                    hereBtn.setChecked(false);
                    // find out if the user is allowed to be here (ie they aren't anywhere else already)
                    if (user != null){
                        DatabaseReference userRef = database.getReference("charging/stations/"+stationID);

                    }


                } else {
                    // The user is marked as at the station currently
                    hereBtn.setChecked(true);
                }
                hereBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if(isChecked)
                        {
                            ref.child("numAvailable").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    Integer number = snapshot.getValue(Integer.class);
                                    if (number > 0) {
                                        ref.child("numAvailable").setValue(snapshot.getValue(Integer.class)-1);
                                    } else {
                                        hereBtn.setEnabled(false);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                        else
                        {
                            ref.child("numAvailable").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    ref.child("numAvailable").setValue(snapshot.getValue(Integer.class)+1);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, databaseError.getMessage()); //Don't ignore errors!
            }
        };
        if (user != null){
            ref.child("currentUsers").child(user.getUid()).addListenerForSingleValueEvent(eventListener);
        }

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

                    if (user!= null) {
                        actionBar.setTitle(user.getDisplayName());
                    }

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