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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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

        //Adding a new user manually (not recommended)
                ((GlobalVariables) getApplication()).addNewUser("Matti", 23.492533f, 63.786543f);

        //Logging all users
        List<User> allUsers = ((GlobalVariables) getApplication()).getAllUsers();
        ArrayList<String> userLog = new ArrayList<>();

        for (int i = 0; i < allUsers.size(); i++) {

            userLog.add(allUsers.get(i)._id + "");
            userLog.add(allUsers.get(i)._userName);
            userLog.add(allUsers.get(i)._lat);
            userLog.add(allUsers.get(i)._lng);

        }

        Log.d("oma", "All users: " + userLog.toString());

        Random r = new Random();

        TextView tvLat =  (TextView) findViewById(R.id.tvLatText);
        TextView tvLng =  (TextView) findViewById(R.id.tvLngText);

        float randLat = -90 + 90 * r.nextFloat();
        float randLng = -180 + 180 * r.nextFloat();

        tvLat.setText(randLat + "");
        tvLng.setText(randLng + "");
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

        EditText etUserName = (EditText) findViewById(R.id.etUserName);
        TextView tvLat =  (TextView) findViewById(R.id.tvLatText);
        TextView tvLng =  (TextView) findViewById(R.id.tvLngText);

        String userName = etUserName.getText().toString();
        float userLat = Float.parseFloat(tvLat.getText().toString());
        float userLng = Float.parseFloat(tvLng.getText().toString());

        if (!userName.equals("")){
            int newUserId = ((GlobalVariables)getApplication()).addNewUser(userName, userLat, userLng);

            TextView tvUser = (TextView) findViewById(R.id.tvUserName);

            //Get the name of the user from GlobalVariables to make sure it is added to
            //the database
            String dbUserName = ((GlobalVariables)getApplication()).getUser(newUserId)._userName;
            ((GlobalVariables) getApplication()).logUser(newUserId);

            tvUser.setText(dbUserName);
        }else{
            Toast toast = Toast.makeText(this, "Käyttäjänimi ei voi olla tyhjä", Toast.LENGTH_LONG);
            toast.show();
        }
    }public void onClearDbClick(View v){

        List<User> allUsers = ((GlobalVariables) getApplication()).getAllUsers();

        for (int i = 0; i < allUsers.size(); i++) {

            ((GlobalVariables)getApplication()).deleteUser(((GlobalVariables)getApplication()).getUser(allUsers.get(i)._id));

        }
    }
}
