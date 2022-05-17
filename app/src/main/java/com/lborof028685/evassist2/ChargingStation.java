package com.lborof028685.evassist2;


import androidx.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ChargingStation {
    /**
     * Organises any particular charging location into one object.
     * Includes multiple ChargingDevice objects which may in turn have multiple ChargingPort objects
     *
     * Was intended for use in ChargingStationActivity to represent data from firebase,
     * however due to the realtime nature this was abandoned at the time
     */
    private String name;
    private String network;
    private LatLng coords;
    //private ArrayList<ChargingDevice> chargingDevices; // One site may have multiple devices
    private int numTotal; // For the sake of this project we will assume that all EV charging uses CCS only
    private int numAvailable; // Again, assume that only CCS devices exist
    private double power; // Define the power of all outputs.

    public ChargingStation(FirebaseDatabase database,int stationID) {
        // Set everything after creation

        DatabaseReference ref = database.getReference("charging/stations/1");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        ref.child("numAvailable").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //only one thing needs to be actively checked and that's numAvailable
                int numAvailable = snapshot.getValue(Integer.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    public String getName() {
        return name;
    }

    public String getFullName(){
        return network + " " + name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LatLng getCoords() {
        return coords;
    }

    public void setCoords(LatLng coords) {
        this.coords = coords;
    }


    public String getNetwork() {
        return network;
    }

    public void setNetwork(String network) {
        this.network = network;
    }
}
