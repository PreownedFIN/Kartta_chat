package com.sqllite.sakari.sqlitetest;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sakari on 09.12.2015.
 */
public class DatabaseHandler extends SQLiteOpenHelper{

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 10;
    // Database Name
    private static final String DATABASE_NAME = "userManager";
    // User table name
    private static final String TABLE_USERS = "users";
    // User Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_USER = " user";
    private static final String KEY_LAT = " lat";
    private static final String KEY_LNG = " lng";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_USERS + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," + KEY_USER + " TEXT,"
                + KEY_LAT + " TEXT, " + KEY_LNG + " TEXT" +")";
        Log.d("oma", "Create users table: " + CREATE_CONTACTS_TABLE);
        db.execSQL(CREATE_CONTACTS_TABLE);
        Log.d("oma", "TABLE: " + db.toString());
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);

        // Create tables again
        onCreate(db);
    }

    /*------------- All CRUD (Create, Read, Update, Delete) operations -------------*/


    // Adding new user
    public int addUser(User user){

        //Initializing database connection
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT ROWID from " + TABLE_USERS + " order by ROWID DESC limit 1";
        int newUserId = -1;

        ContentValues values = new ContentValues();
        values.put(KEY_USER, user.getUserName()); // Username
        values.put(KEY_LAT, user.getLat()); // User Lat
        values.put(KEY_LNG, user.getLng()); //User Lng

        Log.d("oma", "DBHandler adding user: " + values.toString());

        // Inserting Row
        db.insert(TABLE_USERS, null, values);
        db.close(); // Closing database connection

        SQLiteDatabase dbRead = this.getReadableDatabase();

        Cursor cursor = dbRead.rawQuery(query, null);
        if (cursor != null && cursor.moveToFirst()){
            newUserId = Integer.parseInt(cursor.getLong(0) + "");
        }
        Log.d("oma", "newUserId: " + newUserId + "");
        return newUserId;
    }

    // Getting single user
    public User getUser(int id){

        //Initializing database connection
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_USERS, new String[]{KEY_ID,
                        KEY_USER, KEY_LAT, KEY_LNG}, KEY_ID + "=" + id,
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }

        Log.d("oma", cursor.getString(0));
        Log.d("oma", cursor.getString(1));
        Log.d("oma", cursor.getString(2));
        Log.d("oma", cursor.getString(3));

        User user = new User(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1), cursor.getString(2), cursor.getString(3));
        // return user
        return user;

    }

    // Getting all users
    public List<User> getAllUsers(){

        List<User> userList = new ArrayList<User>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_USERS;

        //Initializing database connection
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                User user = new User();
                user.setId(Integer.parseInt(cursor.getString(0)));
                user.setUserName(cursor.getString(1));
                user.setLat(cursor.getString(2));
                user.setLng(cursor.getString(3));
                // Adding user to list
                userList.add(user);
            } while (cursor.moveToNext());
        }

        // return user list
        return userList;

    }

    // Getting usercount
    public int getUserCount(){

        String countQuery = "SELECT  * FROM " + TABLE_USERS;

        //Initializing database connection
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();

    }

    // Updating single user
    public int updateUser(User user){

            //Initializing database connection
            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(KEY_USER, user.getUserName());
            values.put(KEY_LAT, user.getLat());
            values.put(KEY_LNG, user.getLng());

            // updating row
            return db.update(TABLE_USERS, values, KEY_ID + "=" + user._id,
                    new String[] { String.valueOf(user.getId()) });

    }

    //Deleting single user
    public void deleteUser(User user){

        //Initializing database connection
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_USERS, KEY_ID + " = ?",
                new String[] { String.valueOf(user.getId()) });
        db.close();

    }

}
