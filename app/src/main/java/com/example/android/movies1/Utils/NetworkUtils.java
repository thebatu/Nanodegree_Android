package com.example.android.movies1.Utils;

import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by batu on 05/12/17.
 *
 */

public class NetworkUtils {

    final private static String baseMoviePOP = "http://api.themoviedb.org/3/movie/popular?api_key=18e23d5378804a57dc5743d12472408f";
    final private static String baseMovieTOP = "http://api.themoviedb.org/3/movie/top_rated?api_key=18e23d5378804a57dc5743d12472408f";


    public static URL buildUrl(String baseURL) {
        URL url = null;
        if (baseURL.equals("popular")){
            Uri builtUri = Uri.parse(baseMoviePOP).buildUpon()
                    //.appendQueryParameter(SIZE, SIZE)
                    //.appendQueryParameter(PATH, movieToFetch)
                    .build();


            try {
                url = new URL(builtUri.toString());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            return url;
        }else {
            Uri builtUri = Uri.parse(baseMovieTOP).buildUpon()
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
