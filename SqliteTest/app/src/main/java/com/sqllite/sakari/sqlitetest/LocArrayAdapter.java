package com.sqllite.sakari.sqlitetest;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by sakari.saastamoinen on 12.1.2016.
 */
public class LocArrayAdapter extends ArrayAdapter<Location> {

    private final Context context;
    private final List<Location> location;

    public LocArrayAdapter(Context context, List<Location> locationList){
        super(context, -1, locationList);
        this.context = context;
        this.location = locationList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){

        //Initialize the View that the information is inserted to
        //Initialize LocationHolder class
        View rowView = convertView;
        LocationHolder holder = null;

        //Check if rowView is null, if, inflate user_list_item layout
        if(rowView == null){

            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            rowView = inflater.inflate(R.layout.location_list_item, parent, false);

            //Set user info into the holder object for later use
            holder = new LocationHolder();

            holder.tvLocId = (TextView) rowView.findViewById(R.id.tvLocIdValue);
            holder.tvLatValue = (TextView) rowView.findViewById(R.id.tvLatValue);
            holder.tvLngValue = (TextView) rowView.findViewById(R.id.tvLngValue);

            rowView.setTag(holder);
        } else {
            holder = (LocationHolder)rowView.getTag();
        }

        //Get the user object that is in the allUsers list in [position]
        Location location = this.location.get(position);

        //TODO modify to receive all group data from caller and get locations from there
        //Location userLocation = gv.locationLastByUser(user);

        //Set parameters for holder object, REMEMBER to use + "" trick
        //to make sure the parameters are stored as Strings
        holder.tvLocId.setText("LocId: " + location.getId() + "");
        holder.tvLatValue.setText(location.getLat() + "");
        holder.tvLngValue.setText(location.getLng() + "");
        Log.d("oma", "Locations at: " + position + " " + location.getLat() + " "
        + location.getLng());

        return rowView;
    }

    //LocationHolder sub-helper-class for storing user info
    static class LocationHolder {
        TextView tvLocId;
        TextView tvLatValue;
        TextView tvLngValue;
    }

}
