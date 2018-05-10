package com.example.android.movies1;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
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
    private RecyclerView MoviesRecyclerView;
    private MovieAdapter mMovieAdapter;
    private TextView errText;
    private static ProgressBar mLoadingIndicator;
    private  static final int FAV_LOADER_ID = 666;
    private final int MOVIES_LOADER = 22;
    private static final String MOVIES_LOADER_EXTRA = "query";
    private static final String BUNDLE_INSTANCE = "HUMBLE_BUNDLE";
    private String movieType = "popular";
    private ArrayList<Movie> movieList;
    public static int lastFirstVisiblePosition;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //movieType favorite/popular/top rated
        outState.putString(BUNDLE_INSTANCE,movieType);
        //save movies list to pass it to MoviesAdapter for on rotate case
        outState.putParcelableArrayList("movies", movieList);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ProgressDialog dialog = new ProgressDialog(MainActivity.this);


        //mFavRecyclerView = findViewById(R.id.rv_favMoviesMovies);

        MoviesRecyclerView =  findViewById(R.id.rv_mainMovies);
        errText = findViewById(R.id.tv_error_message_display);
        mLoadingIndicator = findViewById(R.id.pb_loading_indicator);

        GridLayoutManager layoutManager
                = new GridLayoutManager(this, 2);

        //check if movie type favorite exists if it does load favorite movies list from savedInstantState
        if(savedInstanceState != null) {
            if (savedInstanceState.containsKey(BUNDLE_INSTANCE)) {
                movieType = savedInstanceState.getString(BUNDLE_INSTANCE);

                switch (movieType) {
                    case "favorite":
                        movieList = savedInstanceState.getParcelableArrayList("favorite_movie_list");
                        MoviesRecyclerView.setLayoutManager(layoutManager);
                        MoviesRecyclerView.setHasFixedSize(true);
                        ArrayList fav_movies  = savedInstanceState.getParcelableArrayList("movies");
                        mMovieAdapter = new MovieAdapter(getApplicationContext(), this , fav_movies);
                        MoviesRecyclerView.setAdapter(mMovieAdapter);
                        favoriteLoader();
                        break;

                    default:
                        MoviesRecyclerView.setLayoutManager(layoutManager);
                        MoviesRecyclerView.setHasFixedSize(true);
                        loadMoviesData();
                        mMovieAdapter = new MovieAdapter(getApplicationContext(), this);
                        MoviesRecyclerView.setAdapter(mMovieAdapter);
                }
            }
        }
        else {
            MoviesRecyclerView.setLayoutManager(layoutManager);
            MoviesRecyclerView.setHasFixedSize(true);
//            loadMoviesData();
            mMovieAdapter = new MovieAdapter(getApplicationContext(), this);
            MoviesRecyclerView.setAdapter(mMovieAdapter);
        }
    }

    /**
     * initiate popular or top_rated movies loader
     */
    private void loadMoviesData() {

        Bundle queryBundle = new Bundle();
        queryBundle.putString(MOVIES_LOADER_EXTRA, movieType);

        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<String> MoviesSearchLoader = loaderManager.getLoader(MOVIES_LOADER);
        if (MoviesSearchLoader  == null) {
            loaderManager.initLoader(MOVIES_LOADER, queryBundle, this).forceLoad();
        } else {
            loaderManager.restartLoader(MOVIES_LOADER, queryBundle, this).forceLoad();
        }
    }


    public static void hideSpinner(){
        mLoadingIndicator.setVisibility(View.INVISIBLE);
    }

    /**
     * show moviesView and hide loading indicator and errMsg
     */
    private void setMoviesRecyclerViewVisibility() {
        errText.setVisibility(View.GONE);
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        MoviesRecyclerView.setVisibility(View.VISIBLE);
    }

    /**
     * hide moviesView and show errMsg
     */
    private void showErrorMessage(){
        errText.setVisibility(View.VISIBLE);
        MoviesRecyclerView.setVisibility(View.INVISIBLE);
    }

    /**
     *
     * @param menu menu
     * @return true
     * inflate main menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /**
     * display selected movie type
     * @param item  selected movie type
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int menuItemSelected = item.getItemId();
        if (menuItemSelected == R.id.top_rated){
            movieType = "top_rated";
            setMoviesRecyclerViewVisibility();
            loadMoviesData();
            return true;

        }else if (menuItemSelected == R.id.popular){

            movieType = "popular";
            setMoviesRecyclerViewVisibility();
            loadMoviesData();
            return true;

        }else if  (menuItemSelected == R.id.favorite_movies) {
            movieType = "favorite";
            setMoviesRecyclerViewVisibility();
            favoriteLoader();
            return true;

        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * load favorite movies type.
     */
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

            mLoadingIndicator.setVisibility(View.INVISIBLE);
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
                    //restore scroll position on orientation change
                    ((LinearLayoutManager) MoviesRecyclerView.getLayoutManager()).scrollToPositionWithOffset(lastFirstVisiblePosition,0);


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

    /**
     * call back for on a movie click
     * @param clickedItemPosition
     * @param clickedOnMovie
     */
    @Override
    public void onMovieItemClick(int clickedItemPosition, Movie clickedOnMovie) {
        Intent intent = new Intent(MainActivity.this, MovieDetailsPage.class);
        intent.putExtra("movie_obj", clickedOnMovie);
        startActivity(intent);

    }



    /**
     * Load top_rated ot favorite movies from a network call and pass the list to the adapter to display
     * @param i
     * @param bundle
     * @return
     */
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
            MoviesRecyclerView.setVisibility(View.VISIBLE);
            setMoviesRecyclerViewVisibility();
            mMovieAdapter.setMovieData(data);
            //restore scroll position on orientation change
            ((LinearLayoutManager) MoviesRecyclerView.getLayoutManager()).scrollToPositionWithOffset(lastFirstVisiblePosition,0);
            mMovieAdapter.notifyDataSetChanged();
        } else {
            showErrorMessage();
        }
    }

    @Override
    public void onLoaderReset(Loader<ArrayList> loader) {

    }

    @Override
    protected void onPause() {
        super.onPause();
        //save last scroll position to be restored on orientation change
        lastFirstVisiblePosition = ((LinearLayoutManager)MoviesRecyclerView.getLayoutManager()).findFirstVisibleItemPosition();

    }

    @Override
    protected void onResume() {
        super.onResume();
        //restore scroll position on orientation change
        ((LinearLayoutManager) MoviesRecyclerView.getLayoutManager()).scrollToPositionWithOffset(lastFirstVisiblePosition,0);
        if (movieType == "favorite") {
            favoriteLoader();
        }else{
            loadMoviesData();
        }

    }

}