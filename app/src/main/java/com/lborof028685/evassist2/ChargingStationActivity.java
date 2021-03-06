package com.lborof028685.evassist2;

import static android.content.ContentValues.TAG;

import android.app.PendingIntent;
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
import androidx.core.app.TaskStackBuilder;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class ChargingStationActivity extends AppCompatActivity {
    /**
     * Activity:
     * Responsible for generating the individual pages displaying the information for each individual charging location
     */
    FirebaseDatabase database = FirebaseDatabase.getInstance("https://ev-assistant-3d3e4-default-rtdb.europe-west1.firebasedatabase.app/");
    DatabaseReference ref = database.getReference("charging/stations");
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    Integer stationID;


    // Define a variable for the "I'm here" button and total devices for the site.
    // This allows it to be accessed in the ValueEventListener
    Integer total;
    ToggleButton hereBtn;

    ValueEventListener availableListener = new ValueEventListener() {
        /**
         * Listen for changes in the site's availability and send a notification if applicable
         * @param snapshot
         */
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            //only one thing needs to be actively checked and that's numAvailable
            int numAvailable = snapshot.getValue(Integer.class);

            // get the relevant textView
            final TextView availableText = (TextView) findViewById(R.id.availableText);

            // set it's text to the new number of available devices
            availableText.setText(Integer.toString(numAvailable));

            // define variables for the notification title and text
            String notiTitle;
            String notiText;
            notiTitle = "Charging Update";

            // if there are more than 0 available (ie 1 or more)
            if (numAvailable > 0){
                // add a plural
                String plural = "s";
                if (numAvailable == 1) {
                    // if it's exactly one then remove it
                    plural="";
                }
                // generate the notification text
                notiText = numAvailable+" charger"+plural+" just became available!";
            } else {

                // 0 chargers are available so show a different message
                notiText = "There are now 0 chargers";

                // set the here button to true (in development context: force the app to allow users to unblock it from 0 available)
                hereBtn.setChecked(true);
            }

            // create a notification builder to build the notification as desired
            NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "Availability")
                    .setSmallIcon(R.drawable.notification_icon)
                    .setContentTitle(notiTitle)
                    .setContentText(notiText)
                    .setPriority(NotificationCompat.PRIORITY_MAX);

            // create an Intent for this activity with the stationID intact
            Intent resultIntent = new Intent(getApplicationContext(), ChargingStationActivity.class);
            resultIntent.putExtra("stationID",stationID);

            // create a TaskStackBuilder
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());

            // add the intent
            stackBuilder.addNextIntentWithParentStack(resultIntent);

            // get the PendingIntent
            PendingIntent resultPendingIntent =
                    stackBuilder.getPendingIntent(0,
                            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);


            // set the desired intent on the notification builder
            builder.setContentIntent(resultPendingIntent);

            // set the origin as this class
            NotificationManagerCompat manCompat = NotificationManagerCompat.from(ChargingStationActivity.this);

            // send the notification
            manCompat.notify(1,builder.build());


            // DEV: additionally send a Toast with the ID
            Context context = getApplicationContext();
            CharSequence text = String.valueOf(numAvailable);

            Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Stop listening once we leave the activity but keep listening onPause
        ref.child("numAvailable").removeEventListener(availableListener);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charging_station);
        ActionBar actionBar = getSupportActionBar();
        hereBtn = findViewById(R.id.hereBtn);

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
                        // if the toggle is pressed:
                            ref.child("numAvailable").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    Integer number = snapshot.getValue(Integer.class);
                                    // check if the toggle is checked
                                    if (isChecked) {
                                        if (number > 0) {
                                            // if so and the number is more than 0, take away 1
                                            ref.child("numAvailable").setValue(snapshot.getValue(Integer.class)-1);
                                        }
                                    } else {
                                        if (number < total)
                                        {
                                            // if not and the number is less than the total add 1
                                            ref.child("numAvailable").setValue(snapshot.getValue(Integer.class)+1);
                                        }
                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, databaseError.getMessage()); //Don't ignore errors!
            }
        };
        if (user != null){
            // if the user is logged in, add the user check
            ref.child("currentUsers").child(user.getUid()).addListenerForSingleValueEvent(eventListener);
        }

        // add the availability listener defined earlier
        ref.child("numAvailable").addValueEventListener(availableListener);

        // get all the initial data remaining to fill the page
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

                    total = snapshot.child("numTotal").getValue(Integer.class);
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