package com.example.android.movies1.Utils;

import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by batu on 28/01/18.
 *
 */

public class movieDetailsNetworkUtil {
    static int key;


    public static URL buildUrl(int baseURL) {
        key = baseURL;

        String DetailedMovie = "https://api.themoviedb.org/3/movie/"+key+"/videos?api_key=18e23d5378804a57dc5743d12472408f&language=en-US";

        Log.d( "RICK Detailed Movie : ", DetailedMovie);

        URL url = null;
        Uri builtUri = Uri.parse(DetailedMovie).buildUpon()
                //.appendQueryParameter(SIZE, SIZE)
                //.appendQueryParameter(PATH, movieToFetch)
                .build();
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }


}