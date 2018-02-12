package com.example.android.movies1.DataBase;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by batu on 11/02/18.
 *
 */

public class MovieContentProvider extends ContentProvider {

    public String TAG = MovieContentProvider.class.getSimpleName();
    public MovieDBHelper mMovieDbHelper;
    public static final int FAVORITES = 200;
    public static final int FAVORITES_ID = 201;
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    public static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;
        uriMatcher.addURI(authority, MovieContract.PATH_FAVORITES, FAVORITES);
        uriMatcher.addURI(authority, MovieContract.PATH_FAVORITES + "/#", FAVORITES_ID);

        return uriMatcher;
    }


    @Override
    public boolean onCreate() {
        mMovieDbHelper = new MovieDBHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        return null;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final SQLiteDatabase db = mMovieDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        switch(match) {
            case FAVORITES:
                Log.v(TAG, "Insert row Successful");
                return insertFavorites(uri, values);
            default:
                Log.e(LOG_TAG, "Error in inserting rows");
                throw new IllegalArgumentException("Error inserting a row");
        }


        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
