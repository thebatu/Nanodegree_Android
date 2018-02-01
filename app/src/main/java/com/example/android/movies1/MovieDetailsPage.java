package com.example.android.movies1;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.movies1.Utils.TheMovieDetailsJonUtils;
import com.example.android.movies1.Utils.movieDetailsNetworkUtil;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class MovieDetailsPage extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList>, DetailsAdapter.DetailsClickListener {

    private String TAG = MovieDetailsPage.class.getSimpleName();
    private RecyclerView dRecyclerView;
    private DetailsAdapter mDetailsAdapter;
    private static final String MOVIE_DETAILS_EXTRA = "movie_query";
    private final int MOVIE_DETAILS_LOADER = 44;

    private TextView movieTitle;
    private ImageView movieImage;
    private TextView movieOverview;
    private TextView movieDate;
    private TextView movieRating;
    private int id;
    Movie mMovie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details_page);
        Context context = this.getApplicationContext();

        dRecyclerView =  findViewById(R.id.rv_movie_details);

        movieTitle = findViewById(R.id.title);
        movieImage = findViewById(R.id.poster_image);
        movieOverview= findViewById(R.id.overview);
        movieDate= findViewById(R.id.date);
        movieRating= findViewById(R.id.rating);


        /*
            get the movie object from mainActivity and get the ID to be used for network call
            to get the trailer
         */
        Movie movie_obj = getIntent().getParcelableExtra("movie_obj");
        mMovie = movie_obj;
        if (movie_obj != null ) {
            String posterPath = movie_obj.getBACKDROP_PATH();
            Picasso.with(context).load(posterPath).into(movieImage);

            String titleOfMovie = movie_obj.getTITLE();
            movieTitle.setText(titleOfMovie);

            String overView = movie_obj.getOVERVIEW();
            movieOverview.setText(overView);

            String date = movie_obj.getRELEASE_DATE();
            movieDate.setText(date);

            String rate = movie_obj.getVOTE_AVERAGE();
            movieRating.setText(rate);

            id = Integer.parseInt(movie_obj.getID());

            GridLayoutManager dLayoutManager
                    = new GridLayoutManager(this, 1);

            dRecyclerView.setLayoutManager(dLayoutManager);
            //dRecyclerView.setNestedScrollingEnabled(false);
            dRecyclerView.setHasFixedSize(true);

            loadMoviesData(id);

            mDetailsAdapter = new DetailsAdapter(getApplicationContext(),this);
            dRecyclerView.setAdapter(mDetailsAdapter);

        }
        //movieImage.setImageBitmap(movie_obj.getBACKDROP_PATH());
    }

    /*
        int id --> the ID of the movie clicked on
     */
    private void loadMoviesData(int id) {

        Bundle queryBundle = new Bundle();
        queryBundle.putInt(MOVIE_DETAILS_EXTRA ,id);

        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<Integer> MoviesSearchLoader = loaderManager.getLoader(MOVIE_DETAILS_LOADER);

        if (MoviesSearchLoader  == null) {
            loaderManager.initLoader(MOVIE_DETAILS_LOADER, queryBundle, this).forceLoad();
        } else {
            loaderManager.restartLoader(MOVIE_DETAILS_LOADER, queryBundle, this);
        }
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public Loader<ArrayList> onCreateLoader(int id, final Bundle bundle) {
        return new AsyncTaskLoader<ArrayList>(this) {
            @Override
            protected void onStartLoading() {
                super.onStartLoading();
                Log.d(TAG, "onStartLoading: Rick");
                int ss =  bundle.getInt(MOVIE_DETAILS_EXTRA);
                Log.d(TAG, "onStartLoading: ");

            }

            @Override
            public ArrayList loadInBackground() {

                Log.d(TAG, "loadinBackground: RICKKKKK ");
                int movie_id = bundle.getInt(MOVIE_DETAILS_EXTRA);
                if (movie_id < 0 ){
                    return null;
                }

                try {

                    URL MoviesDetailRequestUrl = movieDetailsNetworkUtil.buildUrl(movie_id);

                    String jsonMovieResponse =  movieDetailsNetworkUtil
                            .getResponseFromHttpUrl(MoviesDetailRequestUrl);
                    ArrayList trailers_josn = new ArrayList();

                    try {
                        trailers_josn = TheMovieDetailsJonUtils
                                .simpleJsonMovieDataStringsFromJson(MovieDetailsPage.this, jsonMovieResponse, mMovie);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    //https://api.themoviedb.org/3/movie/346364/videos?api_key=18e23d5378804a57dc5743d12472408f&language=en-US

                    return trailers_josn;

                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<ArrayList> loader, ArrayList trailers_obj) {
        Log.d(TAG, "bitch: " + trailers_obj);

        mDetailsAdapter.setMovieData(trailers_obj);
        mDetailsAdapter.notifyDataSetChanged();

    }

    @Override
    public void onLoaderReset(Loader<ArrayList> loader) {

    }

    @Override
    public void onMovieItemClick(int clickedItemPosition, Movie clickedOnMovie) {

    }
}
