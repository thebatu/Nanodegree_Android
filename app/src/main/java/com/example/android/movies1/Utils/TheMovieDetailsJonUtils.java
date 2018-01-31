package com.example.android.movies1.Utils;

import android.content.Context;
import android.util.Log;

import com.example.android.movies1.Movie;

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

        ArrayList movie_key = new ArrayList();

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

        JSONArray JSONMoviesArray = moviesJson.getJSONArray(RESULTS);

        for (int i = 0; i < JSONMoviesArray.length(); i++) {
            JSONObject movieDetail = JSONMoviesArray.getJSONObject(i);

            //String posterPath = movieDetail.getString("poster_path");
            //String backdropPath = movieDetail.getString("backdrop_path");

            //Movie movie_detail_obj = new Movie();
            String mkey =  movieDetail.getString("key");
            if (mkey != "" || mkey != null){
               // movie_detail_obj.setKEY(());
                //movies.add(movie_detail_obj);
                movie_key.add(mkey);
            }else {
                movie_key.add("No Trailers");
            }

        }

        return movie_key;

    }
}
