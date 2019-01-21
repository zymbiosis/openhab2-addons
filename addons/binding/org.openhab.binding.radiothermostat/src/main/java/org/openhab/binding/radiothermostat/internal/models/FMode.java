package org.openhab.binding.radiothermostat.internal.models;

import java.util.HashMap;
import java.util.Map;

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

    private static final Map<Integer, FMode> intToTypeMap = new HashMap<Integer, FMode>();
    static {
        for (FMode type : FMode.values()) {
            intToTypeMap.put(type.value, type);
        }
    }

    public static FMode fromInt(int i) {
        FMode type = intToTypeMap.get(Integer.valueOf(i));
        return type;
    }
}