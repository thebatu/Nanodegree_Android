package com.example.android.movies1;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.android.movies1.Utils.NetworkUtils;
import com.example.android.movies1.Utils.TheMovieDBJsonUtils;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    Context context;
    private RecyclerView mRecyclerView;
    private MovieAdapter mMovieAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_mainMovies);

        GridLayoutManager layoutManager
                = new GridLayoutManager(this, 2);

        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        mMovieAdapter = new MovieAdapter();
        mRecyclerView.setAdapter(mMovieAdapter);

        loadMoviesData();
    }

    private void loadMoviesData() {
        showMoviesDataView();
        new FetchMoviesTask().execute();
    }

    private void showMoviesDataView() {
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int menuItemSelected = item.getItemId();
        if (menuItemSelected == R.id.menu){
            Context context = MainActivity.this;
            String msg = "Menu clicked";
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public class FetchMoviesTask extends AsyncTask<ArrayList, Void, ArrayList> {

        @Override
        protected ArrayList doInBackground(ArrayList... ArrayList) {
            URL MoviesRequestUrl = NetworkUtils.buildUrl();

            try {
                String jsonMovieResponse = NetworkUtils
                        .getResponseFromHttpUrl(MoviesRequestUrl);

                ArrayList simpleJsonMovieData = new ArrayList();
                try {
                    simpleJsonMovieData = TheMovieDBJsonUtils
                            .simpleJsonMovieDataStringsFromJson(MainActivity.this, jsonMovieResponse);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return simpleJsonMovieData;


            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }

        }

        @Override
        protected void onPostExecute(ArrayList movieList) {
            if (movieList != null) {
                showMoviesDataView();
                // COMPLETED (45) Instead of iterating through every string, use mForecastAdapter.setWeatherData and pass in the weather data
                mMovieAdapter.setMovieData(movieList);
            } else {
                //showErrorMessage();
            }
        }
    }
}
