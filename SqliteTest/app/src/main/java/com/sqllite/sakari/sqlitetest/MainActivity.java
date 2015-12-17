package com.sqllite.sakari.sqlitetest;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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

        //Purge database from earlier values
        List<User> allUsers = ((GlobalVariables)getApplication()).getAllUsers();
        for(int i = 0; i < allUsers.size() ;i++){

            User userToDelete = allUsers.get(i);

            ((GlobalVariables)getApplication()).deleteUser(userToDelete);
        }

        Log.d("oma", "All users after purge " + allUsers.toString());

        //Adding a new user manually (not recommended)
                ((GlobalVariables) getApplication()).addNewUser("Matti", 23.492533f, 63.786543f);

        //Logging all users
        allUsers = ((GlobalVariables) getApplication()).getAllUsers();
        ArrayList<String> userLog = new ArrayList<>();

            userLog.add(allUsers.get(0)._id + "");
            userLog.add(allUsers.get(0)._userName);
            userLog.add(allUsers.get(0)._lat);
            userLog.add(allUsers.get(0)._lng);


        Log.d("oma", "All users: " + userLog.toString());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onAddUserClick(View v){
        ((GlobalVariables)getApplication()).addNewUser("Maija", 24.645366f, 67.127634f);

        TextView tvUser = (TextView) findViewById(R.id.tvUserName);

        String userName = ((GlobalVariables)getApplication()).getUser(15)._userName;
        Log.d("oma", "Got username: " + userName);

        tvUser.setText(userName);
    }
}
