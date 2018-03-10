package com.example.android.movies1;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.movies1.Adapers.MovieAdapter;
import com.example.android.movies1.DataBase.MovieContract;
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
    private  static final int FAV_LOADER_ID = 666;
    private ProgressBar mLoadingIndicator;
    private final int SUNSHINE_LOADER = 22;
    private static final String SUNSHINE_LOADER_EXTRA = "query";
    private static final String BUNDLE_INSTANCE = "HUMBLE_BUNDLE";
    private String movieType = "popular";


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(BUNDLE_INSTANCE,movieType);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView =  findViewById(R.id.rv_mainMovies);
        errText = findViewById(R.id.tv_error_message_display);
        mLoadingIndicator = findViewById(R.id.pb_loading_indicator);

        GridLayoutManager layoutManager
                = new GridLayoutManager(this, 2);

        if(savedInstanceState != null)
            if (savedInstanceState.containsKey(BUNDLE_INSTANCE)){
                movieType = savedInstanceState.getString(BUNDLE_INSTANCE);
            }

        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        loadMoviesData(movieType);
        mMovieAdapter = new MovieAdapter(getApplicationContext(),this);
        mRecyclerView.setAdapter(mMovieAdapter);
    }

    private void loadMoviesData(String s) {

        Bundle queryBundle = new Bundle();
        queryBundle.putString(SUNSHINE_LOADER_EXTRA ,movieType);

        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<String> MoviesSearchLoader = loaderManager.getLoader(SUNSHINE_LOADER);
        if (MoviesSearchLoader  == null) {
            loaderManager.initLoader(SUNSHINE_LOADER, queryBundle, this).forceLoad();
        } else {
            loaderManager.restartLoader(SUNSHINE_LOADER, queryBundle, this).forceLoad();
        }

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
    public boolean onOptionsItemSelected(MenuItem item){
        int menuItemSelected = item.getItemId();
        if (menuItemSelected == R.id.top_rated){
            movieType = "top_rated";
            showMoviesDataView();
            loadMoviesData(movieType);
            return true;
        }else if (menuItemSelected == R.id.popular){

            movieType = "popular";
            showMoviesDataView();
            loadMoviesData(movieType);
            return true;
        }else  (menuItemSelected == R.id.favorite_movies){
            showMoviesDataView();
            favoriteLoader();
            return true;

        }
        return super.onOptionsItemSelected(item);
    }

    private void favoriteLoader() {
        getSupportLoaderManager().initLoader(FAV_LOADER_ID, null, favoriteLoader).forceLoad();
    }


    private LoaderManager.LoaderCallbacks<Cursor> favoriteLoader = new LoaderManager.LoaderCallbacks<Cursor>() {
        @SuppressLint("StaticFieldLeak")
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {

            return new AsyncTaskLoader<Cursor>(getApplicationContext()) {

                Cursor fav = null;

                @Override
                protected void onStartLoading() {
                    forceLoad();
                }

                @Override
                public Cursor loadInBackground() {
                    try {
                        return getContentResolver().query(
                                MovieContract.FavoriteEntry.CONTENT_URI,
                                null,
                                null,
                                null,
                                null
                        );


                    }catch (Exception e){
                        return null;
                    }
                }

                @Override
                public void deliverResult(Cursor data) {

                    fav = data;
                    super.deliverResult(data);
                }
            };
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

            mAdapter.setData(null);

            if (loader.getId() == FAV_LOADER_ID){
                if ( data.getCount() < 1) {
                    Log.e(TAG, "no match" );
                }else {
                    mAdapter.clear();
                    while (data.moveToNext()){

                    }
                }
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
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
                String sortingOrder = bundle.getString(SUNSHINE_LOADER_EXTRA);
                if (sortingOrder == null ){
                    return null;
                }
                try {

                    URL MoviesRequestUrl = NetworkUtils.buildUrl(sortingOrder);

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
                //mRecyclerView.setVisibility(View.VISIBLE);
                mLoadingIndicator.setVisibility(View.VISIBLE);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<ArrayList> loader, ArrayList data) {
        if (data != null) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            mRecyclerView.setVisibility(View.VISIBLE);
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
