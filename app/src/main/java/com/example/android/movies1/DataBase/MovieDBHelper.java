package com.example.android.movies1.DataBase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by batu on 10/02/18.
 *
 */

public class MovieDBHelper extends SQLiteOpenHelper {

    public static final String  DATABASE_NAME = "favorites.db";
    public static final int DATABASE_VERSION = 1;

    public MovieDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_FAVORITE_ENTRIES = "CREATE TABLE" + MovieContract.FavoriteEntry.TABLE_NAME +
                " ( " + MovieContract.FavoriteEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + MovieContract.FavoriteEntry.COLUMN_MOVIE_ID + " TEXT NOT NULL, "
                + MovieContract.FavoriteEntry.COLUMN_TITLE + " TEXT NOT NULL, "
                + MovieContract.FavoriteEntry.COLUMN_POSTER + " TEXT NOT NULL, "
                + MovieContract.FavoriteEntry.COLUMN_RATING + " TEXT NOT NULL, "
                + MovieContract.FavoriteEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, "
                + MovieContract.FavoriteEntry.COLUMN_OVERVIEW + " TEXT NOT NULL "
                + " ); ";
        db.execSQL(SQL_CREATE_FAVORITE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(" DROP TABLE IF EXISTS " + MovieContract.FavoriteEntry.TABLE_NAME);
        onCreate(db);

    }
}
