package com.example.android.movies1.Utils;

import android.net.Uri;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by batu on 05/12/17.
 */

public class NetworkUtils {

    final static String baseURL = "http://image.tmdb.org/t/p/";
    final static String baseMovie = "http://api.themoviedb.org/3/movie/popular?api_key=[YOUR_API_KEY]";

    final static String SIZE = "w185";
    static String PATH;

    public static URL buildUrl(String movieToFetch) {
        // COMPLETED (1) Fill in this method to build the proper Github query URL
        Uri builtUri = Uri.parse(baseURL).buildUpon()
                .appendQueryParameter(SIZE, SIZE)
                .appendQueryParameter(PATH, movieToFetch)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }






}
