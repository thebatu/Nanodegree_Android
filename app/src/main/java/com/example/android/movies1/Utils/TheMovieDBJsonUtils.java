package com.example.android.movies1.Utils;

import android.content.Context;
import android.util.Log;
import com.example.android.movies1.Movie;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by batu on 05/12/17.
 *
 */

 public class TheMovieDBJsonUtils {
    private static final String TAG = TheMovieDBJsonUtils.class.getSimpleName();
    private static final String IMG_URL = "http://image.tmdb.org/t/p/";

//    public TheMovieDBJsonUtils(){
//    }

    public static ArrayList simpleJsonMovieDataStringsFromJson(Context context, String movieJsonString)
            throws JSONException {

        ArrayList movies = new ArrayList();

        final String RESULTS = "results";
        final String CONRESULT = "success";
        final String JSONMSG = "status_message";

        JSONObject moviesJson = new JSONObject(movieJsonString);

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
            JSONObject movie = JSONMoviesArray.getJSONObject(i);
            String posterPath = movie.getString("poster_path");
            String backdropPath = movie.getString("backdrop_path");


            Movie movie_obj = new Movie();

            movie_obj.setPOSTER_PATH(IMG_URL + "w185/" + posterPath);
            movie_obj.setBACKDROP_PATH(IMG_URL + "w500/" + backdropPath);
            movie_obj.setID(movie.getString("id"));
            movie_obj.setOVERVIEW(movie.getString("overview"));
            movie_obj.setRELEASE_DATE(movie.getString("release_date"));
            movie_obj.setTITLE(movie.getString("original_title"));
            movie_obj.setVOTE_AVERAGE(movie.getString("vote_average"));
            movies.add(movie_obj);
        }

        return movies;

    }
}
