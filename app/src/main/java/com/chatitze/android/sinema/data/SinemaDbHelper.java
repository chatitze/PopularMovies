package com.chatitze.android.sinema.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.chatitze.android.sinema.data.SinemaContract.SinemaEntry;
/**
 * Created by chatitze on 14/03/2017.
 *
 * Manages a local database for sinema data.
 */

public class SinemaDbHelper extends SQLiteOpenHelper {

    /*
     * This is the name of our database. Database names should be descriptive and end with the
     * .db extension.
     */
    public static final String DATABASE_NAME = "movies.db";

    /*
     * If you change the database schema, you must increment the database version or the onUpgrade
     * method will not be called.
     */
    private static final int DATABASE_VERSION = 1;

    public SinemaDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Called when the database is created for the first time. This is where the creation of
     * tables and the initial population of the tables should happen.
     *
     * @param sqLiteDatabase The database.
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        /*
         * This String will contain a simple SQL statement that will create a table that will
         * cache our movie data.
         */
        final String SQL_CREATE_MOVIES_TABLE =

                "CREATE TABLE " + SinemaEntry.TABLE_NAME + " (" +

                /*
                 * SinemaEntry did not explicitly declare a column called "_ID". However,
                 * SinemaEntry implements the interface, "BaseColumns", which does have a field
                 * named "_ID". We use that here to designate our table's primary key.
                 */
                        SinemaEntry._ID                     + " INTEGER PRIMARY KEY AUTOINCREMENT, " +

                        SinemaEntry.COLUMN_RELEASE_DATE     + " INTEGER, "                 +

                        SinemaEntry.COLUMN_MOVIE_ID         + " INTEGER, "                 +
                        SinemaEntry.COLUMN_RATING           + " INTEGER, "                 +

                        SinemaEntry.COLUMN_ORIGINAL_TITLE   + " REAL, "                    +
                        SinemaEntry.COLUMN_OVERVIEW         + " REAL" + ");";

        /*
         * After we've spelled out our SQLite table creation statement above, we actually execute
         * that SQL with the execSQL method of our SQLite database object.
         */
        sqLiteDatabase.execSQL(SQL_CREATE_MOVIES_TABLE);
    }

    /**
     * This database is only a cache for online data, so its upgrade policy is simply to discard
     * the data and call through to onCreate to recreate the table. Note that this only fires if
     * you change the version number for your database (in our case, DATABASE_VERSION). It does NOT
     * depend on the version number for your application found in your app/build.gradle file. If
     * you want to update the schema without wiping data, commenting out the current body of this
     * method should be your top priority before modifying this method.
     *
     * @param sqLiteDatabase Database that is being upgraded
     * @param oldVersion     The old database version
     * @param newVersion     The new database version
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

    }
}
