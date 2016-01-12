package com.sqllite.sakari.sqlitetest;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import junit.framework.TestCase;

import java.util.List;

/**
 * Created by Sakari on 08.01.2016.
 */
public class UserArrayAdapter extends ArrayAdapter<User> {
    private final Context context;
    private final List<User> user;
    private final List<Location> location;

    //Constructor for the class
    public UserArrayAdapter(Context context, List<User> userList, List<Location> locationList){
        super(context, -1, userList);
        this.context = context;
        this.user = userList;
        this.location = locationList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){

        //Initialize the View that the information is inserted to
        //Initialize UserHolder class
        View rowView = convertView;
        UserHolder holder = null;

        //Check if rowView is null, if, inflate user_list_item layout
        if(rowView == null){

            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            rowView = inflater.inflate(R.layout.user_list_item, parent, false);

            //Set user info into the holder object for later use
            holder = new UserHolder();

            holder.tvId = (TextView) rowView.findViewById(R.id.tvUserId);
            holder.tvUserName = (TextView) rowView.findViewById(R.id.tvUserName);
            holder.tvLat = (TextView) rowView.findViewById(R.id.tvLatObj);
            holder.tvLng = (TextView) rowView.findViewById(R.id.tvLngObj);

            rowView.setTag(holder);
        } else {
            holder = (UserHolder)rowView.getTag();
        }

        //Get the user object that is in the allUsers list in [position]
        User user = this.user.get(position);
        Location location = this.location.get(position);

        //TODO modify to receive all location data from caller and get locations from there
        //Location userLocation = gv.locationLastByUser(user);

        //Set parameters for holder object, REMEMBER to use + "" trick
        //to make sure the parameters are stored as Strings
        holder.tvId.setText(user.getId() + "");
        holder.tvUserName.setText(user.getUserName() + "");
        holder.tvLat.setText(location.getLat() + "");
        holder.tvLng.setText(location.getLng() + "");

        return rowView;
    }

    //UserHolder sub-helper-class for storing user info
    static class UserHolder{
        TextView tvId;
        TextView tvUserName;
        TextView tvLat;
        TextView tvLng;
    }
}
