package com.example.android.movies1;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.movies1.Utils.NetworkUtils;
import com.example.android.movies1.Utils.TheMovieDBJsonUtils;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieClickListener{

    private String TAG = MainActivity.class.getSimpleName();
    private RecyclerView mRecyclerView;
    private MovieAdapter mMovieAdapter;
    private TextView errText;
    private ProgressBar mLoadingIndicator;
    private Toast mToast;
    private Movie movie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView =  findViewById(R.id.rv_mainMovies);
        errText = findViewById(R.id.tv_error_message_display);
        mLoadingIndicator = findViewById(R.id.pb_loading_indicator);

        GridLayoutManager layoutManager
                = new GridLayoutManager(this, 2);

        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        loadMoviesData();
        mMovieAdapter = new MovieAdapter(getApplicationContext(),this);
        mRecyclerView.setAdapter(mMovieAdapter);
    }

    private void loadMoviesData() {
        String popular = "popular";
        showMoviesDataView();
        new FetchMoviesTask(popular).execute();
    }

    private void showMoviesDataView() {
        errText.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage(){
        errText.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.INVISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int menuItemSelected = item.getItemId();
        if (menuItemSelected == R.id.top_rated){
            showMoviesDataView();
            new FetchMoviesTask("top_rated").execute();
        }else {

            showMoviesDataView();
            new FetchMoviesTask("popular").execute();


        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMovieItemClick(int clickedItemPosition, Movie clickedOnMovie) {
        if (mToast != null) {
            mToast.cancel();
        }
        String toastMessage = "Item #" + clickedItemPosition + "clicked";
        mToast = Toast.makeText(this, toastMessage, Toast.LENGTH_LONG);
        mToast.show();

        Intent intent = new Intent(MainActivity.this, MovieDetailsPage.class);
        intent.putExtra("movie_obj", clickedOnMovie);
        startActivity(intent);

    }

    public class FetchMoviesTask extends AsyncTask<ArrayList, Void, ArrayList> {

        String movies_param;

        private FetchMoviesTask(String s){
            if (s.equals("top_rated")) {
                movies_param = "top_rated";
            }else {
                movies_param = "popular";
            }
        }

        @Override
        protected ArrayList doInBackground(ArrayList... ArrayList) {
            URL MoviesRequestUrl = NetworkUtils.buildUrl(movies_param);

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

//                mMovieAdapter = new MovieAdapter(getApplicationContext(),simpleJsonMovieData);
//                mRecyclerView.setAdapter(mMovieAdapter);
                return simpleJsonMovieData;

            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList movieList) {
            if (movieList != null) {
                mLoadingIndicator.setVisibility(View.INVISIBLE);
                mRecyclerView.setVisibility(View.VISIBLE);
                showMoviesDataView();
                // COMPLETED (45) Instead of iterating through every string, use mForecastAdapter.setWeatherData and pass in the weather data
                mMovieAdapter.setMovieData(movieList);
                mMovieAdapter.notifyDataSetChanged();
            } else {
                showErrorMessage();
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mRecyclerView.setVisibility(View.INVISIBLE);

            mLoadingIndicator.setVisibility(View.VISIBLE);

        }
    }
}
