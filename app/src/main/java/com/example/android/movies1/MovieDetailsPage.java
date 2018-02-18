package com.example.android.movies1;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.movies1.Utils.TheMovieDetailsJonUtils;
import com.example.android.movies1.Utils.TheReviewDetailsJsonUtils;
import com.example.android.movies1.Utils.movieDetailsNetworkUtil;
import com.example.android.movies1.Utils.reviewDetailsNetworkUtil;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class MovieDetailsPage extends AppCompatActivity implements DetailsAdapter.DetailsClickListener, DetailsAdapter.ReviewAdapterOnClickHandler {

    private String TAG = MovieDetailsPage.class.getSimpleName();
    private RecyclerView dRecyclerView;
    private DetailsAdapter mDetailsAdapter;
    private static final String TRAILER_DETAILS_EXTRA = "trailer_query";
    private static final String REVIEW_DETAILS_EXTRA = "review_query";
    private final int TRAILER_DETAILS_LOADER = 44;
    private final int REVIEW_DETAILS_LOADER = 88;

    private TextView movieTitle;
    private ImageView movieImage;
    private TextView movieOverview;
    private TextView movieDate;
    private TextView movieRating;
    ImageButton star;
    private int id;
    Trailer trailers;
    Context context;
    Review reviews;

    Movie mMovie;
    ArrayList<Trailer> trailerArrayList;
    ArrayList<Review> reviewArrayList;
    ArrayList<Object> objectsArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details_page);
        context = this.getApplicationContext();

        dRecyclerView = findViewById(R.id.rv_movie_details);

        movieTitle = findViewById(R.id.title);
        movieImage = findViewById(R.id.poster_image);
        movieOverview = findViewById(R.id.overview);
        movieDate = findViewById(R.id.date);
        movieRating = findViewById(R.id.rating);
        star = findViewById(R.id.star);

        trailerArrayList = new ArrayList<>();
        objectsArrayList = new ArrayList<>();


        /*
            get the movie object from mainActivity and get the ID to be used for network call
            to get the trailer
         */
        Movie movie_obj = getIntent().getParcelableExtra("movie_obj");
        mMovie = movie_obj;
        if (movie_obj != null) {
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

            LinearLayoutManager dLayoutManager
                    = new LinearLayoutManager(this);

            dRecyclerView.setLayoutManager(dLayoutManager);
            //dRecyclerView.setNestedScrollingEnabled(true);
            dRecyclerView.setHasFixedSize(true);

            mDetailsAdapter = new DetailsAdapter(this,trailerArrayList, reviewArrayList, this, objectsArrayList  );
            dRecyclerView.setAdapter(mDetailsAdapter);
            activateTrailersLoader(id);
            activateReviewLoader(id);

            star.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    changeStarColor(v);
                }
            });

        }
        //movieImage.setImageBitmap(movie_obj.getBACKDROP_PATH());
    }

    private void changeStarColor(View v) {
        Toast.makeText(getApplicationContext(), "Movie Favorited", Toast.LENGTH_SHORT).show();
    }

    /*
        int id --> the ID of the movie clicked on
     */
    private void activateTrailersLoader(int id) {

        Bundle queryTrailerBundle = new Bundle();
        queryTrailerBundle.putInt(TRAILER_DETAILS_EXTRA, id);

        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<Integer> MoviesSearchLoader = loaderManager.getLoader(TRAILER_DETAILS_LOADER);

        if (MoviesSearchLoader == null) {
            loaderManager.initLoader(TRAILER_DETAILS_LOADER, queryTrailerBundle, trailerLoaderListener).forceLoad();
        } else {
            loaderManager.restartLoader(TRAILER_DETAILS_LOADER, queryTrailerBundle, trailerLoaderListener);
        }
    }

    private void activateReviewLoader(int id) {

        Bundle queryReviewBundle = new Bundle();
        queryReviewBundle.putInt(REVIEW_DETAILS_EXTRA,id);

        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<Integer> ReviewSearchLoader = loaderManager.getLoader(REVIEW_DETAILS_LOADER);

        if (ReviewSearchLoader == null) {
            loaderManager.initLoader(REVIEW_DETAILS_LOADER, queryReviewBundle, reviewLoaderListener).forceLoad();
        } else {
            loaderManager.restartLoader(REVIEW_DETAILS_LOADER, queryReviewBundle, reviewLoaderListener);
        }
    }

    //--------------------------------------------------------------------------------------------

    public LoaderManager.LoaderCallbacks<ArrayList> reviewLoaderListener = new LoaderManager.LoaderCallbacks<ArrayList>() {

        @SuppressLint("StaticFieldLeak")
        @Override
        public AsyncTaskLoader onCreateLoader(int id, final Bundle bundle) {
            return new AsyncTaskLoader(context) {
                @Override
                public Object loadInBackground() {
                    Log.d(TAG, "loadinBackground: TIME ");
                    int movie_id = bundle.getInt(REVIEW_DETAILS_EXTRA);
                    if (movie_id < 0) {
                        return null;
                    }

                    try {

                        URL ReviewsDetailRequestUrl = reviewDetailsNetworkUtil.buildUrl(movie_id);

                        String jsonReviewResponse = reviewDetailsNetworkUtil
                                .getResponseFromHttpUrl(ReviewsDetailRequestUrl);
                        ArrayList reviews_josn = new ArrayList<>();

                        try {
                            reviews_josn = TheReviewDetailsJsonUtils
                                    .simpleJsonMovieDataStringsFromJson(jsonReviewResponse);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        //https://api.themoviedb.org/3/movie/346364/videos?api_key=18e23d5378804a57dc5743d12472408f&language=en-US
                        return reviews_josn;

                    } catch (IOException e) {
                        e.printStackTrace();
                        return null;
                    }
                }
            };

        }

        @Override
        public void onLoadFinished(Loader<ArrayList> loader, ArrayList reviewsList) {
            Log.d(TAG, "RICK onLoadFinished: " + reviewsList);

            if (reviewsList != null) {
                reviewArrayList = reviewsList;
                Log.d(TAG, "PICKLE RICK: " + reviewsList.toString());
                mDetailsAdapter.setReiewData(reviewsList);
                mDetailsAdapter.notifyDataSetChanged();
            }

        }

        @Override
        public void onLoaderReset(Loader<ArrayList> loader) {

        }
    };


    //--------------------------------------------------------------------------------------------

    public LoaderManager.LoaderCallbacks<ArrayList> trailerLoaderListener = new LoaderManager.LoaderCallbacks<ArrayList>() {

        @SuppressLint("StaticFieldLeak")
        public AsyncTaskLoader onCreateLoader(int id, final Bundle bundle) {
            return new AsyncTaskLoader(MovieDetailsPage.this) {
                @Override
                protected void onStartLoading() {
                    super.onStartLoading();
                    Log.d(TAG, "onStartLoading: Rick");
                    int ss = bundle.getInt(TRAILER_DETAILS_EXTRA);
                    Log.d(TAG, "onStartLoading: ");

                }

                @Override
                public ArrayList loadInBackground() {

                    Log.d(TAG, "loadinBackground: RICKKKKK ");
                    int movie_id = bundle.getInt(TRAILER_DETAILS_EXTRA);
                    if (movie_id < 0) {
                        return null;
                    }

                    try {

                        URL MoviesDetailRequestUrl = movieDetailsNetworkUtil.buildUrl(movie_id);

                        String jsonMovieResponse = movieDetailsNetworkUtil
                                .getResponseFromHttpUrl(MoviesDetailRequestUrl);
                        ArrayList trailers_josn = new ArrayList<>();

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
            Log.d(TAG, "RICK onLoadFinished: " + trailers_obj);

            if (trailers_obj != null) {
                trailerArrayList = trailers_obj;
                Log.d(TAG, "PICKLE RICK: " + trailers_obj.toString());
                mDetailsAdapter.setMovieData(trailers_obj);
                mDetailsAdapter.notifyDataSetChanged();
            }

        }


        @Override
        public void onLoaderReset(Loader<ArrayList> loader) {

        }

        //--------------------------------------------------------------------------------------------
    };

    @Override
    public void onListItemClick(int clickedItemPosition) {
        trailers = trailerArrayList.get(clickedItemPosition);
        String urlAsString = "https://www.youtube.com/watch?v=";
        Uri webPage = Uri.parse(urlAsString.concat(trailers.getKey()));
        Intent intent = new Intent(Intent.ACTION_VIEW, webPage);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    @Override
    public void onClick(int click) {
        reviews = reviewArrayList.get(click);
        Intent i = new Intent(this, ReviewContent.class);
        i.putExtra("reviews",  reviews);
        startActivity(i);
    }
}