package org.openhab.binding.radiothermostat.internal.models;

public class Tstat {
    private double lastUpdate = 0;
    private double temp;
    private double humidity;
    private TMode tmode;
    private FMode fmode;
    private int override;
    private int hold;
    private double t_heat;
    private double t_cool;
    private TMode tstate;
    private FState fstate;

    public double getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(double lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public double getTemp() {
        return temp;
    }

    public void setTemp(double temp) {
        this.temp = temp;
    }

    public double getHumidity() {
        return humidity;
    }

    public void setHumidity(double humidity) {
        this.humidity = humidity;
    }

    public TMode getTmode() {
        return tmode;
    }

    public void setTmode(TMode tmode) {
        this.tmode = tmode;
    }

    public FMode getFmode() {
        return fmode;
    }

    public void setFmode(FMode fmode) {
        this.fmode = fmode;
    }

    public int getOverride() {
        return override;
    }

    public void setOverride(int override) {
        this.override = override;
    }

    public int getHold() {
        return hold;
    }

    public void setHold(int hold) {
        this.hold = hold;
    }

    public double getT_heat() {
        return t_heat;
    }

    public void setT_heat(double t_heat) {
        this.t_heat = t_heat;
    }

    public double getT_cool() {
        return t_cool;
    }

    public void setT_cool(double t_cool) {
        this.t_cool = t_cool;
    }

    public TMode getTstate() {
        return tstate;
    }

    public void setTstate(TMode tstate) {
        this.tstate = tstate;
    }

    public FState getFstate() {
        return fstate;
    }

    public void setFstate(FState fstate) {
        this.fstate = fstate;
    }
}
