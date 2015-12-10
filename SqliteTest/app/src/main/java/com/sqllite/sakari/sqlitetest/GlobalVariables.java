package com.sqllite.sakari.sqlitetest;

import android.app.Activity;
import android.app.Application;
import android.util.Log;

/**
 * Created by sakari.saastamoinen on 9.12.2015.
 */
public class GlobalVariables extends Application{
    
    //TODO Check from server if there is new information
    //TODO [OPTIONAL] Have the HttpHelper class update information
    //TODO every x seconds and queue all requests in one packet

    User user = new User();

    public void addNewUser(String userName, float lat, float lng){

        DatabaseHandler db = new DatabaseHandler(this);

        Log.d("oma", "Adding: " + userName);
        db.addUser(new User(userName, lat + "", lng + ""));
    }

    public User getUser(int id){

        User targetUser;
        DatabaseHandler db = new DatabaseHandler(this);

        targetUser = db.getUser(id);

        return targetUser;
    }

}
