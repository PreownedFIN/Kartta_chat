package com.sqllite.sakari.sqlitetest;

import android.app.Activity;
import android.app.Application;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by sakari.saastamoinen on 9.12.2015.
 */
public class GlobalVariables extends Application{

    //TODO Add a method that checks from server if there is new information
    //TODO available

    //TODO [OPTIONAL] Have the (to-be) HttpHelper class update information
    //TODO every x seconds and queue all requests in one packet

    public int addNewUser(String userName){

        DatabaseHandler db = new DatabaseHandler(this);

        Log.d("oma", "Adding user: " + userName);
        int newUserId = db.addUser(new User(userName));

        return newUserId;
    }

    public User getUser(int id){

        User targetUser;
        DatabaseHandler db = new DatabaseHandler(this);

        targetUser = db.getUser(id);

        return targetUser;
    }

    public List<User> getAllUsers(){

        DatabaseHandler db = new DatabaseHandler(this);

        return db.getAllUsers();

    }

    public int getUserCount(){

        DatabaseHandler db = new DatabaseHandler(this);

        return db.getUserCount();
    }

    public int updateUser(User user){

        DatabaseHandler db = new DatabaseHandler(this);

        return db.updateUser(user);

    }

    public void deleteUser(User user){

        DatabaseHandler db = new DatabaseHandler(this);

        db.deleteUser(user);
    }

    public void logUser(int id){
        User targetUser;
        DatabaseHandler db = new DatabaseHandler(this);

        targetUser = db.getUser(id);

        Log.d("oma", "Käyttäjänimi: " + targetUser._userName);

        //TODO Add here a log of user's newest location from locations database
        //Log.d("oma", "Lat: " + targetUser._lat);
        //Log.d("oma", "Lng: " + targetUser._lng);
    }

    public long newLocation(Location location, User user){

        DatabaseHandler db = new DatabaseHandler(this);

        long newLocId = db.newLocation(location, user);

        return newLocId;
    }

    public Location locationById(long locationId){

        DatabaseHandler db = new DatabaseHandler(this);

        Location returnLoc = db.getLocationByLId(locationId);

        return returnLoc;
    }

    public List<Location> locationByUser(User user){

        DatabaseHandler db = new DatabaseHandler(this);

        List<Location> userLocationList = db.getLocationByUser(user);

        return userLocationList;
    }

    public Location locationLastByUser(User user){

        DatabaseHandler db = new DatabaseHandler(this);

        Location lastUserLocation = db.getUserLastLoc(user);

        return lastUserLocation;
    }

}
