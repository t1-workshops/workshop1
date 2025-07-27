package com.viacheslav.model;

import java.time.LocalDate;

public class WeatherData {
    private String city;
    private LocalDate date;
    private int temperature;
    private String condition;

    public WeatherData(String city, LocalDate date, int temperature, String condition) {
        this.city = city;
        this.date = date;
        this.temperature = temperature;
        this.condition = condition;
    }

    public String getCity() {
        return city;
    }

    public LocalDate getDate() {
        return date;
    }

    public int getTemperature() {
        return temperature;
    }

    public String getCondition() {
        return condition;
    }

    @Override
    public String toString() {
        return String.format("%s: %s, %d°C (%s)", city, date, temperature, condition);
    }
}
