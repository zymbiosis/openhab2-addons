package org.openhab.binding.radiothermostat.internal.models;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.annotations.SerializedName;

/**
 * Class for handling the Thermostat Mode
 *
 * @author John M. Gardner - Initial contribution
 */
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

    private static final Map<Integer, TMode> intToTypeMap = new HashMap<Integer, TMode>();
    static {
        for (TMode type : TMode.values()) {
            intToTypeMap.put(type.value, type);
        }
    }

    public static TMode fromInt(int i) {
        TMode type = intToTypeMap.get(Integer.valueOf(i));
        return type;
    }
}
