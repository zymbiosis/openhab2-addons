package org.openhab.binding.radiothermostat.internal.models;

import com.google.gson.annotations.SerializedName;

public enum FMode {
    @SerializedName("0")
    AUTO(0),
    @SerializedName("1")
    CIRC(1),
    @SerializedName("2")
    ON(2);

    private final int value;

    private FMode(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}