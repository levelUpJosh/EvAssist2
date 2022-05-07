package com.lborof028685.evassist2;

import java.util.ArrayList;

public class ChargingDevice {
    private ArrayList<ChargingPort> chargingPorts;
    private int max_concurrent_charges; // some devices may allow two ports at once
    private boolean isAvailable; // For now, we will assume one device supports one output

    public ChargingDevice() {
        this.chargingPorts = new ArrayList<ChargingPort>();
        this.max_concurrent_charges = 1;
    }
    public ChargingDevice(ArrayList<ChargingPort> chargingPortsEntry,boolean isAvailable) {
        this.chargingPorts = chargingPortsEntry;
        this.max_concurrent_charges = 1;
    }
    public ChargingDevice(ArrayList<ChargingPort> chargingPortsEntry,int max_concurrent_charges_entry) {
        this.chargingPorts = chargingPortsEntry;
        this.max_concurrent_charges = max_concurrent_charges_entry;
    }

    public ArrayList<ChargingPort> getChargingPorts() {
        return chargingPorts;
    }

    public void setChargingPorts(ArrayList<ChargingPort> chargingPorts) {
        this.chargingPorts = chargingPorts;
    }

    public int getMax_concurrent_charges() {
        return max_concurrent_charges;
    }

    public void setMax_concurrent_charges(int max_concurrent_charges) {
        this.max_concurrent_charges = max_concurrent_charges;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }
}
