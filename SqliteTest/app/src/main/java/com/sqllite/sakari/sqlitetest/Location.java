package com.sqllite.sakari.sqlitetest;

/**
 * Created by Sakari on 09.01.2016.
 */
public class Location {

    int id;
    int userId;
    float lat;
    float lng;

    public Location(){}

    public Location(float lat, float lng){
        this.lat = lat;
        this.lng = lng;
    }

    public Location(int id, int userId, float lat, float lng){
        this.id = id;
        this.userId = userId;
        this.lat = lat;
        this.lng = lng;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public float getLat() {
        return lat;
    }

    public void setLat(float lat) {
        this.lat = lat;
    }

    public float getLng() {
        return lng;
    }

    public void setLng(float lng) {
        this.lng = lng;
    }

}
