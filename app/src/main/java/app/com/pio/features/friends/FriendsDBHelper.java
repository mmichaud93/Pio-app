package app.com.pio.features.friends;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import app.com.pio.ui.friends.friendslist.FriendsListItem;

/**
 * Created by mmichaud on 3/8/16.
 */
public class FriendsDBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "FriendsDB.db";
    public static final String TABLE_NAME = "friends";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_USER_ID = "userId";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_IMAGE_URL = "profileImageUrl";
    public static final String COLUMN_MONUMENTS = "monuments";
    public static final String COLUMN_XP = "xp";

    public FriendsDBHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(
                "create table " + TABLE_NAME + " " +
                        "(" +
                        COLUMN_ID + " integer primary key, " +
                        COLUMN_USER_ID + " text," +
                        COLUMN_NAME + " text," +
                        COLUMN_IMAGE_URL + " text," +
                        COLUMN_MONUMENTS + " text," +
                        COLUMN_XP + " integer)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // TODO Auto-generated method stub
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public void dropTable() {
        getWritableDatabase().execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        getWritableDatabase().execSQL(
                "create table " + TABLE_NAME + " " +
                        "(" +
                        COLUMN_ID + " integer primary key, " +
                        COLUMN_USER_ID + " text," +
                        COLUMN_NAME + " text," +
                        COLUMN_IMAGE_URL + " text," +
                        COLUMN_MONUMENTS + " text," +
                        COLUMN_XP + " integer)"
        );
    }

    public void storeFriends(List<FriendsListItem> friendsListItems) {
        if (friendsListItems == null) {
            return;
        }
        dropTable();
        SQLiteDatabase db = this.getWritableDatabase();
        for (FriendsListItem item : friendsListItems) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(COLUMN_USER_ID, item.getUserId());
            contentValues.put(COLUMN_NAME, item.getName());
            contentValues.put(COLUMN_IMAGE_URL, item.getProfileImageUrl());
            contentValues.put(COLUMN_MONUMENTS, item.getMonumentsString());
            contentValues.put(COLUMN_XP, item.getXp());

            int result = db.update(TABLE_NAME, contentValues, COLUMN_USER_ID+"="+item.getUserId(), null);
            if (result == 0) {
                db.insert(TABLE_NAME, null, contentValues);
            }
        }

    }

    public ArrayList<FriendsListItem> retrieveFriends() {

        SQLiteDatabase db = this.getReadableDatabase();

        ArrayList<FriendsListItem> items = new ArrayList<>();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                COLUMN_NAME,
                COLUMN_USER_ID,
                COLUMN_IMAGE_URL,
                COLUMN_MONUMENTS,
                COLUMN_XP
        };

        // How you want the results sorted in the resulting Cursor

        Cursor c = db.query(
                TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );
        if (c != null) {
            if (c.moveToFirst()) {
                do {
                    items.add(new FriendsListItem(c.getString(0), c.getString(1), c.getString(2), c.getString(3), c.getInt(4)));
                } while (c.moveToNext());
            }
        }

        return items;
    }


}
