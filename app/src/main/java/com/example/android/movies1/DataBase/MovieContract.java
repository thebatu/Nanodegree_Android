package com.example.android.movies1.DataBase;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by batu on 10/02/18.
 *
 */

public class MovieContract {

    public static final String CONTENT_AUTHORITY = "com.example.android.movies1";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_FAVORITES = "favorites";


    private MovieContract(){}

    /* inner class that defines the table contents */
    public static class FavoriteEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_FAVORITES).build();

        public static final String CONTENT_ALL_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY
                + "/" + PATH_FAVORITES;
        public static final String CONTENT_ITEMS_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY
                + "/" + PATH_FAVORITES;

        public static final String TABLE_NAME = "favorites";
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_TITLE = "movie_title";
        public static final String COLUMN_POSTER = "movie_poster";
        public static final String COLUMN_RATING = "movie_rating";
        public static final String COLUMN_RELEASE_DATE = "movie_release_date";
        public static final String COLUMN_OVERVIEW = "movie_overview";
        public static final String BACKDROP_PATH = "poster_path";

        public static Uri builtFavoriteUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

    }



}
