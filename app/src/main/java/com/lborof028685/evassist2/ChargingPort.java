package com.lborof028685.evassist2;

public class ChargingPort {
    // In EV chargers, one device may have more than one plug but allow only one output.
    // Hence this class exists
    private double power; // in kw
    private String type; // eg CCS2/Chademo

    public ChargingPort(double powerEntry,String typeEntry) {
        this.power = powerEntry;
        this.type = typeEntry;
    }

    public double getPower() {
        return power;
    }

    public void setPower(double power) {
        this.power = power;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
