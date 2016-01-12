package com.sqllite.sakari.sqlitetest;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.List;

/**
 * Created by sakari.saastamoinen on 12.1.2016.
 */
public class LocArrayAdapter extends ArrayAdapter<Location> {

    private final Context context;
    private final List<Location> location;
    private final List<User> user;

    public LocArrayAdapter(Context context, List<Location> locationList, List<User> userList){
        super(context, -1, userList);
        this.context = context;
        this.location = locationList;
        this.user = userList;
    }

}
