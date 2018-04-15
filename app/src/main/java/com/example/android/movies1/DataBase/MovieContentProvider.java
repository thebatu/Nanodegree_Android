package com.example.android.movies1.DataBase;

import android.content.ContentProvider;
import android.content.ContentUris;
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
        SQLiteDatabase db = mMovieDbHelper.getReadableDatabase();

        int match = sUriMatcher.match(uri);

        Cursor retCursor;
        switch (match) {
            case FAVORITES:
                retCursor = db.query
                        (MovieContract.FavoriteEntry.TABLE_NAME,
                                projection,
                                selection,
                                selectionArgs,
                                null,
                                null,
                                sortOrder);
                break;
            case FAVORITES_ID:
                selection = FAVORITES_ID + "=?";
                selectionArgs = new String[]{
                        String.valueOf(ContentUris.parseId(uri))};
                retCursor = db.query(MovieContract.FavoriteEntry.TABLE_NAME, projection, selection
                        , selectionArgs, null, null, sortOrder);

                break;

                default:
                    throw new UnsupportedOperationException("Unknowen uri " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }


    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        int matcher = sUriMatcher.match(uri);
        switch (matcher) {
            case FAVORITES:
                return MovieContract.FavoriteEntry.CONTENT_ALL_TYPE;
            case FAVORITES_ID:
                return MovieContract.FavoriteEntry.CONTENT_ITEMS_TYPE;
            default:
                throw new IllegalArgumentException(
                        "Can't get database selections: "
                                + uri +
                                " according to matcher, " + matcher);
        }
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
                Log.e(TAG, "Error inserting rows");
                throw new IllegalArgumentException("Error inserting a row");
        }
    }

    public Uri insertFavorites(Uri uri, ContentValues contentValues) {
        String movieTitle = contentValues.getAsString(MovieContract.FavoriteEntry.COLUMN_TITLE);
        if (movieTitle == null) {
            throw new IllegalArgumentException("Movie title is empty");
        }
        String posterPath = contentValues.getAsString(MovieContract.FavoriteEntry.COLUMN_BACKDROP_PATH);
        if (posterPath == null) {
            throw new IllegalArgumentException("posterPath is empty");
        }
        String movieId = contentValues.getAsString(MovieContract.FavoriteEntry.COLUMN_MOVIE_ID);
        if (movieId == null) {
            throw new IllegalArgumentException("MovieID is empty");

        }
        String movieReleaseDate = contentValues.getAsString(MovieContract.FavoriteEntry.COLUMN_RELEASE_DATE);
        if (movieReleaseDate == null) {
            throw new IllegalArgumentException("MovieReleaseDate is empty");

        }
        String moviePoster = contentValues.getAsString(MovieContract.FavoriteEntry.COLUMN_POSTER);
        if (moviePoster == null) {
            throw new IllegalArgumentException("MoviePoster is empty");

        }
        String movieRating = contentValues.getAsString(MovieContract.FavoriteEntry.COLUMN_RATING);
        if (movieRating == null) {
            throw new IllegalArgumentException("MovieRating is empty");

        }
        String movieOverview = contentValues.getAsString(MovieContract.FavoriteEntry.COLUMN_OVERVIEW);
        if (movieOverview == null) {
            throw new IllegalArgumentException("MovieOverV=view is empty");

        }
        SQLiteDatabase db = mMovieDbHelper.getWritableDatabase();
        long id = db.insert(
                MovieContract.FavoriteEntry.TABLE_NAME,
                null,
                contentValues
        );
        if (id == -1) {
            Log.e(TAG, "error inserting into DB");
            return null;
        }
        getContext().getContentResolver().notifyChange(uri, null );

        Log.v(TAG, "Id inserted: " + id);
        return ContentUris.withAppendedId(uri, id);


    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = mMovieDbHelper.getWritableDatabase();
        int matcher = sUriMatcher.match(uri);
        int rowsDeleted;

        switch (matcher) {
            case FAVORITES: {
                rowsDeleted = db.delete(MovieContract.FavoriteEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            case FAVORITES_ID: {
                selection = MovieContract.FavoriteEntry._ID + "=?";
                selectionArgs = new String[]{
                        String.valueOf(ContentUris.parseId(uri))
                };
                rowsDeleted = db.delete(MovieContract.FavoriteEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            default: {
                throw new UnsupportedOperationException("Unknown Uri :" + uri);
            }

        }
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);

        }


        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
