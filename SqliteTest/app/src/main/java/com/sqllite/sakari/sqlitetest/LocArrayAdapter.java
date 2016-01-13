package com.sqllite.sakari.sqlitetest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by sakari.saastamoinen on 12.1.2016.
 */
public class LocArrayAdapter extends ArrayAdapter<Group> {

    private final Context context;
    private final List<Group> group;
    private final List<User> user;

    public LocArrayAdapter(Context context, List<Group> groupList, List<User> userList){
        super(context, -1, groupList);
        this.context = context;
        this.group = groupList;
        this.user = userList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){

        //Initialize the View that the information is inserted to
        //Initialize Groupholder class
        View rowView = convertView;
        Groupholder holder = null;

        //Check if rowView is null, if, inflate user_list_item layout
        if(rowView == null){

            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            rowView = inflater.inflate(R.layout.user_list_item, parent, false);

            //Set user info into the holder object for later use
            holder = new Groupholder();

            holder.tvGroupName = (TextView) rowView.findViewById(R.id.tvUserId);
            holder.tvGroupPassWord = (TextView) rowView.findViewById(R.id.tvUserName);
            holder.tvNumOfUsers = (TextView) rowView.findViewById(R.id.tvLatObj);

            rowView.setTag(holder);
        } else {
            holder = (Groupholder)rowView.getTag();
        }

        //Get the user object that is in the allUsers list in [position]
        Group group = this.group.get(position);

        //TODO modify to receive all group data from caller and get locations from there
        //Location userLocation = gv.locationLastByUser(user);

        //Set parameters for holder object, REMEMBER to use + "" trick
        //to make sure the parameters are stored as Strings
        holder.tvGroupName.setText(group.getGroupName() + "");
        holder.tvGroupPassWord.setText(group.getGroupPassWord() + "");
        holder.tvNumOfUsers.setText(group.getUsers().size() + "");

        return rowView;
    }

    //Groupholder sub-helper-class for storing user info
    static class Groupholder {
        TextView tvGroupName;
        TextView tvGroupPassWord;
        TextView tvNumOfUsers;
    }

}
