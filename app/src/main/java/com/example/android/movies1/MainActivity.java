package com.example.android.movies1;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.android.movies1.Utils.NetworkUtils;
import com.example.android.movies1.Utils.TheMovieDBJsonUtils;

import java.io.IOException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    Context context;
    private RecyclerView mRecyclerView;
    private MovieAdapter mMovieAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_mainMovies);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
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

    public class FetchMoviesTask extends AsyncTask<String, Void, String[]> {

        @Override
        protected String[] doInBackground(String... strings) {
            URL MoviesRequestUrl = NetworkUtils.buildUrl();

            try {
                String jsonMovieResponse = NetworkUtils
                        .getResponseFromHttpUrl(MoviesRequestUrl);

                String[] simpleJsonMovieData = TheMovieDBJsonUtils
                        .simpleJsonMovieDataStringsFromJson(MainActivity.this, jsonMovieResponse);

                return simpleJsonMovieData;


            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }

        }
    }
}
