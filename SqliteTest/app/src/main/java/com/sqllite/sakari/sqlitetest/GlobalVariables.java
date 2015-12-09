package com.sqllite.sakari.sqlitetest;

import android.app.Activity;
import android.util.Log;

/**
 * Created by sakari.saastamoinen on 9.12.2015.
 */
public class GlobalVariables extends Activity{

    User user = new User();

    public void addNewUser(String userName, float lat, float lng){

        DatabaseHandler db = new DatabaseHandler(this);

        Log.d("oma", "Adding: " + userName);
        db.addUser(new User(userName, lat + "", lng + ""));
    }

}
