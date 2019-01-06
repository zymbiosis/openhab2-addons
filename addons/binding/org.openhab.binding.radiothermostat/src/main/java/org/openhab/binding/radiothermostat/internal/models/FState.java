package org.openhab.binding.radiothermostat.internal.models;

import com.google.gson.annotations.SerializedName;

public enum FState {
    @SerializedName("0")
    OFF(0),
    @SerializedName("1")
    ON(1);

    private final int value;

    private FState(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}