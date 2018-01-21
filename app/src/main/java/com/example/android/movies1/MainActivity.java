package com.example.android.movies1;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.movies1.Utils.NetworkUtils;
import com.example.android.movies1.Utils.TheMovieDBJsonUtils;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

//import android.app.LoaderManager;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieClickListener, LoaderManager.LoaderCallbacks<ArrayList> {

    private RecyclerView mRecyclerView;
    private MovieAdapter mMovieAdapter;
    private TextView errText;
    private ProgressBar mLoadingIndicator;
    private final int SUNSHINE_LOADER = 22;
    private static final String SUNSHINE_LOADER_EXTRA = "query";

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

        loadMoviesData("popular");
        mMovieAdapter = new MovieAdapter(getApplicationContext(),this);
        mRecyclerView.setAdapter(mMovieAdapter);
    }

    private void loadMoviesData(String s) {
        String movieType = s;
        //showMoviesDataView();

        LoaderCallbacks<ArrayList> callback = MainActivity.this;

        Bundle bundleForLoader = null;

        //Bundle queryBundle = new Bundle();
        //queryBundle.putString(SUNSHINE_LOADER_EXTRA ,movieType);
//
//        LoaderManager loaderManager = getSupportLoaderManager();
//        Loader<String> searchLoader = loaderManager.getLoader(SUNSHINE_LOADER);

        if (bundleForLoader == null) {
            getSupportLoaderManager().initLoader(SUNSHINE_LOADER, bundleForLoader, callback);
        }else {
            getSupportLoaderManager().restartLoader(SUNSHINE_LOADER, bundleForLoader, callback);
        }


        //new FetchMoviesTask(popular).execute();
    }

    private void showMoviesDataView() {
        errText.setVisibility(View.GONE);
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
            loadMoviesData("top_rated");
        }else {

            showMoviesDataView();
            loadMoviesData("popular");

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMovieItemClick(int clickedItemPosition, Movie clickedOnMovie) {
        Intent intent = new Intent(MainActivity.this, MovieDetailsPage.class);
        intent.putExtra("movie_obj", clickedOnMovie);
        startActivity(intent);

    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public Loader<ArrayList> onCreateLoader(int i, final Bundle bundle) {
        return new AsyncTaskLoader<ArrayList>(this) {
            @Override
            public ArrayList loadInBackground() {
                String popular = bundle.getString(SUNSHINE_LOADER_EXTRA);
                if (popular == null ){
                    return null;
                }
                try {

                    URL MoviesRequestUrl = NetworkUtils.buildUrl(popular);

                    String jsonMovieResponse =  NetworkUtils
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
            protected void onStartLoading() {
                super.onStartLoading();
                if (bundle == null) {
                    return;
                }
                mRecyclerView.setVisibility(View.INVISIBLE);
                //mLoadingIndicator.setVisibility(View.VISIBLE);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<ArrayList> loader, ArrayList data) {
        if (data != null) {
            //mLoadingIndicator.setVisibility(View.INVISIBLE);
           // mRecyclerView.setVisibility(View.VISIBLE);
            showMoviesDataView();
            mMovieAdapter.setMovieData(data);
            mMovieAdapter.notifyDataSetChanged();
        } else {
            showErrorMessage();
        }
    }

    @Override
    public void onLoaderReset(Loader<ArrayList> loader) {

    }








}
