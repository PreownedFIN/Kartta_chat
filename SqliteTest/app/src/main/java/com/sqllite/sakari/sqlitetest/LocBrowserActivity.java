package com.sqllite.sakari.sqlitetest;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

public class LocBrowserActivity extends AppCompatActivity {

    //Initialize new GlobalVariables object
    GlobalVariables gv = new GlobalVariables(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loc_browser);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Get views from the layout
        TextView tvUserName = (TextView) findViewById(R.id.tvUserName);
        ListView lvLocations = (ListView) findViewById(R.id.lvLocations);

        //Fetch userId from intent extras
        int userId = getIntent().getIntExtra("user", -1);
        Log.d("oma", "UserId LocBrowAct:issä: " + userId +"");

        //Get user from GlobalVariable to add the username to the textview
        //and to get all locations by user
        User user = gv.getUser(userId);
        List<Location> locations = gv.locationByUser(user);

        //Set tvUserName text to users username
        tvUserName.setText(user.getUserName());

        //Initialize adapter and set it as lvLocations' adapter
        LocArrayAdapter adapter = new LocArrayAdapter(this, locations);
        lvLocations.setAdapter(adapter);
    }

}
