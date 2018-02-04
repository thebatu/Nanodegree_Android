package com.example.android.movies1.Utils;

import android.content.Context;
import android.util.Log;

import com.example.android.movies1.Movie;
import com.example.android.movies1.Trailer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by bats on 1/30/18.
 *
 */

public class TheMovieDetailsJonUtils {

    private static final String TAG = TheMovieDBJsonUtils.class.getSimpleName();
    private static final String VIDEO_URL = "http://image.tmdb.org/t/p/";

//    public TheMovieDBJsonUtils(){
//    }

    public static ArrayList simpleJsonMovieDataStringsFromJson(Context context, String movieDetailJsonString, Movie movie)
            throws JSONException {

        ArrayList<Trailer> trailers = new ArrayList<>();

        final String RESULTS = "results";
        final String CONRESULT = "success";
        final String JSONMSG = "status_message";

        JSONObject moviesJson = new JSONObject(movieDetailJsonString);

        /* Is there an error? */
        if (moviesJson.has(CONRESULT)) {
            String errorCode = moviesJson.getString(CONRESULT);
            if (errorCode.equals("false")){
                Log.e(TAG, "Error Occurred " + moviesJson.getString(JSONMSG));
                return null;
            }
        }

        JSONArray trailersJson = moviesJson.getJSONArray(RESULTS);
        //ArrayList<Trailer> trailersJson = new ArrayList<>();


        for (int i = 0; i < trailersJson.length(); i++) {
            JSONObject movieDetail = trailersJson.getJSONObject(i);
            String mId = movieDetail.getString("id");
            String name = movieDetail.getString("name");
            String key = movieDetail.getString("key");
            String site = movieDetail.getString("site");
            String size = movieDetail.getString("size");
            String type = movieDetail.getString("type");
            String mkey =  movieDetail.getString("key");

            Trailer trailer = new Trailer(mId, name, key, site, size, type);
            trailers.add(trailer);

        }

        return trailers;

    }
}
