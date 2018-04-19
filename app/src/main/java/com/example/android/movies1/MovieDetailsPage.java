package com.example.android.movies1;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
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

import com.example.android.movies1.Adapers.DetailsAdapter;
import com.example.android.movies1.DataBase.MovieContract;
import com.example.android.movies1.Models.Movie;
import com.example.android.movies1.Models.Review;
import com.example.android.movies1.Models.Trailer;
import com.example.android.movies1.Utils.TheMovieDetailsJonUtils;
import com.example.android.movies1.Utils.TheReviewDetailsJsonUtils;
import com.example.android.movies1.Utils.movieDetailsNetworkUtil;
import com.example.android.movies1.Utils.reviewDetailsNetworkUtil;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;


/**
 * Class to display a clicked on movie operations
 */
public class MovieDetailsPage extends AppCompatActivity implements DetailsAdapter.DetailsClickListener,
        DetailsAdapter.ReviewAdapterOnClickHandler {

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
    Cursor mData;
    private int id;
    Trailer trailers;
    Context context;
    private boolean isFavorite = false;
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
        }

            LinearLayoutManager dLayoutManager
                    = new LinearLayoutManager(this);

            dRecyclerView.setLayoutManager(dLayoutManager);
            dRecyclerView.setHasFixedSize(true);

            activateReviewLoader(id);
            activateTrailersLoader(id);

            mDetailsAdapter = new DetailsAdapter(this,trailerArrayList,
                    reviewArrayList, this, objectsArrayList  );
            dRecyclerView.setAdapter(mDetailsAdapter);

            FetchQueryOfDatabase task = new FetchQueryOfDatabase();
            task.execute();

            star.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void onClick(View v) {
                    changeStarColor(v);
                }
            });

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void changeStarColor(View v) {
        Toast.makeText(getApplicationContext(), "Movie Favorited", Toast.LENGTH_SHORT).show();
        if(isFavorite){
            makeUnfavorite();
        }else if (!isFavorite) {
            makeFavorite();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void makeFavorite() {
        isFavorite = true;
        star.setColorFilter(getColor(R.color.Golden));
        addToDBMakeFav();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void makeStarGolden(){
        isFavorite = true;
        star.setColorFilter(getColor(R.color.Golden));
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void makeStarGrey(){
        isFavorite = false;
        star.setColorFilter(getColor(R.color.Grey));

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void makeUnfavorite() {
        isFavorite = false;
        star.setColorFilter(getColor(R.color.Grey));
        deleteFromDB();
    }

    private void deleteFromDB() {

        ContentResolver resolver = getContentResolver();
        String selection = MovieContract.FavoriteEntry.COLUMN_MOVIE_ID + "=?";
        Movie item = getIntent().getParcelableExtra("movie_obj");
        String favoriteId = item.getID();
        long id = Long.parseLong(favoriteId);
        Log.v(TAG, "Movie id to delete");
        Uri uri = MovieContract.FavoriteEntry.builtFavoriteUri(id);

        String[] args = new String[]{
                String.valueOf(ContentUris.parseId(uri))

        };
        try {
            int rowsDeleted = resolver.delete(MovieContract.FavoriteEntry.CONTENT_URI,
                    selection,
                    args);
            if (rowsDeleted == -1) {
                Log.e(TAG, "Error deleting row from DB");

            } else {
                Log.v(TAG, "Row Deleted");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error performing delete", e);

        }
    }

    private void addToDBMakeFav() {
        if (movieTitle == null || movieDate == null ||
                movieRating == null || movieOverview == null) {
            Log.e(TAG, "Empty movie data in TextViews");
            finish();
            return;
        }

        Movie item =  getIntent().getParcelableExtra("movie_obj");

        String imageString = item.getBACKDROP_PATH();
        String favoriteTitle = item.getTITLE();
        String favoriteMovieId = item.getID();
        String favoriteDate = item.getRELEASE_DATE();
        String favoriteRating = item.getVOTE_AVERAGE();
        String favoriteOverview = item.getOVERVIEW();
        String backdrop_path = item.getBACKDROP_PATH();

        long id = 0;
        if (favoriteMovieId != null) {
            id = Long.parseLong(favoriteMovieId);
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put(MovieContract.FavoriteEntry.COLUMN_MOVIE_ID, id);
        contentValues.put(MovieContract.FavoriteEntry.COLUMN_TITLE, favoriteTitle);
        contentValues.put(MovieContract.FavoriteEntry.COLUMN_POSTER, imageString);
        contentValues.put(MovieContract.FavoriteEntry.COLUMN_RATING, favoriteRating);
        contentValues.put(MovieContract.FavoriteEntry.COLUMN_RELEASE_DATE, favoriteDate);
        contentValues.put(MovieContract.FavoriteEntry.COLUMN_OVERVIEW, favoriteOverview);
        contentValues.put(MovieContract.FavoriteEntry.COLUMN_BACKDROP_PATH, backdrop_path);

        try {
            Uri newUri = getContentResolver().insert(MovieContract.FavoriteEntry.CONTENT_URI,
                    contentValues);
            Log.v(TAG, "Uri: " + newUri.toString());
        } catch (Exception e) {
            Log.e(TAG, "Error inserting movie to DB", e);
        }
    }


    ///----------------------------------------------------------------------------------
    private class FetchQueryOfDatabase extends AsyncTask<Void, Void, Cursor> {

        Movie item = getIntent().getParcelableExtra("movie_obj");
        int id = Integer.parseInt(item.getID());
        long id1 = id;

        @Override
        protected Cursor doInBackground(Void... params) {
            ContentResolver resolver = getContentResolver();

            String[] projection = {
                    MovieContract.FavoriteEntry.COLUMN_MOVIE_ID
            };
            String selection = MovieContract.FavoriteEntry.COLUMN_MOVIE_ID + "=?";
            Uri uri = MovieContract.FavoriteEntry.builtFavoriteUri(id1);
            String[] args = new String[]{
                    String.valueOf(ContentUris.parseId(uri))

            };
            Cursor cursor = null;
            try {
                cursor = resolver.query(
                        MovieContract.FavoriteEntry.CONTENT_URI,
                        projection,
                        selection,
                        args,
                        null
                );
            } catch (Exception e) {
                Log.e("Can't query database", e.toString());

            }
            return cursor;
        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        protected void onPostExecute(Cursor cursor) {
            super.onPostExecute(cursor);
            mData = cursor;
            if (null == mData) {
                Log.e(TAG, "Cursor is not working");

            } else if (cursor.getCount() < 1) {
                makeStarGrey();
                Log.v(TAG, "Movie ID not inside DATABASE");
            } else if (mData.moveToFirst()) {
                for (int j = 0; j < mData.getCount(); j++) {
                    if (mData.getCount() > 0) {
                        makeStarGolden();
                    } else {
                        makeStarGrey();
                    }
                    Log.v(TAG, "This movie is in your DATABASE.");
                }
                mData.close();
            }
        }
    }

    ///----------------------------------------------------------------------------------


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
                Log.d(TAG, "PICKLE RICK: " + reviewsList.toString());
                mDetailsAdapter.setReiewData(reviewsList);
                reviewArrayList = reviewsList;
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
            if (trailers_obj != null) {
                Log.d(TAG, "PICKLE RICK: " + trailers_obj.toString());
                mDetailsAdapter.setMovieData(trailers_obj);
                trailerArrayList = trailers_obj;
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