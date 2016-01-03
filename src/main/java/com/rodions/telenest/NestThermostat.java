package com.rodions.telenest;

/**
 * Created by rodion on 27.12.15.
 */
public class NestThermostat {

    private String id;
    private String name;
    private String targetTemperature;
    private String online;

    public NestThermostat(String id, String name, String targetTemperature, String online) {
        this.id = id;
        this.name = name;
        this.targetTemperature = targetTemperature;
        this.online = online;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Name: " + name + ", target temperature: " + targetTemperature;
    }
}
