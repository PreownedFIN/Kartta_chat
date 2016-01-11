package com.sqllite.sakari.sqlitetest;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class DbBrowser extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_db_browser);
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

        //Get all users
        List<User> userList = ((GlobalVariables) getApplication()).getAllUsers();

        //Get listView to which attach the adapter
        ListView dbList = (ListView)findViewById(R.id.lvDbObjects);

            //Log.d("oma", "ArrayAdapteriin menevä merkkijono: " + userList);
        //Initialize UserArrayAdapter
        UserArrayAdapter adapter = new UserArrayAdapter(this, userList);

        //Assigning adapter for ListView
        dbList.setAdapter(adapter);
    }

}
