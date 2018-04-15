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
import com.example.android.movies1.Models.Movie;
import com.example.android.movies1.Utils.NetworkUtils;
import com.example.android.movies1.Utils.TheMovieDBJsonUtils;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieClickListener,
        LoaderManager.LoaderCallbacks<ArrayList> {

    private final String TAG = MainActivity.class.getSimpleName();
    private RecyclerView mRecyclerView;
    private MovieAdapter mMovieAdapter;
    private TextView errText;
    private  static final int FAV_LOADER_ID = 666;
    private ProgressBar mLoadingIndicator;
    private final int MOVIES_LOADER = 22;
    private static final String MOVIES_LOADER_EXTRA = "query";
    private static final String BUNDLE_INSTANCE = "HUMBLE_BUNDLE";
    private String movieType = "popular";
    private ArrayList<Movie> movieList;
    private RecyclerView mFavRecyclerView;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(BUNDLE_INSTANCE,movieType);
        outState.putParcelableArrayList("movies", movieList);
        }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFavRecyclerView = findViewById(R.id.rv_favMoviesMovies);
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
        queryBundle.putString(MOVIES_LOADER_EXTRA,movieType);

        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<String> MoviesSearchLoader = loaderManager.getLoader(MOVIES_LOADER);
        if (MoviesSearchLoader  == null) {
            loaderManager.initLoader(MOVIES_LOADER, queryBundle, this).forceLoad();
        } else {
            loaderManager.restartLoader(MOVIES_LOADER, queryBundle, this).forceLoad();
        }
    }

    private void showMoviesRecyclerView() {
        errText.setVisibility(View.GONE);
        mLoadingIndicator.setVisibility(View.INVISIBLE);
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
            showMoviesRecyclerView();
            loadMoviesData(movieType);
            return true;

        }else if (menuItemSelected == R.id.popular){

            movieType = "popular";
            showMoviesRecyclerView();
            loadMoviesData(movieType);
            return true;
        }else if  (menuItemSelected == R.id.favorite_movies) {
            movieType = "favorite";
            showMoviesRecyclerView();
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

            mMovieAdapter.setMovieData(null);

            movieList = new ArrayList<>();

            if (loader.getId() == FAV_LOADER_ID){
                if ( data.getCount() < 1) {
                    Log.e(TAG, "no match" );
                }else {
                    while (data.moveToNext()){
                        int movieId = data.getColumnIndex(MovieContract.FavoriteEntry.COLUMN_MOVIE_ID);
                        int movieTitle = data.getColumnIndex(MovieContract.FavoriteEntry.COLUMN_TITLE);
                        int moviePoster = data.getColumnIndex(MovieContract.FavoriteEntry.COLUMN_POSTER);
                        int movieOverview = data.getColumnIndex(MovieContract.FavoriteEntry.COLUMN_OVERVIEW);
                        int movieRating = data.getColumnIndex(MovieContract.FavoriteEntry.COLUMN_RATING);
                        int movieDate = data.getColumnIndex(MovieContract.FavoriteEntry.COLUMN_RELEASE_DATE);
                        int movieBackdrop= data.getColumnIndex(MovieContract.FavoriteEntry.COLUMN_BACKDROP_PATH);

                        String id = data.getString(movieId);
                        String title = data.getString(movieTitle);
                        String poster = data.getString(moviePoster);
                        String overview = data.getString(movieOverview);
                        String rating = data.getString(movieRating);
                        String date = data.getString(movieDate);
                        String backdrop = data.getString(movieBackdrop);

                        movieList.add(new Movie(id, date, rating, poster, backdrop , overview, title));
                    }
                    mMovieAdapter.clearMoviePosterData();
                    mMovieAdapter.setMovieData(movieList);
                    Log.v(TAG, "Favorites List have data");
                }
            }else {
                mMovieAdapter.clearMoviePosterData();
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            mMovieAdapter.clearMoviePosterData();
            if (loader != null) {
                mMovieAdapter.clearMoviePosterData();;
            } else {
                mMovieAdapter.setMovieData(null);
            }
        }
    };

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
                String sortingOrder = bundle.getString(MOVIES_LOADER_EXTRA);
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
                mLoadingIndicator.setVisibility(View.VISIBLE);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<ArrayList> loader, ArrayList data) {
        if (data != null) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            mRecyclerView.setVisibility(View.VISIBLE);
            showMoviesRecyclerView();
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