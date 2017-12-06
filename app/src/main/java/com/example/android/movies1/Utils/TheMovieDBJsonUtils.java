package com.example.android.movies1.Utils;

import android.content.Context;
import android.graphics.Movie;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by batu on 05/12/17.
 */

public class TheMovieDBJsonUtils {
    private static final String TAG = TheMovieDBJsonUtils.class.getSimpleName();

    public String[] simpleJsonMovieDataStringsFromJson(Context context, String movieJsonString)
            throws JSONException {

        final String ROOT = "page";
        final String RESULTS = "results";
        final String CONRESULT = "success";
        final String JSONMSG = "status_message";

        String[] parsedMovieData = null;
        JSONObject moviesJson = new JSONObject(movieJsonString);


        /* Is there an error? */
        if (moviesJson.has(CONRESULT)) {
            String errorCode = moviesJson.getString(CONRESULT);
            if (errorCode == "false"){
                Log.e(TAG, "Error Occurred " + moviesJson.getString(JSONMSG));
                return null;
            }
        }

        JSONArray moviesArray = moviesJson.getJSONArray(RESULTS);

        for (int i = 0; i < moviesArray.length(); i++) {
            JSONObject movie = moviesArray.getJSONObject(i);
            Movie movie_object = new Movie(movie);
            movies.add(movie_object);

        }

        return moviesArray;




    }
}
