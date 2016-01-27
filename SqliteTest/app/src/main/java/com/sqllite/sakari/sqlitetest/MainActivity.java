package com.sqllite.sakari.sqlitetest;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    GlobalVariables gv = new GlobalVariables(this);

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

        gv.currentUser = new User();

        //Logging all users
        List<User> allUsers = gv.getAllUsers();
        ArrayList<String> userLog = new ArrayList<>();

        for (int i = 0; i < allUsers.size(); i++) {

            userLog.add(allUsers.get(i)._id + "");
            userLog.add(allUsers.get(i)._userName);

        }

        Log.d("oma", "All users: " + userLog.toString());

        setRandLatLng();
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

        //Serialize layout objects
        EditText etUserName = (EditText) findViewById(R.id.etUserName);
        TextView tvLat =  (TextView) findViewById(R.id.tvLatText);
        TextView tvLng =  (TextView) findViewById(R.id.tvLngText);

        //Get information to enter to the database
        String userName = etUserName.getText().toString();
        float userLat = Float.parseFloat(tvLat.getText().toString());
        float userLng = Float.parseFloat(tvLng.getText().toString());

        Location userLocation = new Location(userLat, userLng);

        //Check if userName-field is empty
        if (!userName.equals("")){

            //Add user to database and receive the userId as return
            int newUserId = gv.addNewUser(userName);

            gv.newLocation(userLocation,
                    gv.getUser(newUserId));

            TextView tvUser = (TextView) findViewById(R.id.tvUserName);

            //Get the name of the user from GlobalVariables to make sure it has been
            //added to the database
            String dbUserName = gv.getUser(newUserId).getUserName();
            gv.logUser(newUserId);

            tvUser.setText(dbUserName);

            setRandLatLng();
        }else{
            //Show empty username toast when username field is empty
            Toast toast = Toast.makeText(this, R.string.error_empty_username, Toast.LENGTH_LONG);
            toast.show();
        }
    }

    public void onClearDbClick(View v){

        //Get allUsers list for deletion
        List<User> allUsers = gv.getAllUsers();

        for (int i = 0; i < allUsers.size(); i++) {
            //Delete all users in the allUsers list
            gv.deleteUser(gv.getUser(allUsers.get(i)._id));
        }
    }

    public void onBtnBrowseDb(View v){
        //Initialize and start intent for UserBrowserActivity activity
        Intent intent = new Intent(this, UserBrowserActivity.class);
        startActivity(intent);
    }

    public void onBtnLocClick(View v){
        Toast.makeText(this, "Browse locations", Toast.LENGTH_LONG).show();
    }

    public void onBtnGroupClick(View v){
        Toast.makeText(this, "Browse groups", Toast.LENGTH_LONG).show();
    }

    //Set random lat and lng values for the textViews where they are extracted
    //for database entry
    private void setRandLatLng(){
        Random r = new Random();

        TextView tvLat =  (TextView) findViewById(R.id.tvLatText);
        TextView tvLng =  (TextView) findViewById(R.id.tvLngText);

        float randLat = -90 + 90 * r.nextFloat();
        float randLng = -180 + 180 * r.nextFloat();

        tvLat.setText(randLat + "");
        tvLng.setText(randLng + "");
    }
}
