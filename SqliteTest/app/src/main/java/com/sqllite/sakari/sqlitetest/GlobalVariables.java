package com.sqllite.sakari.sqlitetest;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
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
public class GlobalVariables{

    Context context;
    User currentUser;

    public GlobalVariables(){

    }

    public GlobalVariables(Context contex){
        this.context = contex;
    }

    //TODO Add a method that checks from server if there is new information
    //TODO available

    //TODO [OPTIONAL] Have the (to-be) HttpHelper class update information
    //TODO every x seconds and queue all requests in one packet

    public boolean loginUser(String userName, String pWord){

        boolean loginValid;

        int userId = -1/*plug HttpHelper login here*/;
        //TODO Add HtmlHelper-class that has a loginUser method that checks
        //TODO from the server if a user with a particular password is a valid user
        //TODO return as userId, if not valid, return -1
        if (userId < 0){
            loginValid = false;
        } else {
            loginValid = true;
        }
        return loginValid;
    }

    public int addNewUser(String userName){

        //New DatabaseHandler
        DatabaseHandler db = new DatabaseHandler(context);

        //Place the value DatabaseHandler to-be returned newUserId
        int newUserId = db.addUser(new User(userName));

        return newUserId;
    }

    public User getUser(int id){

        DatabaseHandler db = new DatabaseHandler(context);

        User targetUser = db.getUser(id);

        return targetUser;
    }

    public List<User> getAllUsers(){

        DatabaseHandler db = new DatabaseHandler(context);

        return db.getAllUsers();

    }

    public int getUserCount(){

        DatabaseHandler db = new DatabaseHandler(context);

        return db.getUserCount();
    }

    public int updateUser(User user){

        DatabaseHandler db = new DatabaseHandler(context);

        return db.updateUser(user);

    }

    public void deleteUser(User user){

        DatabaseHandler db = new DatabaseHandler(context);

        db.deleteLocationsOfUser(user);
        db.deleteUser(user);
    }

    public void logUser(int id){
        User targetUser;
        DatabaseHandler db = new DatabaseHandler(context);

        targetUser = db.getUser(id);

        Log.d("oma", "Käyttäjänimi: " + targetUser.getUserName());

        //TODO Add here a log of user's newest location from locations database
        //Log.d("oma", "Lat: " + targetUser._lat);
        //Log.d("oma", "Lng: " + targetUser._lng);
    }

    public long newLocation(Location location, User user){

        DatabaseHandler db = new DatabaseHandler(context);

        long newLocId = db.newLocation(location, user);

        return newLocId;
    }

    public Location locationById(long locationId){

        DatabaseHandler db = new DatabaseHandler(context);

        Location returnLoc = db.getLocationByLId(locationId);

        return returnLoc;
    }

    public List<Location> locationsByUser(User user){

        DatabaseHandler db = new DatabaseHandler(context);

        List<Location> userLocationList = db.getLocationByUser(user);

        /* Add a dummy location to avoid crashes if user has no locations */
        if (userLocationList == null){
            userLocationList.add(new Location(10.00000f, 10.00000f));
        }
        Log.d("oma", "Got location list by user's id");
        Log.d("oma", userLocationList.get(0).getLat() + " " + userLocationList.get(0).getLng());

        return userLocationList;
    }

    public Location locationLastByUser(User user){

        DatabaseHandler db = new DatabaseHandler(context);

        Location lastUserLocation = db.getUserLastLoc(user);

        return lastUserLocation;
    }

}
