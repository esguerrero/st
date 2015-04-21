package com.example.android.effectivenavigation.dbcontrol;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.example.android.effectivenavigation.db.User;
import com.example.android.effectivenavigation.db.UserBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Esteban on 5/22/14.
 */
public class UsersDataSource  {

    // Database fields
    private SQLiteDatabase database;

    private UserBean dbHelper;
    private String[] allColumns = { UserBean.COLUMN_ID, UserBean.COLUMN_USERNAME,
            UserBean.COLUMN_COMMENT };

//    private String[] averColumn = { UserBean.COLUMN_AVERAGE};


    public UsersDataSource(Context context) {
        dbHelper = new UserBean(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }





    public User createUser(String username, String comment) {

        ContentValues values = new ContentValues();


        values.put(UserBean.COLUMN_USERNAME, username);
        values.put(UserBean.COLUMN_COMMENT, comment);

        long insertId = database.insert(UserBean.TABLE_USERS, null, values);

        Cursor cursor = database.query(UserBean.TABLE_USERS, allColumns,
                UserBean.COLUMN_ID + " = " + insertId, null, null, null, null);
        cursor.moveToFirst();

        User newUser = cursorToUser(cursor);
        cursor.close();
        return newUser;

    }

    public void deleteUser(User user) {

        String username = user.getUsername();
        System.out.println("Measure deleted with usernameSto: " + username);
        database.delete(UserBean.TABLE_USERS, UserBean.COLUMN_USERNAME+ " = "
                + username, null);
    }



/*
    public float getTotalAverage() {
        List<Measure> measures = new ArrayList<Measure>();
        List averaData = new ArrayList();
        float averag = 0.0f;

        Cursor cursor = database.query(UserBean.TABLE_MEASURES, allColumns,
                null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Measure measure = cursorToMeasure(cursor);
            measures.add(measure);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();

        for (Iterator iterator = measures.iterator(); iterator.hasNext();) {
            Measure measu = (Measure) iterator.next();
            averag = averag + measu.getAverage();

            //averaData.add(measu.getAverage());
        }



        return  (averag/measures.size());
    }
*/


    public List<User> getAllUsers() {
        List<User> users = new ArrayList<User>();

        Cursor cursor = database.query(UserBean.TABLE_USERS, allColumns,
                null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            User user = cursorToUser(cursor);
            users.add(user);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return users;
    }

    private User cursorToUser(Cursor cursor) {
        User user = new User();
        user.setUsername(cursor.getString(0));
        user.setComment(cursor.getString(1));
        return user;
    }

}
