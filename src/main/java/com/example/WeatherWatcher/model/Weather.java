package com.example.WeatherWatcher.model;

public class Weather {
    long id;
    String name;
    String description;
    String time;
    String value;
    public long getId() {
        return id;
    }
    public Weather(){}
    public Weather(String name,String description,String time,String value){
        this.name = name;
        this.description = description;
        this.time = time;
        this.value = value;
    }
    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }


}
