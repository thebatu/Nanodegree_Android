package com.example.android.movies1.Utils;

import android.util.Log;

import com.example.android.movies1.Review;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by batu on 04/02/18.
 *
 */

public class TheReviewDetailsJsonUtils {
    private static final String TAG = TheMovieDBJsonUtils.class.getSimpleName();

    public static ArrayList simpleJsonMovieDataStringsFromJson(String reviewDetailJsonString)
            throws JSONException {

        ArrayList<Review> reviews = new ArrayList<>();

        final String RESULTS = "results";
        final String CONRESULT = "success";
        final String JSONMSG = "status_message";

        JSONObject reviewJson = new JSONObject(reviewDetailJsonString);

        /* Is there an error? */
        if (reviewJson.has(CONRESULT)) {
            String errorCode = reviewJson.getString(CONRESULT);
            if (errorCode.equals("false")){
                Log.e(TAG, "Error Occurred " + reviewJson.getString(JSONMSG));
                return null;
            }
        }

        JSONArray reviewsJson = reviewJson.getJSONArray(RESULTS);
        //ArrayList<Trailer> trailersJson = new ArrayList<>();


        for (int i = 0; i < reviewsJson .length(); i++) {
            JSONObject reviewDetail = reviewsJson .getJSONObject(i);
            String mId = reviewDetail.getString("id");
            String author = reviewDetail.getString("author");
            String content = reviewDetail.getString("content");
            String url = reviewDetail.getString("url");

            Review review = new Review(mId, author, content, url);
            reviews.add(review);

        }

        return reviews;

    }
}

