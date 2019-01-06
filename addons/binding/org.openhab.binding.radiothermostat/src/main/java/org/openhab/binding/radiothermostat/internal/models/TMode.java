package org.openhab.binding.radiothermostat.internal.models;

import com.google.gson.annotations.SerializedName;

public enum TMode {
    @SerializedName("0")
    OFF(0),
    @SerializedName("1")
    HEAT(1),
    @SerializedName("2")
    COOL(2),
    @SerializedName("3")
    AUTO(3);

    private final int value;

    private TMode(int value) {
        this.value = value;
    }

    private TMode(String value) {
        this.value = Integer.parseInt(value);
    }

    public int getValue() {
        return value;
    }
}
