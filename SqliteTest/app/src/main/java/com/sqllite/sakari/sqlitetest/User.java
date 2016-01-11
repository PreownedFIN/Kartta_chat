package com.sqllite.sakari.sqlitetest;

/**
 * Created by Sakari on 09.12.2015.
 */
public class User {

    int _id;
    String _userName;
    Location _location;

    public User(){}

    public User(int id, String userName){
        this._id = id;
        this._userName = userName;
    }

    public User(String userName){
        this._userName = userName;
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

}
