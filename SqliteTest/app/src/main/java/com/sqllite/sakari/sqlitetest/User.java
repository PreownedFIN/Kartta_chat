package com.sqllite.sakari.sqlitetest;

/**
 * Created by Sakari on 09.12.2015.
 */
public class User {

    int _id;
    String _userName;
    String _lat;
    String _lng;

    public User(){}

    public User(int id, String userName, String lat, String lng){
        this._id = id;
        this._userName = userName;
        this._lat = lat;
        this._lng = lng;
    }

    public User(String userName, String lat, String lng){
        this._userName = userName;
        this._lat = lat;
        this._lng = lng;
    }

    public int getId() {
        return this._id;
    }

    public void setId(int id) {
        this._id = id;
    }

    public String getUserName() {
        return this._userName;
    }

    public void setUserName(String userName) {
        this._userName = userName;
    }

    public String getLat() {
        return this._lat;
    }

    public void setLat(String lat) {
        this._lat = lat;
    }

    public String getLng() {
        return this._lng;
    }

    public void setLng(String lng) {
        this._lng = lng;
    }

}
